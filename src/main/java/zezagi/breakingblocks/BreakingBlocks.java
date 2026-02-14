package zezagi.breakingblocks;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zezagi.breakingblocks.item.ModItems;

public class BreakingBlocks implements ModInitializer {
	public static final String MOD_ID = "breakingblocks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();
	}
}