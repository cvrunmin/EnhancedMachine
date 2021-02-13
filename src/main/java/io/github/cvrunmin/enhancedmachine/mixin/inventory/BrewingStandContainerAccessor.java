package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BrewingStandContainer.class)
public interface BrewingStandContainerAccessor {
    @Accessor("tileBrewingStand")
    IInventory getTileBrewingStand();
}
