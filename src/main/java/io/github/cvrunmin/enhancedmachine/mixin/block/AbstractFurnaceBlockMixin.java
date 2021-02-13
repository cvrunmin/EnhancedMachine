package io.github.cvrunmin.enhancedmachine.mixin.block;

import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(AbstractFurnaceBlock.class)
public abstract class AbstractFurnaceBlockMixin {

    @Inject(method = "onReplaced", at = @At(value = "HEAD"))
    public void onBlockReplacedInject(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving, CallbackInfo info){
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity != null) {
                tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT).ifPresent(capability->{
                    for (UpgradeDetail upgrade : capability.getUpgrades().pickleUpgrades()) {
                        if (!Upgrades.EMPTY.equals(upgrade.getType())) {
                            ItemStack itemStack = Upgrades.writeUpgrade(new ItemStack(EMItems.UPGRADE_CHIP.get()), upgrade);
                            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                        }
                    }
                });
            }
        }
    }
}
