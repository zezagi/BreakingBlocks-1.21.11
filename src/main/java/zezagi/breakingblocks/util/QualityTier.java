package zezagi.breakingblocks.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.Formatting;
import net.minecraft.util.StringIdentifiable;

public enum QualityTier implements StringIdentifiable {

    TRASH("trash", 0.5f, Formatting.DARK_GRAY),
    LOW("low", 0.8f, Formatting.GRAY),
    NORMAL("normal", 1.0f, Formatting.WHITE),
    HIGH("high", 1.2f, Formatting.AQUA),
    PURE("pure", 1.5f, Formatting.LIGHT_PURPLE),
    LEGENDARY("legendary", 2.0f, Formatting.GOLD);

    private final String name;
    private final float purityModifier;
    private final Formatting color;

    public static final Codec<QualityTier> CODEC = StringIdentifiable.createCodec(QualityTier::values);

    QualityTier(String name, float modifier, Formatting color)
    {
        this.name = name;
        this.purityModifier = modifier;
        this.color = color;
    }

    public float getPurityModifier()
    {return this.purityModifier;}

    public Formatting getColor()
    {return this.color;}

    public String getName()
    {return this.name;}

    @Override
    public String asString() {
        return name;
    }

    public QualityTier getNext()
    {
        int nextId = Math.min(this.ordinal() + 1, QualityTier.values().length - 1);
        return QualityTier.values()[nextId];
    }

    public QualityTier getPrevious() {
        int prevId = Math.max(this.ordinal() - 1, 0);
        return QualityTier.values()[prevId];
    }
}
