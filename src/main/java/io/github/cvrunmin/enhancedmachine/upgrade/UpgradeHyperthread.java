package io.github.cvrunmin.enhancedmachine.upgrade;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Rarity;

import java.util.ArrayList;
import java.util.List;

public class UpgradeHyperthread extends Upgrade {
    public UpgradeHyperthread() {
        setUpgradeName("hyperthread");
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public Rarity getRarity(int level) {
        return level >= 4 ? Rarity.RARE : Rarity.UNCOMMON;
    }


    @Override
    public List<Block> getSupportedBlocks() {
        return Lists.newArrayList(Blocks.FURNACE, Blocks.BLAST_FURNACE, Blocks.SMOKER, Blocks.DISPENSER, Blocks.DROPPER);
    }

    @Override
    public List<String> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<String> list = new ArrayList<>();
        if(cap.getHolder() instanceof IHyperthreadable){
            list.add(I18n.format(getTranslationKey() + ".func", ((IHyperthreadable) cap.getHolder()).getHyperthreadedSlotsCount()));
        }
        return list;
    }
}
