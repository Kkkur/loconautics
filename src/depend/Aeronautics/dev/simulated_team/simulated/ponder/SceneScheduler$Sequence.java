/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderSceneBuilder
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 */
package dev.simulated_team.simulated.ponder;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.ponder.SceneScheduler;
import dev.simulated_team.simulated.util.SimDistUtil;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public static class SceneScheduler.Sequence
extends PonderSceneBuilder {
    private final Queue<SceneScheduler.Instruction> queue = new ArrayDeque<SceneScheduler.Instruction>();
    private final int id;
    private final SceneScheduler scheduler;
    private final SceneBuilder builder;
    private int time = 0;
    private boolean independent = true;
    private int duration = 0;

    private SceneScheduler.Sequence(int id, SceneScheduler scheduler, SceneBuilder builder) {
        super(builder.getScene());
        this.id = id;
        this.scheduler = scheduler;
        this.builder = builder;
    }

    public void addInstruction(PonderInstruction instruction) {
        this.queue.add(new SceneScheduler.Instruction(null, instruction, null));
    }

    public void addInstruction(Consumer<PonderScene> callback) {
        this.addInstruction(PonderInstruction.simple(callback));
    }

    public void idle(int ticks) {
        this.queue.add(new SceneScheduler.Instruction(ticks, null, null));
        this.duration += ticks;
    }

    public void sync(Object o) {
        this.scheduler.syncs.computeIfAbsent(o, k -> new HashSet()).add(this);
        this.queue.add(new SceneScheduler.Instruction(null, null, o));
        this.independent = false;
    }

    private boolean isDone() {
        return this.queue.isEmpty();
    }

    private int getIdle() {
        return Objects.requireNonNullElse(this.queue.element().idle(), 0);
    }

    private boolean isSyncIdle() {
        return this.queue.element().sync != null;
    }

    private Object getSyncKey() {
        return this.queue.element().sync;
    }

    private Set<SceneScheduler.Sequence> getSyncSet() {
        return this.scheduler.syncs.get(this.queue.element().sync);
    }

    private void popAndTryRun() {
        SceneScheduler.Instruction i = this.queue.remove();
        if (i.pInstruction != null) {
            this.builder.addInstruction(i.pInstruction);
        }
    }

    public int getDuration() {
        if (!this.independent) {
            SimDistUtil.getClientPlayer().displayClientMessage((Component)Component.literal((String)("Getting independent timestamp of synced sequence " + this.id)).withStyle(ChatFormatting.RED), false);
            Simulated.LOGGER.error("Getting independent timestamp of synced sequence " + this.id);
        }
        return this.duration;
    }

    public String toString() {
        if (this.isDone()) {
            return "Finished sequence " + this.id;
        }
        SceneScheduler.Instruction i = this.queue.element();
        if (i.idle != null) {
            return "Sequence " + this.id + " waiting " + i.idle + " ticks";
        }
        if (i.pInstruction != null) {
            return "Sequence " + this.id + " running instruction " + String.valueOf(i.pInstruction);
        }
        return "Sequence " + this.id + " waiting for sync of " + String.valueOf(i.sync);
    }
}
