package io.github.cvrunmin.enhancedmachine.upgrade;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.block.Blocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

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
    public List<ITextComponent> computeFunctionTooltips(UpgradeNode node, IUpgradeSlot cap) {
        List<ITextComponent> list = new ArrayList<>();
        list.add(new TranslationTextComponent(getTranslationKey() + ".func", 80 * cap.getEffectMultiplier(node), I18n.format(Blocks.OBSIDIAN.getTranslationKey())));
        return list;
    }
}
