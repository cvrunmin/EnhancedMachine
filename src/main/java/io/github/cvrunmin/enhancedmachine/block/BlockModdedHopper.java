package io.github.cvrunmin.enhancedmachine.block;

public class BlockModdedHopper /*extends BlockHopper*/ {
//    public BlockModdedHopper() {
//        super();
//        setHardness(3.0F);
//        setResistance(8.0F);
//        setSoundType(SoundType.METAL);
//        setTranslationKey("hopper");
//    }
//
//    public TileEntity createNewTileEntity(World worldIn, int meta) {
//        return new TileEntityModdedHopper();
//    }
//
//    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        if (worldIn.isRemote) {
//            return true;
//        } else {
//            TileEntity tileentity = worldIn.getTileEntity(pos);
//
//            if (tileentity instanceof TileEntityHopper) {
////                playerIn.openGui(EnhancedMachine.instance, EMGuiHandler.MODDED_HOPPER, worldIn, pos.getX(), pos.getY(), pos.getZ());
//                playerIn.displayGUIChest((TileEntityHopper) tileentity);
//                playerIn.addStat(StatList.HOPPER_INSPECTED);
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
}
