package io.github.cvrunmin.enhancedmachine.mixin.block;

import io.github.cvrunmin.enhancedmachine.DispenseHarvestBlockBehavior;
import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.block.ModdedDispenserBlock;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.ProxyBlockSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

@Mixin(DispenserBlock.class)
public abstract class DispenserBlockMixin {



//    @Inject(method = "<clinit>", at = @At("RETURN"))
//    private static void injectClassInit(CallbackInfo info){
//
//    }

    @Shadow protected abstract IDispenseItemBehavior getBehavior(ItemStack stack);

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

    @Redirect(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;getBehavior(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/dispenser/IDispenseItemBehavior;"))
//    @Inject(method = "dispense", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/DispenserBlock;getBehavior(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/dispenser/IDispenseItemBehavior;", shift = At.Shift.BY, by = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
    private IDispenseItemBehavior hijackDispenseBehavior(DispenserBlock dispenserBlock, ItemStack stack, World world, BlockPos pos){
        ProxyBlockSource proxyblocksource = new ProxyBlockSource(world, pos);
        DispenserTileEntity dispensertileentity = proxyblocksource.getBlockTileEntity();
        LazyOptional<IUpgradeSlot> optional = dispensertileentity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT);
        if(optional.isPresent()){
            IUpgradeSlot upgradeSlot = optional.orElseThrow(NullPointerException::new);
            UpgradeNode upgradeNode = upgradeSlot.getFirstFoundUpgrade(Upgrades.DRILL);
            if(upgradeNode != null && !EMConfig.isBanned(upgradeNode.getUpgrade())){
                if (ModdedDispenserBlock.TOOL_ON_USE_BEHAVIOR.containsKey(stack.getItem())) {
                    return ModdedDispenserBlock.TOOL_ON_USE_BEHAVIOR.get(stack.getItem());
                }
            }
        }
        return getBehavior(stack);
    }
}
