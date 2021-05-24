package io.github.cvrunmin.enhancedmachine.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.inventory.UpgradeChipPanelContainer;
import io.github.cvrunmin.enhancedmachine.inventory.SlotUpgradeChip;
import io.github.cvrunmin.enhancedmachine.inventory.SlotUpgradeChipParent;
import io.github.cvrunmin.enhancedmachine.network.FocusedUpgradeChipChangeMessage;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class UpgradeChipPanelScreen extends ContainerScreen<UpgradeChipPanelContainer> {
    public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
    public static final ResourceLocation UPGRADE_SLOT = new ResourceLocation("enhancedmachine:textures/gui/container/chip_slot.png");
    IUpgradeSlot cap;
    List<Slot> playerSlots = new ArrayList<>();
    BlockPos pos;
    UpgradeChipPanelContainer containerUpgradeChipPanel;

    public UpgradeChipPanelScreen(UpgradeChipPanelContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.containerUpgradeChipPanel = container;
        this.cap = container.getCap();
        ySize = 222;
        this.pos = container.getPos();
    }

    @Override
    public void init() {
        super.init();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        this.font.drawString(matrixStack, this.title.getString(), (this.xSize - this.font.getStringWidth(this.title.getString())) / 2, 0, 0xffffff);
        this.font.drawString(matrixStack, playerInventory.getDisplayName().getString(), 8, this.ySize - 94, 0x404040);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j + 138 - 16, 0, 0, this.xSize, 16);
        this.blit(matrixStack, i, j + 138, 0, 139, this.xSize, this.ySize - 140);
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiLeft + xSize / 2f, 0, 0);
        RenderSystem.scalef(5, 5, 1);
        RenderSystem.translatef(-8, 0, 0);
//        matrixStack.push();
//        matrixStack.translate(guiLeft + xSize / 2f, 0, 0);
//        matrixStack.scale(5,5,1);
//        matrixStack.translate(-8,0,0);
//        this.zLevel = -150;
//        this.itemRender.zLevel = -150;
        Block block = this.minecraft.world.getBlockState(pos).getBlock();
        this.minecraft.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(block), 0, (this.guiTop + 24) / 5);
        RenderSystem.popMatrix();
//        matrixStack.pop();
//        zLevel = 50;
//        this.itemRender.zLevel = 0;
        boolean depthPreviouslyEnabled = GlStateManager.getInteger(GL11.GL_DEPTH_TEST) == 1;
        RenderSystem.disableDepthTest();
        for (Slot slot : containerUpgradeChipPanel.getSlots()) {
            if (slot instanceof SlotUpgradeChip) {
                this.minecraft.getTextureManager().bindTexture(UPGRADE_SLOT);
                blit(matrixStack, guiLeft + slot.xPos - 4, guiTop + slot.yPos - 4, 0, 0, 32, 32, 32, 32);
            }
        }
        if (depthPreviouslyEnabled) {
            RenderSystem.enableAlphaTest();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (playerInventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            this.renderTooltip(matrixStack, this.hoveredSlot, this.hoveredSlot.getStack(), mouseX, mouseY);
        }
    }

    protected void renderTooltip(MatrixStack matrixStack, Slot slot, ItemStack stack, int x, int y) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.gui.GuiUtils.preItemToolTip(stack);
        List<ITextComponent> itemToolTip = this.getTooltipFromItem(stack);
        if (slot instanceof SlotUpgradeChip) {
            List<ITextComponent> warnings = ((SlotUpgradeChip) slot).getInstalledChipTooltips();
            if (!warnings.isEmpty()) {
                itemToolTip.add(new StringTextComponent(""));
                itemToolTip.addAll(warnings);
            }
        } else if (stack.getItem().equals(EMItems.UPGRADE_CHIP.get())) {
            List<ITextComponent> list = new ArrayList<>();
            UpgradeDetail upgradeDetail = Upgrades.getUpgradeFromItemStack(stack);
            if(cap.getHolder() != null) {
                Block block = cap.getHolder().getBlockState().getBlock();
                boolean noFunc = !upgradeDetail.getType().getSupportedBlocks().isEmpty() && !upgradeDetail.getType().getSupportedBlocks().contains(block);
                if (noFunc) {
                    list.add(new TranslationTextComponent("upgrade.warning.no_function", I18n.format(block.getTranslationKey())).mergeStyle(TextFormatting.YELLOW));
                } else if (!Upgrades.RISER.equals(upgradeDetail.getType())) {
                    if (cap.hasUpgradeInstalled(upgradeDetail.getType())) {
                        list.add(new TranslationTextComponent("upgrade.warning.duplicated").mergeStyle(TextFormatting.YELLOW));
                    }
                }
            }
            if (!list.isEmpty()) {
                itemToolTip.add(new StringTextComponent(""));
                itemToolTip.addAll(list);
            }
        }
        this.func_243308_b(matrixStack, itemToolTip, x, y); //renderTooltips
        net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
    }

    @Override
    public void tick() {
        super.tick();

        doBranchButton();
    }

    private void doBranchButton() {
        children.removeAll(buttons);
        buttons.clear();
        List<Slot> slots = container.getSlots();
        for (int i = 1; i < slots.size() - 36; i++) {
            Slot slot = slots.get(i + 36);
            if (slot instanceof SlotUpgradeChipParent) {
                int finalI = i;
                addButton(new Button(guiLeft + slot.xPos + 2, guiTop + slot.yPos - 18, 12, 12, new StringTextComponent("-"), but ->{
                    if(finalI < slots.size() - 36){
                        int focusSlot = ((SlotUpgradeChip) slot).getUpgradeSlotWrapper().getEid();
                        EnhancedMachine.CHANNEL.sendToServer(new FocusedUpgradeChipChangeMessage(focusSlot));
                    }
                }));
            } else if (slot instanceof SlotUpgradeChip) {
                if (Upgrades.RISER.equals(((SlotUpgradeChip) slot).getUpgradeSlot().getUpgrade().getType())) {
                    int finalI = i;
                    addButton(new Button(guiLeft + (slot.xPos < 80 ? slot.xPos - 18 : slot.xPos + 18 + 4), guiTop + slot.yPos + 2, 12, 12, new StringTextComponent("+"), but->{
                        if(finalI < slots.size() - 36){
                            int focusSlot = ((SlotUpgradeChip) slot).getUpgradeSlotWrapper().getEid();
                            EnhancedMachine.CHANNEL.sendToServer(new FocusedUpgradeChipChangeMessage(focusSlot));
                        }
                    }));
                }
            }
        }
    }
}
