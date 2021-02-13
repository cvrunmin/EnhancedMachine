package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import io.github.cvrunmin.enhancedmachine.Initializer;
import io.github.cvrunmin.enhancedmachine.tileentity.IHyperthreadable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.DispenserContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(DispenserContainer.class)
public abstract class DispenserContainerMixin extends Container {

    @Shadow
    @Final
    @Mutable
    private IInventory dispenserInventory;

    protected DispenserContainerMixin(@Nullable ContainerType<?> type, int id) {
        super(type, id);
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/IInventory;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/Container;<init>(Lnet/minecraft/inventory/container/ContainerType;I)V", remap = false), index = 0)
    private static ContainerType<?> modifyContainerType(ContainerType<?> old){
        return Initializer.MODDED_DISPENSER.get();
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V", at = @At("RETURN"))
    private void addSlotsInConstructorStage(int windowId, PlayerInventory playerInventory, CallbackInfo ci){
        if(!(dispenserInventory instanceof DispenserTileEntity) && dispenserInventory.getSizeInventory() == 9)
            dispenserInventory = new Inventory(27);

        this.inventorySlots.clear();
        ((ContainerAccessor)this).getInventoryItemStacks().clear();

        int count = 9;
        if(dispenserInventory instanceof IHyperthreadable) count = ((IHyperthreadable) dispenserInventory).getHyperthreadedSlotsCount();

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(dispenserInventory, j + i * 3, 62 + j * 18, 17 + i * 18));
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }

        if (count > 9) {
            for (int i = 9; i < count; i++) {
                int posX = ((i - 9) / 3) % 2 == 0 ? 62 - (((i - 9) / 3) / 2 + 1) * 18 : 98 + (((i - 9) / 3) / 2 + 1) * 18;
                int posY = 17 + (i % 3) * 18;
                this.addSlot(new Slot(dispenserInventory, i, posX, posY));
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
            if (index < 9 || index >= 45) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, 9, false)) {
                return ItemStack.EMPTY;
            } else if(!this.mergeItemStack(itemstack1, 45, 45 + (dispenserInventory instanceof IHyperthreadable ? ((IHyperthreadable) dispenserInventory).getHyperthreadedSlotsCount() - 9 : 0), false)){
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
