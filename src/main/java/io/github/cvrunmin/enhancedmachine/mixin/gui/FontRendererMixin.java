package io.github.cvrunmin.enhancedmachine.mixin.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FontRenderer.class)
public interface FontRendererMixin {

    @Invoker
    int invokeRenderString(String text, float x, float y, int color, Matrix4f matrix, boolean dropShadow);

}
