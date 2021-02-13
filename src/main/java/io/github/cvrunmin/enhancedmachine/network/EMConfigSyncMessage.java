package io.github.cvrunmin.enhancedmachine.network;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.inventory.ChipWriterContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class EMConfigSyncMessage {

    private List<String> bannedUpgradeCache;

    public static EMConfigSyncMessage fromBytes(ByteBuf buf) {
        EMConfigSyncMessage msg = new EMConfigSyncMessage();
        PacketBuffer wrapper = new PacketBuffer(buf);
        int size = wrapper.readVarInt();
        if(size > 0) {
            msg.bannedUpgradeCache = Arrays.asList(wrapper.readString().split("\n").clone());
        }
        else{
            msg.bannedUpgradeCache = Collections.emptyList();
        }
        return msg;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer wrapper = new PacketBuffer(buf);
        wrapper.writeVarInt(EMConfig.getBannedUpgradesString().size());
        if(!EMConfig.getBannedUpgradesString().isEmpty())
        wrapper.writeString(String.join("\n", EMConfig.getBannedUpgradesString()));
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(()->{
            ServerConfigHelper.setSyncBannedUpgrade(bannedUpgradeCache);
        });
        ctx.setPacketHandled(true);
    }
}
