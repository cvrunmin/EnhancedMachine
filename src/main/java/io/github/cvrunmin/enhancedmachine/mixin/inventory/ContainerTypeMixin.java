package io.github.cvrunmin.enhancedmachine.mixin.inventory;

import io.github.cvrunmin.enhancedmachine.mixin.tileentity.CreateMenuInvokerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.network.IContainerFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ContainerType.class)
public abstract class ContainerTypeMixin implements IForgeContainerType {
//    @Shadow
    private static <T extends Container> ContainerType<T> register(String key, ContainerType.IFactory<T> factory) {
        throw new NotImplementedException("Shall not being call this! Is mixin failed to shadow this method?");
    }

//    @Shadow
//    @Final
//    @Mutable
//    public static ContainerType<FurnaceContainer> FURNACE = register("furnace", (IContainerFactory<FurnaceContainer>)(windowId, playerInv, data) -> {
//        BlockPos pos = data.readBlockPos();
//        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
//        return (FurnaceContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
//    });

//    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
//            target = "net/minecraft/inventory/container/ContainerType.register(Ljava/lang/String;Lnet/minecraft/inventory/container/ContainerType$IFactory;)Lnet/minecraft/inventory/container/ContainerType;"), index = 1)
    private static ContainerType.IFactory modifyFurnaceFactory(String key, ContainerType.IFactory old){
        switch (key) {
            case "furnace":
                return (IContainerFactory<FurnaceContainer>) (windowId, playerInv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
                    return (FurnaceContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
                };
            case "blast_furnace":
                return (IContainerFactory<BlastFurnaceContainer>) (windowId, playerInv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
                    return (BlastFurnaceContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
                };
            case "smoker":
                return (IContainerFactory<SmokerContainer>) (windowId, playerInv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
                    return (SmokerContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
                };
            case "generic_3x3":
                return (IContainerFactory<DispenserContainer>) (windowId, playerInv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
                    return (DispenserContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
                };
            case "brewing_stand":
                return (IContainerFactory<BrewingStandContainer>) (windowId, playerInv, data) -> {
                    BlockPos pos = data.readBlockPos();
                    TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
                    return (BrewingStandContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
                };
        }
        return old;
    }
}
