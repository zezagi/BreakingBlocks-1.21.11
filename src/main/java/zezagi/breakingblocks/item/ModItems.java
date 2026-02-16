package zezagi.breakingblocks.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.item.customItem.CanisterItem;
import zezagi.breakingblocks.item.customItem.CocaCropBlock;
import zezagi.breakingblocks.item.customItem.DistillerBlock;
import zezagi.breakingblocks.item.customItem.MacerationBarrelBlock;

import java.util.function.Function;

public class ModItems {

    public static final Block COCA_CROP = registerBlockWithoutItem("coca_crop", key ->
            new CocaCropBlock(
                    Block.Settings.create()
                            .registryKey(key)
                            .noCollision()
                            .ticksRandomly()
                            .breakInstantly()
                            .sounds(net.minecraft.sound.BlockSoundGroup.CROP))
    );

    public static final Item COCA_SEEDS = registerItem("coca_seeds", settings -> new BlockItem(ModItems.COCA_CROP, settings));
    public static final Item COCA_LEAF = registerItem("coca_leaf", Item::new);

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

    public static final Item MACERATION_BARREL_ITEM = registerItem("maceration_barrel", settings -> new BlockItem(MACERATION_BARREL, settings));
    public static final Item DISTILLER_ITEM = registerItem("distiller", settings -> new BlockItem(DISTILLER, settings));

    public static final Item CANISTER = registerItem("canister", settings -> new CanisterItem(
            settings
            .maxCount(8)
            .component(ModComponents.GASOLINE_LEVEL,20)));

    private static Item registerItem(String name, Function<Item.Settings, Item> itemFactory) {
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
        BreakingBlocks.LOGGER.info("Registering Mod Items for " + BreakingBlocks.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(COCA_SEEDS);
            entries.add(COCA_LEAF);
            entries.add(MACERATION_BARREL_ITEM);
            entries.add(CANISTER);
            entries.add(DISTILLER_ITEM);
        });
    }
}