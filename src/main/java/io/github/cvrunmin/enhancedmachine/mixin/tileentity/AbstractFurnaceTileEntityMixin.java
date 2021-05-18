package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.network.UpgradeUpdateMessage;
import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(AbstractFurnaceTileEntity.class)
public abstract class AbstractFurnaceTileEntityMixin extends TileEntity implements IHyperthreadable {

    @Final
    @Shadow
    private static int[] SLOTS_HORIZONTAL;

    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();
    @Shadow
    protected NonNullList<ItemStack> items;
    @Shadow
    private int cookTimeTotal;
    @Shadow
    private int cookTime;
    @Shadow
    private int burnTime;
    @Shadow
    private int burnTimeTotal;
    @Shadow
    @Final
    protected IRecipeType<? extends AbstractCookingRecipe> recipeType;
    @Shadow
    public abstract void setRecipeUsed(@Nullable IRecipe<?> recipe);
    @Shadow
    protected abstract boolean isBurning();
    protected AbstractFurnaceTileEntityMixin(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void assignCap(CallbackInfo info){
        items = NonNullList.withSize(1 + 2 * 9, ItemStack.EMPTY);
        upgradeSlot.setHolder((AbstractFurnaceTileEntity)(Object)this);
    }

    @Inject(method = "read", at = @At("TAIL"))
    public void afterReadNBT(BlockState blockState, CompoundNBT nbt, CallbackInfo info){
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt.getList("Upgrades", 10));
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void afterWriteNBT(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> info){
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
    }
    @Overwrite
    protected int getBurnTime(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        } else {
            double scalingRate = 1;
            if (upgradeSlot.hasUpgradeInstalled(Upgrades.FUEL_MASTERY)) {
                UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.FUEL_MASTERY);
                if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
                    scalingRate = scalingRate * (Upgrades.FUEL_MASTERY.getTimeBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail));
            }
            if (upgradeSlot.hasUpgradeInstalled(Upgrades.TIME_ACCELERATION)) {
                UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.TIME_ACCELERATION);
                if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
                scalingRate = scalingRate / (Upgrades.TIME_ACCELERATION.getSpeedBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail));
            }
            return (int) Math.max(1, net.minecraftforge.common.ForgeHooks.getBurnTime(fuel) * scalingRate);
        }
    }

    /**
     * @author
     */
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = ((TileEntityAccessorMixin) this).invokeWriteInternal(new CompoundNBT());
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
        return nbt;
    }

//    @Override
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
//    }

