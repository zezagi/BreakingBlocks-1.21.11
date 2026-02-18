package zezagi.breakingblocks.blockEntity;

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

public class MacerationBarrelBlockEntity extends BlockEntity {

    public int MAX_GASOLINE_LEVEL = 100;
    public int MAX_LEAVES_LEVEL = 64;
    public int MAX_PRODUCTION_PROGRESS = 600;

    int gasolineLevel = 0;
    int leavesLevel = 0;

    private boolean isProductionEnabled = false;
    long productionStartedAtTick = -1;
    int productionProgress = 0;

    private boolean isPasteReadyToCollect = false;

    public boolean isPasteReadyToCollect() {return isPasteReadyToCollect;}
    public boolean isProductionEnabled(){ return isProductionEnabled; }

    public MacerationBarrelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MACERATION_BARREL_BE, pos, state);
    }

    public int getGasolineLevel() {
        return this.gasolineLevel;
    }

    public int getLeavesLevel() {
        return this.leavesLevel;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.put("gasolineLevel", Codec.INT, this.gasolineLevel);
        view.put("leavesCount", Codec.INT, this.leavesLevel);
        view.put("isProductionEnabled", Codec.BOOL, this.isProductionEnabled);
        view.put("productionStartedAtTick", Codec.LONG, this.productionStartedAtTick);
        view.put("productionProgress", Codec.INT, this.productionProgress);
        view.put("isPasteReadyToCollect", Codec.BOOL, this.isPasteReadyToCollect);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.gasolineLevel = view.getInt("gasolineLevel", 0);
        this.leavesLevel = view.getInt("leavesCount", 0);
        this.isProductionEnabled = view.getBoolean("isProductionEnabled", false);
        this.productionStartedAtTick = view.getLong("productionStartedAtTick", -1);
        this.productionProgress = view.getInt("productionProgress", 0);
        this.isPasteReadyToCollect = view.getBoolean("isPasteReadyToCollect", false);
    }

    @Override
    public net.minecraft.network.packet.Packet<net.minecraft.network.listener.ClientPlayPacketListener> toUpdatePacket() {
        return net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public net.minecraft.nbt.NbtCompound toInitialChunkDataNbt(net.minecraft.registry.RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    public void sync() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
            markDirty();
        }
    }

    public int addGasolineAndReturnRest(int amount) {
        int spaceLeft = MAX_GASOLINE_LEVEL - this.gasolineLevel;
        int rest = Math.max(0, amount - spaceLeft);
        this.gasolineLevel = Math.min(this.gasolineLevel + amount, MAX_GASOLINE_LEVEL);

        if(isReadyToProduce())
            isProductionEnabled = true;

        sync();
        return rest;
    }

    public int addLeavesAndReturnRest(int amount) {
        int spaceLeft = MAX_LEAVES_LEVEL - this.leavesLevel;
        int rest = Math.max(0, amount - spaceLeft);
        this.leavesLevel = Math.min(this.leavesLevel+amount, MAX_LEAVES_LEVEL);

        if(isReadyToProduce())
            isProductionEnabled = true;

        sync();
        return rest;
    }

    public boolean isReadyToProduce()
    {
        return this.leavesLevel == MAX_LEAVES_LEVEL && this.gasolineLevel == MAX_GASOLINE_LEVEL;
    }

    public void collectPaste()
    {
        this.isPasteReadyToCollect = false;
        sync();
    }

    public static void tick(World world, BlockPos pos, BlockState state, MacerationBarrelBlockEntity be)
    {
        if(world.isClient())
        {
            if(be.isProductionEnabled())
            {
                if (world.getRandom().nextDouble() < 0.5) {
                    double x = pos.getX() + 0.5;
                    double y = pos.getY() + 1.4;
                    double z = pos.getZ() + 0.5;
                    world.addParticleClient(ParticleTypes.GLOW_SQUID_INK, x, y, z, 0.0, 0.04, 0.0);
                }
            }
            return;
        }
        if(be.isProductionEnabled())
        {
            if(be.productionStartedAtTick==-1)
            {
                be.productionStartedAtTick = world.getTime();
                be.sync();
            }
            be.productionProgress++;
            if (world.getRandom().nextDouble() < 0.15) {
                float randPitch = world.getRandom().nextFloat() + 0.5f;
                world.playSound(null, pos, SoundEvents.BLOCK_WET_SPONGE_BREAK, SoundCategory.BLOCKS, 1.0f, randPitch);
            }
            if(be.productionProgress>=be.MAX_PRODUCTION_PROGRESS)
            {
                be.isProductionEnabled = false;
                be.isPasteReadyToCollect = true;
                be.productionStartedAtTick = -1;
                be.productionProgress = 0;

                be.gasolineLevel = 0;
                be.leavesLevel = 0;

                be.sync();
            }
        }
    }




}
