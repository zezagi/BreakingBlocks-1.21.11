package zezagi.breakingblocks.blockentity;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import zezagi.breakingblocks.BreakingBlocks;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.util.QualityTier;

public class MacerationBarrelBlockEntity extends BlockEntity {

    public int MAX_GASOLINE_LEVEL = 100;
    public int MAX_LEAVES_LEVEL = 64;
    public int MAX_PRODUCTION_PROGRESS = 600;

    private int gasolineLevel = 0;
    private int leavesLevel = 0;

    private boolean isProductionEnabled = false;
    private long productionStartedAtTick = -1;
    private int productionProgress = 0;

    private boolean isPasteReadyToCollect = false;

    private float accumulatedQualityPoints = 0;
    private int qualityBonus = 0;

    private boolean bleachAdded = false;
    public boolean isBleachAdded() {return bleachAdded;}

    private boolean acetoneAdded = false;
    public boolean isAcetoneAdded() {return acetoneAdded;}

    private boolean acidAdded = false;
    private int reactionTemp = 0;
    public boolean iceNearby = false;
    public boolean isAcidAdded() {return acidAdded;}

    private boolean cobaltAdded = false;
    private boolean needsStiring = false;
    public int notStiredFor = 0;
    public boolean isCobaltAdded() {return cobaltAdded;}
    public boolean needsStiring() {return needsStiring;}
    public void stir() {
        if (needsStiring) {
            needsStiring = false;
            notStiredFor = 0;
            sync();
        }
    }

    public void addAcid() {
        this.acidAdded = true;
        qualityBonus++;
        sync();
    }

    public void addCobalt() {
        this.cobaltAdded = true;
        qualityBonus+=2;
        sync();
    }

    public void addBleach(World world) {
        this.bleachAdded = true;

        float progressPercent = (float) this.productionProgress / this.MAX_PRODUCTION_PROGRESS;

        if (progressPercent >= 0.2f && progressPercent <= 0.6f) {
            this.qualityBonus++;
        } else {
            if (world.getRandom().nextBoolean()) {
                this.qualityBonus--;
            }
        }
        sync();
    }

    public void addAcetone() {
        this.acetoneAdded = true;
        qualityBonus++;
        productionProgress=0;
        sync();
    }

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
        view.put("accumulatedQualityPoints", Codec.FLOAT, this.accumulatedQualityPoints);
        view.put("qualityBonus", Codec.INT, this.qualityBonus);
        view.put("bleachAdded", Codec.BOOL, this.bleachAdded);
        view.put("acetoneAdded", Codec.BOOL, this.acetoneAdded);
        view.put("acidAdded", Codec.BOOL, this.acidAdded);
        view.put("reactionTemp", Codec.INT, this.reactionTemp);
        view.put("cobaltAdded", Codec.BOOL, this.cobaltAdded);
        view.put("needsStiring", Codec.BOOL, this.needsStiring);
        view.put("notStiredFor", Codec.INT, this.notStiredFor);
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
        this.accumulatedQualityPoints = view.getFloat("accumulatedQualityPoints", 0.0f);
        this.qualityBonus = view.getInt("qualityBonus", 0);
        this.bleachAdded = view.getBoolean("bleachAdded", false);
        this.acetoneAdded = view.getBoolean("acetoneAdded", false);
        this.acidAdded = view.getBoolean("acidAdded", false);
        this.reactionTemp = view.getInt("reactionTemp", 0);
        this.cobaltAdded = view.getBoolean("cobaltAdded", false);
        this.needsStiring = view.getBoolean("needsStiring", false);
        this.notStiredFor = view.getInt("notStiredFor", 0);
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

    public int addLeavesAndReturnRest(ItemStack stack) {
        int amount = stack.getCount();
        int spaceLeft = MAX_LEAVES_LEVEL - this.leavesLevel;
        int insertedAmount = Math.min(amount, spaceLeft);
        int rest = amount - insertedAmount;

        if (insertedAmount > 0) {
            QualityTier tier = stack.getOrDefault(ModComponents.QUALITY, QualityTier.NORMAL);

            this.accumulatedQualityPoints += (insertedAmount * tier.ordinal());
            this.leavesLevel += insertedAmount;

            if(isReadyToProduce()) isProductionEnabled = true;
            sync();
        }
        return rest;
    }

    public QualityTier calculateQuality() {
        if (this.leavesLevel == 0) return QualityTier.LOW;

        float average = this.accumulatedQualityPoints / (float)this.leavesLevel;

        int finalOrdinal = Math.round(average) + qualityBonus;

        finalOrdinal = Math.max(0, Math.min(finalOrdinal, QualityTier.values().length - 1));

        return QualityTier.values()[finalOrdinal];
    }

    public boolean isReadyToProduce()
    {
        return this.leavesLevel == MAX_LEAVES_LEVEL && this.gasolineLevel == MAX_GASOLINE_LEVEL;
    }

    public void collectPaste()
    {
        this.isPasteReadyToCollect = false;
        this.accumulatedQualityPoints = 0.0f;
        this.qualityBonus = 0;
        this.gasolineLevel = 0;
        this.leavesLevel = 0;
        this.bleachAdded = false;
        this.acetoneAdded = false;
        this.acidAdded = false;
        this.reactionTemp = 0;
        this.checkIceTimer = 0;
        this.needsStiring = false;
        this.notStiredFor = 0;
        this.cobaltAdded = false;

        sync();
    }

