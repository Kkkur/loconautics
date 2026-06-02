/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.mojang.serialization.Codec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.zapper;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.foundation.gui.AllIcons;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.function.Predicate;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum PlacementPatterns implements StringRepresentable
{
    Solid(AllIcons.I_PATTERN_SOLID),
    Checkered(AllIcons.I_PATTERN_CHECKERED),
    InverseCheckered(AllIcons.I_PATTERN_CHECKERED_INVERSED),
    Chance25(AllIcons.I_PATTERN_CHANCE_25),
    Chance50(AllIcons.I_PATTERN_CHANCE_50),
    Chance75(AllIcons.I_PATTERN_CHANCE_75);

    public static final Codec<PlacementPatterns> CODEC;
    public static final StreamCodec<ByteBuf, PlacementPatterns> STREAM_CODEC;
    public final String translationKey = Lang.asId((String)this.name());
    public final AllIcons icon;

    private PlacementPatterns(AllIcons icon) {
        this.icon = icon;
    }

    public static void applyPattern(List<BlockPos> blocksIn, ItemStack stack, RandomSource random) {
        PlacementPatterns pattern = (PlacementPatterns)((Object)stack.getOrDefault(AllDataComponents.PLACEMENT_PATTERN, (Object)Solid));
        Object filter = Predicates.alwaysFalse();
        switch (pattern.ordinal()) {
            case 3: {
                filter = pos -> random.nextBoolean() || random.nextBoolean();
                break;
            }
            case 4: {
                filter = pos -> random.nextBoolean();
                break;
            }
            case 5: {
                filter = pos -> random.nextBoolean() && random.nextBoolean();
                break;
            }
            case 1: {
                filter = pos -> (pos.getX() + pos.getY() + pos.getZ()) % 2 == 0;
                break;
            }
            case 2: {
                filter = pos -> (pos.getX() + pos.getY() + pos.getZ()) % 2 != 0;
                break;
            }
        }
        blocksIn.removeIf((Predicate<BlockPos>)filter);
    }

    @NotNull
    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    static {
        CODEC = StringRepresentable.fromValues(PlacementPatterns::values);
        STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PlacementPatterns.class);
    }
}
