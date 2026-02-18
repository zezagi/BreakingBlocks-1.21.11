package zezagi.breakingblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CocaineBrickBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(3.0, 0.0, 4.0, 14.0, 2.0, 11.0);

    public CocaineBrickBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}