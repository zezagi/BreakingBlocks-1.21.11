package zezagi.breakingblocks;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.client.render.BlockRenderLayer;
import zezagi.breakingblocks.item.ModBlocks;
import zezagi.breakingblocks.item.ModItems;

public class BreakingBlocksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlocks.COCA_CROP, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ModBlocks.DISTILLER, BlockRenderLayer.TRANSLUCENT);
    }
}