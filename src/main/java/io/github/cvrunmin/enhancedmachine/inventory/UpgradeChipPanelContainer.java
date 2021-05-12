package io.github.cvrunmin.enhancedmachine.inventory;

import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.Initializer;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.SplitUpgradeNode;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.cap.UpgradesCollection;
import io.github.cvrunmin.enhancedmachine.mixin.inventory.ContainerAccessor;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpgradeChipPanelContainer extends Container {

    private final List<Slot> playerSlots;
    private final BlockPos pos;
    IUpgradeSlot cap;
//    private int focusedSlot = 0;
    private IntReferenceHolder focusedSlot = IntReferenceHolder.single();
    private UpgradeNode focusedNode;
    private UpgradesCollection.UpgradeNodeWrapper focusedNodeWrapper;
    private Inventory tmpInventory;

    Map<Integer, UpgradesCollection.UpgradeNodeWrapper> upgradeNodes;

    public UpgradeChipPanelContainer(int windowId, PlayerInventory playerInventory, IUpgradeSlot cap, BlockPos pos) {
        super(Initializer.chipPanelContainer.get(), windowId);
        this.cap = cap;
        this.pos = pos;
        playerSlots = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                playerSlots.add(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 139 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            playerSlots.add(new Slot(playerInventory, k, 8 + k * 18, 197));
        }
        focusedNode = cap.getUpgrades().getRoot();
        if(focusedNode == null){
            focusedNode = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
            cap.getUpgrades().setRoot(focusedNode);
        }
        focusedSlot.set(0);
        trackInt(focusedSlot);
        packUpgradeSlots();
        refreshSlots();
        addListener(new IContainerListener() {
            @Override
            public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {

            }

            @Override
            public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
                refreshUpgrades(true);
            }

            @Override
            public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {

            }
        });
    }

    public List<Slot> getSlots() {
        return inventorySlots;
    }

    public BlockPos getPos() {
        return pos;
    }

    public IUpgradeSlot getCap() {
        return cap;
    }

    public void replaceSlots(List<Slot> slots) {
        inventorySlots.clear();
        ((ContainerAccessor)this).getInventoryItemStacks().clear();
        for (Slot slot : slots) {
            addSlot(slot);
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }

    public void focusSlot(int slot) {
        focusedSlot.set(slot);
        refreshSlots();
        detectAndSendChanges();
    }

    public Map<Integer, UpgradesCollection.UpgradeNodeWrapper> getUpgradeNodes() {
        return getUpgradeNodes(false);
    }

    private Map<Integer, UpgradesCollection.UpgradeNodeWrapper> getUpgradeNodes(boolean shouldUpdate) {
        if(upgradeNodes == null || shouldUpdate){
            upgradeNodes = cap.getUpgrades().flattenNodes().stream().collect(Collectors.toMap(UpgradesCollection.UpgradeNodeWrapper::getEid, w -> w));
        }
        return upgradeNodes;
    }

    public void refreshSlots() {
        List<Slot> slots = new ArrayList<>(playerSlots);
        Map<Integer, UpgradesCollection.UpgradeNodeWrapper> upgradeNodes = getUpgradeNodes(true);
        if (upgradeNodes.size() == 0) {
            focusedSlot.set(0);
            focusedNode = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
            cap.getUpgrades().setRoot(focusedNode);
        }
        if (focusedSlot.get() >= upgradeNodes.size()) {
            focusedSlot.set(0);
        } else {
            this.focusedNodeWrapper = upgradeNodes.get(focusedSlot.get());
            this.focusedNode = focusedNodeWrapper.getNode();
            int slotsCount = 1;
            int expansionSlots = 0;
            if (Upgrades.RISER.equals(this.focusedNode.getUpgrade().getType())) {
                expansionSlots = Upgrades.RISER.getExpansionSlots(this.focusedNode.getUpgrade().getLevel());
                slotsCount += expansionSlots;
            }
            if (this.focusedNode.getParent() != null) slotsCount++;
            tmpInventory = new Inventory(slotsCount);
            Slot slot;
            if (this.focusedNode != cap.getUpgrades().getRoot()) {
                slot = new SlotUpgradeChipParent(tmpInventory, 0, 80, 60, cap, this.focusedNodeWrapper, true);
            } else {
                slot = new SlotUpgradeChip(tmpInventory, 0, 80, 60, cap, this.focusedNodeWrapper);
            }
            slots.add(slot);
            if (!this.focusedNode.getUpgrade().getType().equals(Upgrades.EMPTY)) {
                slot.putStack(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), this.focusedNode.getUpgrade()));
            }
            if (this.focusedNode instanceof SplitUpgradeNode) {
                for (int i = 0; i < expansionSlots; i++) {
                    UpgradesCollection.UpgradeNodeWrapper nodeWrapper = this.focusedNodeWrapper.getChildrenWrapper().get(i);
                    UpgradeNode node = nodeWrapper.getNode();
                    int xPosition = 80 + (i >= expansionSlots / 2 ? 50 : -50) + (expansionSlots == 16 ? (i < expansionSlots / 4 ? -40 : i >= expansionSlots / 4 * 3 ? 40 : 0) : 0);
                    int yPosition = expansionSlots == 2 ? 60 : expansionSlots == 16 ? (60 + 12 - (8 / 4) * 28 + i % (8 / 2) * 28) : (60 + 12 - (expansionSlots / 4) * 28 + i % (expansionSlots / 2) * 28);
                    Slot slot1 = new SlotUpgradeChip(tmpInventory, i + 1, xPosition, yPosition, cap, nodeWrapper);
                    slots.add(slot1);
                    if (!node.getUpgrade().getType().equals(Upgrades.EMPTY)) {
                        slot1.putStack(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), node.getUpgrade()));
                    }
                }
            }
            if (this.focusedNode.getParent() != null) {
                Slot slot1 = new SlotUpgradeChipParent(tmpInventory, slotsCount - 1, 80, 28, cap, getUpgradeNodes().get(this.focusedNodeWrapper.getPeid()));
                slots.add(slot1);
                if (!this.focusedNode.getParent().getUpgrade().getType().equals(Upgrades.EMPTY)) {
                    slot1.putStack(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), this.focusedNode.getParent().getUpgrade()));
                }
            }

        }
        replaceSlots(slots);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

