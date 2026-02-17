package zezagi.breakingblocks.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import zezagi.breakingblocks.blockEntity.MacerationBarrelBlockEntity;
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
    protected MapCodec<? extends Block> getCodec() {
        return CODEC;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
    {
        return new MacerationBarrelBlockEntity(pos, state);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (stack.isOf(ModItems.CANISTER)) {
            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof MacerationBarrelBlockEntity barrelBE) {
                int gasInCanister = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);

                if (gasInCanister <= 0 || barrelBE.getGasolineLevel() >= 100) {
                    return ActionResult.PASS;
                }

                if (world.isClient()) return ActionResult.SUCCESS;

                int rest = barrelBE.addGasolineAndReturnRest(gasInCanister);

                ItemStack newCanister = new ItemStack(ModItems.CANISTER);
                newCanister.set(ModComponents.GASOLINE_LEVEL, rest);

                if (stack.getCount() == 1) {
                    player.setStackInHand(hand, newCanister);
                } else {
                    stack.decrement(1);
                    if (!player.getInventory().insertStack(newCanister)) {
                        player.dropItem(newCanister, false);
                    }
                }

                world.playSound(null, pos, net.minecraft.sound.SoundEvents.ITEM_BUCKET_EMPTY, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.0f);
            }
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
}