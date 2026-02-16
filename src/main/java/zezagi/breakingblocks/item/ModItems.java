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

import zezagi.breakingblocks.item.ModBlocks;

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
        });
    }
}