package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.network.UpgradeUpdateMessage;
import io.github.cvrunmin.enhancedmachine.tileentity.IBrewingStandExt;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.BrewingStandTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(BrewingStandTileEntity.class)
public abstract class BrewingStandTileEntityMixin extends TileEntity implements IBrewingStandExt {
    @Shadow private int brewTime;
    @Shadow private int fuel;
    @Shadow private NonNullList<ItemStack> brewingItemStacks;

    @Shadow public abstract boolean[] createFilledSlotsArray();

    @Shadow private boolean[] filledSlots;
    @Shadow private Item ingredientID;

    @Shadow protected abstract void brewPotions();

    @Shadow protected abstract boolean canBrew();

    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();

    public BrewingStandTileEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void assignCap(CallbackInfo info){
        upgradeSlot.setHolder((BrewingStandTileEntity)(Object)this);
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

    @Inject(method = "read", at = @At(value = "TAIL"))
    public void afterReadNBT(CompoundNBT nbt, CallbackInfo info){
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt.getList("Upgrades", 10));
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void afterWriteNBT(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> info){
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
    }

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
        handleUpdateTag(pkt.getNbtCompound());
    }

    public boolean hasFuel(){
        return this.fuel > 0;
    }

    public boolean hasFuelCheckFreeEnergy(){
        if(upgradeSlot.hasUpgradeInstalled(Upgrades.FREE_ENERGY)) {
            UpgradeNode node = upgradeSlot.getFirstFoundUpgrade(Upgrades.FREE_ENERGY);
            if (node.getParent() == null || !EMConfig.isBanned(node.getParent().getUpgrade(), world != null && world.isRemote ? ServerConfigHelper.getSyncBannedUpgrade() : EMConfig.getBannedUpgradesParsed())) {
                return true;
            }
        }
        return hasFuel();
    }

    @Overwrite
    public void tick() {
        ItemStack itemstack = this.brewingItemStacks.get(4);
        if (!hasFuelCheckFreeEnergy() && itemstack.getItem() == Items.BLAZE_POWDER) {
            this.fuel = getFuelTime();
            itemstack.shrink(1);
            this.markDirty();
        }

        boolean flag = this.canBrew();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = this.brewingItemStacks.get(3);
        if (flag1) {
            --this.brewTime;
            boolean flag2 = this.brewTime == 0;
            if (flag2 && flag) {
                this.brewPotions();
                this.markDirty();
            } else if (!flag) {
                this.brewTime = 0;
                this.markDirty();
            } else if (this.ingredientID != itemstack1.getItem()) {
                this.brewTime = 0;
                this.markDirty();
            }
        } else if (flag && hasFuelCheckFreeEnergy()) {
            if(hasFuel()) {
                --this.fuel;
            }
            this.brewTime = getBrewTime();
            this.ingredientID = itemstack1.getItem();
            this.markDirty();
        }

        if (!this.world.isRemote) {
            boolean[] aboolean = this.createFilledSlotsArray();
            if (!Arrays.equals(aboolean, this.filledSlots)) {
                this.filledSlots = aboolean;
                BlockState blockstate = this.world.getBlockState(this.getPos());
                if (!(blockstate.getBlock() instanceof BrewingStandBlock)) {
                    return;
                }

                for(int i = 0; i < BrewingStandBlock.HAS_BOTTLE.length; ++i) {
                    blockstate = blockstate.with(BrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
                }

                this.world.setBlockState(this.pos, blockstate, 2);
            }
        }
        if (upgradeSlot.isDirty()) {
            if(!this.world.isRemote){
                EnhancedMachine.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)), new UpgradeUpdateMessage(this.pos, this.upgradeSlot));
            }
            upgradeSlot.resetDirtyState();
            this.markDirty();
        }
    }

//    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/BrewingStandTileEntity;brewTime:I", opcode = 181, slice = "a"),
//    slice = @Slice(id = "a",from = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/BrewingStandTileEntity;fuel:I", opcode = 180, ordinal = 1)))
//    private void modifyBrewTime(BrewingStandTileEntity tileEntity, int brewTime){
//        this.brewTime = getBrewTime();
//    }
//
//    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/tileentity/BrewingStandTileEntity;fuel:I", opcode = 181, ordinal = 0))
//    private void modifyFuelTime(BrewingStandTileEntity tileEntity, int fuel){
//        this.fuel = getFuelTime();
//    }
//
//    @Inject(method = "tick", at = @At("TAIL"))
//    private void updateUpgradeSlot(CallbackInfo ci){
//        if (upgradeSlot.isDirty()) {
//            if(!this.world.isRemote){
//                EnhancedMachine.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)), new UpgradeUpdateMessage(this.pos, this.upgradeSlot));
//            }
//            upgradeSlot.resetDirtyState();
//            this.markDirty();
//        }
//    }

    public int getBrewTime() {
        if (upgradeSlot.hasUpgradeInstalled(Upgrades.TIME_ACCELERATION)) {
            UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.TIME_ACCELERATION);
            if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
            return (int) Math.max(1, 400 / (Upgrades.TIME_ACCELERATION.getSpeedBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail)));
        }
        return 400;
    }

    public int getFuelTime() {
        double scalingRate = 1;
        if (upgradeSlot.hasUpgradeInstalled(Upgrades.TIME_ACCELERATION)) {
            UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.TIME_ACCELERATION);
            if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
            scalingRate = scalingRate / (Upgrades.TIME_ACCELERATION.getSpeedBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail));
        }
        if (upgradeSlot.hasUpgradeInstalled(Upgrades.FUEL_MASTERY)) {
            UpgradeNode upgradeDetail = upgradeSlot.getFirstFoundUpgrade(Upgrades.FUEL_MASTERY);
            if(!EMConfig.isBanned(upgradeDetail.getUpgrade()))
            scalingRate = scalingRate * (Upgrades.FUEL_MASTERY.getTimeBoost(upgradeDetail.getUpgrade().getLevel()) * upgradeSlot.getEffectMultiplier(upgradeDetail));
        }
        return (int) Math.max(20 * scalingRate, 1);
    }
}
