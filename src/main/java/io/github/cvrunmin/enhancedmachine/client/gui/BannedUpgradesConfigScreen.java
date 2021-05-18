package io.github.cvrunmin.enhancedmachine.client.gui;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.mixin.gui.FontRendererMixin;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BannedUpgradesConfigScreen extends Screen {
    public static final ResourceLocation UI_TEXTURE = new ResourceLocation("enhancedmachine:textures/gui/config_widget.png");
    private final Screen lastScreen;
    private final List<EMConfig.BannedUpgradeInfo> bannedUpgradeInfoList;
    private int listWidth;

    private AvailableUpgradeList list;

    private AvailableUpgradeListEntry selectedEntry = null;
    private int scrollOffset;
    private HashSet<Integer> bannedLevelSet = new HashSet<>();
    private boolean isBannedAll = false;
    private HashSet<Integer> allLevelSet;

    protected BannedUpgradesConfigScreen(Screen lastScreen) {
        super(new TranslationTextComponent(""));
        this.lastScreen = lastScreen;
        bannedUpgradeInfoList = new ArrayList<>(EMConfig.getBannedUpgradesParsed());
    }

    @Override
    protected void init() {
        for (Upgrade upgrade : Upgrades.registeredUpgrades()) {
            listWidth = Math.max(listWidth, font.getStringWidth(I18n.format(upgrade.getTranslationKey())) + 10);
        }
        listWidth = Math.max(Math.min(listWidth, width/3), 100);

        this.addButton(new Button(this.width / 2 - 100, this.height - 26, 200, 20, new TranslationTextComponent("gui.done"), (p_213056_1_) -> {
            this.getMinecraft().displayGuiScreen(this.lastScreen);
        }));

        this.list = new AvailableUpgradeList(this, listWidth, 32, this.height - 32);
        this.list.setLeftPos(6);

        children.add(this.list);
    }

    @Override
    public void onClose() {
        cacheBanItems();
        EMConfig.BANNDED_UPGRADES.set(bannedUpgradeInfoList.stream().map(EMConfig.BannedUpgradeInfo::toString).collect(Collectors.toList()));
    }

    @Override
    public void tick() {
        this.list.setSelected(selectedEntry);
    }

    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY) {
        if(selectedEntry != null) {
            int listViewX = this.width - 100 - 26;
            int listViewY = 32 + 20 + 20;
            for (int i = this.scrollOffset; i < scrollOffset + 25 && i < selectedEntry.upgrade.getMaxLevel(); ++i) {
                int j = i - this.scrollOffset;
                int k = listViewX + j % 5 * 20;
                int l = j / 5;
                int m = listViewY + l * 20;
                if (mouseX >= k && mouseY >= m && mouseX < k + 20 && mouseY < m + 20) {
                    boolean isBanned = bannedLevelSet.contains(i + 1);
                    List<ITextComponent> lines = Stream.of(Upgrades.getUpgradeFullTitle(new UpgradeDetail(selectedEntry.upgrade, i + 1)),
                            new TranslationTextComponent("enhancedmachine.config.general.bannedUpgrades." + (isBanned ? "on" : "off")).mergeStyle(isBanned ? TextFormatting.RED : TextFormatting.WHITE))
                            .collect(Collectors.toList());
                    GuiUtils.drawHoveringText(matrixStack, lines, mouseX, mouseY, width, height, -1, font);
                    break;
                }
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTick) {
        this.list.render(matrixStack, mouseX, mouseY, partialTick);
        if(selectedEntry != null) {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(this.list.getRight() + 6, 32, 0);
            RenderSystem.scalef(4,4,1);
//            matrixStack.push();
//            matrixStack.translate(this.list.getRight() + 6, 32, 0);
//            matrixStack.scale(4, 4, 1);
            this.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), new UpgradeDetail(selectedEntry.upgrade, 1)),
                    /*this.list.getRight() + 6, 32*/0, 0);
            RenderSystem.scalef(0.5f * 1,0.5f * 1,1);
//            matrixStack.scale(0.5f, 0.5f, 1);
            font.drawString(matrixStack, I18n.format(selectedEntry.upgrade.getTranslationKey()), 17 * 2, 0, 0xffffff);
            RenderSystem.popMatrix();
//            matrixStack.pop();
            int listViewX = this.width - 100 - 26;
            font.func_238418_a_(new TranslationTextComponent(selectedEntry.upgrade.getTranslationKey() + ".desc"), this.list.getRight() + 6 + 17*4, 32 + font.FONT_HEIGHT*2, Math.max( listViewX - 6 - (this.list.getRight() + 6 + 17*4), 1), 0xffffff);
            int listViewY = 32 + 20;
            int n = scrollOffset + 25;
            this.renderRecipeBackground(matrixStack, mouseX, mouseY, listViewX, listViewY, n);
            this.renderRecipeIcons(matrixStack, listViewX, listViewY, n);
        }
        super.render(matrixStack, mouseX, mouseY, partialTick);
        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    public void setSelected(AvailableUpgradeListEntry entry){
        cacheBanItems();
        this.selectedEntry = entry == selectedEntry ? null : entry;
        if(selectedEntry != null){
            bannedLevelSet.clear();
            isBannedAll = false;
            allLevelSet = IntStream.rangeClosed(selectedEntry.upgrade.getMinLevel(), selectedEntry.upgrade.getMaxLevel()).boxed().collect(Collectors.toCollection(HashSet::new));
            bannedUpgradeInfoList.stream().filter(info -> info.getUpgrade().equals(selectedEntry.upgrade)).forEach(info -> {
                if(info.isFullCoverage()){
                    isBannedAll = true;
                }
            });
            if(!isBannedAll) {
                bannedLevelSet.addAll(bannedUpgradeInfoList.stream()
                        .filter(info -> info.getUpgrade().equals(selectedEntry.upgrade))
                        .flatMap(info -> IntStream.rangeClosed(Math.max(info.getMinInclude(), info.getUpgrade().getMinLevel()), Math.min(info.getMaxInclude(), info.getUpgrade().getMaxLevel())).boxed()).collect(Collectors.toList()));
            }
            else{
                bannedLevelSet.addAll(allLevelSet);
            }
        }
    }

    private void cacheBanItems() {
        if(selectedEntry != null){
            this.bannedUpgradeInfoList.removeIf(info -> info.getUpgrade().equals(selectedEntry.upgrade));
            if(isBannedAll){
                this.bannedUpgradeInfoList.add(new EMConfig.BannedUpgradeInfo(selectedEntry.upgrade, 0, Integer.MAX_VALUE));
            }
            else if(!bannedLevelSet.isEmpty()){
                int start = -1;
                int next = -1;
                for (Integer integer : bannedLevelSet) {
                    if(start == -1){
                        start = integer;
                        next = integer;
                        continue;
                    }
                    if(integer - next > 1){
                        this.bannedUpgradeInfoList.add(new EMConfig.BannedUpgradeInfo(selectedEntry.upgrade, start, next));
                        start = integer;
                    }
                    next = integer;
                }
                this.bannedUpgradeInfoList.add(new EMConfig.BannedUpgradeInfo(selectedEntry.upgrade, start, next));
            }
        }
    }

    private void renderRecipeBackground(MatrixStack matrixStack, int mouseX, int mouseY, int x, int y, int scrollOffset) {

        {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = 0;
            if(isBannedAll){
                i += 40;
            }
            if (mouseX >= x && mouseY >= y && mouseX < x + 100 && mouseY < y + 20) {
                i += 20;
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.getMinecraft().getTextureManager().bindTexture(UI_TEXTURE);
            this.blit(matrixStack, x, y, 0, 60 + i, 100 / 2, 20);
            this.blit(matrixStack, x + 100 / 2, y, 200 - 100 / 2, 60 + i, 100 / 2, 20);
            int j = isBannedAll ? TextFormatting.RED.getColor() : TextFormatting.WHITE.getColor();
            this.drawCenteredString(matrixStack, font, I18n.format("enhancedmachine.config.general.bannedUpgrades.full." + (isBannedAll ? "on" : "off")), x + 100 / 2, y + (20 - 8) / 2, j | 0xff000000);
        }

        if(selectedEntry.upgrade.getMinLevel() != selectedEntry.upgrade.getMaxLevel())
        for (int i = this.scrollOffset; i < scrollOffset && i < selectedEntry.upgrade.getMaxLevel(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 5 * 20;
            int l = j / 5;
            int m = y + 20 + l * 20;
            int textureX = 0;
            int textureY = 0;
            if (bannedLevelSet.contains(i + 1)) {
                textureX += 20;
            }
            if (mouseX >= k && mouseY >= m && mouseX < k + 20 && mouseY < m + 20) {
                textureY += 40;
            }

            this.getMinecraft().getTextureManager().bindTexture(UI_TEXTURE);

            this.blit(matrixStack, k, m, textureX, textureY, 20, 20);
        }

    }

    private void renderRecipeIcons(MatrixStack matrixStack, int x, int y, int scrollOffset) {
        if(selectedEntry.upgrade.getMinLevel() != selectedEntry.upgrade.getMaxLevel())
        for (int i = this.scrollOffset; i < scrollOffset && i < selectedEntry.upgrade.getMaxLevel(); ++i) {
            int j = i - this.scrollOffset;
            int k = x + j % 5 * 20 + 2;
            int l = j / 5;
            int m = y + 20 + l * 20 + 2;
//            this.getMinecraft().getItemRenderer().renderItemAndEffectIntoGUI(Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), new UpgradeDetail(selectedEntry.upgrade, i + 1)), k, m);

            RenderSystem.enableAlphaTest();
            matrixStack.push();
            matrixStack.translate(0.0D, 0.0D, this.getMinecraft().getItemRenderer().zLevel + 200.0F);
            ((FontRendererMixin)font).invokeRenderString("" + (i + 1), (float) (k - 2 + 20 - font.getStringWidth("" + (i + 1))), (float) (m - 2 + 21 - font.FONT_HEIGHT), 0xffffff, matrixStack.getLast().getMatrix(), true, false);
            matrixStack.pop();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if(selectedEntry != null) {
            int i = this.width - 100 - 26;
            int j = 32 + 20;
            int k = scrollOffset + 25;

            double d = mouseX - (double) (i);
            double e = mouseY - (double) (j);
            if (d >= 0.0D && e >= 0.0D && d < 100.0D && e < 20.0D) {
                this.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                if (isBannedAll) {
                    isBannedAll = false;
                    bannedLevelSet.clear();
                } else {
                    isBannedAll = true;
                    bannedLevelSet.addAll(allLevelSet);
                }
                return true;
            }

            j += 20;

            if (selectedEntry.upgrade.getMinLevel() != selectedEntry.upgrade.getMaxLevel())
                for (int l = this.scrollOffset; l < k; ++l) {
                    int m = l - this.scrollOffset;
                    d = mouseX - (double) (i + m % 5 * 20);
                    e = mouseY - (double) (j + m / 5 * 20);
                    if (d >= 0.0D && e >= 0.0D && d < 20.0D && e < 20.0D) {
                        this.getMinecraft().getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        if (bannedLevelSet.contains(l + 1)) {
                            bannedLevelSet.remove(l + 1);
                            isBannedAll = false;
                        } else {
                            bannedLevelSet.add(l + 1);
                            if (Sets.difference(allLevelSet, bannedLevelSet).isEmpty()) {
                                isBannedAll = true;
                            }
                        }
                        return true;
                    }
                }
        }
        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public static class AvailableUpgradeList extends ExtendedList<AvailableUpgradeListEntry> {

        private final BannedUpgradesConfigScreen parent;
        private final int listWidth;

        public AvailableUpgradeList(BannedUpgradesConfigScreen parent, int listWidth, int topIn, int bottomIn) {
            super(parent.getMinecraft(), listWidth, parent.height, topIn, bottomIn, 20);
            this.parent = parent;
            this.listWidth = listWidth;

            refreshEntries(parent);
        }

        public void refreshEntries(BannedUpgradesConfigScreen parent) {
            for (Upgrade upgrade : Upgrades.registeredUpgrades()) {
                if(EMConfig.INVALID_BAN_UPGRADE_TYPES.contains(upgrade)) continue;
                addEntry(new AvailableUpgradeListEntry(upgrade, parent, this));
            }
        }

        @Override
        protected int getScrollbarPosition()
        {
            return this.listWidth;
        }

        @Override
        public int getRowWidth()
        {
            return this.listWidth;
        }

        @Override
        protected void renderBackground(MatrixStack matrixStack)
        {
            this.parent.renderBackground(matrixStack);
        }

    }

    public static class AvailableUpgradeListEntry extends ExtendedList.AbstractListEntry<AvailableUpgradeListEntry>{

        private final Upgrade upgrade;

        private final BannedUpgradesConfigScreen parent;

        private final AvailableUpgradeList list;

        public AvailableUpgradeListEntry(Upgrade upgrade, BannedUpgradesConfigScreen parent, AvailableUpgradeList list) {
            this.upgrade = upgrade;
            this.parent = parent;
            this.list = list;
        }

        @Override
        public void render(MatrixStack matrixStack, int entryIdx, int top, int left, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isSelected, float partialTicks) {
            String name = I18n.format(upgrade.getTranslationKey());
            FontRenderer font = this.parent.font;
            font.drawString(matrixStack, font.trimStringToWidth(name, entryWidth),left + 3, top + 2, 0xFFFFFF);
        }

        @Override
        public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_)
        {
            parent.setSelected(this);
            list.setSelected(this);
            return false;
        }
    }
}
