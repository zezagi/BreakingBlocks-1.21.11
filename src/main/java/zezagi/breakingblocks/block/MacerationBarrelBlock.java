package zezagi.breakingblocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import zezagi.breakingblocks.ModComponents;
import zezagi.breakingblocks.blockentity.MacerationBarrelBlockEntity;
import zezagi.breakingblocks.blockentity.ModBlockEntities;
import zezagi.breakingblocks.item.CanisterItem;
import zezagi.breakingblocks.item.ModItems;
import zezagi.breakingblocks.util.FertilizerItem;
import zezagi.breakingblocks.util.QualityTier;

public class MacerationBarrelBlock extends Block implements BlockEntityProvider {

    public static final MapCodec<MacerationBarrelBlock> CODEC = createCodec(MacerationBarrelBlock::new);

    private static final VoxelShape SHAPE = VoxelShapes.union(
            Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.createCuboidShape(0.0, 2.0, 0.0, 16.0, 16.0, 2.0),
            Block.createCuboidShape(0.0, 2.0, 14.0, 16.0, 16.0, 16.0),
            Block.createCuboidShape(0.0, 2.0, 2.0, 2.0, 16.0, 14.0),
            Block.createCuboidShape(14.0, 2.0, 2.0, 16.0, 16.0, 14.0)
    );

    public MacerationBarrelBlock(Settings settings) {
        super(settings);
    }

    @Override
    public net.minecraft.block.BlockRenderType getRenderType(net.minecraft.block.BlockState state) {
        return net.minecraft.block.BlockRenderType.MODEL;
    }

    @Override
    protected MapCodec<MacerationBarrelBlock> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.MACERATION_BARREL_BE) {
            return (world1, pos, state1, blockEntity) -> MacerationBarrelBlockEntity.tick(world1, pos, state1, (MacerationBarrelBlockEntity) blockEntity);
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MacerationBarrelBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof MacerationBarrelBlockEntity barrelBE) {

            if (barrelBE.isPasteReadyToCollect()) {
                if (world.isClient()) return ActionResult.SUCCESS;

                QualityTier quality = barrelBE.calculateQuality();
                barrelBE.collectPaste();

                ItemStack pasteStack = new ItemStack(ModItems.COKE_PASTE);
                pasteStack.set(ModComponents.QUALITY, quality);

                if (!player.getInventory().insertStack(pasteStack)) {
                    player.dropItem(pasteStack, false);
                }

                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }

            if(stack.getItem() instanceof FertilizerItem reagent) {

                if (!barrelBE.isProductionEnabled()) {
                    return ActionResult.PASS;
                }

                if(reagent.getFertilizerType() == 4 && !barrelBE.isBleachAdded()) {
                    if (world.isClient()) return ActionResult.SUCCESS;

                    barrelBE.addBleach(world);
                    if(!player.isCreative()) stack.decrement(1);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
                else if(reagent.getFertilizerType() == 5 && !barrelBE.isAcetoneAdded()) {
                    if (world.isClient()) return ActionResult.SUCCESS;

                    barrelBE.addAcetone();
                    if(!player.isCreative()) stack.decrement(1);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
                else if(reagent.getFertilizerType() == 6 && !barrelBE.isAcidAdded()) {
                    if (world.isClient()) return ActionResult.SUCCESS;

                    barrelBE.addAcid();
                    if(!player.isCreative()) stack.decrement(1);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
                else if(reagent.getFertilizerType() == 7 && !barrelBE.isCobaltAdded())
                {
                    if (world.isClient()) return ActionResult.SUCCESS;

                    barrelBE.addCobalt();
                    if(!player.isCreative()) stack.decrement(1);
                    world.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    return ActionResult.SUCCESS;
                }
            }
            if (barrelBE.needsStiring() && stack.isEmpty()) {
                if (world.isClient()) return ActionResult.SUCCESS;

                barrelBE.stir();
                world.playSound(null, pos, SoundEvents.ENTITY_FISHING_BOBBER_SPLASH, SoundCategory.BLOCKS, 1.0f, 1.2f);
                return ActionResult.SUCCESS;
            }
            if (barrelBE.isProductionEnabled()) {
                return ActionResult.PASS;
            }

            if (stack.isOf(ModItems.CANISTER)) {
                int gasInCanister = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);

                if (gasInCanister <= 0 || barrelBE.getGasolineLevel() >= barrelBE.MAX_GASOLINE_LEVEL) {
                    return ActionResult.PASS;
                }

                if (world.isClient()) return ActionResult.SUCCESS;

                int rest = barrelBE.addGasolineAndReturnRest(gasInCanister);

                player.setStackInHand(hand, CanisterItem.getModifiedCanister(stack, player, rest));

                world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            else if (stack.isOf(ModItems.COCA_LEAF)) {
                if (barrelBE.getLeavesLevel() >= 64) return ActionResult.PASS;
                if (world.isClient()) return ActionResult.SUCCESS;

                int rest = barrelBE.addLeavesAndReturnRest(stack);
                stack.setCount(rest);

                world.playSound(null, pos, SoundEvents.BLOCK_COMPOSTER_FILL, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

}