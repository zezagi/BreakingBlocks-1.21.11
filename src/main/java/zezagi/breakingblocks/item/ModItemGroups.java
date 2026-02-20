package zezagi.breakingblocks.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.BreakingBlocks;

public class ModItemGroups {

    public static final ItemGroup BREAKING_BLOCKS_TAB = Registry.register(
            Registries.ITEM_GROUP,
            Identifier.of(BreakingBlocks.MOD_ID, "breaking_blocks_tab"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup.breakingblocks.main"))
                    .icon(() -> new ItemStack(ModItems.COCAINE_BRICK_ITEM))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.COCA_SEEDS);
                        entries.add(ModItems.COCA_LEAF);
                        entries.add(ModItems.MACERATION_BARREL_ITEM);
                        entries.add(ModItems.CANISTER);
                        entries.add(ModItems.DISTILLER_ITEM);
                        entries.add(ModItems.COKE_PASTE);
                        entries.add(ModItems.DRYING_STATION_ITEM);
                        entries.add(ModItems.DRYED_COKE);
                        entries.add(ModItems.BRICK_PRESS_ITEM);
                        entries.add(ModItems.COCAINE_BRICK_ITEM);
                        entries.add(ModItems.HUMIDIFIER_ITEM);
                        entries.add(ModItems.NITROGEN);
                        entries.add(ModItems.PHOSPHORUS);
                        entries.add(ModItems.POTASSIUM);
                        entries.add(ModItems.INDUSTRIAL_BLEACH);
                        entries.add(ModItems.ACETONE_MIX);
                        entries.add(ModItems.SULFURIC_ACID);
                        entries.add(ModItems.BLUE_COBALT);
                    })
                    .build()
    );

    public static void registerItemGroups() {
        BreakingBlocks.LOGGER.info("Registering Item Groups for " + BreakingBlocks.MOD_ID);
    }
}