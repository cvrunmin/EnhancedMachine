package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.inventory.ChipWriterContainer;
import io.github.cvrunmin.enhancedmachine.inventory.UpgradeChipPanelContainer;
import io.github.cvrunmin.enhancedmachine.item.RecipeDecomposeChip;
import io.github.cvrunmin.enhancedmachine.mixin.tileentity.CreateMenuInvokerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.container.*;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = EnhancedMachine.MODID)
public class Initializer {
//    public static final DeferredRegister<TileEntityType<?>> TILEENTITY = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, EnhancedMachine.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = new DeferredRegister<>(ForgeRegistries.CONTAINERS, EnhancedMachine.MODID);
    public static final DeferredRegister<IRecipeSerializer<?>> RECIPE_SERIALIZER = new DeferredRegister<>(ForgeRegistries.RECIPE_SERIALIZERS, EnhancedMachine.MODID);

    public static RegistryObject<ContainerType<ChipWriterContainer>> chipWriterContainer = CONTAINERS.register("chipwriter", ()->{
        return IForgeContainerType.create(((windowId, inv, data) -> new ChipWriterContainer(windowId, inv)));
    });
    public static RegistryObject<ContainerType<UpgradeChipPanelContainer>> chipPanelContainer = CONTAINERS.register("upgrade_panel", ()-> IForgeContainerType.create(((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = Minecraft.getInstance().world.getTileEntity(pos);
        IUpgradeSlot capability = tileEntity != null ? tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT).orElse(null) : null;
        return new UpgradeChipPanelContainer(windowId, inv, capability, pos);
    })));

    public static RegistryObject<ContainerType<FurnaceContainer>> MODDED_FURNACE = CONTAINERS.register("modded_furnace", () -> IForgeContainerType.create((windowId, playerInv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
        return (FurnaceContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
    }));

    public static RegistryObject<ContainerType<BlastFurnaceContainer>> MODDED_BLAST_FURNACE = CONTAINERS.register("modded_blast_furnace", () -> IForgeContainerType.create((windowId, playerInv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
        return (BlastFurnaceContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
    }));

    public static RegistryObject<ContainerType<SmokerContainer>> MODDED_SMOKER = CONTAINERS.register("modded_smoker", () -> IForgeContainerType.create((windowId, playerInv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
        return (SmokerContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
    }));

    public static RegistryObject<ContainerType<DispenserContainer>> MODDED_DISPENSER = CONTAINERS.register("modded_dispenser", () -> IForgeContainerType.create((windowId, playerInv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
        return (DispenserContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
    }));

    public static RegistryObject<ContainerType<BrewingStandContainer>> MODDED_BREWING_STAND = CONTAINERS.register("modded_brewing_stand", () -> IForgeContainerType.create((windowId, playerInv, data) -> {
        BlockPos pos = data.readBlockPos();
        TileEntity tileEntity = playerInv.player.world.getTileEntity(pos);
        return (BrewingStandContainer) ((CreateMenuInvokerMixin) tileEntity).callCreateMenu(windowId, playerInv);
    }));

    public static RegistryObject<RecipeDecomposeChip.Serializer> chipDecomposeRecipe = RECIPE_SERIALIZER.register("upgrade_chip_decompose", RecipeDecomposeChip.Serializer::new);

//    @SubscribeEvent
//    public static void registerBlock(RegistryEvent.Register<Block> event) {
//        event.getRegistry().register(new BlockModdedFurnace(false).setRegistryName("minecraft:furnace"));
//        event.getRegistry().register(new BlockModdedFurnace(true).setRegistryName("minecraft:lit_furnace"));
//        event.getRegistry().register(new BlockModdedDispenser().setRegistryName("minecraft:dispenser"));
//        event.getRegistry().register(new BlockModdedDropper().setRegistryName("minecraft:dropper"));
//        event.getRegistry().register(new BlockModdedBrewingStand().setRegistryName("minecraft:brewing_stand"));
//        event.getRegistry().register(new BlockModdedHopper().setRegistryName("minecraft:hopper"));
//        event.getRegistry().register(EMBlocks.CHIPWRITER);
//        GameRegistry.registerTileEntity(TileEntityModdedFurnace.class, new ResourceLocation("minecraft:furnace"));
//        GameRegistry.registerTileEntity(TileEntityModdedDispenser.class, new ResourceLocation("minecraft:dispenser"));
//        GameRegistry.registerTileEntity(TileEntityModdedDropper.class, new ResourceLocation("minecraft:dropper"));
//        GameRegistry.registerTileEntity(TileEntityModdedBrewingStand.class, new ResourceLocation("minecraft:brewing_stand"));
//        GameRegistry.registerTileEntity(TileEntityModdedHopper.class, new ResourceLocation("minecraft:hopper"));
//    }
//
//    @SubscribeEvent
//    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
//        if (event.getModID().equals(EnhancedMachine.MODID)) {
//            ConfigManager.sync(EnhancedMachine.MODID, Config.Type.INSTANCE);
//        }
//    }

}
