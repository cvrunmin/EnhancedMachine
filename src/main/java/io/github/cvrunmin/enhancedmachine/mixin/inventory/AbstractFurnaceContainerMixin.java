package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(AbstractFurnaceContainer.class)
public abstract class AbstractFurnaceContainerMixin extends Container {

    @Shadow
    protected abstract boolean hasRecipe(ItemStack stack);

    @Shadow
    protected abstract boolean isFuel(ItemStack stack);

    @Mutable
    @Shadow @Final private IInventory furnaceInventory;

    protected AbstractFurnaceContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

//    @Redirect(method = "<init>(Lnet/minecraft/inventory/container/ContainerType;Lnet/minecraft/item/crafting/IRecipeType;ILnet/minecraft/entity/player/PlayerInventory;)V",
//    at = @At(value = "NEW", target = "(I)Lnet/minecraft/inventory/Inventory;"))
//    public Inventory getClientSideInventory(int numSlot){
//        return new Inventory(1 + 2 * 9);
//    }

    @Inject(method = "<init>(Lnet/minecraft/inventory/container/ContainerType;Lnet/minecraft/item/crafting/IRecipeType;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/util/IIntArray;)V",
    at = @At("RETURN"))
    public void overrideConstructorSlots(ContainerType<?> containerTypeIn, IRecipeType<? extends AbstractCookingRecipe> recipeTypeIn, int id, PlayerInventory playerInventoryIn, IInventory furnaceInventoryIn, IIntArray furnaceDataIn,  CallbackInfo info){
        if(!(furnaceInventoryIn instanceof AbstractFurnaceTileEntity) && furnaceInventory.getSizeInventory() == 3)
            furnaceInventoryIn = new Inventory(1 + 2 * 9);
        this.furnaceInventory = furnaceInventoryIn;
        this.inventorySlots.clear();
        ((ContainerAccessor)this).getInventoryItemStacks().clear();

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventoryIn, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventoryIn, k, 8 + k * 18, 142));
        }

        int count = 1;

        if(!(furnaceInventoryIn instanceof AbstractFurnaceTileEntity) || (count = ((IHyperthreadable) furnaceInventoryIn).getHyperthreadedSlotsCount()) == 1){
            this.addSlot(new FurnaceFuelSlot((AbstractFurnaceContainer) (Object)this, furnaceInventoryIn, 1, 56, 53));
            this.addSlot(new Slot(furnaceInventoryIn, 0, 56, 17));
            this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, furnaceInventoryIn, 2, 116, 35));
        }
        else {
//            this.addSlot(new FurnaceFuelSlot((AbstractFurnaceContainer) (Object) this, furnaceInventoryIn, 1, 79, 62));
            this.addSlot(new FurnaceFuelSlot((AbstractFurnaceContainer) (Object) this, furnaceInventoryIn, 1, 71, 53));

            loop:
            for (int i = 0; i < count; i++) {
                int inputX;
                int outputX;
                int inputY;
                int outputY;
                switch (count) {
                    case 2:
                        inputX = 26 + i % 2 * 18;
                        inputY = 35;
                        outputX = 111 + i % 2 * 26;
                        outputY = 35;
                        break;
                    case 3:
                        inputX = i == 2 ? 35 : 26 + i % 2 * 18;
                        inputY = 26 + i / 2 * 18;
                        outputX = i == 2 ? 124 : 111 + i % 2 * 26;
                        outputY = 22 + i / 2 * 26;
                        break;
                    case 4:
                        inputX = 26 + i % 2 * 18;
                        inputY = 26 + i / 2 * 18;
                        outputX = 111 + i % 2 * 26;
                        outputY = 22 + i / 2 * 26;
                        break;
                    case 5:
                        inputX = i < 3 ? 17 + (i) % 3 * 18 : 26 + (i - 3) % 2 * 18;
                        inputY = 26 + i / 3 * 18;
                        outputX = i < 3 ? 106 + (i) % 3 * 18 : 115 + (i - 3) % 2 * 18;
                        outputY = 26 + i / 3 * 18;
                        break;
                    case 6:
                        inputX = 26 + i % 2 * 18;
                        inputY = 17 + (i / 2) * 18;
                        outputX = 115 + i % 2 * 18;
                        outputY = 17 + (i / 2) * 18;
                        break;
                    case 7:
                        inputX = i >= 2 && i <= 4 ? 17 + (i - 2) % 3 * 18 : 26 + (i < 2 ? i : i - 5) % 2 * 18;
                        outputX = i >= 2 && i <= 4 ? 106 + (i - 2) % 3 * 18 : 115 + (i < 2 ? i : i - 5) % 2 * 18;
                        inputY = i < 2 ? 17 : i < 5 ? 35 : 53;
                        outputY = i < 2 ? 17 : i < 5 ? 35 : 53;
                        break;
                    case 8:
                        inputX = i != 3 && i != 4 ? 17 + (i < 3 ? i : i - 5) % 3 * 18 : 26 + (i - 3) % 2 * 18;
                        outputX = i != 3 && i != 4 ? 106 + (i < 3 ? i : i - 5) % 3 * 18 : 115 + (i - 3) % 2 * 18;
                        inputY = i < 3 ? 17 : i < 5 ? 35 : 53;
                        outputY = i < 3 ? 17 : i < 5 ? 35 : 53;
                        break;
                    case 9:
                        inputX = 17 + (i % 3) * 18;
                        outputX = 106 + (i % 3) * 18;
                        inputY = 17 + (i / 3) * 18;
                        outputY = 17 + (i / 3) * 18;
                        break;
                    default:
                        // bug: exceeds max allowed size of furnace
                        break loop;
                }
                this.addSlot(new Slot(furnaceInventoryIn, i == 0 ? 0 : 1 + 2 * i, inputX, inputY));
                this.addSlot(new FurnaceResultSlot(playerInventoryIn.player, furnaceInventoryIn, 2 + 2 * i, outputX, outputY));
            }
        }
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index > 36 && index % 2 == 0) {
                if (!this.mergeItemStack(itemstack1, 0, 36, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(itemstack1, itemstack);
            } else if (index < 36) {
                if (this.hasRecipe(itemstack1)) {
                    int count = furnaceInventory instanceof AbstractFurnaceTileEntity ? ((IHyperthreadable)furnaceInventory).getHyperthreadedSlotsCount() : 1;
                    boolean hasMerged = false;
                    for (int i = 0; i < count; i++) {
                        if (this.mergeItemStack(itemstack1, 36 + 1 + 2 * i, 36 + 2 * (i + 1), false)) {
                            hasMerged = true;
                            break;
                        }
                    }
                    if (!hasMerged) {
                        return ItemStack.EMPTY;
                    }
                } else if (isFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 36, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 27) {
                    if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
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
