package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import io.github.cvrunmin.enhancedmachine.Initializer;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.SmokerContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({FurnaceContainer.class, BlastFurnaceContainer.class, SmokerContainer.class})
public class FurnaceContainersMixin {
    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/AbstractFurnaceContainer;<init>(Lnet/minecraft/inventory/container/ContainerType;Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/item/crafting/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;)V", remap = false), index = 0)
    private static ContainerType<?> modifyContainerType(ContainerType<?> old){
        if(old == ContainerType.BLAST_FURNACE){
            return Initializer.MODDED_BLAST_FURNACE.get();
        }
        else if(old == ContainerType.SMOKER){
            return Initializer.MODDED_SMOKER.get();
        }
        return Initializer.MODDED_FURNACE.get();
    }

    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/util/IIntArray;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/AbstractFurnaceContainer;<init>(Lnet/minecraft/inventory/container/ContainerType;Lnet/minecraft/item/crafting/IRecipeType;Lnet/minecraft/item/crafting/RecipeBookCategory;ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/util/IIntArray;)V", remap = false), index = 0)
    private static ContainerType<?> modifyContainerType2(ContainerType<?> old){
        if(old == ContainerType.BLAST_FURNACE){
            return Initializer.MODDED_BLAST_FURNACE.get();
        }
        else if(old == ContainerType.SMOKER){
            return Initializer.MODDED_SMOKER.get();
        }
        return Initializer.MODDED_FURNACE.get();
    }
}
