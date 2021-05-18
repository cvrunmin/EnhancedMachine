package io.github.cvrunmin.enhancedmachine.upgrade;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

public class UpgradeDrill extends Upgrade {
    public UpgradeDrill() {
        setUpgradeName("drill");
    }

    @Override
    public int getMaxLevel() {
        return 10;
    }

    public int getDrillingDistance(int level) {
        return level == 1 ? 1 : level * 2;
    }

    @Override
    public List<Block> getSupportedBlocks() {
        return Lists.newArrayList(Blocks.DISPENSER);
    }

    @Override
    public List<ITextComponent> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<ITextComponent> list = new ArrayList<>();
        list.add(new TranslationTextComponent(getTranslationKey() + ".func.1", I18n.format(Blocks.DISPENSER.getTranslationKey())));
        if (node.getUpgrade().getLevel() > 1) {
            int i = (int) (getDrillingDistance(node.getUpgrade().getLevel()) * cap.getEffectMultiplier(node));
            if (i > 1) {
                list.add(new TranslationTextComponent(getTranslationKey() + ".func.2", i));
            }
        }
        return list;
    }
}
