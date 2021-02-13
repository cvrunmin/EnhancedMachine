package io.github.cvrunmin.enhancedmachine.network;

import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateLimitedMultiplierMessage {

    private boolean isLimited;

    public UpdateLimitedMultiplierMessage(){

    }

    public UpdateLimitedMultiplierMessage(boolean isLimited){
        this.isLimited = isLimited;
    }

    public void toBytes(PacketBuffer buf){
        buf.writeBoolean(isLimited);
    }

    public static UpdateLimitedMultiplierMessage fromBytes(PacketBuffer buf){
        return new UpdateLimitedMultiplierMessage(buf.readBoolean());
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        supplier.get().enqueueWork(()-> Minecraft.getInstance().world.getGameRules().get(EnhancedMachine.DO_LIMITED_CHIP_MULTIPLIER).set(isLimited, null));
    }

}
