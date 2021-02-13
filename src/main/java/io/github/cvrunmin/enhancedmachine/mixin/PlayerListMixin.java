package io.github.cvrunmin.enhancedmachine.mixin;

import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.network.UpdateLimitedMultiplierMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "sendWorldInfo", at = @At("RETURN"))
    private void injectSendWorldInfo(ServerPlayerEntity playerIn, ServerWorld worldIn, CallbackInfo info){
        EnhancedMachine.CHANNEL.send(PacketDistributor.PLAYER.with(()->playerIn), new UpdateLimitedMultiplierMessage(worldIn.getGameRules().getBoolean(EnhancedMachine.DO_LIMITED_CHIP_MULTIPLIER)));
    }
}
