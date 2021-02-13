package io.github.cvrunmin.enhancedmachine.mixin.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.mixin.inventory.BrewingStandContainerAccessor;
import io.github.cvrunmin.enhancedmachine.tileentity.IBrewingStandExt;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.BrewingStandContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(BrewingStandScreen.class)
public abstract class BrewingStandScreenMixin extends ContainerScreen<BrewingStandContainer> {

    @Shadow
    @Final
    private static ResourceLocation BREWING_STAND_GUI_TEXTURES;

    @Shadow
    @Final
    private static int[] BUBBLELENGTHS;

    public BrewingStandScreenMixin(BrewingStandContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(BREWING_STAND_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.ySize);
        int k = this.container.func_216982_e();
        IInventory tileBrewingStand = ((BrewingStandContainerAccessor) this.container).getTileBrewingStand();
        int fuelTime = tileBrewingStand instanceof IBrewingStandExt ? ((IBrewingStandExt) tileBrewingStand).getFuelTime() : 20;
        int l = MathHelper.clamp((18 * k + fuelTime - 1) / 20, 0, 18);
        if (l > 0) {
            this.blit(i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.container.func_216981_f();
        if (i1 > 0) {
            float brewTime = tileBrewingStand instanceof IBrewingStandExt ? ((IBrewingStandExt) tileBrewingStand).getBrewTime() : 400.0F;
            int j1 = (int) (28.0F * (1.0F - (float) i1 / brewTime));
            if (j1 > 0) {
                this.blit(i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];
            if (j1 > 0) {
                this.blit(i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }
    }
}