//    @Overwrite(remap = false)

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(getWorld().getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    @Override
    public int getHyperthreadedSlotsCount() {
        UpgradeNode htnode = upgradeSlot.getFirstFoundUpgrade(Upgrades.HYPERTHREAD);
        return htnode != null && !EMConfig.isBanned(htnode.getUpgrade()) ? getHyperthreadedSlotsCount(htnode.getUpgrade().getLevel(), upgradeSlot.getEffectMultiplier(htnode)) : 1;
    }

    public int getHyperthreadedSlotsCount(int level, double multiplier) {
        int slots;
        if (level <= 0) {
            slots = 1;
        } else if (level == 1) {
            slots = 2;
        } else if (level == 2) {
            slots = 4;
        } else if (level == 3) {
            slots = 5;
        } else if (level == 4) {
            slots = 7;
        } else {
            slots = 9;
        }
        slots = (int) Math.floor(slots * multiplier);
        return Math.max(slots, 1);
    }

    @Overwrite
    protected int getCookTime() {
        double rate = 1.0;
        if (upgradeSlot.hasUpgradeInstalled(Upgrades.TIME_ACCELERATION)) {
            UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.TIME_ACCELERATION);
            if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
            rate = Upgrades.TIME_ACCELERATION.getSpeedBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail);
        }
        return (int)Math.max(1, Arrays.stream(this.getSlotsForFace(Direction.UP))
                .map(idx -> this.world.getRecipeManager().getRecipe(this.recipeType, new Inventory(this.items.get(idx)), this.world)
                        .map(AbstractCookingRecipe::getCookTime).orElse(200))
                .max().orElse(200) / rate);
    }

    @Inject(method = "setInventorySlotContents", at = @At(value = "JUMP", opcode = 154, ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void onSetInventorySlotContent(int index, ItemStack stack, CallbackInfo ci, ItemStack itemstack, boolean flag){
        if ((index == 0 || index != 1 && index % 2 == 1) && !flag) {
            this.cookTimeTotal = this.getCookTime();
            this.cookTime *= (1 - 1f / getHyperthreadedSlotsCount());
            this.markDirty();
        }
        ci.cancel();
    }

    @ModifyVariable(method = "isItemValidForSlot", argsOnly = true, at = @At(value = "JUMP", opcode = 160, ordinal = 0), index = 1)
    private int reduceIndexIsItemValidForSlot(int index){
        if(index != 0 && index % 2 == 0){
            return 2;
        }
        return index;
    }

    @Overwrite
    public int[] getSlotsForFace(Direction side) {
        if (side == Direction.DOWN) {
            List<Integer> list = new ArrayList<>();
            int count = getHyperthreadedSlotsCount();
            for (int i = 0; i < count; i++) {
                list.add(2 * (i + 1));
            }
            list.add(1); // Hopper can pull empty buckets in fuel slot out
            return list.stream().mapToInt(Integer::intValue).toArray();
        } else if (side == Direction.UP) {
            List<Integer> list = new ArrayList<>();
            int count = getHyperthreadedSlotsCount();
            list.add(0);
            for (int i = 1; i < count; i++) {
                list.add(1 + 2 * i);
            }
            return list.stream().mapToInt(Integer::intValue).toArray();
        } else {
            return SLOTS_HORIZONTAL;
        }
    }

    private boolean isInputReady(){
        for (int i : getSlotsForFace(Direction.UP)) {
            if(!items.get(i).isEmpty()) return true;
        }
        return false;
    }

    private boolean isFuelBurning(){
        return isBurning();
    }

    private boolean freeEnergyBurnFlag = false;

    private boolean wasBurning(){
        return isBurning() || freeEnergyBurnFlag;
    }

    private boolean isBurningCheckFreeEnergy(){
        if (canUseFreeEnergy()) return true;

        return isFuelBurning();
    }

    private boolean canUseFreeEnergy() {
        if(upgradeSlot.hasUpgradeInstalled(Upgrades.FREE_ENERGY)){
            UpgradeNode node = upgradeSlot.getFirstFoundUpgrade(Upgrades.FREE_ENERGY);
            if (node.getParent() == null || !EMConfig.isBanned(node.getParent().getUpgrade(), world != null && world.isRemote ? ServerConfigHelper.getSyncBannedUpgrade() : EMConfig.getBannedUpgradesParsed())) {
                return true;
            }
        }
        return false;
    }

    @Overwrite
    public void tick() {
        boolean flag = this.wasBurning();
        boolean flag1 = false;
        if (this.isBurning()) {
            --this.burnTime;
        }

        if (!this.world.isRemote) {
            ItemStack itemstack = this.items.get(1);
            if (this.isBurningCheckFreeEnergy() || !itemstack.isEmpty() && isInputReady()) {
                if (!this.isFuelBurning() && !this.canUseFreeEnergy() && this.canSmelt()) {
                    this.burnTime = this.getBurnTime(itemstack);
                    this.burnTimeTotal = this.burnTime;
                    if (this.isBurning()) {
                        flag1 = true;
                        if (itemstack.hasContainerItem())
                            this.items.set(1, itemstack.getContainerItem());
                        else
                        if (!itemstack.isEmpty()) {
                            Item item = itemstack.getItem();
                            itemstack.shrink(1);
                            if (itemstack.isEmpty()) {
                                this.items.set(1, itemstack.getContainerItem());
                            }
                        }
                    }
                }

                if (this.isBurningCheckFreeEnergy() && this.canSmelt()) {
                    if(this.cookTime == 0 && !flag){
                        this.cookTimeTotal = this.getCookTime();
                    }
                    if(this.isBurningCheckFreeEnergy() && ! this.isBurning() && !freeEnergyBurnFlag){
                        freeEnergyBurnFlag = true;
                    }
                    ++this.cookTime;
                    if (this.cookTime == this.cookTimeTotal) {
                        this.cookTime = 0;
                        this.cookTimeTotal = this.getCookTime();
                        this.smelt();
                        flag1 = true;
                    }
                } else {
                    freeEnergyBurnFlag = false;
                    this.cookTime = 0;
                }
            } else if (!this.isBurningCheckFreeEnergy() && this.cookTime > 0) {
                this.cookTime = MathHelper.clamp(this.cookTime - 2, 0, this.cookTimeTotal);
            }

            boolean burning = this.isBurning() || freeEnergyBurnFlag;
            if (flag != burning) {
                flag1 = true;
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(AbstractFurnaceBlock.LIT, burning), 3);
            }
        }
        if (upgradeSlot.isDirty()) {
            flag1 = true;
            if(!this.world.isRemote){
                EnhancedMachine.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)), new UpgradeUpdateMessage(this.pos, this.upgradeSlot));
            }
            upgradeSlot.resetDirtyState();
        }

        if (flag1) {
            this.markDirty();
        }

    }

    protected boolean canSmelt(){
        return IntStream.range(0, getHyperthreadedSlotsCount()).anyMatch(pairSlot -> {
            int inputSlot = pairSlot == 0 ? 0 : 1 + 2 * pairSlot;
            IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, new Inventory(this.items.get(inputSlot)), this.world).orElse(null);
            return canSmelt(irecipe, pairSlot);
        });
    }

    protected boolean canSmelt(@Nullable IRecipe<?> recipeIn, int pairSlot) {
        int inputSlot = pairSlot == 0 ? 0 : 1 + 2 * pairSlot;
        int outputSlot = 2 + 2 * pairSlot;
        if (!this.items.get(inputSlot).isEmpty() && recipeIn != null) {
            ItemStack itemstack = recipeIn.getRecipeOutput();
            if (itemstack.isEmpty()) {
                return false;
            } else {
                return getSuitableOutputSlot(recipeIn) != -1;
//                ItemStack itemstack1 = this.items.get(outputSlot);
//                if (itemstack1.isEmpty()) {
//                    return true;
//                } else if (!itemstack1.isItemEqual(itemstack)) {
//                    return false;
//                } else if (itemstack1.getCount() + itemstack.getCount() <= ((AbstractFurnaceTileEntity)(Object)this).getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
//                    return true;
//                } else {
//                    return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
//                }
            }
        } else {
            return false;
        }
    }

    private int getSuitableOutputSlot(IRecipe<?> recipe){
        ItemStack itemstack = recipe.getRecipeOutput();
        for (int i = 0; i < getHyperthreadedSlotsCount(); i++) {
            int outputSlot = 2 + 2 * i;
            ItemStack itemstack1 = this.items.get(outputSlot);
            if (itemstack1.isEmpty()) {
                return outputSlot;
            } else if (!itemstack1.isItemEqual(itemstack)) {
                continue;
            } else if (itemstack1.getCount() + itemstack.getCount() <= ((AbstractFurnaceTileEntity)(Object)this).getInventoryStackLimit() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                return outputSlot;
            } else if (itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize()) {
                return outputSlot;// Forge fix: make furnace respect stack sizes in furnace recipes
            }
        }
        return -1;
    }

    private void smelt(){
        IntStream.range(0, getHyperthreadedSlotsCount()).forEach(pairSlot -> {
            int inputSlot = pairSlot == 0 ? 0 : 1 + 2 * pairSlot;
            IRecipe<?> irecipe = this.world.getRecipeManager().getRecipe((IRecipeType<AbstractCookingRecipe>)this.recipeType, new Inventory(this.items.get(inputSlot)), this.world).orElse(null);
            smelt(irecipe, pairSlot);
        });
    }

    private void smelt(@Nullable IRecipe<?> recipe, int pairSlot) {
        if (recipe != null && this.canSmelt(recipe, pairSlot)) {
            int inputSlot = pairSlot == 0 ? 0 : pairSlot * 2 + 1;
            int outputSlot = getSuitableOutputSlot(recipe); //canSmelt already check if there is a suitable slot
            ItemStack itemstack = this.items.get(inputSlot);
            ItemStack itemstack1 = recipe.getRecipeOutput();
            ItemStack itemstack2 = this.items.get(outputSlot);
            if (itemstack2.isEmpty()) {
                this.items.set(outputSlot, itemstack1.copy());
            } else if (itemstack2.getItem() == itemstack1.getItem()) {
                itemstack2.grow(itemstack1.getCount());
            }

            if (!this.world.isRemote) {
                this.setRecipeUsed(recipe);
            }

            if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
                this.items.set(1, new ItemStack(Items.WATER_BUCKET));
            }

            itemstack.shrink(1);
        }
    }

    @Inject(method = "getCapability", at = @At("HEAD"), remap = false, cancellable = true)
    private <T> void injectGetCap(Capability<T> capability, @Nullable Direction facing, CallbackInfoReturnable<LazyOptional<T>> ci){
        if (capability == CapabilityUpgradeSlot.UPGRADE_SLOT) {
            ci.setReturnValue(LazyOptional.of(() -> upgradeSlot).cast());
        }
    }

//    @Override
//    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
//        if (capability == CapabilityUpgradeSlot.UPGRADE_SLOT) {
//            return LazyOptional.of(() -> upgradeSlot).cast();
//        }
//        return super.getCapability(capability, facing);
//    }
}
