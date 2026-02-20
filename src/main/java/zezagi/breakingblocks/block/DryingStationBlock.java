package zezagi.breakingblocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.blockentity.DryingStationBlockEntity;
import zezagi.breakingblocks.blockentity.ModBlockEntities;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.util.QualityTier;

public class DryingStationBlock extends Block implements BlockEntityProvider {

    public static final MapCodec<DryingStationBlock> CODEC = createCodec(DryingStationBlock::new);
    public static final IntProperty PASTE = IntProperty.of("paste", 0, 4);
    public static final BooleanProperty DRY = BooleanProperty.of("dry");
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);

    public DryingStationBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(PASTE, 0).with(DRY, false).with(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<DryingStationBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PASTE, DRY, FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DryingStationBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.DRYING_STATION_BE) {
            return (world1, pos, state1, blockEntity) -> DryingStationBlockEntity.tick(world1, pos, state1, (DryingStationBlockEntity) blockEntity);
        }
        return null;
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int currentPaste = state.get(PASTE);
        boolean isDry = state.get(DRY);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof DryingStationBlockEntity dryingBE)) {
            return ActionResult.PASS;
        }

        if (currentPaste == 4 && isDry) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(PASTE, 0).with(DRY, false), 3);

                QualityTier finalQuality = dryingBE.calculateFinalQuality();
                dryingBE.resetQuality();

                ItemStack dryPasteStack = new ItemStack(ModItems.DRYED_COKE, 1);
                dryPasteStack.set(ModComponents.QUALITY, finalQuality);

                if (!player.getInventory().insertStack(dryPasteStack)) {
                    player.dropItem(dryPasteStack, false);
                }
                world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_SAND_BREAK, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }

        if (stack.isOf(ModItems.COKE_PASTE)) {
            if (currentPaste < 4 && !isDry) {
                if (!world.isClient()) {

                    QualityTier pasteQuality = stack.getOrDefault(ModComponents.QUALITY, QualityTier.NORMAL);

                    dryingBE.addPasteQuality(pasteQuality);

                    world.setBlockState(pos, state.with(PASTE, currentPaste + 1), 3);
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    float randPitch = world.getRandom().nextFloat() + 1.5f;
                    world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_SLIME_BLOCK_PLACE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, randPitch);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}