package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpgradeSlotCapabilityProvider implements ICapabilityProvider, INBTSerializable<ListNBT> {
    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityUpgradeSlot.UPGRADE_SLOT ? LazyOptional.of(()->upgradeSlot).cast() : LazyOptional.empty();
    }

    @Override
    public ListNBT serializeNBT() {
        return ((ListNBT) CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
    }

    @Override
    public void deserializeNBT(ListNBT nbt) {
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt);
    }
}
