package zezagi.breakingblocks.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zezagi.breakingblocks.block.DryingStationBlock;
import zezagi.breakingblocks.util.QualityTier;

public class DryingStationBlockEntity extends BlockEntity {

    public static final int MAX_DRYING_TIME = 700;
    public static final int MIN_DRYING_TIME = 400;

    private long dryingStartedAt = -1;
    private int drawnDryingTime = 0;
    private int dryingProgress = 0;

    private int accumulatedPasteQuality = 0;

    public DryingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DRYING_STATION_BE, pos, state);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("dryingStartedAt", Codec.LONG, this.dryingStartedAt);
        view.put("drawnDryingTime", Codec.INT, this.drawnDryingTime);
        view.put("dryingProgress", Codec.INT, this.dryingProgress);
        view.put("accumulatedPasteQuality", Codec.INT, this.accumulatedPasteQuality);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.dryingStartedAt = view.getLong("dryingStartedAt", -1);
        this.drawnDryingTime = view.getInt("drawnDryingTime", 0);
        this.dryingProgress = view.getInt("dryingProgress", 0);
        this.accumulatedPasteQuality = view.getInt("accumulatedPasteQuality", 0);
    }

    public void addPasteQuality(QualityTier tier) {
        this.accumulatedPasteQuality += tier.ordinal();
        markDirty();
    }

    public QualityTier calculateFinalQuality() {
        if (this.accumulatedPasteQuality == 0) return QualityTier.LOW;

        float average = this.accumulatedPasteQuality / 4.0f;
        int finalOrdinal = Math.round(average);

        finalOrdinal = Math.max(0, Math.min(finalOrdinal, QualityTier.values().length - 1));
        return QualityTier.values()[finalOrdinal];
    }

    public void resetQuality() {
        this.accumulatedPasteQuality = 0;
        markDirty();
    }

    public static void tick(World world, BlockPos pos, BlockState state, DryingStationBlockEntity be) {
        boolean isDry = state.get(DryingStationBlock.DRY);
        int pasteCount = state.get(DryingStationBlock.PASTE);

        if (world.isClient()) {
            if (!isDry && pasteCount == 4 && world.getRandom().nextDouble() < 0.05) {
                double x = pos.getX() + 0.2 + world.getRandom().nextDouble() * 0.6;
                double y = pos.getY() + 0.3;
                double z = pos.getZ() + 0.2 + world.getRandom().nextDouble() * 0.6;
                world.addParticleClient(ParticleTypes.SMOKE, x, y, z, 0.0, 0.02, 0.0);
            }
            return;
        }

        if (!isDry && pasteCount == 4) {
            if (be.dryingStartedAt == -1 || be.drawnDryingTime < MIN_DRYING_TIME) {
                be.dryingStartedAt = world.getTime();
                be.drawnDryingTime = world.getRandom().nextBetween(MIN_DRYING_TIME, MAX_DRYING_TIME);
            }

            be.dryingProgress++;

            if (world.getRandom().nextDouble() < 0.02) {
                float randPitch = world.getRandom().nextFloat() * 0.5f + 0.8f;
                world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 0.2f, randPitch);
            }

            if (be.dryingProgress >= be.drawnDryingTime) {
                world.setBlockState(pos, state.with(DryingStationBlock.DRY, true), 3);
                be.dryingProgress = 0;
                be.dryingStartedAt = -1;
            }
        } else {
            be.dryingProgress = 0;
            be.dryingStartedAt = -1;
        }
    }
}