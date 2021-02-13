package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Container.class)
public interface ContainerAccessor {
    @Accessor
    NonNullList<ItemStack> getInventoryItemStacks();
}
