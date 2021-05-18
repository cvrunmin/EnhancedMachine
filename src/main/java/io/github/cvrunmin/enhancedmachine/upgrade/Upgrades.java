package io.github.cvrunmin.enhancedmachine.upgrade;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import io.github.cvrunmin.enhancedmachine.ServerConfigHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import java.util.*;
import java.util.stream.Collectors;

public final class Upgrades {
    public static final Upgrade EMPTY = new Upgrade().setUpgradeName("empty").setChipColor(0);
    public static final Upgrade INTACT = new Upgrade().setUpgradeName("intact").setChipColor(0xb1eab1);
    public static final Upgrade EXCEPTIONAL = new Upgrade().setUpgradeName("exceptional").setChipColor(0xffffff);
    public static final Upgrade DAMAGED = new UpgradeCustomLevel().setMaxLevel(0xf).setUpgradeName("damaged").setChipColor(0x7f7f7f);
    public static final UpgradeRiser RISER = (UpgradeRiser) new UpgradeRiser().setChipColor(0x00afbf);
    public static final UpgradeObsidianCoating OBSIDIAN_COATING = (UpgradeObsidianCoating) new UpgradeObsidianCoating().setChipColor(0x2c2c5b);
    public static final UpgradeTimeAcceleration TIME_ACCELERATION = (UpgradeTimeAcceleration) new UpgradeTimeAcceleration().setChipColor(0x0f69af);
    public static final UpgradeFuelMastery FUEL_MASTERY = (UpgradeFuelMastery) new UpgradeFuelMastery().setChipColor(0xcf562a);
    public static final UpgradeHyperthread HYPERTHREAD = (UpgradeHyperthread) new UpgradeHyperthread().setChipColor(0x67b967);
    public static final UpgradeDrill DRILL = (UpgradeDrill) new UpgradeDrill().setChipColor(0xc8c8e9);
    public static final Upgrade FREE_ENERGY = new UpgradeFreeEnergy().setChipColor(0xc0ff00);

    private static final HashMap<String, Upgrade> REGISTERED_UPGRADES = new HashMap<>();

    public static void registerALl() {
        register(EMPTY);
        register(INTACT);
        register(EXCEPTIONAL);
        register(DAMAGED);
        register(RISER);
        register(OBSIDIAN_COATING);
        register(TIME_ACCELERATION);
        register(FUEL_MASTERY);
        register(HYPERTHREAD);
        register(DRILL);
        register(FREE_ENERGY);
    }

    public static Upgrade register(Upgrade upgrade) {
        return REGISTERED_UPGRADES.put(upgrade.getUpgradeName(), upgrade);
    }

    public static Upgrade getUpgradeFromId(String id) {
//        if (EMConfig.getBannedUpgradesList().contains(id)) {
//            return null;
//        }
        if (REGISTERED_UPGRADES.containsKey(id)) {
            return REGISTERED_UPGRADES.get(id);
        }
        return null;
    }

    public static Collection<Upgrade> registeredUpgrades() {
        return REGISTERED_UPGRADES.values();
    }

    public static ItemStack writeUpgrade(ItemStack stack, UpgradeDetail upgrade) {
        if (upgrade.getType() == EMPTY) {
            if (stack.hasTag()) {
                stack.getTag().remove("Upgrade");
                if (stack.getTag().isEmpty()) {
                    stack.setTag(null);
                }
            }
        } else {
//            if (EMConfig.getBannedUpgradesList().contains(upgrade.getType().getUpgradeName())) {
//                CompoundNBT tmp = upgrade.getExtras();
//                upgrade = new UpgradeDetail(upgrade.getLevel() < 5 ? INTACT : EXCEPTIONAL, 1);
//                upgrade.getExtras().merge(tmp);
//            }
            stack.setTag(writeUpgradeToNBT(upgrade));
        }
        return stack;
    }

    public static CompoundNBT writeUpgradeToNBT(UpgradeDetail upgrade) {
        CompoundNBT compound = new CompoundNBT();
        CompoundNBT sub = new CompoundNBT();
        sub.putString("id", upgrade.getType().getUpgradeName());
        sub.putInt("level", upgrade.getLevel());
        if (!upgrade.getExtras().isEmpty()) {
            sub.put("extra", upgrade.getExtras());
        } else {
            CompoundNBT defaultExtra = upgrade.getType().getDefaultExtraData(new CompoundNBT(), upgrade.getLevel());
            if (!defaultExtra.isEmpty()) {
                sub.put("extra", defaultExtra);
            }
        }
        compound.put("Upgrade", sub);
        return compound;
    }

