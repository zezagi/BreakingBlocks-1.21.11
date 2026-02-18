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

                barrelBE.collectPaste();

                ItemStack pasteStack = new ItemStack(ModItems.COKE_PASTE);
                if (!player.getInventory().insertStack(pasteStack)) {
                    player.dropItem(pasteStack, false);
                }

                world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_SLIME_BLOCK_PLACE, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
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

                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_EMPTY, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
            else if (stack.isOf(ModItems.COCA_LEAF)) {
                if (barrelBE.getLeavesLevel() >= 64) {
                    return ActionResult.PASS;
                }

                if (world.isClient()) return ActionResult.SUCCESS;

                int leavesInHand = stack.getCount();
                int rest = barrelBE.addLeavesAndReturnRest(leavesInHand);
                int insertedAmount = leavesInHand - rest;

                stack.decrement(insertedAmount);

                world.playSound(null, pos, net.minecraft.sound.SoundEvents.BLOCK_COMPOSTER_FILL, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }

}