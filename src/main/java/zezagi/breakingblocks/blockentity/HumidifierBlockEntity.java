package zezagi.breakingblocks.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zezagi.breakingblocks.block.HumidifierBlock;
import zezagi.breakingblocks.block.ModBlocks;

public class HumidifierBlockEntity extends BlockEntity {

    private final int MAX_WATER_LEVEL = 1000;

    private int waterLevel = 0;

    public int getWaterLevel()
    {
        return waterLevel;
    }

    public void refillWater()
    {
        waterLevel = MAX_WATER_LEVEL;
        markDirty();
    }

    public HumidifierBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HUMIDIFIER_BE, pos, state);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("waterLevel", Codec.INT, this.waterLevel);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.waterLevel = view.getInt("waterLevel", 0);
    }

    public static void tick(World world, BlockPos pos, BlockState state, HumidifierBlockEntity blockEntity) {
        int waterLevel = blockEntity.waterLevel;
        if (world.isClient()) {
            boolean isWorking = state.get(HumidifierBlock.WATERED);
            if (isWorking) {
                if (world.getRandom().nextInt(3) == 0) {
                    double centerX = pos.getX() + 0.5;
                    double centerY = pos.getY() + 0.9;
                    double centerZ = pos.getZ() + 0.5;
                    world.addParticleClient(ParticleTypes.CAMPFIRE_COSY_SMOKE, centerX, centerY, centerZ, 0.0, 0.07, 0.0);
                }

                for (int i = 0; i < 3; i++) {
                    double randomX = (world.getRandom().nextDouble() - 0.5) * 11.0;
                    double randomY = (world.getRandom().nextDouble() - 0.5) * 5.0;
                    double randomZ = (world.getRandom().nextDouble() - 0.5) * 11.0;

                    double pX = pos.getX() + 0.5 + randomX;
                    double pY = pos.getY() + 0.5 + randomY;
                    double pZ = pos.getZ() + 0.5 + randomZ;

                    world.addParticleClient(ParticleTypes.SPLASH, pX, pY, pZ, 0.0, 0.0, 0.0);
                }
            }
            return;
        }

        if(waterLevel>0)
        {
            blockEntity.waterLevel--;
            if (blockEntity.waterLevel == 0) {
                if (state.get(HumidifierBlock.WATERED)) {
                    world.setBlockState(pos, state.with(HumidifierBlock.WATERED, false), 3);
                    world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1.0f, 1.3f);
                }
            }
            markDirty(world, pos, state);
        }
        else
        {
            if(state.get(HumidifierBlock.WATERED))
            {
                world.setBlockState(pos, state.with(HumidifierBlock.WATERED, false), 3);
                world.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 1.0f, 1.3f);
            }
            markDirty(world, pos, state);
        }
    }


}
