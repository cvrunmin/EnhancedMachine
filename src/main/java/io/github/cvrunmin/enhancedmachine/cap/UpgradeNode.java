package io.github.cvrunmin.enhancedmachine.cap;

import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeRiser;

import javax.annotation.Nonnull;
import java.util.Objects;

public class UpgradeNode implements Cloneable {
    protected UpgradeNode parent;
    protected UpgradeDetail upgrade;

    public UpgradeNode(@Nonnull UpgradeDetail upgrade) {
        this.upgrade = upgrade;
    }

    public static UpgradeNode createNode(UpgradeDetail detail){
        if(detail.getType() instanceof UpgradeRiser){
            return new SplitUpgradeNode(detail);
        }
        return new UpgradeNode((detail));
    }

    public UpgradeDetail getUpgrade() {
        return upgrade;
    }

    public UpgradeNode setUpgrade(UpgradeDetail upgrade) {
        this.upgrade = upgrade;
        return this;
    }

    public UpgradeNode getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!equalsIgnoreParent(o)) return false;
        UpgradeNode that = (UpgradeNode) o;
        return Objects.equals(parent, that.parent);
    }

    public boolean equalsIgnoreParent(Object o) {
        if (this == o) return true;
        if (!(o instanceof UpgradeNode)) return false;
        UpgradeNode that = (UpgradeNode) o;
        return upgrade.equals(that.upgrade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parent, upgrade);
    }

//    @Override
//    public UpgradeNode clone() {
//        UpgradeNode node = new UpgradeNode(upgrade.clone());
//        node.parent = parent;
//        return node;
//    }
}
