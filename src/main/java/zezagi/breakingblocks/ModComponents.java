package zezagi.breakingblocks;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {

    public static final ComponentType<Integer> GASOLINE_LEVEL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BreakingBlocks.MOD_ID, "gasoline_level"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<Integer> LEAVES_COUNT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(BreakingBlocks.MOD_ID, "leaves_count"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static void InitializeModComponents()
    {
        BreakingBlocks.LOGGER.info("Registering Mod Components for " + BreakingBlocks.MOD_ID);
    }
}
