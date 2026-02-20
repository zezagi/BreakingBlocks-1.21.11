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
import zezagi.breakingblocks.util.BlockItemWithQuality;
import zezagi.breakingblocks.util.FertilizerItem;
import zezagi.breakingblocks.util.ItemWithQuality;

public class ModItems {
    
    public static final Item COCA_SEEDS = registerItem("coca_seeds", settings -> new BlockItem(ModBlocks.COCA_CROP, settings));
    public static final Item COCA_LEAF = registerItem("coca_leaf", ItemWithQuality::new);

    public static final Item MACERATION_BARREL_ITEM = registerItem("maceration_barrel", settings -> new BlockItem(ModBlocks.MACERATION_BARREL, settings));
    public static final Item DISTILLER_ITEM = registerItem("distiller", settings -> new BlockItem(ModBlocks.DISTILLER,
            settings.component(ModComponents.FUEL_LEVEL,0)));

    public static final Item CANISTER = registerItem("canister", settings -> new CanisterItem(
            settings
            .maxCount(8)
            .component(ModComponents.GASOLINE_LEVEL,0)));

    public static final Item COKE_PASTE = registerItem("coke_paste", ItemWithQuality::new);

    public static final Item DRYING_STATION_ITEM = registerItem("drying_station", settings ->
            new BlockItem(ModBlocks.DRYING_STATION, settings)
            );

    public static final Item DRYED_COKE = registerItem("dryed_coke", ItemWithQuality::new);

    public static final Item BRICK_PRESS_ITEM = registerItem("brick_press", settings ->
            new BlockItem(ModBlocks.BRICK_PRESS, settings));

    public static final Item COCAINE_BRICK_ITEM = registerItem("cocaine_brick", settings ->
            new BlockItemWithQuality(ModBlocks.COCAINE_BRICK, settings)
            );

    public static final Item HUMIDIFIER_ITEM = registerItem("humidifier", settings ->
           new BlockItem(ModBlocks.HUMIDIFIER, settings)
            );

    public static final Item NITROGEN = registerItem("nitrogen", settings ->
            new FertilizerItem(settings, 1)
    );

    public static final Item PHOSPHORUS = registerItem("phosphorus", settings ->
            new FertilizerItem(settings, 2)
    );

    public static final Item POTASSIUM = registerItem("potassium", settings ->
            new FertilizerItem(settings, 3));

    public static final Item INDUSTRIAL_BLEACH = registerItem("industrial_bleach", settings ->
            new FertilizerItem(settings, 4)
            );

    public static final Item ACETONE_MIX = registerItem("acetone_mix", settings ->
            new FertilizerItem(settings, 5)
            );

    public static final Item SULFURIC_ACID = registerItem("sulfuric_acid", settings ->
            new FertilizerItem(settings, 6)
            );

    public static final Item BLUE_COBALT = registerItem("blue_cobalt", settings ->
            new FertilizerItem(settings, 7)
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
    }
}