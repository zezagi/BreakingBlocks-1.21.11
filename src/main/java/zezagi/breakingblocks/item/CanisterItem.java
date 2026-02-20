package zezagi.breakingblocks.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zezagi.breakingblocks.ModComponents;

import java.util.function.Consumer;

public class CanisterItem extends Item {

    public static final int MAX_CAPACITY = 100;

    public CanisterItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack)
    {
        return stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int currentCapacity = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
        return Math.round(13.0f * currentCapacity / MAX_CAPACITY);
    }

    @Override
    public int getItemBarColor(ItemStack stack)
    {
        int currentCapacity = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
        if(currentCapacity <= MAX_CAPACITY / 3) return 0xFF0000;
        else if(currentCapacity <= MAX_CAPACITY / 2) return 0xFFFF00;
        return 0x00FF00;
    }

    public static ItemStack getModifiedCanister(ItemStack inputStack, PlayerEntity player, int newGasolineLevel) {
        ItemStack outputStack = new ItemStack(ModItems.CANISTER);
        if (newGasolineLevel > 0) {
            outputStack.set(ModComponents.GASOLINE_LEVEL, newGasolineLevel);
        }

        return ItemUsage.exchangeStack(inputStack, player, outputStack);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int currentCapacity = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
        textConsumer.accept(Text.translatable("item.breakingblocks.canister.capacity", currentCapacity, MAX_CAPACITY).formatted(Formatting.DARK_GRAY));
    }
}
