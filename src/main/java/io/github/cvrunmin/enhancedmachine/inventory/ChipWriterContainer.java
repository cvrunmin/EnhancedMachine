package io.github.cvrunmin.enhancedmachine.inventory;

import io.github.cvrunmin.enhancedmachine.EMBlocks;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.Initializer;
import io.github.cvrunmin.enhancedmachine.network.ChipwriterAttributeMessage;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeRecipes;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.*;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class ChipWriterContainer extends Container {

    private final IWorldPosCallable worldPosCallable;
    private final Slot sacrificeSlot;
    private PlayerInventory inventoryPlayer;
    private Inventory inputInventory;
    private CraftResultInventory outputInventory;
    private Slot inputSlot;
    private ItemStack inputStack;
    private final World world;
    private Slot outputSlot;
    private ItemStack sacrificeStack;

    private Runnable inputInvChangeListener;
    private final IntReferenceHolder selectedUpgrade = IntReferenceHolder.single();
    private List<Tuple<UpgradeDetail, Integer>> availableRecipes;

    public ChipWriterContainer(int windowId, PlayerInventory playerInventory){
        this(windowId, playerInventory, IWorldPosCallable.DUMMY);
    }

    public ChipWriterContainer(int windowId, PlayerInventory inventoryPlayer, IWorldPosCallable worldPosCallable) {
        super(Initializer.chipWriterContainer.get(),windowId);
        this.inventoryPlayer = inventoryPlayer;
        this.world = inventoryPlayer.player.world;

        this.worldPosCallable = worldPosCallable;
        this.inputInvChangeListener = () -> {
        };
        this.availableRecipes = new ArrayList<>();
        inputInventory = new Inventory(2) {
            @Override
            public void markDirty() {
                super.markDirty();
                ChipWriterContainer.this.onCraftMatrixChanged(this);
                inputInvChangeListener.run();
            }
        };
        outputInventory = new CraftResultInventory();

        inputStack = ItemStack.EMPTY;
        sacrificeStack = ItemStack.EMPTY;

        int k;
        for (k = 0; k < 3; ++k) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new SlotAllowDisable(inventoryPlayer, j + k * 9 + 9, 8 + j * 18, 84 + 6 + k * 18));
            }
        }

        for (k = 0; k < 9; ++k) {
            this.addSlot(new SlotAllowDisable(inventoryPlayer, k, 8 + k * 18, 142 + 6));
        }

        inputSlot = this.addSlot(new Slot(this.inputInventory, 0, 12, 15) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return EMItems.UPGRADE_CHIP.get().equals(stack.getItem());
            }
        });
        sacrificeSlot = this.addSlot(new SlotAllowDisable(this.inputInventory, 1, 48, 15));
        outputSlot = this.addSlot(new SlotAllowDisable(this.outputInventory, 0, 26 + 4, 49 + 6) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public ItemStack onTake(PlayerEntity player, ItemStack stack) {
                if (selectedUpgrade.get() != -1) {
                    if (!player.isCreative()) {
                        int level = availableRecipes.get(selectedUpgrade.get()).getB();
                        if (level > 0) {
                            player.addExperienceLevel(-level);
                        }
                    }
                }
                ItemStack itemStack = inputSlot.decrStackSize(1);
                sacrificeSlot.decrStackSize(1);
                if (!itemStack.isEmpty()) {
                    populateResult();
                }

                stack.getItem().onCreated(stack, player.world, player);
                worldPosCallable.consume(((world1, blockPos) -> {
                    world1.playSound(null, blockPos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 1.0F, world1.rand.nextFloat() * 0.1F + 0.9F);
                }));
                return super.onTake(player, stack);
            }
        });
        selectedUpgrade.set(-1);
        trackInt(selectedUpgrade);
    }

    @Override
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return slotIn.inventory != outputInventory && super.canMergeSlot(stack, slotIn);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemStack2 = slot.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (index == 38) {
                item.onCreated(itemStack2, playerIn.world, playerIn);
                if (!this.mergeItemStack(itemStack2, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemStack2, itemStack);
            } else if (index == 36 || index == 37) {
                if (!this.mergeItemStack(itemStack2, 0, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.getItem().equals(EMItems.UPGRADE_CHIP)) {
                if (!this.mergeItemStack(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 0 && index < 27) {
                if (!this.mergeItemStack(itemStack2, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= 27 && index < 36 && !this.mergeItemStack(itemStack2, 0, 27, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }

            slot.inventory.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemStack2);
            this.detectAndSendChanges();
        }

        return itemStack;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPosCallable, playerIn, EMBlocks.CHIPWRITER.get());
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
//        super.onCraftMatrixChanged(inventoryIn);
        ItemStack stack = inputSlot.getStack();
        ItemStack stack1 = sacrificeSlot.getStack();
        boolean shouldUpdateInput = false;
        if (!inputStack.isItemEqual(stack)) {
            this.inputStack = stack.copy();
            shouldUpdateInput = true;
        }
        if (!sacrificeStack.isItemEqual(stack1)) {
            this.sacrificeStack = stack1.copy();
            shouldUpdateInput = true;
        }
        if (shouldUpdateInput) {
            updateInput(inputInventory, inputStack, sacrificeStack);
        }
    }

    private void updateInput(IInventory inventory, ItemStack itemStack, ItemStack itemStack1) {
        this.availableRecipes.clear();
        this.selectedUpgrade.set(-1);
        this.outputSlot.putStack(ItemStack.EMPTY);
        if (!itemStack.isEmpty()) {
            this.availableRecipes = UpgradeRecipes.getInstance().getAvailableRecipes(itemStack, itemStack1);
        }
    }

    private void populateResult() {
        if (!availableRecipes.isEmpty()) {
            UpgradeDetail output = availableRecipes.get(selectedUpgrade.get()).getA();
            this.outputSlot.putStack(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), output));
        } else {
            this.outputSlot.putStack(ItemStack.EMPTY);
        }

        this.detectAndSendChanges();
    }

    @Override
    public boolean enchantItem(PlayerEntity playerIn, int id) {
        if (id >= 0 && id < this.availableRecipes.size()) {
            if (playerIn.experienceLevel >= this.getAvailableRecipes().get(id).getB() || playerIn.isCreative()) {
                this.selectedUpgrade.set(id);
                this.populateResult();
            }
            return true;
        }

        return false;
    }

    public void previewRiserWeightWrittenOutput(UpgradeDetail upgradeDetail) {
        this.outputSlot.putStack(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), upgradeDetail));
    }

    public void populateRiserWeightWrittenOutput(int[] weight) {
        UpgradeDetail detail = Upgrades.getUpgradeFromItemStack(this.inputStack);
        if (Upgrades.RISER.equals(detail.getType())) {
            if (Upgrades.RISER.getExpansionSlots(detail.getLevel()) == weight.length) {
                detail.getExtras().putIntArray("Weights", weight);
                populateRiserWeightWrittenOutput(detail);
            }
        }
    }

    public void populateRiserWeightWrittenOutput(UpgradeDetail upgradeDetail) {
        previewRiserWeightWrittenOutput(upgradeDetail);
        this.detectAndSendChanges();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }

    public void setSelectedUpgrade(int selectedUpgrade) {
        if (selectedUpgrade >= 0 && selectedUpgrade < this.availableRecipes.size()) {
            this.selectedUpgrade.set(selectedUpgrade);
            this.populateResult();
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        super.updateProgressBar(id, data);
        if (id == 0) {
            selectedUpgrade.set(data);
        }
    }

    public int getSelectedUpgrade() {
        return selectedUpgrade.get();
    }

    public List<Tuple<UpgradeDetail, Integer>> getAvailableRecipes() {
        return availableRecipes;
    }

    public int getAvailableRecipesCount() {
        return availableRecipes.size();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean canCraft() {
        return this.inputSlot.getHasStack() && !this.availableRecipes.isEmpty();
    }

    @OnlyIn(Dist.CLIENT)
    public void setInputInvChangeListener(Runnable inputInvChangeListener) {
        this.inputInvChangeListener = inputInvChangeListener;
    }

    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);
        outputInventory.clear();

        if (!this.world.isRemote) {
            this.clearContainer(playerIn, this.world, this.inputInventory);
        }
    }
}
