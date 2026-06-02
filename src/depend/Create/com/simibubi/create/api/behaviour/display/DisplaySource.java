/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.tterrag.registrate.builders.BlockBuilder
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  com.tterrag.registrate.util.nullness.NonNullUnaryOperator
 *  net.createmod.catnip.nbt.NBTProcessors
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.api.behaviour.display;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.AllDisplaySources;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.api.registry.CreateRegistries;
import com.simibubi.create.api.registry.SimpleRegistry;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayBoardTarget;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.tterrag.registrate.builders.BlockBuilder;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullUnaryOperator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.createmod.catnip.nbt.NBTProcessors;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public abstract class DisplaySource {
    public static final SimpleRegistry.Multi<Block, DisplaySource> BY_BLOCK = SimpleRegistry.Multi.create();
    public static final SimpleRegistry.Multi<BlockEntityType<?>, DisplaySource> BY_BLOCK_ENTITY = SimpleRegistry.Multi.create();
    public static final List<MutableComponent> EMPTY = ImmutableList.of((Object)Component.empty());
    public static final MutableComponent EMPTY_LINE = Component.empty();
    public static final MutableComponent WHITESPACE = CommonComponents.space();

    public abstract List<MutableComponent> provideText(DisplayLinkContext var1, DisplayTargetStats var2);

    public void transferData(DisplayLinkContext context, DisplayTarget activeTarget, int line) {
        List<MutableComponent> text;
        DisplayTargetStats stats = activeTarget.provideStats(context);
        if (activeTarget instanceof DisplayBoardTarget) {
            DisplayBoardTarget fddt = (DisplayBoardTarget)activeTarget;
            List<List<MutableComponent>> flapDisplayText = this.provideFlapDisplayText(context, stats);
            fddt.acceptFlapText(line, flapDisplayText, context);
        }
        if ((text = this.provideText(context, stats)).isEmpty()) {
            text = EMPTY;
        }
        if (activeTarget.requiresComponentSanitization()) {
            for (MutableComponent component : text) {
                if (!NBTProcessors.textComponentHasClickEvent((Component)component)) continue;
                return;
            }
        }
        activeTarget.acceptText(line, text, context);
    }

    public void onSignalReset(DisplayLinkContext context) {
    }

    public void populateData(DisplayLinkContext context) {
    }

    public int getPassiveRefreshTicks() {
        return 100;
    }

    public boolean shouldPassiveReset() {
        return true;
    }

    protected final ResourceLocation getId() {
        return CreateBuiltInRegistries.DISPLAY_SOURCE.getKey((Object)this);
    }

    protected String getTranslationKey() {
        return this.getId().getPath();
    }

    public Component getName() {
        return Component.translatable((String)(this.getId().getNamespace() + ".display_source." + this.getTranslationKey()));
    }

    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout, int lineIndex) {
        this.loadFlapDisplayLayout(context, flapDisplay, layout);
    }

    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        if (!layout.isLayout("Default")) {
            layout.loadDefault(flapDisplay.getMaxCharCount());
        }
    }

    public List<List<MutableComponent>> provideFlapDisplayText(DisplayLinkContext context, DisplayTargetStats stats) {
        return this.provideText(context, stats).stream().map(xva$0 -> Arrays.asList(xva$0)).toList();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
    }

    public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> displaySource(RegistryEntry<DisplaySource, ? extends DisplaySource> source) {
        return builder -> (BlockBuilder)builder.onRegisterAfter(CreateRegistries.DISPLAY_SOURCE, block -> BY_BLOCK.add((Block)block, (DisplaySource)source.get()));
    }

    @Nullable
    public static DisplaySource get(@Nullable ResourceLocation id) {
        if (id == null) {
            return null;
        }
        if (id.getNamespace().equals("create") && AllDisplaySources.LEGACY_NAMES.containsKey(id.getPath())) {
            return (DisplaySource)AllDisplaySources.LEGACY_NAMES.get(id.getPath()).get();
        }
        return (DisplaySource)CreateBuiltInRegistries.DISPLAY_SOURCE.get(id);
    }

    public static List<DisplaySource> getAll(LevelAccessor level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Object byBlock = BY_BLOCK.get((StateHolder)state);
        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) {
            return byBlock;
        }
        Object byBe = BY_BLOCK_ENTITY.get(be.getType());
        if (byBlock.isEmpty()) {
            if (byBe.isEmpty()) {
                return List.of();
            }
            return byBe;
        }
        if (byBe.isEmpty()) {
            return byBlock;
        }
        ArrayList<DisplaySource> combined = new ArrayList<DisplaySource>((Collection<DisplaySource>)byBlock);
        combined.addAll((Collection<DisplaySource>)byBe);
        return combined;
    }
}
