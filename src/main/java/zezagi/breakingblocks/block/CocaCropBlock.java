package zezagi.breakingblocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import zezagi.breakingblocks.item.ModItems;

public class CocaCropBlock extends PlantBlock implements Fertilizable {

    public static final MapCodec<CocaCropBlock> CODEC = createCodec(CocaCropBlock::new);

    public static final int MAX_AGE = 10;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);

    private static final VoxelShape[] AGE_TO_SHAPE = new VoxelShape[]{
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 14.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0),
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 32.0, 16.0)
    };

    public CocaCropBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(AGE, 0));
    }

    @Override
    public MapCodec<CocaCropBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return AGE_TO_SHAPE[state.get(AGE)];
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int age = state.get(AGE);

        if (stack.isOf(Items.SHEARS) && (age == 7 || age==10)) {
            if (!world.isClient()) {
                world.setBlockState(pos, state.with(AGE, 8), Block.NOTIFY_LISTENERS);
                HandleDrop(world, pos, age);
                world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private void HandleDrop(World world, BlockPos pos, int age) {
        if(age == 7) {
            dropStack(world, pos, new ItemStack(ModItems.COCA_SEEDS, world.getRandom().nextInt(2)));
            dropStack(world, pos, new ItemStack(ModItems.COCA_LEAF, 5 + world.getRandom().nextInt(5)));
        }
        else if(age==10)
        {
            dropStack(world, pos, new ItemStack(ModItems.COCA_LEAF, world.getRandom().nextInt(5)));
        }
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = state.get(AGE);
            if (random.nextInt(5) == 0) {
                if (age == 10) {
                    world.setBlockState(pos, state.with(AGE, 5), Block.NOTIFY_LISTENERS);
                } else if (age < 10 && age != 7) {
                    world.setBlockState(pos, state.with(AGE, age + 1), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return state.get(AGE) != 7;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        int growth = 1 + random.nextInt(2);

        if(world.getBaseLightLevel(pos, 0) < 7)
        {
            world.breakBlock(pos, true);
            return;
        }

        if (age >= 8) {
            int newAge = age + growth;
            if (newAge > 10) {
                world.setBlockState(pos, state.with(AGE, 5), Block.NOTIFY_LISTENERS);
            } else {
                world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
            }
        } else if (age < 7) {
            int newAge = age + growth;
            if (newAge > 7) {
                newAge = 7;
            }
            world.setBlockState(pos, state.with(AGE, newAge), Block.NOTIFY_LISTENERS);
        }
    }
}