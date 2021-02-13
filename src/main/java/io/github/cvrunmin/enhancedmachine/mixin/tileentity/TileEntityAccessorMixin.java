package io.github.cvrunmin.enhancedmachine.mixin.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TileEntity.class)
public interface TileEntityAccessorMixin {
    @Invoker("writeInternal")
    CompoundNBT invokeWriteInternal(CompoundNBT compoundNBT);

//    @Accessor("pos")
//    BlockPos getPos();
}
