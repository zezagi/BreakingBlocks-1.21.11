package zezagi.breakingblocks.util;

import net.minecraft.block.Block;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import zezagi.breakingblocks.ModComponents;

import java.util.function.Consumer;

public class BlockItemWithQuality extends BlockItem {


    public BlockItemWithQuality(Block block, Settings settings) {
        super(block, settings.component(ModComponents.QUALITY, QualityTier.NORMAL));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        QualityTier currentQuality = stack.getOrDefault(ModComponents.QUALITY, QualityTier.NORMAL);

        Text formattedTier = Text.translatable("tier.breakingblocks." + currentQuality.getName())
                .formatted(currentQuality.getColor());

        textConsumer.accept(
                Text.translatable("item.breakingblocks.qualityTooltipMessage", formattedTier)
                        .formatted(Formatting.GRAY)
        );
    }
}
