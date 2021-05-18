package io.github.cvrunmin.enhancedmachine.mixin.block;

import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(IForgeBlock.class)
public interface ForgeBlockMixin {
    //    @Inject(method = "Lnet/minecraft/block/Block;getExplosionResistance(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorldReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;Lnet/minecraft/world/Explosion;)F",
    //            at = @At("HEAD"),
    //            cancellable = true,
    //            remap = false)
    /**
    * @author
    */
    @Overwrite(remap = false)
    default float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion){
        float furnaceR = state.getBlock().getExplosionResistance();
        TileEntity tileEntity = world.getTileEntity(pos);
        final float[] outerVal = new float[]{furnaceR};
        if (tileEntity != null) {
            tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT).ifPresent(cap->{
                if (cap.hasUpgradeInstalled(Upgrades.OBSIDIAN_COATING)) {
                    float obsidianR = (float) (Blocks.OBSIDIAN.getExplosionResistance() * 0.8 * cap.getEffectMultiplier(Upgrades.OBSIDIAN_COATING));
                    float max = Math.max(obsidianR, furnaceR);
                    outerVal[0] = max;
//                    info.setReturnValue(max);
                }
            });
        }
        return outerVal[0];
    }
}
