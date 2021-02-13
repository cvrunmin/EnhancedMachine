package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.LockableTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LockableTileEntity.class)
public interface CreateMenuInvokerMixin {

    @Invoker("createMenu")
    Container callCreateMenu(int id, PlayerInventory player);
}
