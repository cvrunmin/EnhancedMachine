package io.github.cvrunmin.enhancedmachine.cap;

import com.google.common.collect.Lists;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrade;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeRiser;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;

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
        else if (node instanceof SplitUpgradeNode) {
            for (UpgradeNode child : ((SplitUpgradeNode) node).getChildren()) {
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
        else if (node instanceof SplitUpgradeNode) {
            for (UpgradeNode child : ((SplitUpgradeNode) node).getChildren()) {
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
        // BFS method
        if (root == null) return Collections.emptyList();
        List<UpgradeNode> list = new ArrayList<>();
        Deque<UpgradeNode> openList = new ArrayDeque<>();
        openList.offer(getRoot());
        while(!openList.isEmpty()){
            UpgradeNode node = openList.poll();
            list.add(node);
            if(node instanceof SplitUpgradeNode){
                ((SplitUpgradeNode) node).getChildren().forEach(openList::offer);
            }
        }
        return list;
    }

    public List<UpgradeNode> pickleUpgradeNodesWithEmptyFilledOld() {
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
        if (root == null) return Collections.emptyList();
        List<UpgradeDetail> list = new ArrayList<>();
        Deque<UpgradeNode> openList = new ArrayDeque<>();
        openList.offer(getRoot());
        while(!openList.isEmpty()){
            UpgradeNode node = openList.poll();
            if(Upgrades.EMPTY.equals(node.getUpgrade().getType())) continue;
            list.add(node.getUpgrade());
            if(node instanceof SplitUpgradeNode){
                ((SplitUpgradeNode) node).getChildren().forEach(openList::offer);
            }
        }
        return list;
    }

    public static class UpgradeNodeWrapper{
        private UpgradeNode node;
        private int peid; //parent eid
        private int eid; //element id
        private int cid; //child no.

        private List<UpgradeNodeWrapper> childrenWrapper = new ArrayList<>();

        public UpgradeNodeWrapper(UpgradeNode node){
            this.node = node;
            peid = -1;
            eid = -1;
            cid = -1;
        }

        public UpgradeNode getNode() {
            return node;
        }

        public int getPeid() {
            return peid;
        }

        public int getEid() {
            return eid;
        }

        public int getCid() {
            return cid;
        }

        public List<UpgradeNodeWrapper> getChildrenWrapper() {
            return childrenWrapper;
        }
    }

    public List<UpgradeNodeWrapper> flattenNodes(){
        if (root == null) return Collections.emptyList();
        List<UpgradeNodeWrapper> list = new ArrayList<>();
        Deque<UpgradeNodeWrapper> openList = new ArrayDeque<>();
        UpgradeNodeWrapper w = new UpgradeNodeWrapper(getRoot());
        openList.offer(w);
        int counter = 0;
        while(!openList.isEmpty()){
            UpgradeNodeWrapper node = openList.poll();
            node.eid = counter++;
            list.add(node);
            if(node.getNode() instanceof SplitUpgradeNode){
                int c = 0;
                for (UpgradeNode child : ((SplitUpgradeNode) node.getNode()).getChildren()) {
                    UpgradeNodeWrapper childWrapper = new UpgradeNodeWrapper(child);
                    childWrapper.peid = node.eid;
                    childWrapper.cid = c++;
                    node.childrenWrapper.add(childWrapper);
                    openList.offer(childWrapper);
                }
            }
        }
        return list;
    }

    public void handleNBT(ListNBT nbt){
        TreeMap<Integer, UpgradeNodeWrapper> map = new TreeMap<>();
        for (int i = 0; i < nbt.size(); i++) {
            CompoundNBT compound = nbt.getCompound(i);
            String upgradeId = compound.getString("id");
            Upgrade upgrade = Upgrades.getUpgradeFromId(upgradeId);
            if (upgrade == null) {
                continue;
            }
            int level = compound.getInt("level");
            UpgradeDetail detail = new UpgradeDetail(upgrade, level);
            CompoundNBT sub = compound.getCompound("extra");
            detail.getExtras().merge(sub);
            int eid = compound.getInt("eid");
            int peid = compound.contains("peid") ? compound.getInt("peid") : -1;
            int cid = compound.contains("cid") ? compound.getInt("cid") : -1;
            UpgradeNode node = UpgradeNode.createNode(detail);
            UpgradeNodeWrapper wrapper = new UpgradeNodeWrapper(node);
            wrapper.eid = eid;
            wrapper.peid = peid;
            wrapper.cid = cid;
            if(wrapper.peid != -1){
                UpgradeNodeWrapper wrapper1 = map.get(wrapper.peid);
                if(wrapper1 == null || !(wrapper1.getNode() instanceof SplitUpgradeNode)){
                    EnhancedMachine.LOGGER.warn(String.format("upgrade entry #%d is pointing to null or leaf node #%d, skipping...", eid, peid));
                    continue;
                }
                ((SplitUpgradeNode) wrapper1.getNode()).getChildren().set(wrapper.cid, wrapper.node);
            }
            map.put(wrapper.eid, wrapper);
        }
        root = map.ceilingEntry(0).getValue().getNode();
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
        if (node instanceof SplitUpgradeNode) {
            List<UpgradeNode> children = ((SplitUpgradeNode) node).getChildren();
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

    public void fromPickle(List<UpgradeDetail> upgradeDetails){
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
                    SplitUpgradeNode root = new SplitUpgradeNode(rootDetail);
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

    private void handlePickledChildNode(List<UpgradeDetail> upgradeDetails, SplitUpgradeNode node) {
        int parentSlot = node.getUpgrade().getExtras().getInt("Slot");
        node.getChildren().addAll(Arrays.asList(new UpgradeNode[Upgrades.RISER.getExpansionSlots(node.getUpgrade().getLevel())]));
        upgradeDetails.parallelStream()
                .filter(detail -> detail.getExtras().contains("Parent", 3)
                        && detail.getExtras().getInt("Parent") == parentSlot)
                .sorted(Comparator.comparingInt(detail -> detail.getExtras().getInt("SlotOnParent")))
                .forEachOrdered(child -> {
                    UpgradeNode childNode;
                    if (child.getType() instanceof UpgradeRiser) {
                        childNode = new SplitUpgradeNode(child);
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
                    if (childNode instanceof SplitUpgradeNode) {
                        handlePickledChildNode(upgradeDetails, (SplitUpgradeNode) childNode);
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
            if (current instanceof SplitUpgradeNode) {
                List<UpgradeNode> list = new ArrayList<>();
                for (UpgradeNode child : ((SplitUpgradeNode) current).getChildren()) {
                    list.addAll(getNodesImpl(requiredDepth, depth + 1, child));
                }
                return list;
            }
            return Collections.emptyList();
        }
    }

}
