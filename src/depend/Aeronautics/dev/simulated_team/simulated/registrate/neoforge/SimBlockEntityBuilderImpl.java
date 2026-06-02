/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.AbstractRegistrate
 *  com.tterrag.registrate.builders.BlockEntityBuilder
 *  com.tterrag.registrate.builders.BlockEntityBuilder$BlockEntityFactory
 *  com.tterrag.registrate.builders.BuilderCallback
 *  com.tterrag.registrate.util.OneTimeEventReceiver
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer$Factory
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 */
package dev.simulated_team.simulated.registrate.neoforge;

import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.util.OneTimeEventReceiver;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.mixin.accessor.CreateBlockEntityBuilderAccessor;
import dev.simulated_team.simulated.registrate.SimBlockEntityBuilder;
import java.util.function.Predicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class SimBlockEntityBuilderImpl<T extends BlockEntity, P>
extends SimBlockEntityBuilder<T, P> {
    protected SimBlockEntityBuilderImpl(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        super(owner, parent, name, callback, factory);
    }

    public static <T extends BlockEntity, P> BlockEntityBuilder<T, P> create(AbstractRegistrate<?> owner, P parent, String name, BuilderCallback callback, BlockEntityBuilder.BlockEntityFactory<T> factory) {
        return new SimBlockEntityBuilderImpl<T, P>(owner, parent, name, callback, factory);
    }

    protected void registerVisualizer() {
        OneTimeEventReceiver.addModListener((AbstractRegistrate)Simulated.getRegistrate(), FMLClientSetupEvent.class, $ -> {
            NonNullSupplier visualFactory = ((CreateBlockEntityBuilderAccessor)((Object)this)).getVisualFactory();
            if (visualFactory != null) {
                Predicate renderNormally = ((CreateBlockEntityBuilderAccessor)((Object)this)).getRenderNormally();
                SimpleBlockEntityVisualizer.builder((BlockEntityType)((BlockEntityType)this.getEntry())).factory((SimpleBlockEntityVisualizer.Factory)visualFactory.get()).skipVanillaRender(be -> !renderNormally.test((BlockEntity)be)).apply();
            }
        });
    }
}
