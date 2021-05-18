package io.github.cvrunmin.enhancedmachine.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.cvrunmin.enhancedmachine.EMConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.util.text.TranslationTextComponent;

public class EnhancedMachineConfigScreen extends Screen {

    private final Screen lastScreen;

    public EnhancedMachineConfigScreen(Screen parentScreen) {
        super(new TranslationTextComponent("enhancedmachine.config.title"));
        this.lastScreen = parentScreen;
    }

    @Override
    protected void init() {

        int j = this.width / 2 - 155 + 0 % 2 * 160;
        int k = this.height / 6 - 12 + 24 * (0 >> 1);

        this.addButton(new CheckboxButton(j, k, 150, 20, new TranslationTextComponent("enhancedmachine.config.general.alwaysShowPanel"), EMConfig.ALWAYS_SHOW_UPGRADE_PANEL.get()){
            @Override
            public void onPress() {
                super.onPress();
                EMConfig.ALWAYS_SHOW_UPGRADE_PANEL.set(isChecked());
            }
        });
        k = this.height / 6 - 12 + 24 * (2 >> 1);

        this.addButton(new Button(this.width / 2 - 100, k, 200, 20, new TranslationTextComponent("enhancedmachine.config.general.bannedUpgrades"), (p_213056_1_) -> {
            this.getMinecraft().displayGuiScreen(new BannedUpgradesConfigScreen(this));
        }));
        this.addButton(new Button(this.width / 2 - 100, this.height - 26, 200, 20, new TranslationTextComponent("gui.done"), (p_213056_1_) -> {
            this.getMinecraft().displayGuiScreen(this.lastScreen);
        }));
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partial) {
        this.renderBackground(matrixStack);
        this.drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 15, 0xffffff);
        super.render(matrixStack, mouseX, mouseY, partial);
    }
}
