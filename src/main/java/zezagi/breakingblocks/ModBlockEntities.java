package zezagi.breakingblocks;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.item.ModBlocks;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.item.customItem.DistillerBlockEntity;

public class ModBlockEntities {
    public static final BlockEntityType<DistillerBlockEntity> DISTILLER_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of("breakingblocks", "distiller"),
                    FabricBlockEntityTypeBuilder.create(DistillerBlockEntity::new, ModBlocks.DISTILLER).build()
            );

    public static void RegisterBlockEntities() {}
}