package io.github.cvrunmin.enhancedmachine.upgrade;

import net.minecraft.nbt.CompoundNBT;

import java.util.Objects;

public class UpgradeDetail implements Cloneable {
    private final Upgrade type;
    private final int level;

    private CompoundNBT extras = new CompoundNBT();

    public UpgradeDetail(Upgrade type, int level) {
        this.type = type;
        this.level = level;
    }

    public Upgrade getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public CompoundNBT getExtras() {
        return extras;
    }

    @Override
    public UpgradeDetail clone() {
        UpgradeDetail detail = new UpgradeDetail(type, level);
        detail.getExtras().merge(this.getExtras());
        return detail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpgradeDetail that = (UpgradeDetail) o;
        return level == that.level &&
                type.equals(that.type) && extras.equals(that.extras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, level, extras);
    }
}
