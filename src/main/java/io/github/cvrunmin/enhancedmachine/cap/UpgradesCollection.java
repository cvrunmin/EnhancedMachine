package io.github.cvrunmin.enhancedmachine.cap;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeRiser;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.nbt.CompoundNBT;

import java.util.*;
import java.util.stream.Collectors;

public class UpgradesCollection {

    private UpgradeNode root;

    public static int getDepth(UpgradeNode node) {
        int depth = 0;
        UpgradeNode node1 = node.parent;
        while (node1 != null) {
            node1 = node1.parent;
            depth++;
        }
        return depth;
    }

    public UpgradeNode getRoot() {
        return root;
    }

    public UpgradesCollection setRoot(UpgradeNode root) {
        this.root = root;
        return this;
    }

    public boolean hasUpgrade(UpgradeDetail detail) {
        return hasUpgrade(root, detail);
    }

    private boolean hasUpgrade(UpgradeNode node, UpgradeDetail detail) {
        if (detail == null) return false;
        if (node.getUpgrade().equals(detail)) return true;
        else if (node instanceof SplitUpgrade) {
            for (UpgradeNode child : ((SplitUpgrade) node).getChildren()) {
                if (hasUpgrade(child, detail))
                    return true;
            }
        }
        return false;
    }

    public boolean hasUpgrade(Upgrade type) {
        return hasUpgrade(root, type);
    }

//    private UpgradeNode lastUpgradeNode;
//    private List<UpgradeNode> pickleCache;

    private boolean hasUpgrade(UpgradeNode node, Upgrade type) {
        if (type == null) return false;
        if (node == null) return false;
        if (node.getUpgrade().getType().equals(type)) return true;
        else if (node instanceof SplitUpgrade) {
            for (UpgradeNode child : ((SplitUpgrade) node).getChildren()) {
                if (hasUpgrade(child, type))
                    return true;
            }
        }
        return false;
    }

    public List<UpgradeNode> pickleUpgradeNodes() {
        List<UpgradeNode> list = new ArrayList<>();
        if (root == null) return Collections.emptyList();
        root.getUpgrade().getExtras().putInt("Slot", list.size());
        root.getUpgrade().getExtras().remove("Parent");
        root.getUpgrade().getExtras().remove("SlotOnParent");
        list.add(root);
        handleSubNodes(list, root, false);
//        lastUpgradeNode = root.clone();
//        pickleCache = list;
//        return list;
//        for (int i = 0, listSize = list.size(); i < listSize; i++) {
//            UpgradeNode detail = list.get(i);
//            detail.getUpgrade().getExtras().setInteger("Slot", i);
//        }
        return list;
    }

    public List<UpgradeNode> pickleUpgradeNodesWithEmptyFilled() {
        List<UpgradeNode> list = new ArrayList<>();
        if (root == null) return Collections.emptyList();
        root.getUpgrade().getExtras().putInt("Slot", list.size());
        list.add(root);
        handleSubNodes(list, root, true);
//        lastUpgradeNode = root.clone();
//        pickleCache = list;
        return list;
    }

    public List<UpgradeDetail> pickleUpgrades() {
        return pickleUpgradeNodes().stream().map(UpgradeNode::getUpgrade).collect(Collectors.toList());
    }

