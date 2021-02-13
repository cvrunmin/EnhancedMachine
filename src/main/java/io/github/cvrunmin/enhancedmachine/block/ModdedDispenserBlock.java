package io.github.cvrunmin.enhancedmachine.block;

import io.github.cvrunmin.enhancedmachine.DispenseHarvestBlockBehavior;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;

public class ModdedDispenserBlock /*extends BlockDispenser*/ {
    public static final Map<Item, IDispenseItemBehavior> TOOL_ON_USE_BEHAVIOR = new HashMap<>();
    public static final Map<Item, IDispenseItemBehavior> OTHER_BEHAVIOR = new HashMap<>();

    static {
        registerBehaviors();
    }
//
//    public BlockModdedDispenser() {
//        super();
//        this.setHardness(3.5F);
//        setSoundType(SoundType.STONE);
//        setTranslationKey("dispenser");
//    }
//
    public static void registerBehaviors() {
        DispenseHarvestBlockBehavior behavior = new DispenseHarvestBlockBehavior();
        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_PICKAXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_PICKAXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_PICKAXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_PICKAXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_PICKAXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_AXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_AXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_AXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_AXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_AXE, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.DIAMOND_SHOVEL, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.GOLDEN_SHOVEL, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.IRON_SHOVEL, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.STONE_SHOVEL, behavior);
        TOOL_ON_USE_BEHAVIOR.put(Items.WOODEN_SHOVEL, behavior);
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
//
//    @Override
//    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        if (worldIn.isRemote) {
//            return true;
//        } else {
//            TileEntity tileentity = worldIn.getTileEntity(pos);
//
//            if (tileentity instanceof TileEntityModdedDispenser) {
//                playerIn.openGui(EnhancedMachine.instance, EMGuiHandler.MODDED_DISPENSER, worldIn, pos.getX(), pos.getY(), pos.getZ());
//
//                if (tileentity instanceof TileEntityModdedDropper) {
//                    playerIn.addStat(StatList.DROPPER_INSPECTED);
//                } else {
//                    playerIn.addStat(StatList.DISPENSER_INSPECTED);
//                }
//            }
//
//            return true;
//        }
//    }
//
//    @Override
//    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
//        float originR = super.getExplosionResistance(world, pos, exploder, explosion);
//        TileEntity tileEntity = world.getTileEntity(pos);
//        if (tileEntity != null && tileEntity.hasCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null)) {
//            IUpgradeSlot capability = tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null);
//            if (capability.hasUpgradeInstalled(Upgrades.OBSIDIAN_COATING)) {
//                float obsidianR = (float) (Blocks.OBSIDIAN.getExplosionResistance(world, pos, exploder, explosion) * 0.8 * capability.getEffectMultiplier(Upgrades.OBSIDIAN_COATING));
//                return Math.max(obsidianR, originR);
//            }
//        }
//        return originR;
//    }
//
//    @Override
//    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
//        TileEntity tileEntity = worldIn.getTileEntity(pos);
//        if (tileEntity != null && tileEntity.hasCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null)) {
//            for (UpgradeDetail upgrade : tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null).getUpgrades().pickleUpgrades()) {
//                if (!Upgrades.EMPTY.equals(upgrade.getType())) {
//                    ItemStack itemStack = Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP), upgrade);
//                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack);
//                }
//            }
//        }
//        super.breakBlock(worldIn, pos, state);
//    }
//
//    @Override
//    public TileEntity createNewTileEntity(World worldIn, int meta) {
//        return new TileEntityModdedDispenser();
//    }
//
//    protected void dispense(World worldIn, BlockPos pos) {
//        BlockSourceImpl blocksourceimpl = new BlockSourceImpl(worldIn, pos);
//        TileEntityDispenser tileentitydispenser = blocksourceimpl.getBlockTileEntity();
//
//        if (tileentitydispenser != null) {
//            int i = tileentitydispenser.getDispenseSlot();
//
//            if (i < 0) {
//                worldIn.playEvent(1001, pos, 0);
//            } else {
//                ItemStack itemstack = tileentitydispenser.getStackInSlot(i);
//                IBehaviorDispenseItem ibehaviordispenseitem = this.getBehavior(itemstack, tileentitydispenser);
//
//                if (ibehaviordispenseitem != IBehaviorDispenseItem.DEFAULT_BEHAVIOR) {
//                    tileentitydispenser.setInventorySlotContents(i, ibehaviordispenseitem.dispense(blocksourceimpl, itemstack));
//                }
//            }
//        }
//    }
//
//    protected IBehaviorDispenseItem getBehavior(ItemStack stack, TileEntityDispenser te) {
//        if (te.hasCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null)) {
//            if (te.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null).hasUpgradeInstalled(Upgrades.DRILL)) {
//                if (TOOL_ON_USE_BEHAVIOR.containsKey(stack.getItem())) {
//                    return TOOL_ON_USE_BEHAVIOR.get(stack.getItem());
//                }
//            }
//        }
//        if (OTHER_BEHAVIOR.containsKey(stack.getItem())) {
//            return OTHER_BEHAVIOR.get(stack.getItem());
//        }
//        return this.getBehavior(stack);
//    }
//
//    private static class BehaviorDispenseHarvestBlock extends Bootstrap.BehaviorDispenseOptional {
//        @Override
//        protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
//            World world = source.getWorld();
//            this.successful = true;
//
//            TileEntityModdedDispenser te = source.getBlockTileEntity();
//            IUpgradeSlot capability = te.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null);
//            UpgradeNode node = capability.getFirstFoundUpgrade(Upgrades.DRILL);
//            int allowedDistance = (int) Math.max(1, Upgrades.DRILL.getDrillingDistance(node.getUpgrade().getLevel()) * capability.getEffectMultiplier(node));
//
//            EnumFacing front = source.getBlockState().getValue(BlockDispenser.FACING);
//            BlockPos backPos = source.getBlockPos().offset(front.getOpposite());
//            BlockPos frontPos = source.getBlockPos();
//            int d = 0;
//            do {
//                frontPos = frontPos.offset(front);
//                d++;
//            } while (d < allowedDistance && world.isAirBlock(frontPos));
//            if (d >= allowedDistance && world.isAirBlock(frontPos)) {
//                successful = false;
//                return stack;
//            }
//            IBlockState blockState = world.getBlockState(frontPos);
//            if (blockState.getBlock().getBlockHardness(blockState, world, frontPos) != -1
//                    && ForgeHooks.canToolHarvestBlock(world, frontPos, stack)) {
//                world.destroyBlock(frontPos, false);
//                if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) > 0) {
//                    Item item = Item.getItemFromBlock(blockState.getBlock());
//                    int i = 0;
//
//                    if (item.getHasSubtypes()) {
//                        i = blockState.getBlock().getMetaFromState(blockState);
//                    }
//                    ItemStack itemstack = new ItemStack(item, 1, i);
//
//                    if (!itemstack.isEmpty()) {
//                        spawnAsEntity(world, backPos, itemstack);
//                    }
//                } else {
//                    int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack);
//                    blockState.getBlock().dropBlockAsItem(world, backPos, blockState, i);
//                }
//                if (stack.attemptDamageItem(1, world.rand, null)) {
//                    stack.shrink(1);
//                }
//            } else {
//                successful = false;
//            }
//            return stack;
//        }
//    }
}