    public static UpgradeDetail getUpgradeFromNBT(CompoundNBT compound) {
        if (compound == null) {
            return new UpgradeDetail(EMPTY, 1);
        } else {
            CompoundNBT sub = compound.getCompound("Upgrade");
            if (sub.isEmpty()) {
                return new UpgradeDetail(INTACT, 1);
            }
            Upgrade upgrade = getUpgradeFromId(sub.getString("id"));
            int level = sub.getInt("level");
            if (upgrade == null) {
                return new UpgradeDetail(level < 5 ? INTACT : EXCEPTIONAL, 1);
            }
            if (level <= 0) {
                return new UpgradeDetail(INTACT, 1);
            }
            UpgradeDetail detail = new UpgradeDetail(upgrade, level);
            detail.getExtras().merge(sub.getCompound("extra"));
            return detail;
        }
    }

    public static void writeUpgradeTooltip(ItemStack stack, World worldIn, List<ITextComponent> lores) {
        UpgradeDetail upgradeDetail = getUpgradeFromItemStack(stack);
        Upgrade type = upgradeDetail.getType();
        if (type == EMPTY) return;
        lores.add(new StringTextComponent(""));
        lores.addAll(writeUpgradeTooltip(upgradeDetail));
        if(EMConfig.isBanned(upgradeDetail, worldIn != null && worldIn.isRemote ? ServerConfigHelper.getSyncBannedUpgrade() : EMConfig.getBannedUpgradesParsed())){
            lores.add(new TranslationTextComponent("enhancedmachine.config.general.bannedUpgrades.on").mergeStyle(TextFormatting.RED));
        }
        else{
            lores.add(new TranslationTextComponent("upgrade.applicable_block").appendString(type.getSupportedBlocks().stream().map(blk -> I18n.format(blk.getTranslationKey())).collect(Collectors.joining(", "))));
        }
    }

    public static List<ITextComponent> writeUpgradeTooltip(UpgradeDetail upgradeDetail) {
        Upgrade type = upgradeDetail.getType();
        if (type == EMPTY) return Collections.emptyList();
        TextFormatting descriptiveColor = getDescriptiveColor(type);
        List<ITextComponent> lores = new ArrayList<>();
        lores.add(getUpgradeFullTitle(upgradeDetail));
        lores.add(new TranslationTextComponent("upgrade.type." + type.getUpgradeName() + ".desc").mergeStyle(descriptiveColor));
        return lores;
    }

    public static ITextComponent getUpgradeFullTitle(UpgradeDetail detail) {
        Upgrade type = detail.getType();
        TextFormatting descriptiveColor = getDescriptiveColor(type);
        IFormattableTextComponent textComponent = new TranslationTextComponent("upgrade.type." + type.getUpgradeName()).mergeStyle(descriptiveColor);
        if (type.getMaxLevel() > type.getMinLevel()) {
            int level = detail.getLevel();
            TextFormatting levelColor;
            if (level <= (type.getMaxLevel() - type.getMinLevel()) * 0.317310508 / 2 + type.getMinLevel()) {
                levelColor = TextFormatting.WHITE;
            } else if (level >= (type.getMaxLevel() - type.getMinLevel()) * (0.317310508 / 2 + 0.682689492) + type.getMinLevel()) {
                levelColor = TextFormatting.GOLD;
            } else {
                levelColor = TextFormatting.BLUE;
            }
            textComponent.appendString(" ").appendSibling(new TranslationTextComponent("upgrade.level", level).mergeStyle(levelColor));
        }
        return textComponent;
    }

    public static TextFormatting getDescriptiveColor(Upgrade type) {
        TextFormatting descriptiveColor = TextFormatting.LIGHT_PURPLE;
        if (type == EMPTY || type == INTACT || type == EXCEPTIONAL) {
            descriptiveColor = TextFormatting.WHITE;
        } else if (type == DAMAGED) {
            descriptiveColor = TextFormatting.GRAY;
        }
        return descriptiveColor;
    }

    public static int getUpgradeChipColor(ItemStack stack) {
        CompoundNBT nbttagcompound = stack.getTag();

        if (nbttagcompound != null && nbttagcompound.contains("CustomDieColor", 99)) {
            return nbttagcompound.getInt("CustomDieColor");
        } else {
            return getOfficialChipColor(getUpgradeFromItemStack(stack));
        }
    }

    private static int getOfficialChipColor(UpgradeDetail upgradeFromNBT) {
        return upgradeFromNBT.getType().getChipColor();
    }

    public static UpgradeDetail getUpgradeFromItemStack(ItemStack stack) {
        return getUpgradeFromNBT(stack.getTag());
    }
}
