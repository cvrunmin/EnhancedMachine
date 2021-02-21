package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.network.UpgradeUpdateMessage;
import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@SuppressWarnings("OverwriteAuthorRequired")
@Mixin(DispenserTileEntity.class)
public abstract class DispenserTileEntityMixin extends LockableLootTileEntity implements IHyperthreadable, ITickableTileEntity {
    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();
    @Shadow
    private NonNullList<ItemStack> stacks;

    public DispenserTileEntityMixin(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void assignCap(CallbackInfo info){
        stacks = NonNullList.withSize(27, ItemStack.EMPTY);
        upgradeSlot.setHolder((DispenserTileEntity)(Object)this);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityUpgradeSlot.UPGRADE_SLOT) {
            return LazyOptional.of(() -> upgradeSlot).cast();
        }
        return super.getCapability(capability, facing);
    }

    @Inject(method = "read", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    public void afterReadNBT(CompoundNBT nbt, CallbackInfo info){
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt.getList("Upgrades", 10));
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void afterWriteNBT(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> info){
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
    }

//    @Override
//    public CompoundNBT getUpdateTag() {
//        CompoundNBT nbt = ((TileEntityAccessorMixin) this).invokeWriteInternal(new CompoundNBT());
//        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
//        return nbt;
//    }

//    @Override
//    public SUpdateTileEntityPacket getUpdatePacket() {
//        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
//    }

//    @Overwrite(remap = false)

//    @Override
//    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
//        super.onDataPacket(net, pkt);
//        handleUpdateTag(pkt.getNbtCompound());
//    }

    @Override
    public void tick() {
        if (upgradeSlot.isDirty()) {
            if(!this.world.isRemote){
                EnhancedMachine.CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> this.world.getChunkAt(this.pos)), new UpgradeUpdateMessage(this.pos, this.upgradeSlot));
            }
            upgradeSlot.resetDirtyState();
            this.markDirty();
        }
    }

    @Overwrite
    public int getSizeInventory() {
        return getHyperthreadedSlotsCount();
    }

    @Override
    public int getHyperthreadedSlotsCount() {
        UpgradeNode htnode = upgradeSlot.getFirstFoundUpgrade(Upgrades.HYPERTHREAD);
        int level = htnode != null ? htnode.getUpgrade().getLevel() : 0;
        if(htnode != null && EMConfig.isBanned(htnode.getUpgrade())) return 9;
        double multiplier = htnode != null ? upgradeSlot.getEffectMultiplier(htnode) : 1;
        return getHyperthreadedSlotsCount(level, multiplier);
    }

    public int getHyperthreadedSlotsCount(int level, double multiplier) {
        int slots;
        if (level <= 0) {
            return 9;
        } else if (level == 1) {
            slots = 12;
        } else if (level == 2) {
            slots = 15;
        } else if (level == 3) {
            slots = 18;
        } else if (level == 4) {
            slots = 21;
        } else {
            slots = 27;
        }
        slots = (int) Math.floor((slots - 9) * multiplier + 9);
        return Math.max(slots, 9);
    }
}
