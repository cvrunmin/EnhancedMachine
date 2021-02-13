package io.github.cvrunmin.enhancedmachine.mixin;

import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(GameRules.RuleType.class)
public interface GameRuleTypeInvoker {
    @Invoker("<init>")
    static  GameRules.RuleType createRuleType(Supplier<ArgumentType<?>> argTypeSupplier, Function<GameRules.RuleType, ?> valueCreator, BiConsumer<MinecraftServer, ?> changeListener) {
        throw new NotImplementedException("");
    }
}
