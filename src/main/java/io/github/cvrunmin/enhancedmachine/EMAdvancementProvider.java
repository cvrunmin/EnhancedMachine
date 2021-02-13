package io.github.cvrunmin.enhancedmachine;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.advancements.*;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class EMAdvancementProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Advancement>>> advancements = ImmutableList.of(new EndAdvancements(), new HusbandryAdvancements(), new AdventureAdvancements(), new NetherAdvancements(), new StoryAdvancements());

    public EMAdvancementProvider(DataGenerator generatorIn) {
        this.generator = generatorIn;
    }

    /**
     * Performs this provider's action.
     */
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = Sets.newHashSet();
        Consumer<Advancement> consumer = (p_204017_3_) -> {
            if (!set.add(p_204017_3_.getId())) {
                throw new IllegalStateException("Duplicate advancement " + p_204017_3_.getId());
            } else {
                Path path1 = getPath(path, p_204017_3_);

                try {
                    IDataProvider.save(GSON, cache, p_204017_3_.copy().serialize(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save advancement {}", path1, ioexception);
                }

            }
        };

        Advancement.Builder.builder().withRewards(AdvancementRewards.Builder.recipe(new ResourceLocation("enhancedmachine:upgrade_chip_intact")))
                .withCriterion("has_chip", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(EMItems.UPGRADE_CHIP.get()).build()))
                .withCriterion("has_chipwriter", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(EMBlocks.CHIPWRITER.get()).build()))
                .withRequirementsStrategy(IRequirementsStrategy.OR).register(consumer, "enhancedmachine:upgrade_chip_intact");
        Advancement.Builder.builder().withRewards(AdvancementRewards.Builder.recipe(new ResourceLocation("enhancedmachine:upgrade_chip_exceptional")))
                .withCriterion("has_chip", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(EMItems.UPGRADE_CHIP.get()).build()))
                .withCriterion("has_chipwriter", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(EMBlocks.CHIPWRITER.get()).build()))
                .withRequirementsStrategy(IRequirementsStrategy.OR).register(consumer, "enhancedmachine:upgrade_chip_exceptional");
        Advancement.Builder.builder().withRewards(AdvancementRewards.Builder.recipe(new ResourceLocation("enhancedmachine:chipwriter")))
                .withCriterion("has_ingot", InventoryChangeTrigger.Instance.forItems(ItemPredicate.Builder.create().item(Items.IRON_INGOT).build()))
                .register(consumer, "enhancedmachine:chipwriter");

//        for(Consumer<Consumer<Advancement>> consumer1 : this.advancements) {
//            consumer1.accept(consumer);
//        }

    }

    private static Path getPath(Path pathIn, Advancement advancementIn) {
        return pathIn.resolve("data/" + advancementIn.getId().getNamespace() + "/advancements/" + advancementIn.getId().getPath() + ".json");
    }

    /**
     * Gets a name for this provider, to use in logging.
     */
    public String getName() {
        return "EMAdvancements";
    }
}
