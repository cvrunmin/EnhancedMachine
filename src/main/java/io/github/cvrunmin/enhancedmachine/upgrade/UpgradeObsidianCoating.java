package io.github.cvrunmin.enhancedmachine.upgrade;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Rarity;

import java.util.ArrayList;
import java.util.List;

public class UpgradeObsidianCoating extends Upgrade {
    public UpgradeObsidianCoating() {
        this.setUpgradeName("obsidian_coating");
    }

    @Override
    public Rarity getRarity(int level) {
        return Rarity.UNCOMMON;
    }

    @Override
    public List<String> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<String> list = new ArrayList<>();
        list.add(I18n.format(getTranslationKey() + ".func", 80 * cap.getEffectMultiplier(node), I18n.format(Blocks.OBSIDIAN.getTranslationKey())));
        return list;
    }
}
