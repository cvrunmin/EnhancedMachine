package io.github.cvrunmin.enhancedmachine.cap;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class CapabilityUpgradeSlot {
    @CapabilityInject(IUpgradeSlot.class)
    public static Capability<IUpgradeSlot> UPGRADE_SLOT = null;
}
