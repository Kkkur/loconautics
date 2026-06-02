/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BuilderCallback
 *  com.tterrag.registrate.builders.EntityBuilder
 *  com.tterrag.registrate.util.OneTimeEventReceiver
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer
 *  dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer$Factory
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$EntityFactory
 *  net.minecraft.world.entity.MobCategory
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.foundation.data;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleEntityVisualizer;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
public class CreateEntityBuilder<T extends Entity, P>
extends EntityBuilder<T, P> {
    @Nullable
    private NonNullSupplier<SimpleEntityVisualizer.Factory<T>> visualFactory;
    private Predicate<@NotNull T> renderNormally;

    public static <T extends Entity, P> EntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory classification) {
        return new CreateEntityBuilder<T, P>(owner, parent, name, callback, factory, classification).defaultLang();
    }

    public CreateEntityBuilder(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, EntityType.EntityFactory<T> factory, MobCategory classification) {
        super(owner, parent, name, callback, factory, classification);
    }

    public CreateEntityBuilder<T, P> visual(NonNullSupplier<SimpleEntityVisualizer.Factory<T>> visualFactory) {
        return this.visual(visualFactory, true);
    }

    public CreateEntityBuilder<T, P> visual(NonNullSupplier<SimpleEntityVisualizer.Factory<T>> visualFactory, boolean renderNormally) {
        return this.visual(visualFactory, entity -> renderNormally);
    }

    public CreateEntityBuilder<T, P> visual(NonNullSupplier<SimpleEntityVisualizer.Factory<T>> visualFactory, Predicate<@NotNull T> renderNormally) {
        if (this.visualFactory == null) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> this::registerVisualizer);
        }
        this.visualFactory = visualFactory;
        this.renderNormally = renderNormally;
        return this;
    }

    protected void registerVisualizer() {
        OneTimeEventReceiver.addModListener((AbstractRegistrate)this.getOwner(), FMLClientSetupEvent.class, $ -> {
            NonNullSupplier<SimpleEntityVisualizer.Factory<T>> visualFactory = this.visualFactory;
            if (visualFactory != null) {
                Predicate<@NotNull T> renderNormally = this.renderNormally;
                SimpleEntityVisualizer.builder((EntityType)((EntityType)this.getEntry())).factory((SimpleEntityVisualizer.Factory)visualFactory.get()).skipVanillaRender(entity -> !renderNormally.test(entity)).apply();
            }
        });
    }
}
