package io.github.cvrunmin.enhancedmachine.block;

import io.github.cvrunmin.enhancedmachine.DispenseHarvestBlockBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

public class ModdedDispenserBlock /*extends BlockDispenser*/ {
    public static final Map<Item, IDispenseItemBehavior> TOOL_ON_USE_BEHAVIOR = new HashMap<>();
    public static final Map<Item, IDispenseItemBehavior> OTHER_BEHAVIOR = new HashMap<>();

    static {
        registerBehaviors();
    }

    public static void registerBehaviors() {
        DispenseHarvestBlockBehavior behavior = new DispenseHarvestBlockBehavior();
        GameRegistry.findRegistry(Item.class).getValues();
        ForgeRegistries.ITEMS.getValues().stream()
                .filter(item -> !item.getToolTypes(new ItemStack(item)).isEmpty())
                .forEach(item -> TOOL_ON_USE_BEHAVIOR.put(item, behavior));
//        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_PICKAXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_PICKAXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_PICKAXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_PICKAXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_PICKAXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_AXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_AXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_AXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_AXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_AXE, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_SHOVEL, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_SHOVEL, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_SHOVEL, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_SHOVEL, behavior);
//        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_SHOVEL, behavior);
//        OTHER_BEHAVIOR.put(Item.getItemFromBlock(Blocks.TNT), new BehaviorDefaultDispenseItem() {
//            /**
//             * Dispense the specified stack, play the dispense sound and spawn particles.
//             */
//            protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
//                World world = source.getWorld();
//                EnumFacing facing = source.getBlockState().getValue(BlockDispenser.FACING);
//                BlockPos blockpos = source.getBlockPos().offset(facing);
//                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockpos.getX() + 0.5D, blockpos.getY(), (double) blockpos.getZ() + 0.5D, (EntityLivingBase) null);
//                entitytntprimed.motionX += facing.getXOffset() * 0.9 + world.rand.nextFloat() * 2 - 1;
//                entitytntprimed.motionY += 0.4;
//                entitytntprimed.motionZ += facing.getZOffset() * 1 + 0.5;
//                world.spawnEntity(entitytntprimed);
//                world.playSound(null, entitytntprimed.posX, entitytntprimed.posY, entitytntprimed.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
//                stack.shrink(1);
//                return stack;
//            }
//        });
    }
}
