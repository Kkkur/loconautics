/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.data.CreateBlockEntityBuilder
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer$Factory
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  org.jetbrains.annotations.NotNull
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.simulated_team.simulated.mixin.accessor;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import java.util.function.Predicate;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={CreateBlockEntityBuilder.class})
public interface CreateBlockEntityBuilderAccessor<T extends BlockEntity, P> {
    @Accessor
    public NonNullSupplier<SimpleBlockEntityVisualizer.Factory<T>> getVisualFactory();

    @Accessor
    public Predicate<@NotNull T> getRenderNormally();
}
