package io.github.cvrunmin.enhancedmachine.mixin;

import com.mojang.brigadier.arguments.BoolArgumentType;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.network.UpdateLimitedMultiplierMessage;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRules.class)
public class GameRulesMixin {

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void registerModRules(CallbackInfo info){
        EnhancedMachine.DO_LIMITED_CHIP_MULTIPLIER = GameRules.register("doLimitedChipMultiplier", GameRuleTypeInvoker.createRuleType(BoolArgumentType::bool, type -> new GameRules.BooleanValue(type, true), (minecraftServer, value) -> {
            EnhancedMachine.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateLimitedMultiplierMessage(((GameRules.BooleanValue) value).get()));
        }));
    }
}
