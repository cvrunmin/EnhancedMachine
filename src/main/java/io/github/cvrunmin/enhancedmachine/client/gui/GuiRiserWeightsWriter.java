package io.github.cvrunmin.enhancedmachine.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.network.ChipwriterAttributeMessage;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.Objects;

public class GuiRiserWeightsWriter extends ExtendedList<GuiRiserWeightsWriter.Entry> {
    protected ChipWriterScreen owner;
    protected int[] weights;
    protected UpgradeDetail upgradeDetail;

    public GuiRiserWeightsWriter(ChipWriterScreen owner, UpgradeDetail upgradeDetail) {
        this(upgradeDetail, owner.getMinecraft(), owner.getXSize(), owner.height - 14 - 8, owner.getGuiTop() + 14, owner.getGuiTop() + owner.getYSize() - 8, 18);
        this.owner = owner;
        this.setLeftPos(owner.getGuiLeft() + 73);
    }

    public GuiRiserWeightsWriter(UpgradeDetail upgradeDetail, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);

        this.upgradeDetail = upgradeDetail.clone();
        initEntries();
    }

    @Override
    public void render(int mouseXIn, int mouseYIn, float partialTicks) {
        if (true) {
//            this.mouseX = mouseXIn;
//            this.mouseY = mouseYIn;
            int i = getScrollbarPosition();
            int j = i + 6;
//            this.bindAmountScrolled();
            RenderSystem.disableLighting();
            RenderSystem.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            double scaleW = (double) minecraft.getMainWindow().getFramebufferWidth() / minecraft.getMainWindow().getScaledWidth();
            double scaleH = (double) minecraft.getMainWindow().getFramebufferHeight() / minecraft.getMainWindow().getScaledHeight();
            int viewHeight = this.y1 - this.y0;
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((int) (x0 * scaleW), (int) (minecraft.getMainWindow().getFramebufferHeight() - (y1 * scaleH)),
                    (int) ((x1) * scaleW), (int) (viewHeight * scaleH));
            // Forge: background rendering moved into separate method.
//            this.drawContainerBackground(tessellator);
            int k = this.x0;
            int l = this.y0 + 4 - (int) this.getScrollAmount();

            if (this.renderHeader) {
                this.renderHeader(k, l, tessellator);
            }

            this.renderList(k, l, mouseXIn, mouseYIn, partialTicks);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
            RenderSystem.disableAlphaTest();
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
            RenderSystem.disableTexture();
            int i1 = 4;
            int j1 = this.getMaxScroll();

            if (j1 > 0) {
                int k1 = (this.y1 - this.y0) * (this.y1 - this.y0) / this.getMaxPosition();
                k1 = MathHelper.clamp(k1, 32, this.y1 - this.y0 - 8);
                int l1 = (int) this.getScrollAmount() * (this.y1 - this.y0 - k1) / j1 + this.y0;

                if (l1 < this.y0) {
                    l1 = this.y0;
                }

                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferbuilder.pos(i, this.y1, 0.0D).tex(0.0f, 1.0f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(+j, this.y1, 0.0D).tex(1.0f, 1.0f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(+j, this.y0, 0.0D).tex(1.0f, 0.0f).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(+i, this.y0, 0.0D).tex(0.0f, 0.0f).color(0, 0, 0, 255).endVertex();

                bufferbuilder.pos(+i, l1 + k1, 0.0D).tex(0.0f, 1.0f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(+j, l1 + k1, 0.0D).tex(1.0f, 1.0f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(+j, l1, 0.0D).tex(1.0f, 0.0f).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(+i, l1, 0.0D).tex(0.0f, 0.0f).color(128, 128, 128, 255).endVertex();

                bufferbuilder.pos(+i, l1 + k1 - 1, 0.0D).tex(0.0f, 1.0f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos(+j - 1, l1 + k1 - 1, 0.0D).tex(1.0f, 1.0f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos(+j - 1, l1, 0.0D).tex(1.0f, 0.0f).color(192, 192, 192, 255).endVertex();
                bufferbuilder.pos(+i, l1, 0.0D).tex(0.0f, 0.0f).color(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }

            this.renderDecorations(mouseXIn, mouseYIn);
            RenderSystem.enableTexture();
            RenderSystem.shadeModel(GL11.GL_FLAT);
            RenderSystem.enableAlphaTest();
            RenderSystem.disableBlend();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
    }

    @Override
    public int getRowWidth() {
        return this.width - 6;
    }

    protected void initEntries() {
        if (upgradeDetail.getType() == Upgrades.RISER) {
            int size = Upgrades.RISER.getExpansionSlots(upgradeDetail.getLevel());
            int[] weights = upgradeDetail.getExtras().getIntArray("Weights");
            if (weights.length != size) {
                this.weights = new int[size];
                Arrays.fill(this.weights, 1);
            } else {
                this.weights = weights;
            }
            for (int i = 0; i < this.weights.length; i++) {
                this.children().add(new Entry(this, i));
            }
        }
    }

    public UpgradeDetail getUpgradeDetail() {
        return upgradeDetail;
    }

    private void writeWeights() {
        EnhancedMachine.CHANNEL.sendToServer(new ChipwriterAttributeMessage(weights));
//        upgradeDetail.getExtras().setIntArray("Weights", weights);
//        ((ContainerChipWriter) owner.inventorySlots).previewRiserWeightWrittenOutput(upgradeDetail);
    }

//    public int getSlotIndexFromScreenCoords(int posX, int posY) {
//        int i = this.left;
//        int j = this.left + this.getListWidth();
//        int k = posY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
//        int l = k / this.slotHeight;
//        return posX < this.getScrollBarX() && posX >= i && posX <= j && l >= 0 && k >= 0 && l < this.getSize() ? l : -1;
//    }

    protected void renderList(int insideLeft, int insideTop, int mouseXIn, int mouseYIn, float partialTicks) {
        int i = this.children().size();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();

        for (int j = 0; j < i; ++j) {
            int k = insideTop + j * this.itemHeight + this.headerHeight;
            int l = this.itemHeight;
            Entry e = this.getEntry(j);

            if (k > this.y1 || k + l < this.y0) {
                continue;
//                centerScrollOn(getEntry(j));
//                this.updateItemPos(j, insideLeft, k, partialTicks);
            }

            if (this.renderSelection && this.isSelectedItem(j)) {
                int i1 = this.x0 + (this.width / 2 - this.getRowWidth() / 2);
                int j1 = this.x0 + this.width / 2 + this.getRowWidth() / 2;
                RenderSystem.color4f(1,1,1,1);
                RenderSystem.disableTexture();
                bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferbuilder.pos(i1, k + l + 2, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(j1, k + l + 2, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(j1, k - 2, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(i1, k - 2, 0.0D).color(128, 128, 128, 255).endVertex();
                bufferbuilder.pos(i1 + 1, k + l + 1, 0.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(j1 - 1, k + l + 1, 0.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(j1 - 1, k - 1, 0.0D).color(0, 0, 0, 255).endVertex();
                bufferbuilder.pos(i1 + 1, k - 1, 0.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                RenderSystem.enableTexture();
            }

            e.render(j, insideLeft, k, getRowWidth(), l, mouseXIn, mouseYIn, this.isMouseOver(mouseXIn, mouseYIn) && Objects.equals(this.getEntryAtPosition(mouseXIn, mouseYIn), e), partialTicks);
        }
    }

    public static class Entry extends ExtendedList.AbstractListEntry<Entry> {

        protected final ExtendedButton btnAdd;
        protected final ExtendedButton btnSubtract;
        private GuiRiserWeightsWriter parent;
        private Minecraft mc;
        private int index;

        public Entry(GuiRiserWeightsWriter parent, int index) {
            this.parent = parent;
            this.mc = parent.minecraft;
            this.index = index;
            this.btnAdd = new ExtendedButton(0, 0, 18, 18, "+", but->{
                
            });
            btnAdd.setFGColor(GuiUtils.getColorCode('2', true));
//            this.btnAdd.active = parent.isVisible;
            this.btnSubtract = new ExtendedButton(0, 0, 18, 18, "-", but->{});
            this.btnSubtract.setFGColor(GuiUtils.getColorCode('c', true));
//            this.btnSubtract.active = parent.getEnabled();
        }

        @Override
        public void render(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            AbstractGui.fill(x, y, x + listWidth, y + slotHeight, 0xff000000);
            float total = Arrays.stream(parent.weights).sum();
            this.mc.fontRenderer.drawString(I18n.format("upgrade.riser.weight", index + 1, parent.weights[index], parent.weights[index] / total * 100), x + 2, y + ((slotHeight - this.mc.fontRenderer.FONT_HEIGHT) / 2), 0xFFFFFF);
            this.btnAdd.visible = true;
            this.btnAdd.x = x + listWidth - 40;
            this.btnAdd.y = y;
            this.btnAdd.renderButton(mouseX, mouseY, partialTicks);
            this.btnSubtract.visible = true;
            this.btnSubtract.x = x + listWidth - 20;
            this.btnSubtract.y = y;
            this.btnSubtract.renderButton(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
            if (this.btnAdd.mouseClicked(mouseX, mouseY, mouseButton)) {
                btnAdd.playDownSound(mc.getSoundHandler());
                parent.weights[index]++;
                parent.writeWeights();
                return true;
            } else if (this.btnSubtract.mouseClicked(mouseX, mouseY, mouseButton)) {
                btnSubtract.playDownSound(mc.getSoundHandler());
                if (parent.weights[index] > 1) {
                    parent.weights[index]--;
                    parent.writeWeights();
                }
                return true;
            }
            return false;
        }
    }
}
