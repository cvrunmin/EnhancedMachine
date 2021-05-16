package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeSlotCapabilityProvider;
import io.github.cvrunmin.enhancedmachine.network.EMConfigSyncMessage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.PacketDistributor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EMEventListeners {
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getPlayer() instanceof ServerPlayerEntity) {
            EnhancedMachine.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), new EMConfigSyncMessage());
        }
    }

    @SubscribeEvent
    public static void onCapabilityAttach(AttachCapabilitiesEvent<TileEntity> event){
        TileEntity tileEntity = event.getObject();
        if(tileEntity instanceof ChestTileEntity){
            UpgradeSlotCapabilityProvider provider = new UpgradeSlotCapabilityProvider();
            provider.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null).ifPresent(c -> c.setHolder(tileEntity));
            event.addCapability(new ResourceLocation(EnhancedMachine.MODID, "upgrades"), provider);
        }
    }
}
