package zezagi.breakingblocks.util;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class FertilizerItem extends Item {
    public static int FERTILIZERS_QUANTITY = 3;
    public static int LAST_CROP_FERTILIZER_INDEX = 3;

    private int fertilizerType;

    public FertilizerItem(Settings settings, int fertilizerType) {
        super(settings);
        this.fertilizerType = fertilizerType;
    }

    public int getFertilizerType() {
        return fertilizerType;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("item.breakingblocks.fertilizerTooltipMessage"+fertilizerType)
                .formatted(Formatting.DARK_GRAY)
        );
    }
}
