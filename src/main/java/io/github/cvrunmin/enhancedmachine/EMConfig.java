package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = EnhancedMachine.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EMConfig {

    public static String[] bannedUpgrades = new String[0];

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BANNDED_UPGRADES;

    public static final ForgeConfigSpec COMMON_CONFIG;

    public static final ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.BooleanValue ALWAYS_SHOW_UPGRADE_PANEL;

    public static final ForgeConfigSpec.ConfigValue<Integer> UPGRADES_ABSTRACT_VIEWING_PERMISSION;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();
        COMMON_BUILDER.comment("General settings").push("general");
        BANNDED_UPGRADES = COMMON_BUILDER.comment("This config will ban all upgrades mentioned in all worlds").translation("enhancedmachine.config.general.bannedUpgrades").worldRestart().defineList("banned_upgrades", new ArrayList<String>(), str->true);
        ALWAYS_SHOW_UPGRADE_PANEL = COMMON_BUILDER.comment("If false, right-clicking a machine with a chip will try installing the chip directly before showing the upgrade panel").translation("enhancedmachine.config.general.alwaysShowPanel").define("always_show_upgrade_panel", false);
        COMMON_BUILDER.pop();
        COMMON_CONFIG = COMMON_BUILDER.build();
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder().comment("Server settings");
        builder.push("server");
        UPGRADES_ABSTRACT_VIEWING_PERMISSION = builder.comment("Control which permission levels of players can view the upgrade abstract of a machine. Use op-permission-level. Set to 0 to allow everyone to view it, and any number >4 to disable abstract-viewing")
                .define("upgrades_abstract_viewing_permission", 0);
        builder.pop();
        SERVER_CONFIG = builder.build();
    }

    public EMConfig() {

    }

    public static List<String> getBannedUpgradesString() {
        return (List<String>) BANNDED_UPGRADES.get();
    }

    private static List<BannedUpgradeInfo> bannedUpgradeInfosCache;

    public static List<BannedUpgradeInfo> getBannedUpgradesParsed(){
        if(bannedUpgradeInfosCache == null){
            bannedUpgradeInfosCache = parseBannedUpgrades();
        }
        return bannedUpgradeInfosCache;
    }

    private static List<BannedUpgradeInfo> parseBannedUpgrades() {
        return BANNDED_UPGRADES.get().stream().map(BannedUpgradeInfo::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @SubscribeEvent
    public static void onConfigLoad(ModConfig.Loading event){
        if(Objects.equals(event.getConfig().getModId(), EnhancedMachine.MODID)){
            bannedUpgradeInfosCache = parseBannedUpgrades();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfig.Reloading event){
        if(Objects.equals(event.getConfig().getModId(), EnhancedMachine.MODID)){
            bannedUpgradeInfosCache = parseBannedUpgrades();
        }
    }

    public static boolean isBanned(UpgradeDetail detail){
        return isBanned(detail, getBannedUpgradesParsed());
    }

    public static boolean isBanned(UpgradeDetail detail, List<BannedUpgradeInfo> list){
        return list.stream().anyMatch(info -> info.upgrade == detail.getType() && info.in(detail.getLevel()));
    }

    public static final Collection<Upgrade> INVALID_BAN_UPGRADE_TYPES = Collections.unmodifiableList(Arrays.asList(Upgrades.EMPTY, Upgrades.INTACT, Upgrades.EXCEPTIONAL));

    public static class BannedUpgradeInfo{
        private Upgrade upgrade;
        private int minInclude;
        private int maxInclude;

        public BannedUpgradeInfo(Upgrade upgrade, int minInclude, int maxInclude){
            this.upgrade = upgrade;

            if(minInclude > maxInclude){
                int tmp = minInclude;
                minInclude = maxInclude;
                maxInclude = tmp;
            }

            this.minInclude = Math.max(minInclude, 0);
            this.maxInclude = Math.max(maxInclude, 0);
        }

        public String toString(){
            if(isFullCoverage()){
                return upgrade.getUpgradeName();
            }
            if(minInclude == 0){
                return upgrade.getUpgradeName() + ":(," + maxInclude + "]";
            }
            if(maxInclude == Integer.MAX_VALUE){
                return upgrade.getUpgradeName() + ":[" + minInclude + ",)";
            }
            return upgrade.getUpgradeName() + ":[" + minInclude + "," + maxInclude + "]";
        }

        public boolean isFullCoverage(){
            return minInclude == 0 && maxInclude == Integer.MAX_VALUE;
        }

        public static BannedUpgradeInfo fromString(String str){
            String[] split = str.split(":");
            Upgrade upgrade = Upgrades.getUpgradeFromId(split[0]);
            if(upgrade == null){
//                throw new IllegalArgumentException(split[0] + " has not been registered as a upgrade");
                Logger.getLogger(EnhancedMachine.MODID).log(Level.SEVERE, new IllegalArgumentException(split[0] + " has not been registered as a upgrade"), ()->"");
                return null;
            }
            if(INVALID_BAN_UPGRADE_TYPES.contains(upgrade)){
                Logger.getLogger(EnhancedMachine.MODID).warning("entry '" + str + "' has invalid upgrade type " + upgrade.getUpgradeName());
                return null;
            }
            if(split.length == 1){
                return new BannedUpgradeInfo(upgrade, 0, Integer.MAX_VALUE);
            }
            if(split.length > 2){
                Logger.getLogger(EnhancedMachine.MODID).warning("Too many colons in banned upgrade entry '" + str + "'!");
            }

            Pattern pattern = Pattern.compile("(?<standard>^(?<left>[(\\[])(?<min>\\d*),(?<max>\\d*)(?<right>[)\\]])$)|(?<dot>^(?<dotmin>\\d+)\\.\\.(?<dotmax>\\d+)$)");
            Matcher matcher = pattern.matcher(split[1]);
            if(!matcher.matches()){
//                throw new IllegalArgumentException("invalid range format '" + split[1] + "'");
                Logger.getLogger(EnhancedMachine.MODID).log(Level.SEVERE, new IllegalArgumentException("invalid range format '" + split[1] + "'"), ()->"");
                return null;
            }
            if(matcher.group("standard") != null && !matcher.group("standard").isEmpty()){
                String minS = matcher.group("min");
                String maxS = matcher.group("max");
                if((minS == null || minS.isEmpty()) && (maxS == null || maxS.isEmpty())){
//                    throw new IllegalArgumentException("range '" + split[1] + "' have no bound");
                    Logger.getLogger(EnhancedMachine.MODID).log(Level.SEVERE, new IllegalArgumentException("range '" + split[1] + "' have no bound"), ()->"");
                    return null;
                }
                int min;
                int max;
                if((minS == null || minS.isEmpty())){
                    min = 0;
                }
                else {
                    min = Integer.parseInt(minS);
                    if(matcher.group("left").equals("(")){
                        min++;
                    }
                }
                if((maxS == null || maxS.isEmpty())){
                    max = Integer.MAX_VALUE;
                }
                else {
                    max = Integer.parseInt(maxS);
                    if(matcher.group("right").equals(")")){
                        max--;
                    }
                }
                return new BannedUpgradeInfo(upgrade, min, max);
            }
            else{
                int min = Integer.parseInt(matcher.group("dotmin"));
                int max = Integer.parseInt(matcher.group("dotmax"));
                return new BannedUpgradeInfo(upgrade, min, max);
            }
        }

        public Upgrade getUpgrade() {
            return upgrade;
        }

        public int getMinInclude() {
            return minInclude;
        }

        public int getMaxInclude() {
            return maxInclude;
        }

        public boolean in(int testLevel){
            if(maxInclude == upgrade.getMaxLevel() && maxInclude < testLevel) return true;
            if(minInclude == upgrade.getMinLevel() && testLevel < minInclude) return true;
            return minInclude <= testLevel && testLevel <= maxInclude;
        }
    }
}
