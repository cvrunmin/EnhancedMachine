package io.github.cvrunmin.enhancedmachine;

import io.github.cvrunmin.enhancedmachine.block.BlockChipwriter;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class EMBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnhancedMachine.MODID);

    public static RegistryObject<BlockChipwriter> CHIPWRITER = BLOCKS.register("chipwriter", BlockChipwriter::new);
}
