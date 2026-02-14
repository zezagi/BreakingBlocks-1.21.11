package zezagi.breakingblocks.item;

import net.fabricmc.fabric.api.block.v1.FabricBlock;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.item.customItem.CocaCropBlock;
import java.util.function.Function;

public class ModItems {

    public static final Block COCA_CROP = registerBlockWithoutItem("coca_crop", key ->
            new CocaCropBlock(AbstractBlock.Settings.copy(Blocks.WHEAT)
                    .registryKey(key)
                    .nonOpaque()
                    .noCollision()
                    .breakInstantly()
            )
    );
    public static final Item COCA_SEEDS = registerItem("coca_seeds", settings -> new BlockItem(ModItems.COCA_CROP, settings));

    private static Item registerItem(String name, java.util.function.Function<Item.Settings, Item> itemFactory) {
        Identifier id = Identifier.of(BreakingBlocks.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);

        Item.Settings settings = new Item.Settings().registryKey(key);

        Item item = itemFactory.apply(settings);

        return Registry.register(Registries.ITEM, id, item);
    }

    private static <T extends Block> T registerBlockWithoutItem(String name, Function<RegistryKey<Block>, T> blockFactory) {
        Identifier id = Identifier.of(BreakingBlocks.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);

        T block = blockFactory.apply(key);
        return Registry.register(Registries.BLOCK, id, block);
    }

    public static void registerModItems() {
        BreakingBlocks.LOGGER.info("Registering Mod Items for"+BreakingBlocks.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(COCA_SEEDS);
        });
    }
}