//        if(focusedSlot.isDirty()) {
//            refreshUpgrades(true);
//        }
    }

    private void packUpgradeSlots() {
        UpgradeNode checkingNode = focusedNode;
        UpgradeDetail oldUpgrade = checkingNode.getUpgrade().clone();
        if (focusedNode instanceof SplitUpgradeNode) return;
        if (Upgrades.RISER.equals(oldUpgrade.getType())) {
            {
                checkingNode = new SplitUpgradeNode(oldUpgrade);
                cap.getUpgrades().setRoot(checkingNode);
            }
        }
    }

    protected void refreshUpgrades(boolean isServerSide) {
        boolean shouldMarkDirty = false;
        if(isServerSide)
        for (int i = 0; i < tmpInventory.getSizeInventory(); i++) {
            UpgradeDetail upgrade = Upgrades.getUpgradeFromItemStack(tmpInventory.getStackInSlot(i)).clone();
            UpgradeNode checkingNode;
            if (i == 0) {
                checkingNode = focusedNode;
            } else if (focusedNode.getParent() != null && i == tmpInventory.getSizeInventory() - 1) {
                continue;
            } else if (focusedNode instanceof SplitUpgradeNode) {
                checkingNode = ((SplitUpgradeNode) focusedNode).getChildren().get(i - 1);
            } else continue;
            UpgradeDetail oldUpgrade = checkingNode.getUpgrade().clone();
            if (!oldUpgrade.equals(upgrade)) {
                if (Upgrades.RISER.equals(upgrade.getType())) {
                    if (checkingNode.getParent() != null) {
                        // constraint: if change happens to a node with parent, this must be leaf node
                        if (checkingNode.getParent() instanceof SplitUpgradeNode) {
                            SplitUpgradeNode parent = (SplitUpgradeNode) checkingNode.getParent();
                            int index = parent.getChildren().indexOf(checkingNode);
                            index = i - 1;
                            checkingNode = new SplitUpgradeNode(upgrade);
                            parent.getChildren().set(index, checkingNode);
                        }
                    } else {
                        checkingNode = new SplitUpgradeNode(upgrade);
                        cap.getUpgrades().setRoot(checkingNode);
                    }
                } else {
                    if (checkingNode instanceof SplitUpgradeNode) {
                        if (checkingNode.getParent() != null) {
                            if (checkingNode.getParent() instanceof SplitUpgradeNode) {
                                SplitUpgradeNode parent = (SplitUpgradeNode) checkingNode.getParent();
                                int index = parent.getChildren().indexOf(checkingNode);
                                index = i - 1;
                                checkingNode = new UpgradeNode(upgrade);
                                parent.getChildren().set(index, checkingNode);
                            }
                        } else {
                            checkingNode = new UpgradeNode(upgrade);
                            cap.getUpgrades().setRoot(checkingNode);
                        }
                    } else {
                        checkingNode.setUpgrade(upgrade);
                    }
                }
                shouldMarkDirty = true;
            }
        }
        if (shouldMarkDirty) {
            if (isServerSide) {
                cap.markDirty();
                List<IContainerListener> listeners = ObfuscationReflectionHelper.getPrivateValue(Container.class, this, "field_75149_d");
                for (IContainerListener listener : listeners) {
                    listener.sendWindowProperty(this, -1, 0);
                }
            }
            refreshSlots();
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        if(id != -1) { //id -1 is used for force update in client in this container type ONLY
            super.updateProgressBar(id, data);
        }
        if (id == 0) {
            focusedSlot.set(data);
            refreshSlots();
        }
        refreshUpgrades(false);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 27) {
                if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index < 36 && !this.mergeItemStack(itemstack1, 0, 27, false)) {
                return ItemStack.EMPTY;
            } else {
                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }
}
