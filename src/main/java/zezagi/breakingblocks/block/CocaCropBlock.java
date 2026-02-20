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
import net.minecraft.particle.ParticleTypes;
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
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.util.FertilizerItem;
import zezagi.breakingblocks.util.QualityTier;

public class CocaCropBlock extends PlantBlock implements Fertilizable {

    public static final MapCodec<CocaCropBlock> CODEC = createCodec(CocaCropBlock::new);

    public static final int MAX_AGE = 10;
    public static final IntProperty AGE = IntProperty.of("age", 0, MAX_AGE);
    public static final IntProperty CROP_QUALITY = IntProperty.of("crop_quality", 0, 7);
    public static final IntProperty ACTIVE_FERTILIZER = IntProperty.of("active_fertilizer", 0, FertilizerItem.FERTILIZERS_QUANTITY);
    public static final IntProperty SHEAR_COUNT = IntProperty.of("shear_count", 0, 3);

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
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(AGE, 0)
                .with(CROP_QUALITY, 0)
                .with(ACTIVE_FERTILIZER, 0)
                .with(SHEAR_COUNT, 0));
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
        builder.add(AGE, CROP_QUALITY, ACTIVE_FERTILIZER, SHEAR_COUNT);
    }

    @Override
    protected boolean canPlantOnTop(BlockState floor, BlockView world, BlockPos pos) {
        return floor.isOf(Blocks.FARMLAND);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int fertilizerType = state.get(ACTIVE_FERTILIZER);

        if (fertilizerType != 0 && random.nextInt(7) == 0) {
            double d = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.5;
            double e = pos.getY() + 0.2 + random.nextDouble() * 0.5;
            double f = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.5;

            switch (fertilizerType) {
                case 1 -> world.addParticleClient(ParticleTypes.HAPPY_VILLAGER, d, e, f, 0.0, 0.0, 0.0);
                case 2 -> world.addParticleClient(ParticleTypes.CRIMSON_SPORE, d, e, f, 0.0, 0.0, 0.0);
                case 3 -> world.addParticleClient(ParticleTypes.WITCH, d, e, f, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        int age = state.get(AGE);

        if (stack.isOf(Items.SHEARS) && (age >= 5 && age != 8 && age != 9)) {
            if (!world.isClient()) {
                int currentShears = state.get(SHEAR_COUNT);
                int nextShear = currentShears + 1;

                BlockState nextState = state.with(AGE, 8);

                if (nextShear >= 3) {
                    nextState = nextState.with(SHEAR_COUNT, 0).with(ACTIVE_FERTILIZER, 0);

                    float randPitch = world.getRandom().nextFloat() + 0.3f;
                    world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, randPitch);

                    if (world instanceof ServerWorld serverWorld) {
                        serverWorld.spawnParticles(ParticleTypes.SMOKE,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                10,
                                0.2, 0.2, 0.2,
                                0.01
                        );

                        serverWorld.spawnParticles(ParticleTypes.ASH,
                                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                                5, 0.3, 0.1, 0.3, 0.01
                        );
                    }
                } else {
                    nextState = nextState.with(SHEAR_COUNT, nextShear);
                }

                world.setBlockState(pos, nextState, Block.NOTIFY_LISTENERS);
                handleDrop(world, pos, age, state.get(CROP_QUALITY), state.get(ACTIVE_FERTILIZER));
                world.playSound(null, pos, SoundEvents.ENTITY_SHEEP_SHEAR, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        else if (stack.getItem() instanceof FertilizerItem fertilizerItem) {
            int currentType = state.get(ACTIVE_FERTILIZER);
            int newType = fertilizerItem.getFertilizerType();

            if (currentType == newType || newType > FertilizerItem.LAST_CROP_FERTILIZER_INDEX) {
                return ActionResult.PASS;
            }

            if (world.isClient()) {
                spawnFertilizerParticles(world, pos, currentType != 0);
                return ActionResult.SUCCESS;
            } else {
                world.setBlockState(pos, state.with(ACTIVE_FERTILIZER, newType), Block.NOTIFY_LISTENERS);
                if (!player.isCreative()) stack.decrement(1);

                float randPitch = world.getRandom().nextFloat() + 0.7f;
                world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_FILL_SUCCESS, SoundCategory.BLOCKS, 1.0f, randPitch);

                return ActionResult.SUCCESS;
            }
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    private void spawnFertilizerParticles(World world, BlockPos pos, boolean isOverride) {
        Random random = world.getRandom();
        double d = pos.getX() + 0.5;
        double e = pos.getY() + 0.5;
        double f = pos.getZ() + 0.5;

        if (isOverride) {
            for (int i = 0; i < 5; i++) {
                world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, d + (random.nextDouble() - 0.5) * 0.5, e + random.nextDouble() * 0.5, f + (random.nextDouble() - 0.5) * 0.5, 0.0, 0.05, 0.0);
            }
            for (int i = 0; i < 8; i++) {
                world.addParticleClient(ParticleTypes.WITCH, d + (random.nextDouble() - 0.5) * 0.6, e + random.nextDouble() * 0.5, f + (random.nextDouble() - 0.5) * 0.6, 0.0, 0.1, 0.0);
            }
            for (int i = 0; i < 4; i++) {
                world.addParticleClient(ParticleTypes.WHITE_ASH, d + (random.nextDouble() - 0.5) * 0.5, e + random.nextDouble() * 0.5, f + (random.nextDouble() - 0.5) * 0.5, 0.0, 0.02, 0.0);
            }
        } else {
            for (int i = 0; i < 7; i++) {
                world.addParticleClient(ParticleTypes.END_ROD, d + (random.nextDouble() - 0.5) * 0.5, e + random.nextDouble() * 0.5, f + (random.nextDouble() - 0.5) * 0.5, 0.0, 0.5, 0.0);
            }
            for (int i = 0; i < 4; i++) {
                world.addParticleClient(ParticleTypes.HAPPY_VILLAGER, d + (random.nextDouble() - 0.5) * 0.6, e + random.nextDouble() * 0.5, f + (random.nextDouble() - 0.5) * 0.6, 0.0, 0.1, 0.0);
            }
        }
    }

    private void handleDrop(World world, BlockPos pos, int age, int quality, int fertilizerType) {
        ItemStack leavesStack = new ItemStack(ModItems.COCA_LEAF);

        int fertilizerBonus = fertilizerType == 2 ? world.getRandom().nextInt(3) + 1 : 0;

        if (quality == 7) {
            leavesStack.set(ModComponents.QUALITY, QualityTier.HIGH);
        } else if (quality <= 2) {
            leavesStack.set(ModComponents.QUALITY, QualityTier.LOW);
        }

        if (age == 7) {
            leavesStack.setCount(world.getRandom().nextInt(5) + 5 + fertilizerBonus);
            dropStack(world, pos, new ItemStack(ModItems.COCA_SEEDS, world.getRandom().nextInt(2)));
            dropStack(world, pos, leavesStack);
        } else if (age == 10) {
            leavesStack.setCount(3 + world.getRandom().nextInt(4) + fertilizerBonus);
            dropStack(world, pos, leavesStack);
        } else if (age == 5 || age == 6) {
            leavesStack.setCount(3 + world.getRandom().nextInt(3) + fertilizerBonus);
            dropStack(world, pos, leavesStack);
        }
    }

    private boolean isHumidifierNearbyAndWorking(World world, BlockPos pos) {
        for (BlockPos checkPos : BlockPos.iterate(pos.add(-5, -2, -5), pos.add(5, 2, 5))) {
            BlockState state = world.getBlockState(checkPos);
            if (state.getBlock() == ModBlocks.HUMIDIFIER) {
                if (state.get(HumidifierBlock.WATERED)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (world.getBaseLightLevel(pos, 0) >= 9) {
            int age = state.get(AGE);
            boolean hasHumidifier = isHumidifierNearbyAndWorking(world, pos);

            int bound = hasHumidifier ? 3 : 5;
            if (state.get(ACTIVE_FERTILIZER) == 1) {
                bound = hasHumidifier ? 2 : 3;
            }

            if (random.nextInt(bound) == 0) {
                int currentQuality = state.get(CROP_QUALITY);
                int newQuality = currentQuality;

                if (hasHumidifier) {
                    int fertilizerBonus = state.get(ACTIVE_FERTILIZER) == 3 && world.getRandom().nextDouble() < 0.1 ? 1 : 0;
                    newQuality = Math.min(7, currentQuality + 1 + fertilizerBonus);
                } else {
                    if (state.get(ACTIVE_FERTILIZER) != 3) {
                        newQuality = Math.max(0, currentQuality - 1);
                    }
                }

                if (age == 10) {
                    world.setBlockState(pos, state.with(AGE, 5).with(CROP_QUALITY, newQuality), Block.NOTIFY_LISTENERS);
                } else if (age < 10 && age != 7) {
                    world.setBlockState(pos, state.with(AGE, age + 1).with(CROP_QUALITY, newQuality), Block.NOTIFY_LISTENERS);
                }
            }
        }
    }

    @Override
    public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        int age = state.get(AGE);
        int growth = 1 + random.nextInt(2);

        if (world.getBaseLightLevel(pos, 0) < 7) {
            world.breakBlock(pos, true);
            return;
        }

        int currentQuality = state.get(CROP_QUALITY);
        boolean hasHumidifier = isHumidifierNearbyAndWorking(world, pos);
        int newQuality = hasHumidifier ? Math.min(7, currentQuality + growth) : Math.max(0, currentQuality - growth);

        if (age >= 8) {
            int newAge = age + growth;
            if (newAge > 10) {
                world.setBlockState(pos, state.with(AGE, 5).with(CROP_QUALITY, newQuality), Block.NOTIFY_LISTENERS);
            } else {
                world.setBlockState(pos, state.with(AGE, newAge).with(CROP_QUALITY, newQuality), Block.NOTIFY_LISTENERS);
            }
        } else if (age < 7) {
            int newAge = age + growth;
            if (newAge > 7) {
                newAge = 7;
            }
            world.setBlockState(pos, state.with(AGE, newAge).with(CROP_QUALITY, newQuality), Block.NOTIFY_LISTENERS);
        }
    }
}