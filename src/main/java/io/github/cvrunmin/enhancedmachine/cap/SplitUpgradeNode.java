package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeRiser;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class SplitUpgradeNode extends UpgradeNode {

    private List<UpgradeNode> children = new ArrayList<UpgradeNode>() {
        @Override
        public boolean add(UpgradeNode upgradeNode) {
            if(maxChildren <= this.size()) return false;
            upgradeNode.parent = SplitUpgradeNode.this;
            return super.add(upgradeNode);
        }

        @Override
        public void add(int index, UpgradeNode element) {
            if(maxChildren <= this.size()) return;
            element.parent = SplitUpgradeNode.this;
            super.add(index, element);
        }

        @Override
        public UpgradeNode set(int index, UpgradeNode element) {
            element.parent = SplitUpgradeNode.this;
            return super.set(index, element);
        }

        @Override
        public UpgradeNode remove(int index) {
            UpgradeNode old = get(index);
            set(index, new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1)));
            return old;
        }

        @Override
        public boolean remove(Object o) {
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return false;
        }
    };

    private final int maxChildren;

    public SplitUpgradeNode(UpgradeDetail upgrade) {
        super(upgrade);
        if (upgrade.getType() instanceof UpgradeRiser) {
            maxChildren = Upgrades.RISER.getExpansionSlots(upgrade.getLevel());
            Stream.generate(() -> new UpgradeNode(new UpgradeDetail(Upgrades.EMPTY, 1))).limit(maxChildren).forEach(children::add);
        } else {
            maxChildren = 0;
        }
    }

    public List<UpgradeNode> getChildren() {
        return children;
    }

    public int getMaxAllowedChildren() {
        return maxChildren;
    }

    //    @Override
//    public SplitUpgrade clone() {
//        SplitUpgrade node = new SplitUpgrade(upgrade.clone());
//        node.parent = parent;
//        for (UpgradeNode child : children) {
//            node.children.add(child.clone());
//        }
//        return node;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!equalsIgnoreParent(o)) return false;
        SplitUpgradeNode that = (SplitUpgradeNode) o;
        return Objects.equals(parent, that.parent);
    }

    @Override
    public boolean equalsIgnoreParent(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitUpgradeNode)) return false;
        if (!super.equalsIgnoreParent(o)) return false;
        SplitUpgradeNode that = (SplitUpgradeNode) o;
        if (children.size() != that.children.size()) return false;
        for (int i = 0; i < children.size(); i++) {
            UpgradeNode node = children.get(i);
            UpgradeNode thatNode = that.children.get(i);
            if (node == null && thatNode == null) continue;
            if ((node == null) ^ (thatNode == null)) return false;
            if (!node.equalsIgnoreParent(thatNode)) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), children);
    }
}
