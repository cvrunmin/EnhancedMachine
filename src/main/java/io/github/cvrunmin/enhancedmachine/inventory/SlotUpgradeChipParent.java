package io.github.cvrunmin.enhancedmachine.inventory;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradesCollection;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class SlotUpgradeChipParent extends SlotUpgradeChip {

    private boolean branchLock;

    public SlotUpgradeChipParent(IInventory inventoryIn, int index, int xPosition, int yPosition, IUpgradeSlot cap, UpgradesCollection.UpgradeNodeWrapper upgradeSlot) {
        this(inventoryIn, index, xPosition, yPosition, cap, upgradeSlot, false);
    }

    public SlotUpgradeChipParent(IInventory inventoryIn, int index, int xPosition, int yPosition, IUpgradeSlot cap, UpgradesCollection.UpgradeNodeWrapper upgradeSlot, boolean branchLock) {
        super(inventoryIn, index, xPosition, yPosition, cap, upgradeSlot);
        this.branchLock = branchLock;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    public List<ITextComponent> getInstalledChipTooltips() {
        List<ITextComponent> list = super.getInstalledChipTooltips();
        list.add(new TranslationTextComponent(branchLock ? "upgrade.warning.branch_lock" : "upgrade.warning.parent").mergeStyle(TextFormatting.RED));
        return list;
    }
}
