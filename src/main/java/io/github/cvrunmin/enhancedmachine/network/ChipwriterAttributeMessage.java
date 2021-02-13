package io.github.cvrunmin.enhancedmachine.network;

import io.github.cvrunmin.enhancedmachine.inventory.ChipWriterContainer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ChipwriterAttributeMessage {

    private int selectedUpgrade;

    private int mode;

    private int[] weights;

    public ChipwriterAttributeMessage() {
    }

    public ChipwriterAttributeMessage(int selectedUpgrade) {
        this.mode = 0;
        this.selectedUpgrade = selectedUpgrade;
    }

    public ChipwriterAttributeMessage(int[] weights) {
        this.mode = 1;
        this.weights = weights;
    }

    public int getSelectedUpgrade() {
        return selectedUpgrade;
    }

    public int getMode() {
        return mode;
    }

    public int[] getWeights() {
        return weights;
    }

    public static ChipwriterAttributeMessage fromBytes(ByteBuf buf) {
        ChipwriterAttributeMessage msg = new ChipwriterAttributeMessage();
        PacketBuffer wrapper = new PacketBuffer(buf);
        msg.mode = wrapper.readVarInt();
        if (msg.mode == 0) {
            msg.selectedUpgrade = wrapper.readVarInt();
        } else if (msg.mode == 1) {
            msg.weights = wrapper.readVarIntArray();
        }
        return msg;
    }

    public void toBytes(ByteBuf buf) {
        PacketBuffer wrapper = new PacketBuffer(buf);
        wrapper.writeVarInt(mode);
        if (mode == 0) {
            wrapper.writeVarInt(selectedUpgrade);
        } else if (mode == 1) {
            wrapper.writeVarIntArray(weights);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ServerPlayerEntity serverPlayer = ctx.getSender();
        if (serverPlayer.openContainer instanceof ChipWriterContainer) {
            ctx.enqueueWork(() -> {
                if (this.getMode() == 0) {
                    ((ChipWriterContainer) serverPlayer.openContainer).setSelectedUpgrade(this.getSelectedUpgrade());
                } else if (this.getMode() == 1) {
                    ((ChipWriterContainer) serverPlayer.openContainer).populateRiserWeightWrittenOutput(this.getWeights());
                }
            });
            ctx.setPacketHandled(true);
        }
    }
}
