package io.github.cvrunmin.enhancedmachine.item;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import io.github.cvrunmin.enhancedmachine.cap.CapabilityUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.IUpgradeSlot;
import io.github.cvrunmin.enhancedmachine.cap.UpgradeNode;
import io.github.cvrunmin.enhancedmachine.inventory.UpgradeChipPanelContainer;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;

public class ItemUpgradeChip extends Item {
    public ItemUpgradeChip() {
        super(new Properties().maxStackSize(1).group(EnhancedMachine.UPGRADE_CHIPS));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public ItemStack getDefaultInstance() {
        return Upgrades.writeUpgrade(super.getDefaultInstance(), new UpgradeDetail(Upgrades.INTACT, 1));
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return getTranslationKey() + "." + Upgrades.getUpgradeFromItemStack(stack).getType().getUpgradeName();
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null) {
            LazyOptional<IUpgradeSlot> capability1 = tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, null);
            if (capability1.isPresent()) {
                IUpgradeSlot capability = capability1.orElseThrow(NullPointerException::new);
                return capability.getUpgrades().getRoot() != null && capability.getUpgrades().getRoot().getUpgrade() != null && !capability.getUpgrades().getRoot().getUpgrade().getType().equals(Upgrades.EMPTY);
            }
        }
        return super.doesSneakBypassUse(stack, world, pos, player);
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        TileEntity tileEntity = context.getWorld().getTileEntity(context.getPos());
        if (!context.getWorld().isRemote && tileEntity != null) {
            LazyOptional<IUpgradeSlot> capability1 = tileEntity.getCapability(CapabilityUpgradeSlot.UPGRADE_SLOT, context.getFace());
            if (capability1.isPresent()) {
                if (!context.getPlayer().isSneaking()) {
                    IUpgradeSlot capability = capability1.orElse(null);
                    if (!EMConfig.ALWAYS_SHOW_UPGRADE_PANEL.get() && (capability.getUpgrades().getRoot() == null || capability.getUpgrades().getRoot().getUpgrade() == null || capability.getUpgrades().getRoot().getUpgrade().getType().equals(Upgrades.EMPTY))) {
                        UpgradeDetail detail = Upgrades.getUpgradeFromItemStack(stack);
                        detail.getExtras().putInt("Slot", 0);
                        capability.getUpgrades().setRoot(new UpgradeNode(detail));
                        capability.markDirty();
                        stack.shrink(1);
                        return ActionResultType.SUCCESS;
                    } else {
                        NetworkHooks.openGui(((ServerPlayerEntity) context.getPlayer()), new INamedContainerProvider() {
                            @Override
                            public ITextComponent getDisplayName() {
                                return new TranslationTextComponent("upgrade.title");
                            }

                            @Override
                            public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                                return new UpgradeChipPanelContainer(syncId, playerInventory, capability, context.getPos());
                            }
                        }, packetBuffer -> packetBuffer.writeBlockPos(context.getPos()));
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    @Override
    public void addInformation(ItemStack stack,  World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Upgrades.writeUpgradeTooltip(stack, worldIn, tooltip);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.isInGroup(group)) {
            for (Upgrade upgrade : Upgrades.registeredUpgrades()) {
                if (upgrade == Upgrades.EMPTY)
                    continue;
                for (int i = upgrade.getMinLevel(); i <= upgrade.getMaxLevel(); i++) {
                    if(!EMConfig.isBanned(new UpgradeDetail(upgrade, i), ServerConfigHelper.getSyncBannedUpgrade())) {
                        items.add(Upgrades.writeUpgrade(new ItemStack(this), new UpgradeDetail(upgrade, i)));
                    }
                }
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack) {
        return super.hasEffect(stack) || Upgrades.getUpgradeFromItemStack(stack).getType().shouldShowEffect();
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        UpgradeDetail detail = Upgrades.getUpgradeFromItemStack(stack);
        return detail.getType().getRarity(detail.getLevel());
    }
}
