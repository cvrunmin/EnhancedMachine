package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.tileentity.TileEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UpgradeSlot implements IUpgradeSlot {
    private UpgradesCollection upgrades = new UpgradesCollection();
    private TileEntity holder;
    private boolean dirty = false;

    @Override
    public UpgradesCollection getUpgrades() {
        return upgrades;
    }

    @Override
    public void setUpgrades(UpgradesCollection list) {
        if (list == null) return;
        this.upgrades = list;
    }

    @Override
    public double getEffectMultiplier(Upgrade detail) {
        if (!hasUpgradeInstalled(detail)) return 1;
        int depth = 0;
        List<UpgradeNode> nodes;
        while ((nodes = upgrades.getNodes(depth)).size() > 0) {
            for (UpgradeNode node : nodes) {
                if (node.getUpgrade().getType().equals(detail)) {
                    return getEffectMultiplier(node);
                }
            }
            depth++;
        }
        return 0.0;
    }

    @Override
    public double getEffectMultiplier(UpgradeNode node) {
        if (node.getParent() == null) return 1.0;
        else if (holder != null && !holder.getWorld().getGameRules().getBoolean(EnhancedMachine.DO_LIMITED_CHIP_MULTIPLIER)) {
            if (Upgrades.RISER.equals(node.getParent().getUpgrade().getType())
                    && EMConfig.isBanned(node.getParent().getUpgrade(), holder != null && holder.getWorld().isRemote ? ServerConfigHelper.getSyncBannedUpgrade() : EMConfig.getBannedUpgradesParsed()))
                return 0;
            return 1.0;
        } else {
            if (!Upgrades.RISER.equals(node.getParent().getUpgrade().getType())) return 0.0;
            if(EMConfig.isBanned(node.getParent().getUpgrade(), holder != null && holder.getWorld().isRemote ? ServerConfigHelper.getSyncBannedUpgrade() : EMConfig.getBannedUpgradesParsed()))
                return 0;
            int childSlot = node.getUpgrade().getExtras().getInt("SlotOnParent");
            int expansionSlots = Upgrades.RISER.getExpansionSlots(node.getParent().getUpgrade().getLevel());
            if (childSlot < 0 || childSlot >= expansionSlots) return 0;
            int[] weights = node.getParent().getUpgrade().getExtras().getIntArray("Weights");
            if (weights.length == 0) {
                weights = new int[expansionSlots];
                Arrays.fill(weights, 1);
            }
            if (weights.length != expansionSlots) return 1.0;
            return getEffectMultiplier(node.getParent()) * weights[childSlot] / Arrays.stream(weights).sum();
        }
    }

    @Override
    public UpgradeNode getFirstFoundUpgrade(Upgrade type) {
        if (!hasUpgradeInstalled(type)) return null;
        int depth = 0;
        List<UpgradeNode> nodes;
        while ((nodes = upgrades.getNodes(depth)).size() > 0) {
            nodes.sort(Collections.reverseOrder(Comparator.comparingInt(node -> node.getUpgrade().getLevel())));
            for (UpgradeNode node : nodes) {
                if (node.getUpgrade().getType().equals(type)) {
                    return node;
                }
            }
            depth++;
        }
        return null;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public void resetDirtyState() {
        dirty = false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public TileEntity getHolder() {
        return holder;
    }

    @Override
    public UpgradeSlot setHolder(TileEntity holder) {
        this.holder = holder;
        return this;
    }
}
