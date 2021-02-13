package io.github.cvrunmin.enhancedmachine.upgrade;

import io.github.cvrunmin.enhancedmachine.EMConfig;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.*;

public final class UpgradeRecipes {

    private static final UpgradeRecipes instance = new UpgradeRecipes();

    public static UpgradeRecipes getInstance() {
        return instance;
    }

    static {
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_obsidian_coating"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.OBSIDIAN_COATING, 1), new ItemStack(Blocks.OBSIDIAN, 1), 0);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_hyperthread_1"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.HYPERTHREAD, 1), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_hyperthread_2"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.HYPERTHREAD, 2), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_hyperthread_3"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.HYPERTHREAD, 3), 4);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_hyperthread_3a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.HYPERTHREAD, 3), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_hyperthread_4"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.HYPERTHREAD, 4), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_1"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 1), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_2"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 2), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_3"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 3), 4);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_4"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 4), 8);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_5"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 5), 16);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_4a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 4), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_5a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 5), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_6"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 6), 3);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_7"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 7), 5);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_time_acceleration_8"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.TIME_ACCELERATION, 8), 7);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_1"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 1), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_2"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 2), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_3"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 3), 4);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_4"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 4), 8);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_5"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 5), 16);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_4a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 4), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_5a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 5), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_6"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 6), 3);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_7"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 7), 5);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_fuel_mastery_8"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.FUEL_MASTERY, 8), 7);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_1"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.DRILL, 1), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_2"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.DRILL, 2), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_3"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.DRILL, 3), 4);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_4"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.DRILL, 4), 8);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_5"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.DRILL, 5), 16);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_4a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.DRILL, 4), 1);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_5a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.DRILL, 5), 2);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_6"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.DRILL, 6), 3);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_7"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.DRILL, 7), 5);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_drill_8"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.DRILL, 8), 7);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_riser_1"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.RISER, 1), 3);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_riser_2"), new UpgradeDetail(Upgrades.INTACT, 1), new UpgradeDetail(Upgrades.RISER, 2), 6);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_riser_2a"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.RISER, 2), 3);
        instance.registerRecipes(new ResourceLocation("enhancedmachine:write_chip_riser_3"), new UpgradeDetail(Upgrades.EXCEPTIONAL, 1), new UpgradeDetail(Upgrades.RISER, 3), 8);
    }

    private List<ResourceLocation> registeredRecipeList = new ArrayList<>();
    private Map<ResourceLocation, UpgradeDetail> recipeInputMap = new HashMap<>();
    private Map<ResourceLocation, Tuple<CompareMode, CompareMode>> recipeCompareModeMap = new HashMap<>();
    private Map<ResourceLocation, UpgradeDetail> recipeOutputMap = new HashMap<>();
    private Map<ResourceLocation, ItemStack> recipeSacrificesMap = new HashMap<>();
    private Map<ResourceLocation, Integer> recipeExperienceLevelRequirementMap = new HashMap<>();

    public void registerRecipes(ResourceLocation recipeName, UpgradeDetail input, UpgradeDetail output, int level) {
        registerRecipes(recipeName, input, CompareMode.EQUAL, CompareMode.ALWAYS, output, ItemStack.EMPTY, level);
    }

    public void registerRecipes(ResourceLocation recipeName, UpgradeDetail input, CompareMode inputMode, CompareMode levelMode, UpgradeDetail output, int level) {
        registerRecipes(recipeName, input, inputMode, levelMode, output, ItemStack.EMPTY, level);
    }

    public void registerRecipes(ResourceLocation recipeName, UpgradeDetail input, UpgradeDetail output, ItemStack sacrifices, int level) {
        registerRecipes(recipeName, input, CompareMode.EQUAL, CompareMode.ALWAYS, output, sacrifices, level);
    }

    public void registerRecipes(ResourceLocation recipeName, UpgradeDetail input, CompareMode inputMode, CompareMode levelMode, UpgradeDetail output, ItemStack sacrifices, int level) {
        this.recipeInputMap.put(recipeName, input);
        this.recipeCompareModeMap.put(recipeName, new Tuple<>(inputMode, levelMode));
        this.recipeOutputMap.put(recipeName, output);
        this.recipeSacrificesMap.put(recipeName, sacrifices);
        this.recipeExperienceLevelRequirementMap.put(recipeName, level);
        this.registeredRecipeList.add(recipeName);
    }

    public List<Tuple<UpgradeDetail, Integer>> getAvailableRecipes(ItemStack input, ItemStack sacrifices) {
        UpgradeDetail detail = Upgrades.getUpgradeFromItemStack(input);
        List<Tuple<UpgradeDetail, Integer>> output = new ArrayList<>();
        loop:
        for (ResourceLocation location : registeredRecipeList) {
            UpgradeDetail outputDetail = recipeOutputMap.get(location);
            if (EMConfig.getBannedUpgradesString().contains(outputDetail.getType().getUpgradeName())) continue;
            ItemStack recipeSacrifice = recipeSacrificesMap.get(location);
            if (!recipeSacrifice.isEmpty())
                if ((!Objects.equals(recipeSacrifice.getItem(), sacrifices.getItem()))
                        || sacrifices.getCount() < recipeSacrifice.getCount()
//                        || (recipeSacrifice.getMetadata() != 0x7fff && sacrifices.getMetadata() != recipeSacrifice.getMetadata())
                        || (recipeSacrifice.hasTag() && Objects.equals(sacrifices.getTag(), recipeSacrifice.getTag()))) {
                    continue;
                }
            if (recipeSacrifice.isEmpty() && !sacrifices.isEmpty()) continue;
            UpgradeDetail recipeInputDetail = recipeInputMap.get(location);
            CompareMode inputMode = recipeCompareModeMap.get(location).getA();
            CompareMode levelMode = recipeCompareModeMap.get(location).getB();
            switch (inputMode) {
                case NOT_EQUAL:
                    if (recipeInputDetail.getType().equals(detail.getType())) {
                        continue loop;
                    }
                    break;
                default:
                    if (!recipeInputDetail.getType().equals(detail.getType())) {
                        continue loop;
                    }
            }
            switch (levelMode) {
                case EQUAL:
                    if (detail.getLevel() != recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case LESS:
                    if (detail.getLevel() >= recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case GREATER:
                    if (detail.getLevel() <= recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case LESS_EQUAL:
                    if (detail.getLevel() > recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case GREATER_EQUAL:
                    if (detail.getLevel() < recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case NOT_EQUAL:
                    if (detail.getLevel() == recipeInputDetail.getLevel()) {
                        continue loop;
                    }
                    break;
                case ALWAYS:
                    break;
            }
            output.add(new Tuple<>(outputDetail, recipeExperienceLevelRequirementMap.get(location)));
        }
        return output;
    }

    public enum CompareMode {
        EQUAL, LESS, GREATER, LESS_EQUAL, GREATER_EQUAL, NOT_EQUAL, ALWAYS
    }
}
