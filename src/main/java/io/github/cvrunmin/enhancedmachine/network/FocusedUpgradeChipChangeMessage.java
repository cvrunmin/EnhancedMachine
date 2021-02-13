package io.github.cvrunmin.enhancedmachine.network;

import io.github.cvrunmin.enhancedmachine.inventory.UpgradeChipPanelContainer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FocusedUpgradeChipChangeMessage {

    private int focusSlot;

    public FocusedUpgradeChipChangeMessage() {
    }

    public FocusedUpgradeChipChangeMessage(int focusSlot) {
        this.focusSlot = focusSlot;
    }

    public int getFocusSlot() {
        return focusSlot;
    }

    public static FocusedUpgradeChipChangeMessage fromBytes(PacketBuffer buf) {
        FocusedUpgradeChipChangeMessage msg = new FocusedUpgradeChipChangeMessage();
        msg.focusSlot = buf.readInt();
        return msg;
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeInt(focusSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> supplierCtx) {
        NetworkEvent.Context ctx = supplierCtx.get();
        ServerPlayerEntity serverPlayer = ctx.getSender();
        if (serverPlayer.openContainer instanceof UpgradeChipPanelContainer) {
            ctx.enqueueWork(() -> {
                ((UpgradeChipPanelContainer) serverPlayer.openContainer).focusSlot(this.getFocusSlot());
            });
            ctx.setPacketHandled(true);
        }
    }
}
