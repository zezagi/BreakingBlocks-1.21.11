package zezagi.breakingblocks.blockEntity;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.jspecify.annotations.Nullable;

public class MacerationBarrelBlockEntity extends BlockEntity {

    public int MAX_GASOLINE_LEVEL = 100;
    public int MAX_LEAVES_LEVEL = 64;

    int gasolineLevel = 0;
    int leavesLevel = 0;
    boolean isProductionEnabled = false;

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
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.gasolineLevel = view.getInt("gasolineLevel", 0);
        this.leavesLevel = view.getInt("leavesCount", 0);
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
        sync();
        return rest;
    }

    public int addLeavesAndReturnRest(int amount) {
        int spaceLeft = MAX_LEAVES_LEVEL - this.leavesLevel;
        int rest = Math.max(0, amount - spaceLeft);
        this.leavesLevel = Math.min(this.leavesLevel+amount, MAX_LEAVES_LEVEL);
        sync();
        return rest;
    }

    public boolean isReadyToProduce()
    {
        return this.leavesLevel == MAX_LEAVES_LEVEL && this.gasolineLevel == MAX_GASOLINE_LEVEL;
    }

    public void startProducing()
    {

    }




}
