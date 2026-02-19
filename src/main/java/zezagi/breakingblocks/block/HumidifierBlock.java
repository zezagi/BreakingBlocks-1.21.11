package zezagi.breakingblocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;
import zezagi.breakingblocks.blockentity.HumidifierBlockEntity;
import zezagi.breakingblocks.blockentity.MacerationBarrelBlockEntity;
import zezagi.breakingblocks.blockentity.ModBlockEntities;

public class HumidifierBlock extends HorizontalFacingBlock implements BlockEntityProvider {

    public static final MapCodec<HumidifierBlock> CODEC = createCodec(HumidifierBlock::new);
    public static final BooleanProperty WATERED = BooleanProperty.of("watered");

    private static final VoxelShape SHAPE_NORTH = Block.createCuboidShape(0.0, 0.0, 2.0, 16.0, 16.0, 16.0);

    private static final VoxelShape SHAPE_SOUTH = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 14.0);

    private static final VoxelShape SHAPE_EAST = Block.createCuboidShape(0.0, 0.0, 0.0, 14.0, 16.0, 16.0);

    private static final VoxelShape SHAPE_WEST = Block.createCuboidShape(2.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public HumidifierBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERED, false));
    }

    @Override
    protected MapCodec<? extends HorizontalFacingBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        switch (state.get(FACING)) {
            case NORTH: return SHAPE_NORTH;
            case SOUTH: return SHAPE_SOUTH;
            case EAST: return SHAPE_EAST;
            case WEST: return SHAPE_WEST;
            default: return SHAPE_NORTH;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.HUMIDIFIER_BE) {
            return (world1, pos, state1, blockEntity) -> HumidifierBlockEntity.tick(world1, pos, state1, (HumidifierBlockEntity) blockEntity);
        }
        return null;
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERED);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        boolean isFull = state.get(WATERED);
        BlockEntity be = world.getBlockEntity(pos);

        if(be instanceof HumidifierBlockEntity humidifierBE)
        {
            if (stack.isOf(Items.WATER_BUCKET) && !isFull) {
                if (!world.isClient()) {
                    if (!player.isCreative()) {
                        player.setStackInHand(hand, new ItemStack(Items.BUCKET));
                    }
                    world.setBlockState(pos, state.with(WATERED, true), 3);
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    humidifierBE.refillWater();
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HumidifierBlockEntity(pos, state);
    }
}