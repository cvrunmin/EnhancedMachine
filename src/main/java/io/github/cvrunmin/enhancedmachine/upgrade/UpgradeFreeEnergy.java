package io.github.cvrunmin.enhancedmachine.upgrade;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Rarity;

import java.util.ArrayList;
import java.util.List;

public class UpgradeFreeEnergy extends Upgrade {
    public UpgradeFreeEnergy() {
        setUpgradeName("free_energy");
    }

    @Override
    public Rarity getRarity(int level) {
        return Rarity.EPIC;
    }

    @Override
    public boolean shouldShowEffect() {
        return true;
    }

    @Override
    public List<Block> getSupportedBlocks() {
        return Lists.newArrayList(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.BREWING_STAND);
    }

    @Override
    public List<String> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<String> list = new ArrayList<>();
        list.add(I18n.format(getTranslationKey() + ".func"));
        return list;
    }
}
