package io.github.cvrunmin.enhancedmachine.mixin;

import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.*;
import net.minecraft.item.crafting.ServerRecipePlacer;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;


public abstract class FurnaceRecipeMixins {

    @Mixin(ServerRecipePlacer.class)
    public static abstract class ServerRecipePlacerMixin{
        @Shadow protected RecipeBookContainer recipeBookContainer;

        @ModifyArg(method = {"getMaxAmount", "tryPlaceRecipe"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/RecipeBookContainer;getSlot(I)Lnet/minecraft/inventory/container/Slot;"))
        private int modifyGetSlot(int slot){
            if(recipeBookContainer instanceof AbstractFurnaceContainer){
                if(slot == 0) return 37;
                if(slot == 1) return 36;
                return slot + 36;
            }
            return slot;
        }
    }

    @Mixin(ServerRecipePlacerFurnace.class)
    public static abstract class FurnaceRecipePlacerMixin<C extends IInventory> extends ServerRecipePlacer<C>{

        public FurnaceRecipePlacerMixin(RecipeBookContainer<C> p_i50752_1_) {
            super(p_i50752_1_);
        }

        @ModifyArg(method = {"tryPlaceRecipe"}, at = @At(value = "INVOKE", target = "net/minecraft/item/crafting/ServerRecipePlacerFurnace.giveToPlayer(I)V"), index = 0)
        private int modifyPlayerGivingSlot(int slot){
            if(recipeBookContainer instanceof AbstractFurnaceContainer){
                if (slot == 0) {
                    return 37;
                }
                if (slot == 2) {
                    return 38;
                }
            }
            return slot;
        }

        @ModifyArg(method = {"func_201516_a", "tryPlaceRecipe"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/RecipeBookContainer;getSlot(I)Lnet/minecraft/inventory/container/Slot;"))
        private int modifyGetSlot(int slot){
            if(recipeBookContainer instanceof AbstractFurnaceContainer){
                if(slot == 0) return 37;
                if(slot == 1) return 36;
                return slot + 36;
            }
            return slot;
        }

        @Overwrite
        protected void clear() {
            for (int i = 0; i < this.recipeBookContainer.inventorySlots.size() - 36; i++) {
                this.giveToPlayer(36 + i);
            }

            this.recipeBookContainer.clear();
        }
    }

    @Mixin(AbstractRecipeBookGui.class)
    public static abstract class AbstractRecipeBookGuiMixin extends RecipeBookGui {

        @ModifyArg(method = "setupGhostRecipe", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"))
        private int offsetSlotGetIndex(int old){
            if(old == 0){
                return 37;
            }
            else if(old == 1){
                return 36;
            }
            return 36 + old;
        }

        @Shadow
        private Slot field_212966_k;

        @Overwrite
        public void slotClicked(Slot slotIn){
            if (slotIn != null) {
                int slotNumber;
                if(slotIn instanceof FurnaceFuelSlot){
                    slotNumber = 1;
                }
                else if(slotIn instanceof FurnaceResultSlot){
                    slotNumber = 2;
                }
                else if(slotIn.slotNumber < 36){
                    slotNumber = slotIn.slotNumber + 36;
                }
                else slotNumber = 0;
                if (slotNumber < this.field_201522_g.getSize()) {
                    this.ghostRecipe.clear();
                    this.field_212966_k = null;
                    if (this.isVisible()) {
                        ((RecipeBookGuiInvoker)this).invokeUpdateStackedContents();
                    }
                }
            }
        }

//        @Redirect(method = "slotClicked", at = @At(value = "FIELD", target = "Lnet/minecraft/inventory/container/Slot;slotNumber:I", opcode = 180))
//        private int redirectSlotNumber(Slot slot){
//            if(slot instanceof FurnaceFuelSlot){
//                return 1;
//            }
//            else if(slot instanceof FurnaceResultSlot){
//                return 2;
//            }
//            else if(slot.slotNumber < 36){
//                return slot.slotNumber + 36;
//            }
//            else return 0;
//        }
    }

    @Mixin(RecipeBookGui.class)
    public interface RecipeBookGuiInvoker{
        @Invoker
        void invokeUpdateStackedContents();
    }
}
