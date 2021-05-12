package io.github.cvrunmin.enhancedmachine.client;

import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.Initializer;
import io.github.cvrunmin.enhancedmachine.client.gui.ChipWriterScreen;
import io.github.cvrunmin.enhancedmachine.client.gui.UpgradeChipPanelScreen;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EnhancedMachine.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientInitializer {

    @SubscribeEvent
    public static void registerItemColor(ColorHandlerEvent.Item event) {
        event.getItemColors().register((stack, tintIndex) -> {
            if (tintIndex == 1) { //layer1: chip die
                return Upgrades.getUpgradeChipColor(stack);
            }
            return -1;
        }, EMItems.UPGRADE_CHIP.get());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event){
        ScreenManager.registerFactory(Initializer.chipWriterContainer.get(), ChipWriterScreen::new);
        ScreenManager.registerFactory(Initializer.chipPanelContainer.get(), UpgradeChipPanelScreen::new);
        ScreenManager.registerFactory(Initializer.MODDED_FURNACE.get(), FurnaceScreen::new);
        ScreenManager.registerFactory(Initializer.MODDED_BLAST_FURNACE.get(), BlastFurnaceScreen::new);
        ScreenManager.registerFactory(Initializer.MODDED_SMOKER.get(), SmokerScreen::new);
        ScreenManager.registerFactory(Initializer.MODDED_DISPENSER.get(), DispenserScreen::new);
        ScreenManager.registerFactory(Initializer.MODDED_BREWING_STAND.get(), BrewingStandScreen::new);
    }
}
