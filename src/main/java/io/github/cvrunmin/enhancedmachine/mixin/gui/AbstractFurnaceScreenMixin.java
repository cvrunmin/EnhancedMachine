package io.github.cvrunmin.enhancedmachine.mixin.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.recipebook.AbstractRecipeBookGui;
import net.minecraft.client.gui.screen.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractFurnaceScreen.class)
public abstract class AbstractFurnaceScreenMixin extends ContainerScreen {
    private static final ResourceLocation FURNACE_BP_TEXTURES = new ResourceLocation("enhancedmachine:textures/gui/container/furnace_bp.png");
    @Shadow
    @Final
    private static ResourceLocation BUTTON_TEXTURE;
    @Shadow
    private boolean widthTooNarrowIn;
    @Shadow
    @Final
    public AbstractRecipeBookGui recipeGui;

    public AbstractFurnaceScreenMixin(Container screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @ModifyArg(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/inventory/AbstractFurnaceScreen;addButton(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"))
    private Widget modifyRecipeButton(Widget old){
        int count = (container.getInventory().size() - 37) / 2;
        if(count > 1){
            return new ImageButton(this.guiLeft + 78, this.guiTop + 16, 20, 18, 0, 0, 19, BUTTON_TEXTURE, (p_214087_1_) -> {
                this.recipeGui.initSearchBar(this.widthTooNarrowIn);
                this.recipeGui.toggleVisibility();
                this.guiLeft = this.recipeGui.updateScreenPosition(this.widthTooNarrowIn, this.width, this.xSize);
                ((ImageButton)p_214087_1_).setPosition(this.guiLeft + 78, this.guiTop + 16);
            });
        }
        return old;
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/recipebook/AbstractRecipeBookGui;func_230477_a_(Lcom/mojang/blaze3d/matrix/MatrixStack;IIZF)V"), index = 3)
    private boolean setBigBackgroundForGhostResult(boolean old){
        int count = (container.getInventory().size() - 37) / 2;
        return count <= 4 && old;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(FURNACE_BP_TEXTURES);
        int left = this.guiLeft;
        int top = this.guiTop;
        this.blit(matrixStack, left, top, 0, 0, this.xSize, this.ySize);

        int count = (container.getInventory().size() - 37) / 2;

        for (int i = 0; i < count * 2 + 1; i++) {
            Slot slot = container.getSlot(36 + i);
            if (i == 0) { //Fuel slot
                this.blit(matrixStack, left + (slot != null ? slot.xPos - 1 : 55), top + (slot != null ? slot.yPos - 1 : 52), 194, 48, 18, 18);
            } else if (i % 2 == 1) { //Input
                if (slot == null) continue;
                this.blit(matrixStack, left + slot.xPos - 1, top + slot.yPos - 1, 176, 48, 18, 18);
            } else { //Output
                if (slot == null) continue;
                this.blit(matrixStack, left + slot.xPos - (count > 4 ? 1 : 5), top + slot.yPos - (count > 4 ? 1 : 5), count > 4 ? 202 : 176, 66, count > 4 ? 18 : 26, count > 4 ? 18 : 26);
            }
        }
        //Furnace fuel left
        this.blit(matrixStack, left + (count == 1 ? 56 : /*79*/ 89), top + (count == 1 ? 36 : /*46*/ 56), 190, 0, 14, 14);
        //Furnace progress
        this.blit(matrixStack, left + (count == 1 ? 79 : 76), top + (count == 1 ? 34 : 29 + 5), 176, 30, 24, 16);

        if (((AbstractFurnaceContainer) container).isBurning()) {
            int k = ((AbstractFurnaceContainer)this.container).getBurnLeftScaled();
            this.blit(matrixStack, left + (count == 1 ? 56 : 89), top + (count == 1 ? 36 : 56) + 12 - k, 176, 12 - k, 14, k + 1);
        }

        int l = ((AbstractFurnaceContainer)this.container).getCookProgressionScaled();
        this.blit(matrixStack, left + (count == 1 ? 79 : 76), top + (count == 1 ? 34 : 29 + 5), 176, 14, l + 1, 16);
    }
}
