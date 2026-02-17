package zezagi.breakingblocks;

import net.fabricmc.api.ModInitializer;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zezagi.breakingblocks.block.ModBlocks;
import zezagi.breakingblocks.blockEntity.ModBlockEntities;
import zezagi.breakingblocks.client.render.block.MacerationBarrelBlockEntityRenderer;
import zezagi.breakingblocks.item.CanisterItem;
import zezagi.breakingblocks.item.ModItems;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import zezagi.breakingblocks.mixin.AbstractFurnaceBlockEntityAccessor;

public class BreakingBlocks implements ModInitializer {
	public static final String MOD_ID = "breakingblocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModBlockEntities.RegisterBlockEntities();
		ModBlocks.RegisterModBlocks();
		ModItems.registerModItems();
		ModComponents.InitializeModComponents();
		UseBlockCallback.EVENT.register(this::FurnaceBlockCallback);
	}

	private ActionResult FurnaceBlockCallback(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult)
	{
		ItemStack stack = player.getStackInHand(hand);
		if(stack.isOf(ModItems.CANISTER))
		{
			BlockEntity blockEntity = world.getBlockEntity(hitResult.getBlockPos());
			if(blockEntity instanceof AbstractFurnaceBlockEntityAccessor accessor)
			{
				if (world.isClient()) return net.minecraft.util.ActionResult.SUCCESS;

				int currentCapacity = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
				if(currentCapacity >= CanisterItem.MAX_CAPACITY) return ActionResult.PASS;

				int ticksPerCapacity = 685;
				int spaceLeft = CanisterItem.MAX_CAPACITY - currentCapacity;
				int availableTicks = accessor.getLitTimeRemaining();

				if(availableTicks >= ticksPerCapacity)
				{
					int capacityToAdd = Math.min(availableTicks / ticksPerCapacity, spaceLeft);
					if (capacityToAdd > 0) {
						accessor.setLitTimeRemaining(0);

						BlockState furnaceState = world.getBlockState(hitResult.getBlockPos());
						world.setBlockState(hitResult.getBlockPos(), furnaceState.with(net.minecraft.state.property.Properties.LIT, false), 3);

						ItemStack filledCanister = new ItemStack(ModItems.CANISTER);
						filledCanister.set(ModComponents.GASOLINE_LEVEL, currentCapacity + capacityToAdd);

						if (stack.getCount() == 1) {
							player.setStackInHand(hand, filledCanister);
						} else {
							stack.decrement(1);
							if (!player.getInventory().insertStack(filledCanister)) {
								player.dropItem(filledCanister, false);
							}
						}
						world.playSound(null, blockEntity.getPos(), SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0f, 1.0f);
						blockEntity.markDirty();
						return ActionResult.SUCCESS;
					}
				}
				else if(availableTicks > 0)
				{
					world.playSound(null, hitResult.getBlockPos(), net.minecraft.sound.SoundEvents.BLOCK_DISPENSER_FAIL, net.minecraft.sound.SoundCategory.BLOCKS, 1.0f, 1.5f);
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}
}