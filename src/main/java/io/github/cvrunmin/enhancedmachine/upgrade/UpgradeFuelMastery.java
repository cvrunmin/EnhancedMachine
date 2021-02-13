package io.github.cvrunmin.enhancedmachine.upgrade;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Items;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.BrewingStandTileEntity;

import java.util.ArrayList;
import java.util.List;

public class UpgradeFuelMastery extends Upgrade {
    public UpgradeFuelMastery() {
        setUpgradeName("fuel_mastery");
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    public float getTimeBoost(int level) {
        return level == 1 ? 1.5f : level;
    }

    @Override
    public Rarity getRarity(int level) {
        return level < getMaxLevel() - 2 ? Rarity.UNCOMMON : Rarity.RARE;
    }

    @Override
    public List<Block> getSupportedBlocks() {
        return Lists.newArrayList(Blocks.FURNACE, Blocks.BREWING_STAND);
    }

    @Override
    public List<String> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<String> list = new ArrayList<>();
        if (cap.getHolder() instanceof BrewingStandTileEntity) {
            int i = (int) (20 * getTimeBoost(node.getUpgrade().getLevel()) * cap.getEffectMultiplier(node));
            list.add(I18n.format(getTranslationKey() + ".func.brewing_stand", I18n.format(Items.BLAZE_POWDER.getTranslationKey() + ".name"), i));
        } else {
            list.add(I18n.format(getTranslationKey() + ".func", 100 * getTimeBoost(node.getUpgrade().getLevel()) * cap.getEffectMultiplier(node)));
        }
        return list;
    }
}
