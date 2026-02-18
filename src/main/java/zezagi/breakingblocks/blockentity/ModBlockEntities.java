package zezagi.breakingblocks.blockentity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.block.ModBlocks;

public class ModBlockEntities {
    public static final BlockEntityType<DistillerBlockEntity> DISTILLER_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(BreakingBlocks.MOD_ID, "distiller"),
                    FabricBlockEntityTypeBuilder.create(DistillerBlockEntity::new, ModBlocks.DISTILLER).build()
            );

    public static final BlockEntityType<MacerationBarrelBlockEntity> MACERATION_BARREL_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(BreakingBlocks.MOD_ID, "maceration_barrel"),
                    FabricBlockEntityTypeBuilder.create(MacerationBarrelBlockEntity::new, ModBlocks.MACERATION_BARREL).build()
            );

    public static final BlockEntityType<DryingStationBlockEntity> DRYING_STATION_BE =
            Registry.register(
                    Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(BreakingBlocks.MOD_ID, "drying_station"),
                    FabricBlockEntityTypeBuilder.create(DryingStationBlockEntity::new, ModBlocks.DRYING_STATION).build()
            );

    public static void RegisterBlockEntities() {
        BreakingBlocks.LOGGER.info("Registering Block Entities for " + BreakingBlocks.MOD_ID);
        BreakingBlocks.LOGGER.info("[BreakingBlocks] MACERATION_BARREL_BE id=" + Registries.BLOCK_ENTITY_TYPE.getId(MACERATION_BARREL_BE));
    }
}