    public List<UpgradeDetail> pickleUpgradesCompact() {
        List<UpgradeDetail> list = pickleUpgrades().stream().filter(detail -> !detail.getType().equals(Upgrades.EMPTY))
                .sorted(Comparator.comparingInt(detail -> detail.getExtras().getInt("Slot"))).collect(Collectors.toList());
        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            UpgradeDetail detail = list.get(i);
            detail.getExtras().putInt("Slot", i);
        }
        return list;
    }

    private void handleSubNodes(List<UpgradeNode> list, UpgradeNode node, boolean shallFillEmpty) {
        if (node instanceof SplitUpgrade) {
            List<UpgradeNode> children = ((SplitUpgrade) node).getChildren();
            int size = children.size();
            if (shallFillEmpty) {
                size = Upgrades.RISER.getExpansionSlots(node.getUpgrade().getLevel());
            }
            for (int i = 0; i < size; i++) {
                UpgradeNode child;
                if (i >= children.size()) {
                    child = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
                    children.add(child);
                } else {
                    child = children.get(i);
                    if (child == null && shallFillEmpty) {
                        child = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
                        children.set(i, child);
                    }
                }
                if (child == null) continue;
                CompoundNBT extras = child.getUpgrade().getExtras();
                extras.putInt("Slot", list.size());
                extras.putInt("Parent", node.getUpgrade().getExtras().getInt("Slot"));
                extras.putInt("SlotOnParent", i);
                list.add(child);
            }
            for (UpgradeNode child : children) {
                handleSubNodes(list, child, shallFillEmpty);
            }
        }
    }

    public void fromPickle(List<UpgradeDetail> upgradeDetails) {
        if (upgradeDetails.size() == 0) {
            root = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
        }
        else {
            Optional<UpgradeDetail> firstRoot = upgradeDetails.parallelStream()
                    .filter(detail -> !detail.getExtras().contains("Parent", 3))
                    .min(Comparator.comparingInt(detail -> detail.getExtras().getInt("Slot")));
            if (firstRoot.isPresent()) {
                UpgradeDetail rootDetail = firstRoot.get();
                if (rootDetail.getType() instanceof UpgradeRiser) {
                    SplitUpgrade root = new SplitUpgrade(rootDetail);
                    this.root = root;
                    handlePickledChildNode(upgradeDetails, root);
                } else {
                    this.root = new UpgradeNode(rootDetail);
                }
            } else {
                root = new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1));
            }
        }
    }

    private void handlePickledChildNode(List<UpgradeDetail> upgradeDetails, SplitUpgrade node) {
        int parentSlot = node.getUpgrade().getExtras().getInt("Slot");
        node.getChildren().addAll(Arrays.asList(new UpgradeNode[Upgrades.RISER.getExpansionSlots(node.getUpgrade().getLevel())]));
        upgradeDetails.parallelStream()
                .filter(detail -> detail.getExtras().contains("Parent", 3)
                        && detail.getExtras().getInt("Parent") == parentSlot)
                .sorted(Comparator.comparingInt(detail -> detail.getExtras().getInt("SlotOnParent")))
                .forEachOrdered(child -> {
                    UpgradeNode childNode;
                    if (child.getType() instanceof UpgradeRiser) {
                        childNode = new SplitUpgrade(child);
                    } else {
                        childNode = new UpgradeNode(child);
                    }
                    if (child.getExtras().contains("SlotOnParent")) {
                        node.getChildren().set(child.getExtras().getInt("SlotOnParent"), childNode);
                    } else {
                        int size = node.getChildren().size();
                        for (int i = 0; i < size; i++) {
                            if (node.getChildren().get(i) == null) {
                                node.getChildren().set(i, childNode);
                                break;
                            }
                        }
                    }
                    if (childNode instanceof SplitUpgrade) {
                        handlePickledChildNode(upgradeDetails, (SplitUpgrade) childNode);
                    }
                });
    }

    public List<UpgradeNode> getNodes(int depth) {
        return getNodesImpl(depth, 0, root);
    }

    private List<UpgradeNode> getNodesImpl(int requiredDepth, int depth, UpgradeNode current) {
        if (current == null) return Collections.emptyList();
        if (requiredDepth == depth) {
            return Lists.newArrayList(current);
        } else {
            if (current instanceof SplitUpgrade) {
                List<UpgradeNode> list = new ArrayList<>();
                for (UpgradeNode child : ((SplitUpgrade) current).getChildren()) {
                    list.addAll(getNodesImpl(requiredDepth, depth + 1, child));
                }
                return list;
            }
            return Collections.emptyList();
        }
    }

}
