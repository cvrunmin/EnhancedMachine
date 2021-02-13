package io.github.cvrunmin.enhancedmachine.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

public class SlotAllowDisable extends Slot {
    private boolean enabled = true;

    public SlotAllowDisable(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
