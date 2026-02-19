package zezagi.breakingblocks.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.block.ModBlocks;

public class ModItems {
    
    public static final Item COCA_SEEDS = registerItem("coca_seeds", settings -> new BlockItem(ModBlocks.COCA_CROP, settings));
    public static final Item COCA_LEAF = registerItem("coca_leaf", Item::new);

    public static final Item MACERATION_BARREL_ITEM = registerItem("maceration_barrel", settings -> new BlockItem(ModBlocks.MACERATION_BARREL, settings));
    public static final Item DISTILLER_ITEM = registerItem("distiller", settings -> new BlockItem(ModBlocks.DISTILLER,
            settings.component(ModComponents.FUEL_LEVEL,0)));

    public static final Item CANISTER = registerItem("canister", settings -> new CanisterItem(
            settings
            .maxCount(8)
            .component(ModComponents.GASOLINE_LEVEL,0)));

    public static final Item COKE_PASTE = registerItem("coke_paste", Item::new);

    public static final Item DRYING_STATION_ITEM = registerItem("drying_station", settings ->
            new BlockItem(ModBlocks.DRYING_STATION, settings)
            );

    public static final Item DRYED_COKE = registerItem("dryed_coke", Item::new);

    public static final Item BRICK_PRESS_ITEM = registerItem("brick_press", settings ->
            new BlockItem(ModBlocks.BRICK_PRESS, settings));

    public static final Item COCAINE_BRICK_ITEM = registerItem("cocaine_brick", settings ->
            new BlockItem(ModBlocks.COCAINE_BRICK, settings)
            );

    public static final Item HUMIDIFIER_ITEM = registerItem("humidifier", settings ->
           new BlockItem(ModBlocks.HUMIDIFIER, settings)
            );

    private static Item registerItem(String name, Function<Item.Settings, Item> itemFactory) {
        Identifier id = Identifier.of(BreakingBlocks.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings settings = new Item.Settings().registryKey(key);
        Item item = itemFactory.apply(settings);
        return Registry.register(Registries.ITEM, id, item);
    }


    public static void registerModItems() {
        BreakingBlocks.LOGGER.info("Registering Mod Items for " + BreakingBlocks.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(COCA_SEEDS);
            entries.add(COCA_LEAF);
            entries.add(MACERATION_BARREL_ITEM);
            entries.add(CANISTER);
            entries.add(DISTILLER_ITEM);
            entries.add(COKE_PASTE);
            entries.add(DRYING_STATION_ITEM);
            entries.add(DRYED_COKE);
            entries.add(BRICK_PRESS_ITEM);
            entries.add(COCAINE_BRICK_ITEM);
            entries.add(HUMIDIFIER_ITEM);
        });
    }
}