package io.github.cvrunmin.enhancedmachine.upgrade;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Upgrade {

    private static final List<Block> GENERAL_BLOCK_SUPPORTED_LIST = Collections.unmodifiableList(Arrays.asList(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.DISPENSER, Blocks.DROPPER, Blocks.BREWING_STAND, Blocks.CHEST, Blocks.TRAPPED_CHEST));
    private String name = null;

    private int chipColor;

    public int getChipColor() {
        return chipColor;
    }

    public Upgrade setChipColor(int chipColor) {
        this.chipColor = chipColor;
        return this;
    }

    public String getUpgradeName() {
        return name;
    }

    public Upgrade setUpgradeName(String name) {
        if (this.name == null) {
            this.name = name;
        }
        return this;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean shouldShowEffect() {
        return false;
    }

    public Rarity getRarity(int level) {
        return Rarity.COMMON;
    }

    public CompoundNBT getDefaultExtraData(CompoundNBT compound, int level) {
        return compound;
    }

    public List<Block> getSupportedBlocks() {
        return GENERAL_BLOCK_SUPPORTED_LIST;
    }

    public List<String> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        return Collections.emptyList();
    }

    public String getTranslationKey() {
        return "upgrade.type." + (name == null ? "null" : name);
    }
}
