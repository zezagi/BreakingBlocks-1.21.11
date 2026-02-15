package zezagi.breakingblocks.item.customItem;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class MacerationBarrelBlock extends Block {

    public static final MapCodec<MacerationBarrelBlock> CODEC = createCodec(MacerationBarrelBlock::new);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(0.0, 2.0, 0.0, 16.0, 16.0, 2.0),
            Block.createCuboidShape(0.0, 2.0, 14.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 2.0, 2.0, 2.0, 16.0, 14.0),
            Block.createCuboidShape(14.0, 2.0, 2.0, 16.0, 16.0, 14.0)
    );

    public MacerationBarrelBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}