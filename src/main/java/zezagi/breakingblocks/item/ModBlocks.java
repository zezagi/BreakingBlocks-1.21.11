package zezagi.breakingblocks.item;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.item.customItem.CocaCropBlock;
import zezagi.breakingblocks.item.customItem.DistillerBlock;
import zezagi.breakingblocks.item.customItem.MacerationBarrelBlock;

import java.util.function.Function;

public class ModBlocks {
    public static final Block MACERATION_BARREL = registerBlockWithoutItem("maceration_barrel", key ->
            new MacerationBarrelBlock(Block.Settings.create()
                    .registryKey(key)
                    .nonOpaque()
                    .strength(2.0f)
                    .sounds(net.minecraft.sound.BlockSoundGroup.WOOD))
    );

    public static final Block DISTILLER = registerBlockWithoutItem("distiller", key ->
            new DistillerBlock(Block.Settings.create()
                    .registryKey(key)
                    .nonOpaque()
                    .strength(4.0f)
                    .luminance(state -> 15)
                    .requiresTool()
                    .sounds(net.minecraft.sound.BlockSoundGroup.STONE))
    );
    public static final Block COCA_CROP = registerBlockWithoutItem("coca_crop", key ->
            new CocaCropBlock(
                    Block.Settings.create()
                            .registryKey(key)
                            .noCollision()
                            .ticksRandomly()
                            .breakInstantly()
                            .sounds(net.minecraft.sound.BlockSoundGroup.CROP))
    );
    private static <T extends Block> T registerBlockWithoutItem(String name, Function<RegistryKey<Block>, T> blockFactory) {
        Identifier id = Identifier.of(BreakingBlocks.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        T block = blockFactory.apply(key);
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void RegisterModBlocks() {
       BreakingBlocks.LOGGER.info("Registering Mod Blocks for " + BreakingBlocks.MOD_ID);
    }
}