    private int checkIceTimer = 0;

    private boolean checkForIce(World world, BlockPos pos) {
        for (BlockPos checkPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
            if (checkPos.equals(pos)) continue;

            BlockState state = world.getBlockState(checkPos);
            if (state.getBlock() == Blocks.ICE
                    || state.getBlock() == Blocks.PACKED_ICE
                    || state.getBlock() == Blocks.BLUE_ICE
                    || state.getBlock() == Blocks.FROSTED_ICE) {
                return true;
            }
        }
        return false;
    }

    public static void tick(World world, BlockPos pos, BlockState state, MacerationBarrelBlockEntity be)
    {
        if (world.isClient()) {
            if (be.isProductionEnabled()) {
                Random random = world.getRandom();
                double x = pos.getX() + 0.2 + random.nextDouble() * 0.6;
                double y = pos.getY() + 1.1;
                double z = pos.getZ() + 0.2 + random.nextDouble() * 0.6;

                if (random.nextDouble() < 0.1) {
                    world.addParticleClient(net.minecraft.particle.ParticleTypes.BUBBLE_POP, x, y, z, 0.0, 0.02, 0.0);
                }

                if (random.nextDouble() < 0.2) {
                    world.addParticleClient(net.minecraft.particle.ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 0.0, 0.03, 0.0);
                }

                if (random.nextDouble() < 0.05) {
                    world.addParticleClient(net.minecraft.particle.ParticleTypes.SPLASH, x, y + 0.1, z, 0.0, 0.0, 0.0);
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

            //REAGENT: ACETONE
            if(!be.isAcetoneAdded())
                be.productionProgress++;
            else if(be.isAcetoneAdded() && world.getRandom().nextBoolean())
                    be.productionProgress++;

            //REAGENT: ACID
            if(be.isAcidAdded())
            {
                be.checkIceTimer++;
                if(be.checkIceTimer>=20)
                {
                    be.checkIceTimer = 0;
                    be.iceNearby = be.checkForIce(world, pos);
                    if(be.iceNearby && world.getRandom().nextDouble() < 0.1) {
                        for (BlockPos checkPos : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                            if (checkPos.equals(pos)) continue;
                            BlockState s = world.getBlockState(checkPos);
                            if (s.isOf(Blocks.ICE) || s.isOf(Blocks.PACKED_ICE) || s.isOf(Blocks.BLUE_ICE) || s.isOf(Blocks.FROSTED_ICE)) {
                                world.setBlockState(checkPos, Blocks.WATER.getDefaultState(), Block.NOTIFY_ALL);
                                world.playSound(null, checkPos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1f, 1.2f);
                                break;
                            }
                        }
                    }
                }

                if(be.iceNearby)
                    be.reactionTemp = Math.max(0, be.reactionTemp - 2);
                else
                {
                    be.reactionTemp++;
                    if (be.reactionTemp > 60 && world.getTime() % 5 == 0) {
                        world.playSound(null, pos, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.5f, 1.5f);
                        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5, 3, 0.2, 0.2, 0.2, 0.05);
                        }
                    }
                }

                if(be.reactionTemp>120)
                {
                    world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 4.0f, true, World.ExplosionSourceType.BLOCK);
                    return;
                }
            }

            // REAGENT: COBALT
            if (be.isCobaltAdded()) {
                if (!be.needsStiring && world.getRandom().nextDouble() < 0.01) {
                    be.needsStiring = true;
                    be.notStiredFor = 0;
                    be.sync();
                }

                if (be.needsStiring) {
                    be.notStiredFor++;

                    if (be.notStiredFor % 5 == 0) {
                        world.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 1.5f);
                        if (world instanceof net.minecraft.server.world.ServerWorld serverWorld) {
                            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 2, 0.2, 0.1, 0.2, 0.05);
                            serverWorld.spawnParticles(net.minecraft.particle.ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 3, 0.2, 0.1, 0.2, 0.05);
                        }
                    }

                    if (be.notStiredFor >= 100) {
                        be.accumulatedQualityPoints = 0;
                        be.qualityBonus = -10;
                        be.needsStiring = false;
                        be.sync();
                    }
                }
            }

            if (world.getRandom().nextDouble() < 0.04) {
                float randPitch = world.getRandom().nextFloat() * 0.4f + 0.6f;

                net.minecraft.sound.SoundEvent randomSound = switch (world.getRandom().nextInt(3)) {
                    case 0 -> SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_AMBIENT;
                    case 1 -> SoundEvents.BLOCK_LAVA_POP;
                    default -> SoundEvents.BLOCK_BREWING_STAND_BREW;
                };

                world.playSound(null, pos, randomSound, SoundCategory.BLOCKS, 0.4f, randPitch);
            }
            if(be.productionProgress>=be.MAX_PRODUCTION_PROGRESS)
            {
                be.isProductionEnabled = false;
                be.isPasteReadyToCollect = true;
                be.productionStartedAtTick = -1;
                be.productionProgress = 0;

                be.sync();
            }
        }
    }




}
