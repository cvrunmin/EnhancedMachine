package io.github.cvrunmin.enhancedmachine.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.inventory.ChipWriterContainer;
import io.github.cvrunmin.enhancedmachine.inventory.SlotAllowDisable;
import io.github.cvrunmin.enhancedmachine.mixin.gui.FontRendererMixin;
import io.github.cvrunmin.enhancedmachine.network.ChipwriterAttributeMessage;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChipWriterScreen extends ContainerScreen<ChipWriterContainer> {
    public static final ResourceLocation UI_TEXTURE = new ResourceLocation("enhancedmachine:textures/gui/container/chipwriter.png");

    private float scrollAmount;
    private boolean mouseClicked;
    private int scrollOffset;
    private boolean canCraft;

    private GuiRiserWeightsWriter subWriter;

    public ChipWriterScreen(ChipWriterContainer chipWriterContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(chipWriterContainer,playerInventory, title);
        this.xSize = 176;
        this.ySize = 172;
        chipWriterContainer.setInputInvChangeListener(this::onInventoryChange);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (subWriter != null) subWriter.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        if (this.playerInventory.getItemStack().isEmpty()) {
            int listViewX = guiLeft + 73;
            int listViewY = guiTop + 15;
            List<Tuple<UpgradeDetail, Integer>> list = this.container.getAvailableRecipes();
            for (int i = this.scrollOffset; i < scrollOffset + 12 && i < this.container.getAvailableRecipesCount(); ++i) {
                int j = i - this.scrollOffset;
                int k = listViewX + j % 4 * 20;
                int l = j / 4;
                int m = listViewY + l * 20;
                if (mouseX >= k && mouseY >= m && mouseX < k + 20 && mouseY < m + 20) {
                    Tuple<UpgradeDetail, Integer> tuple = list.get(i);
                    List<ITextComponent> lines = Upgrades.writeUpgradeTooltip(tuple.getA());
                    lines.add(new TranslationTextComponent("upgrade.applicable_block").appendString(tuple.getA().getType().getSupportedBlocks().stream().map(blk -> I18n.format(blk.getTranslationKey())).collect(Collectors.joining(", "))));
                    if (playerInventory.player.experienceLevel < this.container.getAvailableRecipes().get(i).getB() && !this.playerInventory.player.isCreative()) {
                        lines.add(new StringTextComponent(""));
                        lines.add(new TranslationTextComponent("container.enchant.level.requirement", tuple.getB()).mergeStyle(TextFormatting.RED));
                    }
                    GuiUtils.drawHoveringText(matrixStack, lines, mouseX, mouseY, width, height, -1, font);
                    break;
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.drawString(matrixStack, this.title.getString(), 8, 4, 0x404040);
        if (subWriter == null) {
            this.font.drawString(matrixStack, this.playerInventory.getDisplayName().getString(), 8, this.ySize - 94, 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        if (subWriter == null) {
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            this.minecraft.getTextureManager().bindTexture(UI_TEXTURE);
            this.blit(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);
            int k = (int) (45 * scrollAmount);
            this.blit(matrixStack, guiLeft + 156, guiTop + 15 + k, 176 + (shouldScroll() ? 0 : 12), 0, 12, 15);
            int listViewX = guiLeft + 73;
            int listViewY = guiTop + 15;
            int n = scrollOffset + 12;
            this.renderRecipeBackground(matrixStack, mouseX, mouseY, listViewX, listViewY, n);
            this.renderRecipeIcons(matrixStack, listViewX, listViewY, n);
        } else {
            RenderSystem.color4f(1f, 1f, 1f, 1f);
            this.minecraft.getTextureManager().bindTexture(UI_TEXTURE);
            this.blit(matrixStack, guiLeft + 7, guiTop + 3, 7, 3, font.getStringWidth(title.getString()) + 1, 10);
            this.blit(matrixStack, guiLeft + 11, guiTop + 14, 11, 14, 18, 18);
            this.blit(matrixStack, guiLeft + 29 - 4, guiTop + 54 - 4, 29 - 4, 54 - 4, 26, 26);
        }
    }

    private void renderRecipeBackground(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y, int scrollOffset) {
        for (int i = this.scrollOffset; i < scrollOffset && i < this.container.getAvailableRecipesCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 20;
            int l = j / 4;
            int m = y + l * 20;
            int textureX = 0;
            int textureY = this.ySize;
            if (minecraft.player.experienceLevel < this.container.getAvailableRecipes().get(i).getB() && !this.playerInventory.player.isCreative()) {
                textureX += 20;
            } else if (i == this.container.getSelectedUpgrade()) {
                textureY += 20;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 20 && mouseY < m + 20) {
                textureY += 40;
            }

            this.blit(matrixStack, k, m, textureX, textureY, 20, 20);
        }

    }

    private void renderRecipeIcons(MatrixStack matrixStack, int x, int y, int scrollOffset) {
        List<Tuple<UpgradeDetail, Integer>> list = this.container.getAvailableRecipes();

        for (int i = this.scrollOffset; i < scrollOffset && i < this.container.getAvailableRecipesCount(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 4 * 20 + 2;
            int l = j / 4;
            int m = y + l * 20 + 2;
            this.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), list.get(i).getA()), k, m);
            int expLevel = list.get(i).getB();
            renderRecipeIconsExperienceRequirement(matrixStack, k - 2, m - 2, expLevel, minecraft.player.experienceLevel >= expLevel || this.playerInventory.player.isCreative());
        }
    }

    private void renderRecipeIconsExperienceRequirement(MatrixStack matrixStack, int x, int y, int expLevel, boolean enough) {
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();
        this.minecraft.getTextureManager().bindTexture(UI_TEXTURE);
        this.blit(matrixStack, x, y, 40, ySize, 20, 20);
        RenderSystem.disableBlend();
        matrixStack.push();
        matrixStack.translate(0.0D, 0.0D, (double)(this.getMinecraft().getItemRenderer().zLevel + 200.0F));
        ((FontRendererMixin)font).invokeRenderString("" + expLevel, (float) (x + 20 - font.getStringWidth("" + expLevel)), (float) (y + 21 - font.FONT_HEIGHT), enough ? 0x00ef00 : 0xef0000, matrixStack.getLast().getMatrix(), true, false);
        matrixStack.pop();
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        this.mouseClicked = false;
        if (subWriter == null) {
            if (this.canCraft) {
                int i = this.guiLeft + 72;
                int j = this.guiTop + 14;
                int k = this.scrollOffset + 12;

                for (int l = this.scrollOffset; l < k; ++l) {
                    int m = l - this.scrollOffset;
                    double d = mouseX - (double) (i + m % 4 * 20);
                    double e = mouseY - (double) (j + m / 4 * 20);
                    if (d >= 0.0D && e >= 0.0D && d < 20.0D && e < 20.0D && (this.container).enchantItem(this.minecraft.player, l)) {
                        this.minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        if (minecraft.player.experienceLevel >= this.container.getAvailableRecipes().get(l).getB() || this.minecraft.player.isCreative()) {
                            EnhancedMachine.CHANNEL.sendToServer(new ChipwriterAttributeMessage(l));
                        }
                        //                    this.minecraft.interactionManager.clickButton(((StonecutterContainer)this.container).syncId, l);
                        return true;
                    }
                }

                i = this.guiLeft + 156;
                j = this.guiTop + 15;
                if (mouseX >= (double) i && mouseX < (double) (i + 12) && mouseY >= (double) j && mouseY < (double) (j + 60)) {
                    this.mouseClicked = true;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public boolean mouseScrolled(double d, double e, double amount) {
        if (this.shouldScroll()) {
            int i = this.getMaxScroll();
            this.scrollAmount = (float) ((double) this.scrollAmount - amount / (double) i);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) i) + 0.5D) * 4;
        }
        return true;
    }

    private boolean shouldScroll() {
        return this.canCraft && this.container.getAvailableRecipesCount() > 12;
    }

    protected int getMaxScroll() {
        return (this.container.getAvailableRecipesCount() + 4 - 1) / 4 - 3;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int mouseButton, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        if (subWriter != null || !this.mouseClicked || !this.shouldScroll()) {
            return super.mouseDragged(mouseX, mouseY, mouseButton, p_mouseDragged_6_, p_mouseDragged_8_);
        } else {
            int i = this.guiTop + 14;
            int j = i + 60;
            this.scrollAmount = ((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
            this.scrollAmount = MathHelper.clamp(this.scrollAmount, 0.0F, 1.0F);
            this.scrollOffset = (int) ((double) (this.scrollAmount * (float) this.getMaxScroll()) + 0.5D) * 4;
            return true;
        }
    }

    private void onInventoryChange() {
        this.canCraft = this.container.canCraft();
        if (!this.canCraft) {
            this.scrollAmount = 0.0F;
            this.scrollOffset = 0;
        }
        if (subWriter != null) {
            closeRiserWeightWriting();
        }
    }

    @Override
    public void tick() {
        super.tick();
        doBranchButton();
    }

    private void doBranchButton() {
        children.removeAll(buttons);
        buttons.clear();
        Slot inputSlot = container.getSlot(36);
        Slot outputSlot = container.getSlot(38);
        if (subWriter != null) {
            addButton(new Button(guiLeft + outputSlot.xPos - 18, guiTop + outputSlot.yPos + 2, 12, 12, new StringTextComponent("-"), but -> {
                UpgradeDetail upgradeDetail = Upgrades.getUpgradeFromItemStack(inputSlot.getStack());
                if (inputSlot.getHasStack() && Upgrades.RISER.equals(upgradeDetail.getType())) {
                    if (!Arrays.equals(subWriter.weights, upgradeDetail.getExtras().getIntArray("Weights"))) {
                        EnhancedMachine.CHANNEL.sendToServer(new ChipwriterAttributeMessage(subWriter.weights));
                    }
                }
                closeRiserWeightWriting();
            }));
        } else if (inputSlot.getHasStack() && Upgrades.RISER.equals(Upgrades.getUpgradeFromItemStack(inputSlot.getStack()).getType())) {
            addButton(new Button(guiLeft + outputSlot.xPos - 18, guiTop + outputSlot.yPos + 2, 12, 12, new StringTextComponent("+"), but -> {
                UpgradeDetail upgradeDetail = Upgrades.getUpgradeFromItemStack(inputSlot.getStack());
                if (inputSlot.getHasStack() && Upgrades.RISER.equals(upgradeDetail.getType())) {
                    subWriter = new GuiRiserWeightsWriter(this, upgradeDetail);
                    for (int i = 0; i < 36; i++) {
                        ((SlotAllowDisable) container.getSlot(i)).setEnabled(false);
                    }
                    ((SlotAllowDisable) container.getSlot(37)).setEnabled(false);
                    children.add(subWriter);
//                ((SlotAllowDisable) inventorySlots.getSlot(38)).setEnabled(false);
                }
            }));
        }
    }

    private void closeRiserWeightWriting() {
        children.remove(subWriter);
        subWriter = null;
        for (int i = 0; i < 36; i++) {
            ((SlotAllowDisable) container.getSlot(i)).setEnabled(true);
        }
        ((SlotAllowDisable) container.getSlot(37)).setEnabled(true);
//        ((SlotAllowDisable) inventorySlots.getSlot(38)).setEnabled(true);
    }
}
