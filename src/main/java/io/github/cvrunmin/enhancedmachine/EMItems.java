package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.item.ItemUpgradeChip;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EMItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnhancedMachine.MODID);

    private static RegistryObject<Item> chipWriterItem = ITEMS.register("chipwriter", ()->new BlockItem(EMBlocks.CHIPWRITER.get(), new Item.Properties().group(EnhancedMachine.UPGRADE_CHIPS)));
    public static RegistryObject<ItemUpgradeChip> UPGRADE_CHIP = ITEMS.register("upgrade_chip",  ItemUpgradeChip::new);
}
