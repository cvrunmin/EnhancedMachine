package io.github.cvrunmin.enhancedmachine.inventory;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.SplitUpgrade;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.cap.UpgradesCollection;
import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class SlotUpgradeChip extends Slot {

    private IUpgradeSlot cap;
    private UpgradeNode upgradeSlot;

    public SlotUpgradeChip(IInventory inventoryIn, int index, int xPosition, int yPosition, IUpgradeSlot cap, UpgradeNode upgradeSlot) {
        super(inventoryIn, index, xPosition, yPosition);
        this.cap = cap;
        this.upgradeSlot = upgradeSlot;
    }

    public UpgradeNode getUpgradeSlot() {
        return upgradeSlot;
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
        if (upgradeSlot instanceof SplitUpgrade && ((SplitUpgrade) upgradeSlot).getChildren().stream().anyMatch(node -> node != null && !node.getUpgrade().getType().equals(Upgrades.EMPTY))) {
            return false;
        }
        if(isHyperthreadSlotOccupied()) return false;
        return super.canTakeStack(playerIn);
    }

    private boolean isHyperthreadSlotOccupied(){
        if (cap.getHolder() instanceof IHyperthreadable) {
            IHyperthreadable hyperthreadable = (IHyperthreadable) cap.getHolder();
            int count = hyperthreadable.getHyperthreadedSlotsCount();
            if(cap.getHolder() instanceof AbstractFurnaceTileEntity){
                for (int i = 3; i < count * 2 + 1; i++) {
                    if (!((AbstractFurnaceTileEntity) cap.getHolder()).getStackInSlot(i).isEmpty()) {
                        return true;
                    }
                }
            }
            else if(cap.getHolder() instanceof DispenserTileEntity){
                for (int i = 9; i < count; i++) {
                    if (!((DispenserTileEntity) cap.getHolder()).getStackInSlot(i).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<String> getInstalledChipTooltips() {
        List<String> list = new ArrayList<>();
        list.add(I18n.format("upgrade.status.multiplier", cap.getEffectMultiplier(upgradeSlot) * 100));
        if(EMConfig.isBanned(upgradeSlot.getUpgrade(), ServerConfigHelper.getSyncBannedUpgrade())){
//            list.add(TextFormatting.RED + I18n.format("enhancedmachine.config.general.bannedUpgrades.on"));
        }
        else {
            if (upgradeSlot instanceof SplitUpgrade && ((SplitUpgrade) upgradeSlot).getChildren().stream().anyMatch(node -> node != null && !node.getUpgrade().getType().equals(Upgrades.EMPTY))) {
                list.add(TextFormatting.RED + I18n.format("upgrade.warning.containsChildren"));
            } else if (Upgrades.HYPERTHREAD.equals(upgradeSlot.getUpgrade().getType())) {
                if (isHyperthreadSlotOccupied()) {
                    list.add(TextFormatting.RED + I18n.format("upgrade.warning.featureSlotsOccupied"));
                }
            }
            Block block = cap.getHolder().getBlockState().getBlock();
            boolean noFunc = !upgradeSlot.getUpgrade().getType().getSupportedBlocks().isEmpty() && !upgradeSlot.getUpgrade().getType().getSupportedBlocks().contains(block);
            if (noFunc) {
                list.add(TextFormatting.YELLOW + I18n.format("upgrade.warning.no_function", I18n.format(block.getTranslationKey())));
            } else if (!Upgrades.RISER.equals(upgradeSlot.getUpgrade().getType())) {
                UpgradeNode firstFoundUpgrade = cap.getFirstFoundUpgrade(upgradeSlot.getUpgrade().getType());
                if (firstFoundUpgrade != null && !firstFoundUpgrade.equals(upgradeSlot)) {
                    int foundDepth = UpgradesCollection.getDepth(firstFoundUpgrade);
                    int myDepth = UpgradesCollection.getDepth(upgradeSlot);
                    if (foundDepth != myDepth) {
                        list.add(TextFormatting.YELLOW + I18n.format("upgrade.warning.not_active"));
                    } else {
                        list.add(TextFormatting.YELLOW + I18n.format("upgrade.warning.not_active_same_level"));
                    }
                } else {
                    list.addAll(upgradeSlot.getUpgrade().getType().computeFunctionTooltips(upgradeSlot, cap));
                }
            }
        }
        return list;
    }
}
