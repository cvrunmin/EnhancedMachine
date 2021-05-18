package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;

public class DispenseHarvestBlockBehavior extends OptionalDispenseBehavior {
    @Override
    public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        World world = source.getWorld();
        setSuccessful(true);
        DispenserTileEntity te = source.getBlockTileEntity();
        LazyOptional<IUpgradeSlot> optional = te.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT);
        if(!optional.isPresent()){
            setSuccessful(false);
            return stack;
        }
        IUpgradeSlot capability = optional.orElseThrow(NullPointerException::new);
        UpgradeNode node = capability.getFirstFoundUpgrade(Upgrades.DRILL);
        if(EMConfig.isBanned(node.getUpgrade())) return stack;
        int allowedDistance = (int) Math.max(1, Upgrades.DRILL.getDrillingDistance(node.getUpgrade().getLevel()) * capability.getEffectMultiplier(node));

        Direction front = source.getBlockState().get(DispenserBlock.FACING);
        BlockPos backPos = source.getBlockPos().offset(front.getOpposite());
        BlockPos frontPos = source.getBlockPos();
        int d = 0;
        do {
            frontPos = frontPos.offset(front);
            d++;
        } while (d < allowedDistance && world.isAirBlock(frontPos));
        if (d >= allowedDistance && world.isAirBlock(frontPos)) {
            setSuccessful(false);
            return stack;
        }
        BlockState blockstate = world.getBlockState(frontPos);
        if (blockstate.getBlockHardness(world, frontPos) != -1
                && ForgeHooks.isToolEffective(world, frontPos, stack)) {
            world.destroyBlock(frontPos, false);
//            Block.spawnDrops(blockstate, world, frontPos, null, null, stack);

            if (world instanceof ServerWorld) {
                Block.getDrops(blockstate, (ServerWorld)world, frontPos, null, null, stack).forEach((p_220057_2_) -> {
                    Block.spawnAsEntity(world, backPos, p_220057_2_);
                });
            }

//            blockstate.spawnAdditionalDrops(world, frontPos, stack);
            if (stack.attemptDamageItem(1, world.rand, null)) {
                stack.shrink(1);
            }
        } else {
            setSuccessful(false);
        }
        return stack;
    }
}
