package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ChestTileEntity.class)
public abstract class ChestTileEntityMixin extends LockableLootTileEntity {
    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();

    protected ChestTileEntityMixin(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void assignCap(CallbackInfo info){
        upgradeSlot.setHolder((ChestTileEntity)(Object)this);
    }

    @Inject(method = "read", at = @At("TAIL"))
    public void afterReadNBT(CompoundNBT nbt, CallbackInfo info){
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt.getList("Upgrades", 10));
    }

    @Inject(method = "write", at = @At("TAIL"))
    public void afterWriteNBT(CompoundNBT nbt, CallbackInfoReturnable<CompoundNBT> info){
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
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
