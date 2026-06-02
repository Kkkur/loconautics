/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package com.simibubi.create.foundation.ponder.instruction;

import com.simibubi.create.content.contraptions.bearing.IBearingBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlockEntity;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AnimateBlockEntityInstruction
extends TickingInstruction {
    protected double deltaPerTick;
    protected double totalDelta;
    protected double target;
    protected final BlockPos location;
    private final BiConsumer<PonderLevel, Float> setter;
    private final Function<PonderLevel, Float> getter;

    public static AnimateBlockEntityInstruction bearing(BlockPos location, float totalDelta, int ticks) {
        return new AnimateBlockEntityInstruction(location, totalDelta, ticks, (w, f) -> AnimateBlockEntityInstruction.castIfPresent(w, location, IBearingBlockEntity.class).ifPresent(bte -> bte.setAngle(f.floatValue())), w -> AnimateBlockEntityInstruction.castIfPresent(w, location, IBearingBlockEntity.class).map(bte -> Float.valueOf(bte.getInterpolatedAngle(0.0f))).orElse(Float.valueOf(0.0f)));
    }

    public static AnimateBlockEntityInstruction bogey(BlockPos location, float totalDelta, int ticks) {
        float movedPerTick = totalDelta / (float)ticks;
        return new AnimateBlockEntityInstruction(location, totalDelta, ticks, (w, f) -> AnimateBlockEntityInstruction.castIfPresent(w, location, AbstractBogeyBlockEntity.class).ifPresent(bte -> bte.animate(f.equals(Float.valueOf(totalDelta)) ? 0.0f : movedPerTick)), w -> Float.valueOf(0.0f));
    }

    public static AnimateBlockEntityInstruction pulley(BlockPos location, float totalDelta, int ticks) {
        return new AnimateBlockEntityInstruction(location, totalDelta, ticks, (w, f) -> AnimateBlockEntityInstruction.castIfPresent(w, location, PulleyBlockEntity.class).ifPresent(pulley -> pulley.animateOffset(f.floatValue())), w -> AnimateBlockEntityInstruction.castIfPresent(w, location, PulleyBlockEntity.class).map(pulley -> Float.valueOf(pulley.offset)).orElse(Float.valueOf(0.0f)));
    }

    public static AnimateBlockEntityInstruction deployer(BlockPos location, float totalDelta, int ticks) {
        return new AnimateBlockEntityInstruction(location, totalDelta, ticks, (w, f) -> AnimateBlockEntityInstruction.castIfPresent(w, location, DeployerBlockEntity.class).ifPresent(deployer -> deployer.setAnimatedOffset(f.floatValue())), w -> AnimateBlockEntityInstruction.castIfPresent(w, location, DeployerBlockEntity.class).map(deployer -> Float.valueOf(deployer.getHandOffset(1.0f))).orElse(Float.valueOf(0.0f)));
    }

    protected AnimateBlockEntityInstruction(BlockPos location, float totalDelta, int ticks, BiConsumer<PonderLevel, Float> setter, Function<PonderLevel, Float> getter) {
        super(false, ticks);
        this.location = location;
        this.setter = setter;
        this.getter = getter;
        this.deltaPerTick = (double)totalDelta * (1.0 / (double)ticks);
        this.totalDelta = totalDelta;
        this.target = totalDelta;
    }

    protected final void firstTick(PonderScene scene) {
        super.firstTick(scene);
        this.target = (double)this.getter.apply(scene.getWorld()).floatValue() + this.totalDelta;
    }

    public void tick(PonderScene scene) {
        super.tick(scene);
        PonderLevel world = scene.getWorld();
        float current = this.getter.apply(world).floatValue();
        float next = (float)(this.remainingTicks == 0 ? this.target : (double)current + this.deltaPerTick);
        this.setter.accept(world, Float.valueOf(next));
        if (this.remainingTicks == 0) {
            this.setter.accept(world, Float.valueOf(next));
        }
    }

    private static <T> Optional<T> castIfPresent(PonderLevel world, BlockPos pos, Class<T> beType) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (beType.isInstance(blockEntity)) {
            return Optional.of(beType.cast(blockEntity));
        }
        return Optional.empty();
    }
}
