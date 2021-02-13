package io.github.cvrunmin.enhancedmachine.upgrade;

import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntArrayNBT;

import java.util.Arrays;

public class UpgradeRiser extends Upgrade {

    public UpgradeRiser() {
        this.setUpgradeName("riser");
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    public int getExpansionSlots(int level) {
        return Math.max(0, Math.min(1 << level, 16));
    }

    @Override
    public Rarity getRarity(int level) {
        return level < getMaxLevel() ? Rarity.UNCOMMON : Rarity.RARE;
    }

    @Override
    public CompoundNBT getDefaultExtraData(CompoundNBT compound, int level) {
        CompoundNBT compound1 = super.getDefaultExtraData(compound, level);
        int[] weights = new int[getExpansionSlots(level)];
        Arrays.fill(weights, 1);
        compound1.put("Weights", new IntArrayNBT(weights));
        return compound1;
    }
}
