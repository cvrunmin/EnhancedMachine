package io.github.cvrunmin.enhancedmachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ServerConfigHelper {

    private static List<EMConfig.BannedUpgradeInfo> syncBannedUpgrade = new ArrayList<>();

    private static Boolean alwaysShowPanel = null;

    private static Integer upgradeSummaryOpLevel = null;

    public static boolean shouldDoFastInstallUpgrade(){
        return alwaysShowPanel != null ? alwaysShowPanel : EMConfig.ALWAYS_SHOW_UPGRADE_PANEL.get();
    }

    public static int getUpgradeAbstractPermissionLevel(){
        return upgradeSummaryOpLevel != null ? upgradeSummaryOpLevel : EMConfig.UPGRADES_ABSTRACT_VIEWING_PERMISSION.get();
    }

    public static List<EMConfig.BannedUpgradeInfo> getSyncBannedUpgrade() {
        return syncBannedUpgrade;
    }

    public static void setSyncBannedUpgrade(List<String> entries){
        syncBannedUpgrade = entries.stream().map(EMConfig.BannedUpgradeInfo::fromString).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void handleConnectToServer(){

    }

    public static void handleDisconnectFromServer(){

    }

}
