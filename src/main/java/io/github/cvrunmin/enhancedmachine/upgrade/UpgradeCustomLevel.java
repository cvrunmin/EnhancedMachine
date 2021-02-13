package io.github.cvrunmin.enhancedmachine.upgrade;

import net.minecraft.item.Rarity;

import java.util.function.Function;

public class UpgradeCustomLevel extends Upgrade {
    private int minLevel = 1;
    private int maxLevel = 1;

    private Function<Integer, Rarity> rarityGetter = super::getRarity;

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    public UpgradeCustomLevel setMinLevel(int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    public UpgradeCustomLevel setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
        return this;
    }

    public UpgradeCustomLevel setRarity(Function<Integer, Rarity> rarityGetter) {
        this.rarityGetter = rarityGetter;
        return this;
    }

    @Override
    public Rarity getRarity(int level) {
        return rarityGetter.apply(level);
    }
}
