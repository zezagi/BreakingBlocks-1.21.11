package zezagi.breakingblocks.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.util.QualityTier;


public class BrickPressBlock extends Block {

    public static final IntProperty STATE = IntProperty.of("state", 0, 2);
    public static final IntProperty QUALITY = IntProperty.of("quality", 0, QualityTier.values().length - 1);
    public static final EnumProperty<Direction> FACING = EnumProperty.of("facing", Direction.class, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    private static final VoxelShape SHAPE = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public BrickPressBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(STATE, 0).with(QUALITY, 0).with(FACING, Direction.NORTH));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STATE, QUALITY, FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int currentState = state.get(STATE);
        ItemStack itemStack = player.getStackInHand(player.getActiveHand());

        if (currentState == 0) {
            if (itemStack.isOf(ModItems.DRYED_COKE)) {
                if (!world.isClient()) {
                    QualityTier tier = itemStack.getOrDefault(ModComponents.QUALITY, QualityTier.NORMAL);
                    world.setBlockState(pos, state.with(STATE, 1).with(QUALITY, tier.ordinal()));
                    if (!player.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                }
                return ActionResult.SUCCESS;
            }
        } else if (currentState == 1) {
            if (world.isClient()) {
                for (int i = 0; i < 12; i++) {
                    double offsetX = pos.getX() + 0.2 + world.getRandom().nextDouble() * 0.6;
                    double offsetY = pos.getY() + 0.5;
                    double offsetZ = pos.getZ() + 0.2 + world.getRandom().nextDouble() * 0.6;
                    world.addParticleClient(ParticleTypes.POOF, offsetX, offsetY, offsetZ, 0.0, 0.05, 0.0);
                    world.addParticleClient(ParticleTypes.WHITE_ASH, offsetX, offsetY + 0.1, offsetZ, 0.0, 0.01, 0.0);
                }
            } else {
                float randPitch = world.getRandom().nextFloat() + 0.75f;
                world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.BLOCKS, 1.0f, randPitch);
                world.setBlockState(pos, state.with(STATE, 2));
            }
            return ActionResult.SUCCESS;
        } else if (currentState == 2) {
            if (!world.isClient()) {
                int qualityOrdinal = state.get(QUALITY);
                QualityTier tier = QualityTier.values()[qualityOrdinal];

                ItemStack result = new ItemStack(ModItems.COCAINE_BRICK_ITEM);
                result.set(ModComponents.QUALITY, tier);

                if (!player.getInventory().insertStack(result)) {
                    player.dropItem(result, false);
                }
                world.setBlockState(pos, state.with(STATE, 0).with(QUALITY, 0));
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }
}