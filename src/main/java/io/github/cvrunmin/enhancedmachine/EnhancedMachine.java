package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradesCollection;
import io.github.cvrunmin.enhancedmachine.network.*;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameRules;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(EnhancedMachine.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EnhancedMachine {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "enhancedmachine";
    public static final String NAME = "Enhanced Machine";
    public static final String VERSION = "1.0";

    public static final ItemGroup UPGRADE_CHIPS = new ItemGroup("upgrade_chips") {
        @Override
        public ItemStack createIcon() {
            return EMItems.UPGRADE_CHIP.get().getDefaultInstance();
        }
    };
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "channel"), () -> VERSION, ver -> ver.equals(VERSION), ver -> ver.equals(VERSION));
    public static GameRules.RuleKey<GameRules.BooleanValue> DO_LIMITED_CHIP_MULTIPLIER;

    public EnhancedMachine(){
        Upgrades.registerALl();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EMConfig.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, EMConfig.SERVER_CONFIG);
//        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parent) -> new EnhancedMachineConfigScreen(parent));
        EMBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        EMItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Initializer.RECIPE_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
        Initializer.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(IUpgradeSlot.class, new Capability.IStorage<IUpgradeSlot>() {
            @Override
            public INBT writeNBT(Capability<IUpgradeSlot> capability, IUpgradeSlot instance, Direction side) {
                ListNBT nbtTagList = new ListNBT();
                List<UpgradesCollection.UpgradeNodeWrapper> details = instance.getUpgrades().flattenNodes();
                for (UpgradesCollection.UpgradeNodeWrapper wrapper : details) {
                    CompoundNBT compound = new CompoundNBT();
                    UpgradeDetail upgrade = wrapper.getNode().getUpgrade();
                    compound.putString("id", upgrade.getType().getUpgradeName());
                    compound.putInt("level", upgrade.getLevel());
                    if (!upgrade.getExtras().isEmpty()) {
                        compound.put("extra", upgrade.getExtras());
                    }
                    compound.putInt("eid", wrapper.getEid());
                    if(wrapper.getPeid() != -1){
                        compound.putInt("peid", wrapper.getPeid());
                        compound.putInt("cid", wrapper.getCid());
                    }
                    nbtTagList.add(compound);
                }
                return nbtTagList;
            }

            @Override
            public void readNBT(Capability<IUpgradeSlot> capability, IUpgradeSlot instance, Direction side, INBT nbt) {
                if (nbt instanceof ListNBT) {
                    ListNBT listNBT = (ListNBT) nbt;
                    boolean oldMode = !listNBT.getCompound(0).contains("eid");
                    if(oldMode){
                        List<UpgradeDetail> details = new ArrayList<>();
                        for (int i = 0; i < listNBT.size(); i++) {
                            CompoundNBT compound = listNBT.getCompound(i);
                            String upgradeId = compound.getString("id");
                            Upgrade upgrade = Upgrades.getUpgradeFromId(upgradeId);
                            if (upgrade == null) {
                                continue;
                            }
                            int level = compound.getInt("level");
                            UpgradeDetail detail = new UpgradeDetail(upgrade, level);
                            CompoundNBT sub = compound.getCompound("extra");
                            detail.getExtras().merge(sub);
                            details.add(detail);
                        }
                        instance.getUpgrades().fromPickle(details);
                    }
                    else{
                        instance.getUpgrades().handleNBT(listNBT);
                    }
                }
            }
        }, UpgradeSlot::new);
        CHANNEL.registerMessage(0, FocusedUpgradeChipChangeMessage.class, FocusedUpgradeChipChangeMessage::toBytes, FocusedUpgradeChipChangeMessage::fromBytes, FocusedUpgradeChipChangeMessage::handle);
        CHANNEL.registerMessage(1, ChipwriterAttributeMessage.class, ChipwriterAttributeMessage::toBytes, ChipwriterAttributeMessage::fromBytes, ChipwriterAttributeMessage::handle);
        CHANNEL.registerMessage(2, UpgradeUpdateMessage.class, UpgradeUpdateMessage::toBytes, UpgradeUpdateMessage::fromBytes, UpgradeUpdateMessage::handle);
        CHANNEL.registerMessage(3, UpdateLimitedMultiplierMessage.class, UpdateLimitedMultiplierMessage::toBytes, UpdateLimitedMultiplierMessage::fromBytes, UpdateLimitedMultiplierMessage::handle);
        CHANNEL.registerMessage(4, EMConfigSyncMessage.class, EMConfigSyncMessage::toBytes, EMConfigSyncMessage::fromBytes, EMConfigSyncMessage::handle);
//        DO_LIMITED_CHIP_MULTIPLIER = GameRules.register("doLimitedChipMultiplier", GameRuleTypeInvoker.createRuleType(BoolArgumentType::bool, type -> new GameRules.BooleanValue(type, true), (minecraftServer, value) -> {
//            CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateLimitedMultiplierMessage(((GameRules.BooleanValue) value).get()));
//        }));
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event){
        event.getGenerator().addProvider(new EMAdvancementProvider(event.getGenerator()));
    }

//    @Mod.EventHandler
//    public void init(FMLInitializationEvent event) {
//        // intended to use vanilla data fixers
//        CompoundDataFixer fixer = FMLCommonHandler.instance().getDataFixer();
//        TileEntityModdedBrewingStand.registerFixes(fixer);
//        TileEntityModdedFurnace.registerFixes(fixer);
//        TileEntityModdedDispenser.registerFixes(fixer);
//        TileEntityModdedDropper.registerFixes(fixer);
//        TileEntityModdedHopper.registerFixes(fixer);
//        proxy.init(event);
//    }
//
//    @Mod.EventHandler
//    public void postInit(FMLPostInitializationEvent event) {
//        proxy.postInit(event);
//    }
}
