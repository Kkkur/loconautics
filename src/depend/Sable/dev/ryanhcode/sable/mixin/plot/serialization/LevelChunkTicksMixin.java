/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.ticks.LevelChunkTicks
 *  net.minecraft.world.ticks.SavedTick
 *  net.minecraft.world.ticks.ScheduledTick
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Final
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.ryanhcode.sable.mixin.plot.serialization;

import dev.ryanhcode.sable.mixinterface.plot.serialization.LevelChunkTicksExtension;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.LevelChunkTicks;
import net.minecraft.world.ticks.SavedTick;
import net.minecraft.world.ticks.ScheduledTick;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={LevelChunkTicks.class})
public class LevelChunkTicksMixin<T>
implements LevelChunkTicksExtension<T> {
    @Shadow
    @Nullable
    private List<SavedTick<T>> pendingTicks;
    @Shadow
    @Final
    private Set<ScheduledTick<?>> ticksPerPosition;

    @Override
    public void sable$copy(LevelChunkTicks<T> ticks) {
        this.pendingTicks = ((LevelChunkTicksMixin)ticks).pendingTicks;
        for (SavedTick<T> savedTick : this.pendingTicks) {
            this.ticksPerPosition.add(ScheduledTick.probe((Object)savedTick.type(), (BlockPos)savedTick.pos()));
        }
    }
}
