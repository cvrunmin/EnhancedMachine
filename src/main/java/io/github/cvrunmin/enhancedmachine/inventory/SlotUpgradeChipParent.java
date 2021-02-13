package io.github.cvrunmin.enhancedmachine.inventory;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

public class SlotUpgradeChipParent extends SlotUpgradeChip {

    private boolean branchLock;

    public SlotUpgradeChipParent(IInventory inventoryIn, int index, int xPosition, int yPosition, IUpgradeSlot cap, UpgradeNode upgradeSlot) {
        this(inventoryIn, index, xPosition, yPosition, cap, upgradeSlot, false);
    }

    public SlotUpgradeChipParent(IInventory inventoryIn, int index, int xPosition, int yPosition, IUpgradeSlot cap, UpgradeNode upgradeSlot, boolean branchLock) {
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
    public List<String> getInstalledChipTooltips() {
        List<String> list = super.getInstalledChipTooltips();
        list.add(TextFormatting.RED + I18n.format(branchLock ? "upgrade.warning.branch_lock" : "upgrade.warning.parent"));
        return list;
    }
}
