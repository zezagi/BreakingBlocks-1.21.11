package zezagi.breakingblocks.item.customItem;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zezagi.breakingblocks.ModComponents;

import java.util.List;
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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int currentCapacity = stack.getOrDefault(ModComponents.GASOLINE_LEVEL, 0);
        textConsumer.accept(Text.translatable("item.breakingblocks.canister.capacity", currentCapacity, MAX_CAPACITY).formatted(Formatting.DARK_GRAY));
    }
}
