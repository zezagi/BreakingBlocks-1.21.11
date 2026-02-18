package zezagi.breakingblocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import zezagi.breakingblocks.block.ModBlocks;
import zezagi.breakingblocks.blockentity.ModBlockEntities;
import zezagi.breakingblocks.client.render.block.MacerationBarrelBlockEntityRenderer;

public class BreakingBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.COCA_CROP, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.DISTILLER, BlockRenderLayer.TRANSLUCENT);
        BlockEntityRendererFactories.register(ModBlockEntities.MACERATION_BARREL_BE, MacerationBarrelBlockEntityRenderer::new);
    }
}