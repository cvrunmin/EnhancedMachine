package io.github.cvrunmin.enhancedmachine.mixin.inventory;


import io.github.cvrunmin.enhancedmachine.Initializer;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.inventory.container.ContainerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BrewingStandContainer.class)
public class BrewingStandContainerMixin {
    @ModifyArg(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/inventory/IInventory;Lnet/minecraft/util/IIntArray;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/container/Container;<init>(Lnet/minecraft/inventory/container/ContainerType;I)V", remap = false), index = 0)
    private static ContainerType<?> modifyContainerType(ContainerType<?> old){
        return Initializer.MODDED_BREWING_STAND.get();
    }
}
