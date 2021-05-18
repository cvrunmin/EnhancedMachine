package io.github.cvrunmin.enhancedmachine;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.ILootFunction;
import net.minecraft.loot.functions.SetNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = EnhancedMachine.MODID)
public class LootTableInjector {

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        switch (event.getName().toString()) {
            case "minecraft:chests/abandoned_mineshaft": {
                LootPool pool = LootPool.builder()
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(2)
                                .quality(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.RISER, 4)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(3)
                                .quality(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FUEL_MASTERY, 9)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(3)
                                .quality(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.TIME_ACCELERATION, 9)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(1)
                                .quality(2)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FUEL_MASTERY, 10)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(1)
                                .quality(2)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.TIME_ACCELERATION, 10)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(2)
                                .quality(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.HYPERTHREAD, 5)))))
                        .addEntry(EmptyLootEntry.func_216167_a().weight(4).quality(-1))
                        .rolls(new RandomValueRange(1,2))
                        .bonusRolls(0,3)
                        .name("upgrade_chip")
                        .build();
                event.getTable().addPool(pool);
                pool = LootPool.builder()
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(4)
                        .quality(1)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FREE_ENERGY, 1)))))
                        .addEntry(EmptyLootEntry.func_216167_a().weight(8).quality(-1))
                        .rolls(new ConstantRange(1)).bonusRolls(0,0).name("upgrade_chip_rare").build();
                event.getTable().addPool(pool);
                break;
            }
            case "minecraft:chests/woodland_mansion":
            case "minecraft:chests/nether_bridge": {
                LootPool pool = LootPool.builder()
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(8)
                                .quality(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FREE_ENERGY, 1)))))
                        .addEntry(EmptyLootEntry.func_216167_a().weight(2).quality(-1))
                        .rolls(new ConstantRange(1)).build();
                event.getTable().addPool(pool);
                break;
            }
            case "minecraft:chests/end_city_treasure": {
                LootPool pool = LootPool.builder()
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                                .weight(1)
                                .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FREE_ENERGY, 1)))))
                        .rolls(new ConstantRange(1)).build();
                event.getTable().addPool(pool);
                break;
            }
            case "minecraft:chests/jungle_temple":
            case "minecraft:chests/desert_pyramid":
            case "minecraft:chests/igloo_chest":
            case "minecraft:chests/stronghold_library": {
                LootPool pool = LootPool.builder()
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(2)
                        .quality(1)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.RISER, 4)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(3)
                        .quality(1)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FUEL_MASTERY, 9)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(3)
                        .quality(1)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.TIME_ACCELERATION, 9)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(1)
                        .quality(3)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.FUEL_MASTERY, 10)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(1)
                        .quality(3)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.TIME_ACCELERATION, 10)))))
                        .addEntry(ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get())
                        .weight(2)
                        .quality(1)
                        .acceptFunction(SetNBT.builder(Upgrades.writeUpgradeToNBT(new UpgradeDetail(Upgrades.HYPERTHREAD, 5)))))
                        .rolls(new RandomValueRange(1,3))
                        .bonusRolls(0, 3)
                        .build();
                event.getTable().addPool(pool);
                break;
            }
            case "minecraft:gameplay/fishing/junk": {
                LootPool pool = event.getTable().getPool("main");
                LootEntry entry = ItemLootEntry.builder(EMItems.UPGRADE_CHIP.get()).weight(5).quality(-1)
                        .acceptFunction(CustomSetNBT.builder())
                        .build();
                Field field = ObfuscationReflectionHelper.findField(LootPool.class, "field_186453_a");
                field.setAccessible(true);
                try {
                    List<LootEntry> entries = (List<LootEntry>) field.get(pool);
                    entries.add(entry);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class CustomSetNBT extends LootFunction{

        public static LootFunctionType CUSTOM_SET_NBT = Registry.register(Registry.LOOT_FUNCTION_TYPE, new ResourceLocation(EnhancedMachine.MODID, "custom_set_nbt"), new LootFunctionType(new ILootSerializer<ILootFunction>() {
            @Override
            public void serialize(JsonObject p_230424_1_, ILootFunction p_230424_2_, JsonSerializationContext p_230424_3_) {

            }

            @Override
            public ILootFunction deserialize(JsonObject p_230423_1_, JsonDeserializationContext p_230423_2_) {
                return builder().build();
            }
        }));

        public CustomSetNBT(ILootCondition[] conditionsIn) {
            super(conditionsIn);
        }

        @Override
        protected ItemStack doApply(ItemStack stack, LootContext context) {
            return Upgrades.writeUpgrade(stack, new UpgradeDetail(Upgrades.DAMAGED, context.getRandom().nextInt(10) + 1));
        }

        @Override
        public LootFunctionType getFunctionType() {
            return CUSTOM_SET_NBT;
        }

        static LootFunction.Builder<?> builder() {
            return builder(CustomSetNBT::new);
        }
    }
}
