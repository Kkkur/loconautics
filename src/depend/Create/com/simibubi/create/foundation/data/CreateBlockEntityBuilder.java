/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BlockEntityBuilder
 *  com.tterrag.registrate.builders.BlockEntityBuilder$BlockEntityFactory
 *  com.tterrag.registrate.builders.BuilderCallback
 *  com.tterrag.registrate.util.OneTimeEventReceiver
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer$Factory
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data;

import com.simibubi.create.api.behaviour.display.DisplaySource;
import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.api.registry.CreateRegistries;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.entry.RegistryEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CreateBlockEntityBuilder<T extends BlockEntity, P>
extends BlockEntityBuilder<T, P> {
    @Nullable
    private NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactory;
    private Predicate<@NotNull T> renderNormally;
    private Collection<NonNullSupplier<? extends Collection<NonNullSupplier<? extends Block>>>> deferredValidBlocks = new ArrayList<NonNullSupplier<? extends Collection<NonNullSupplier<? extends Block>>>>();

    public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return new CreateBlockEntityBuilder<T, P>(owner, parent, name, callback, factory);
    }

    protected CreateBlockEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    }

    public CreateBlockEntityBuilder<T, P> validBlocksDeferred(NonNullSupplier<? extends Collection<NonNullSupplier<? extends Block>>> blocks) {
        this.deferredValidBlocks.add(blocks);
        return this;
    }

    protected BlockEntityType<T> createEntry() {
        this.deferredValidBlocks.stream().map(Supplier::get).flatMap(Collection::stream).forEach(arg_0 -> ((CreateBlockEntityBuilder)this).validBlock(arg_0));
        return super.createEntry();
    }

    public CreateBlockEntityBuilder<T, P> displaySource(RegistryEntry<DisplaySource, ? extends DisplaySource> source) {
        this.onRegisterAfter(CreateRegistries.DISPLAY_SOURCE, type -> DisplaySource.BY_BLOCK_ENTITY.add((BlockEntityType<?>)type, (DisplaySource)source.get()));
        return this;
    }

    public CreateBlockEntityBuilder<T, P> displayTarget(RegistryEntry<DisplayTarget, ? extends DisplayTarget> target) {
        this.onRegisterAfter(CreateRegistries.DISPLAY_TARGET, type -> DisplayTarget.BY_BLOCK_ENTITY.register((BlockEntityType<?>)type, (DisplayTarget)target.get()));
        return this;
    }

    public CreateBlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactory) {
        return this.visual(visualFactory, true);
    }

    public CreateBlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactory, boolean renderNormally) {
        return this.visual(visualFactory, be -> renderNormally);
    }

    public CreateBlockEntityBuilder<T, P> visual(NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactory, Predicate<@NotNull T> renderNormally) {
        if (this.visualFactory == null) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::registerVisualizer);
        }
        this.visualFactory = visualFactory;
        this.renderNormally = renderNormally;
        return this;
    }

    protected void registerVisualizer() {
        OneTimeEventReceiver.addModListener((AbstractRegistrate)this.getOwner(), FMLClientSetupEvent.class, $ -> {
            NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> visualFactory = this.visualFactory;
            if (visualFactory != null) {
                Predicate<@NotNull T> renderNormally = this.renderNormally;
                SimpleBlockEntityVisualizer.builder((BlockEntityType)((BlockEntityType)this.getEntry())).factory((SimpleBlockEntityVisualizer.Factory)visualFactory.get()).skipVanillaRender(be -> !renderNormally.test(be)).apply();
            }
        });
    }
}
