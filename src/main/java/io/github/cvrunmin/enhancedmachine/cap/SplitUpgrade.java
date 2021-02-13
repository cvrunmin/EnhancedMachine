package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplitUpgrade extends UpgradeNode {

    private List<UpgradeNode> children = new ArrayList<UpgradeNode>() {
        @Override
        public boolean add(UpgradeNode upgradeNode) {
            upgradeNode.parent = SplitUpgrade.this;
            return super.add(upgradeNode);
        }

        @Override
        public void add(int index, UpgradeNode element) {
            element.parent = SplitUpgrade.this;
            super.add(index, element);
        }

        @Override
        public UpgradeNode set(int index, UpgradeNode element) {
            element.parent = SplitUpgrade.this;
            return super.set(index, element);
        }
    };

    public SplitUpgrade(UpgradeDetail upgrade) {
        super(upgrade);
    }

    public List<UpgradeNode> getChildren() {
        return children;
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
        SplitUpgrade that = (SplitUpgrade) o;
        return Objects.equals(parent, that.parent);
    }

    @Override
    public boolean equalsIgnoreParent(Object o) {
        if (this == o) return true;
        if (!(o instanceof SplitUpgrade)) return false;
        if (!super.equalsIgnoreParent(o)) return false;
        SplitUpgrade that = (SplitUpgrade) o;
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
