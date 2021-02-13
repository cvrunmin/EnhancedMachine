package io.github.cvrunmin.enhancedmachine.mixin.gui;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.DispenserScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DispenserScreen.class)
public abstract class DispenserScreenMixin extends ContainerScreen {
//    @Shadow
//    @Final
//    private static ResourceLocation DISPENSER_GUI_TEXTURES;


    public DispenserScreenMixin(Container screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Inject(method = "drawGuiContainerBackgroundLayer", at = @At("TAIL"))
    private void renderExtraSlots(CallbackInfo info){
        int count = (container.getInventory().size() - 36);
        for (int i = 9; i < count; i++) {
            int posX = (((i - 9) / 3) % 2 == 0 ? 62 - (((i - 9) / 3) / 2 + 1) * 18 : 98 + (((i - 9) / 3) / 2 + 1) * 18) - 1;
            int posY = 17 + (i % 3) * 18 - 1;
            int basePosX = (((i - 9) / 3) % 2 == 0 ? 62 : 98) - 1;
            this.blit(guiLeft + posX, guiTop + posY, basePosX, posY, 18, 18);
        }
    }

}
