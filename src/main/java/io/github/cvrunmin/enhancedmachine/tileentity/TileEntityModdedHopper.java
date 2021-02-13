package io.github.cvrunmin.enhancedmachine.tileentity;

public class TileEntityModdedHopper /*extends TileEntityHopper*/ {
//    IUpgradeSlot upgradeSlot = CapabilityUpgradeSlot.UPGRADE_SLOT.getDefaultInstance();
//
//    public TileEntityModdedHopper() {
//        super();
//        upgradeSlot.setHolder(this);
//    }
//
//    public static void registerFixes(CompoundDataFixer fixer) {
//        fixer.registerWalker(FixTypes.BLOCK_ENTITY, new ItemStackDataLists(TileEntityModdedHopper.class, "Items"));
//    }
//
//    @Override
//    public void readFromNBT(NBTTagCompound compound) {
//        super.readFromNBT(compound);
////        this.brewingItemStacks = NonNullList.<ItemStack>withSize(this.getSizeInventory(), ItemStack.EMPTY);
////        ItemStackHelper.loadAllItems(compound, this.brewingItemStacks);
////        this.brewTime = compound.getShort("BrewTime");
////        this.fuel = compound.getByte("Fuel");
//        CapabilityUpgradeSlot.UPGRADE_SLOT.readNBT(upgradeSlot, null, compound.getTagList("Upgrades", 10));
//    }
//
//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
//        NBTTagCompound compound1 = super.writeToNBT(compound);
////        compound1.setShort("BrewTime", (short)this.brewTime);
////        ItemStackHelper.saveAllItems(compound1, this.brewingItemStacks);
////        compound1.setByte("Fuel", (byte)this.fuel);
//        compound1.setTag("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
//        return compound1;
//    }
//
//    @Override
//    public NBTTagCompound getUpdateTag() {
//        NBTTagCompound compound = super.getUpdateTag();
//        compound.setTag("Upgrades", CapabilityUpgradeSlot.UPGRADE_SLOT.writeNBT(upgradeSlot, null));
//        return compound;
//    }
//
//    @Nullable
//    @Override
//    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
//        if (capability == CapabilityUpgradeSlot.UPGRADE_SLOT) {
//            return (T) upgradeSlot;
//        }
//        return super.getCapability(capability, facing);
//    }
//
//    @Override
//    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
//        return capability == CapabilityUpgradeSlot.UPGRADE_SLOT || super.hasCapability(capability, facing);
//    }
//
//    @Nullable
//    @Override
//    public SPacketUpdateTileEntity getUpdatePacket() {
//        return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
//    }
//
//    @Override
//    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
//        super.onDataPacket(net, pkt);
//        handleUpdateTag(pkt.getNbtCompound());
//    }
//
//
//    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
//        return (oldState.getBlock() != newSate.getBlock());
//    }
}
