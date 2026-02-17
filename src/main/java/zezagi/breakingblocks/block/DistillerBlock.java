package zezagi.breakingblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import org.jetbrains.annotations.Nullable;
import zezagi.breakingblocks.blockEntity.ModBlockEntities;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.item.CanisterItem;
import zezagi.breakingblocks.blockEntity.DistillerBlockEntity;

public class DistillerBlock extends Block implements BlockEntityProvider {

    public static final EnumProperty<DoubleBlockHalf> HALF = Properties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<Direction> FACING = Properties.HORIZONTAL_FACING;

    public static final net.minecraft.state.property.IntProperty LEVEL = net.minecraft.state.property.IntProperty.of("level", 0, 4);

    public DistillerBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(HALF, DoubleBlockHalf.LOWER)
                .with(FACING, Direction.NORTH)
                .with(LEVEL, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING, LEVEL);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        if (pos.getY() < world.getBottomY() + world.getHeight() - 1 && world.getBlockState(pos.up()).canReplace(ctx)) {
            return this.getDefaultState()
                    .with(HALF, DoubleBlockHalf.LOWER)
                    .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                    .with(LEVEL, 0);
        }
        return null;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER).with(LEVEL, 0), 3);
        if (!world.isClient()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DistillerBlockEntity distillerBE) {
                int fuel = itemStack.getOrDefault(ModComponents.FUEL_LEVEL, 0);
                distillerBE.setFuelLevel(fuel);
            }
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView, BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        DoubleBlockHalf half = state.get(HALF);
        if (direction.getAxis() == Direction.Axis.Y && half == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
            if (!neighborState.isOf(this) || neighborState.get(HALF) == half) {
                return Blocks.AIR.getDefaultState();
            }
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (state.get(HALF) == DoubleBlockHalf.LOWER) {
            return new DistillerBlockEntity(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.DISTILLER_BE) {
            return (world1, pos, state1, blockEntity) -> DistillerBlockEntity.tick(world1, pos, state1, (DistillerBlockEntity) blockEntity);
        }
        return null;
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && !player.isCreative()) {
            BlockPos actualPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();
            BlockEntity blockEntity = world.getBlockEntity(actualPos);

            if (blockEntity instanceof DistillerBlockEntity distillerBE) {
                ItemStack drop = new ItemStack(this.asItem());

                if (distillerBE.getFuelLevel() > 0) {
                    drop.set(zezagi.breakingblocks.ModComponents.FUEL_LEVEL, distillerBE.getFuelLevel());
                }

                Block.dropStack(world, actualPos, drop);
            }
        }
        return super.onBreak(world, pos, state, player);
    }


    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(Items.COAL_BLOCK)) {
            BlockPos actualPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();

            if (!world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(actualPos);
                if (blockEntity instanceof DistillerBlockEntity distillerBE && distillerBE.canAcceptFuel()) {
                    if (!player.getAbilities().creativeMode) {
                        stack.decrement(1);
                    }
                    distillerBE.addFuel(1);
                    float randPitch = 0.7f + world.getRandom().nextFloat() * 0.6f;
                    world.playSound(null, actualPos, SoundEvents.BLOCK_BAMBOO_WOOD_PLACE, SoundCategory.BLOCKS, 1.0f, randPitch);
                    return ActionResult.SUCCESS;
                }
            } else {
                return ActionResult.SUCCESS;
            }
        }
        else if (stack.isOf(ModItems.CANISTER)) {
            BlockPos actualPos = state.get(HALF) == DoubleBlockHalf.LOWER ? pos : pos.down();

            if (!world.isClient()) {
                BlockEntity blockEntity = world.getBlockEntity(actualPos);
                if (blockEntity instanceof DistillerBlockEntity distillerBE) {
                    if (!distillerBE.isGasolineReady()) return ActionResult.PASS;

                    int currentGasolineLevelInCanister = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
                    if (currentGasolineLevelInCanister == CanisterItem.MAX_CAPACITY) {
                        return ActionResult.PASS;
                    } else {
                        distillerBE.consumeGasoline();

                        BlockState lowerState = world.getBlockState(actualPos);
                        world.setBlockState(actualPos, lowerState.with(LEVEL, 0), 3);

                        BlockState upperState = world.getBlockState(actualPos.up());
                        if (upperState.isOf(this)) {
                            world.setBlockState(actualPos.up(), upperState.with(LEVEL, 0), 3);
                        }

                        ItemStack filledCanister = new ItemStack(ModItems.CANISTER);
                        filledCanister.set(ModComponents.GASOLINE_LEVEL, CanisterItem.MAX_CAPACITY);

                        if (stack.getCount() == 1) {
                            player.setStackInHand(hand, filledCanister);
                        } else {
                            stack.decrement(1);
                            if (!player.getInventory().insertStack(filledCanister)) {
                                player.dropItem(filledCanister, false);
                            }
                        }

                        world.playSound(null, actualPos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_FILL, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);

                        return ActionResult.SUCCESS;
                    }
                }
            } else {
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
}