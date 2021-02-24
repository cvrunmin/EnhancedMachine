package io.github.cvrunmin.enhancedmachine.item;

import com.google.gson.JsonObject;
import io.github.cvrunmin.enhancedmachine.EMItems;
import io.github.cvrunmin.enhancedmachine.EnhancedMachine;
import io.github.cvrunmin.enhancedmachine.Initializer;
import io.github.cvrunmin.enhancedmachine.upgrade.UpgradeDetail;
import io.github.cvrunmin.enhancedmachine.upgrade.Upgrades;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class RecipeDecomposeChip implements ICraftingRecipe {
    private final ResourceLocation recipeId;

    public RecipeDecomposeChip(ResourceLocation recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        ItemStack retrievedStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
                if (!itemstack.getItem().equals(EMItems.UPGRADE_CHIP.get())) {
                    return false;
                } else if (retrievedStack.isEmpty()) {
                    retrievedStack = itemstack;
                } else return false;
            }
        }
        return Upgrades.getUpgradeFromItemStack(retrievedStack).getType().equals(Upgrades.DAMAGED);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        ItemStack retrievedStack = ItemStack.EMPTY;
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack itemstack = inv.getStackInSlot(i);

            if (!itemstack.isEmpty()) {
                if (!itemstack.getItem().equals(EMItems.UPGRADE_CHIP.get())) {
                    return ItemStack.EMPTY;
                } else if (retrievedStack.isEmpty()) {
                    retrievedStack = itemstack;
                } else return ItemStack.EMPTY;
            }
        }
        UpgradeDetail upgradeDetail = Upgrades.getUpgradeFromItemStack(retrievedStack);
        if (!upgradeDetail.getType().equals(Upgrades.DAMAGED))
            return ItemStack.EMPTY;
        if (upgradeDetail.getLevel() <= 5)
            return new ItemStack(Items.IRON_NUGGET, upgradeDetail.getLevel());
        else {
            return new ItemStack(Items.GOLD_NUGGET, upgradeDetail.getLevel() - 5);
        }
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 1 && height >= 1;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public String getGroup() {
        return "enhancedmachine:decompose_damaged_chip";
    }

    @Override
    public ResourceLocation getId() {
        return recipeId;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return Initializer.chipDecomposeRecipe.get();
    }

    public static class Serializer extends net.minecraftforge.registries.ForgeRegistryEntry<IRecipeSerializer<?>>  implements IRecipeSerializer<RecipeDecomposeChip> {

        @Override
        public RecipeDecomposeChip read(ResourceLocation recipeId, JsonObject json) {
            return new RecipeDecomposeChip(recipeId);
        }

        @Nullable
        @Override
        public RecipeDecomposeChip read(ResourceLocation recipeId, PacketBuffer buffer) {
            return new RecipeDecomposeChip(recipeId);
        }

        @Override
        public void write(PacketBuffer buffer, RecipeDecomposeChip recipe) {

        }
    }
}
