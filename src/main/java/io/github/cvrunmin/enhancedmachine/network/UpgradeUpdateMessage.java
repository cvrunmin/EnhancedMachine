package io.github.cvrunmin.enhancedmachine.network;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.inventory.UpgradeChipPanelContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class UpgradeUpdateMessage {
    private BlockPos blockPos;
    private IUpgradeSlot capability;

    public UpgradeUpdateMessage(){}

    public UpgradeUpdateMessage(BlockPos blockPos, IUpgradeSlot capability)
    {
        this.blockPos = blockPos;
        this.capability = capability;
    }

    public void toBytes(PacketBuffer buffer){
        buffer.writeBlockPos(blockPos);
        CompoundNBT nbt = new CompoundNBT();
        nbt.put("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(capability, null));
        buffer.writeCompoundTag(nbt);
    }

    public static UpgradeUpdateMessage fromBytes(PacketBuffer buffer){
        BlockPos blockPos = buffer.readBlockPos();
        CompoundNBT nbt = buffer.readCompoundTag();
        IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();
        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, nbt.get("Upgrades"));
        return new UpgradeUpdateMessage(blockPos, upgradeSlot);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(blockPos);
        if(tileEntity != null){
            tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT).ifPresent(cap->{
                ctx.enqueueWork(()->{
                    cap.setUpgrades(capability.getUpgrades());
                    cap.markDirty();
                    if(Minecraft.getInstance().player.openContainer != null && Minecraft.getInstance().player.openContainer instanceof UpgradeChipPanelContainer){
                        ((UpgradeChipPanelContainer) Minecraft.getInstance().player.openContainer).refreshSlots();
                    }
                });
                ctx.setPacketHandled(true);
            });
        }

    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public IUpgradeSlot getCapability() {
        return capability;
    }
}
