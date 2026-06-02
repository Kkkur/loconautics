/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.gui.ModularGuiLineBuilder
 *  joptsimple.internal.Strings
 *  net.createmod.catnip.lang.LangNumberFormat
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.eriksonn.aeronautics.content.display_sources;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.NumericSingleLineDisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import dev.eriksonn.aeronautics.content.blocks.hot_air.BlockEntityLiftingGasProvider;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.ServerBalloon;
import dev.eriksonn.aeronautics.data.AeroLang;
import joptsimple.internal.Strings;
import net.createmod.catnip.lang.LangNumberFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class GasDisplaySource
extends NumericSingleLineDisplaySource {
    protected MutableComponent provideLine(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {
        BlockEntity blockEntity = displayLinkContext.getSourceBlockEntity();
        if (!(blockEntity instanceof BlockEntityLiftingGasProvider)) {
            return ZERO.copy();
        }
        BlockEntityLiftingGasProvider provider = (BlockEntityLiftingGasProvider)blockEntity;
        Balloon balloon = provider.getBalloon();
        if (!(balloon instanceof ServerBalloon)) {
            return GasDisplaySource.noBalloon();
        }
        ServerBalloon info = (ServerBalloon)balloon;
        switch (displayLinkContext.sourceConfig().getInt("GasDataSelection")) {
            case 0: {
                int totalBar = 15;
                int capacity = info.getCapacity();
                int targetBar = (int)Math.ceil(15.0 * info.getTotalTargetVolume() / (double)capacity);
                int volume = Mth.clamp((int)((int)Math.ceil(15.0 * (info.getTotalFilledVolume() + info.getTotalVolumeChange()) / (double)capacity)), (int)0, (int)15);
                return GasDisplaySource.barComponent(volume, targetBar, 15);
            }
            case 1: {
                return AeroLang.text(LangNumberFormat.format((double)info.getTotalLift())).component();
            }
        }
        return ZERO.copy();
    }

    private static MutableComponent noBalloon() {
        return AeroLang.text("No Balloon above").component();
    }

    static MutableComponent barComponent(int amount, int target, int total) {
        int lower = Math.min(amount, target - 1);
        int upper = Math.max(amount - target, 0);
        int filledChar = 9608;
        int halfFillChar = 9618;
        int emptyChar = 9617;
        return Component.empty().append((Component)GasDisplaySource.bars(Math.max(0, lower), ChatFormatting.DARK_AQUA, '\u2588')).append((Component)GasDisplaySource.bars(Math.max(0, target - lower - 1), ChatFormatting.DARK_GRAY, '\u2592'));
    }

    private static MutableComponent bars(int count, ChatFormatting format, char ch) {
        return Component.literal((String)Strings.repeat((char)ch, (int)count)).withStyle(format);
    }

    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        super.initConfigurationWidgets(context, builder, isFirstLine);
        if (isFirstLine) {
            return;
        }
        builder.addSelectionScrollInput(0, 60, (selectionScrollInput, label) -> selectionScrollInput.forOptions(AeroLang.translatedOptions("display_source.lifting_gas", "volume", "total_lift")), "GasDataSelection");
    }

    protected boolean allowsLabeling(DisplayLinkContext displayLinkContext) {
        return true;
    }

    protected String getTranslationKey() {
        return "lifting_gas.data";
    }
}
