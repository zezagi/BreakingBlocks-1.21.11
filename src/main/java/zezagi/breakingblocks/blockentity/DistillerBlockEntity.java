package zezagi.breakingblocks.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import com.mojang.serialization.Codec;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;


public class DistillerBlockEntity extends BlockEntity {

    private static final int REQUIRED_FUEL_LEVEL = 3;
    private static final int MAX_BURNING_PROGRESS = 2000;

    private int fuelLevel = 0;
    private boolean isFull = false;
    private boolean isBurning = false;
    private int burningProgress = 0;
    private long startedBurningTick = -1;

    public DistillerBlockEntity(BlockPos pos, BlockState state) {

        super(ModBlockEntities.DISTILLER_BE, pos, state);
    }

    public int getFuelLevel() {
        return fuelLevel;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("fuel_level", Codec.INT, this.fuelLevel);
        view.put("is_full", Codec.BOOL, this.isFull);
        view.put("is_burning", Codec.BOOL, this.isBurning);
        view.put("burning_progress", Codec.INT, this.burningProgress);
        view.put("started_burning_tick", Codec.LONG, this.startedBurningTick);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.fuelLevel = view.getInt("fuel_level", 0);
        this.isFull = view.getBoolean("is_full", false);
        this.isBurning = view.getBoolean("is_burning", false);
        this.burningProgress = view.getInt("burning_progress", 0);
        this.startedBurningTick = view.getLong("started_burning_tick", -1L);
    }

    public void setFuelLevel(int fuelLevel) {
        this.fuelLevel = fuelLevel;
        markDirty();
    }

    public boolean canAcceptFuel() {
        return !isFull && !isBurning && fuelLevel < REQUIRED_FUEL_LEVEL;
    }

    public void addFuel(int fuelCount) {
        this.fuelLevel += fuelCount;
        markDirty();

        if (this.fuelLevel >= REQUIRED_FUEL_LEVEL) {
            this.isBurning = true;
            this.fuelLevel = 0;
            sync();
            markDirty();
            if (world != null && !world.isClient()) {
                net.minecraft.block.BlockState currentState = world.getBlockState(pos);
                world.setBlockState(pos, currentState.with(zezagi.breakingblocks.block.DistillerBlock.LEVEL, 1), 3);
            }
        }

    }

    public static void tick(World world, BlockPos pos, BlockState state, DistillerBlockEntity be) {
        if (world.isClient()) {
            if (be.isBurning) {
                if (world.getRandom().nextDouble() < 0.5) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.2;
                    double z = pos.getZ() + 0.5;
                    world.addParticleClient(net.minecraft.particle.ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0, 0.04, 0.0);
                }
            }
            return;
        }

        if (be.isBurning) {
            if (be.startedBurningTick == -1) {
                be.startedBurningTick = world.getTime();
                be.sync();
                be.markDirty();
            }

            be.burningProgress++;

            int expectedLevel = 1;

            int third = MAX_BURNING_PROGRESS / 3;
            if (be.burningProgress > third * 2) {
                expectedLevel = 3;
            } else if (be.burningProgress > third) {
                expectedLevel = 2;
            }

            net.minecraft.block.BlockState currentTickState = world.getBlockState(pos);
            if (currentTickState.get(zezagi.breakingblocks.block.DistillerBlock.LEVEL) != expectedLevel) {
                world.setBlockState(pos, currentTickState.with(zezagi.breakingblocks.block.DistillerBlock.LEVEL, expectedLevel), 3);
            }

            if (world.getRandom().nextDouble() < 0.15) {
                world.playSound(null, pos, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            }

            if (be.burningProgress >= MAX_BURNING_PROGRESS) {
                be.isBurning = false;
                be.isFull = true;
                be.burningProgress = 0;
                be.startedBurningTick = -1;
                be.sync();
                be.markDirty();

                net.minecraft.block.BlockState finalState = world.getBlockState(pos);
                world.setBlockState(pos, finalState.with(zezagi.breakingblocks.block.DistillerBlock.LEVEL, 4), 3);
            }
        }
    }

    @Nullable
    @Override
    public net.minecraft.network.packet.Packet<net.minecraft.network.listener.ClientPlayPacketListener> toUpdatePacket() {
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt(net.minecraft.registry.RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    private void sync() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    public boolean isGasolineReady() {
        return !isBurning && isFull;
    }

    public void consumeGasoline() {
        this.isFull = false;
        markDirty();
        sync();
    }

    public int getTimeToFinishBurningInTicks() {
        if (isBurning) return MAX_BURNING_PROGRESS - burningProgress;
        else return -1;
    }
}