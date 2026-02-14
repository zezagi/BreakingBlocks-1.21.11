package zezagi.breakingblocks.item.customItem;

import net.minecraft.block.CropBlock;
import net.minecraft.item.ItemConvertible;
import zezagi.breakingblocks.item.ModItems;

public class CocaCropBlock extends CropBlock {
    public CocaCropBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ItemConvertible getSeedsItem() {
        return ModItems.COCA_SEEDS;
    }
}
