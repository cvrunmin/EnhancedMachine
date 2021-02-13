package io.github.cvrunmin.enhancedmachine.mixin.block;

import net.minecraft.block.BlastFurnaceBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.block.SmokerBlock;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.OptionalInt;

@Mixin({FurnaceBlock.class, BlastFurnaceBlock.class, SmokerBlock.class})
public class FurnaceBlocksMixin {

    @Redirect(method = "interactWith", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;openContainer(Lnet/minecraft/inventory/container/INamedContainerProvider;)Ljava/util/OptionalInt;"))
    public OptionalInt openContainerWithForge(PlayerEntity player, INamedContainerProvider containerProvider){
        if(player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, ((TileEntity) containerProvider).getPos());
        }
        return OptionalInt.empty();
    }
}
