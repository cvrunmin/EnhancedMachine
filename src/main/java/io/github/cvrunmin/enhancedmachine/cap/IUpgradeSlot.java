package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.tileentity.TileEntity;

public interface IUpgradeSlot {

    UpgradesCollection getUpgrades();

    void setUpgrades(UpgradesCollection list);

    default boolean hasUpgradeInstalled(UpgradeDetail detail) {
        return getUpgrades().hasUpgrade(detail);
    }

    default boolean hasUpgradeInstalled(Upgrade upgrade) {
        return getUpgrades().hasUpgrade(upgrade);
    }

    default boolean hasUpgradeInstalled(String name) {
        return getUpgrades().hasUpgrade(Upgrades.getUpgradeFromId(name));
    }

    double getEffectMultiplier(Upgrade detail);

    double getEffectMultiplier(UpgradeNode node);

    UpgradeNode getFirstFoundUpgrade(Upgrade type);

    void markDirty();

    void resetDirtyState();

    boolean isDirty();

    TileEntity getHolder();

    UpgradeSlot setHolder(TileEntity holder);
}
