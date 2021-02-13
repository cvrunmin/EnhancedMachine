package io.github.cvrunmin.enhancedmachine.mixin.block;

import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(BrewingStandBlock.class)
public class BrewingStandBlockMixin {

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

    @Redirect(method = "onBlockActivated", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;openContainer(Lnet/minecraft/inventory/container/INamedContainerProvider;)Ljava/util/OptionalInt;"))
    public OptionalInt openContainerWithForge(PlayerEntity player, INamedContainerProvider containerProvider){
        if(player instanceof ServerPlayerEntity) {
            NetworkHooks.openGui(((ServerPlayerEntity) player), containerProvider, ((TileEntity) containerProvider).getPos());
        }
        return OptionalInt.empty();
    }
}
