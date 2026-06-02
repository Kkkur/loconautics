/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.PonderSceneBuilder
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 */
package dev.simulated_team.simulated.ponder;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.util.SimDistUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.PonderSceneBuilder;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class SceneScheduler {
    private final SceneBuilder builder;
    private final List<Sequence> sequences = new ObjectArrayList();
    private final Map<Object, Set<Sequence>> syncs = new Object2ObjectOpenHashMap();
    private int time = 0;
    private boolean ran = false;

    public SceneScheduler(SceneBuilder builder) {
        this.builder = builder;
    }

    public Sequence get(int i) {
        if (this.sequences.size() <= i) {
            this.sequences.add(i, new Sequence(i, this, this.builder));
        }
        return this.sequences.get(i);
    }

    public void run() {
        this.run(false);
    }

    public void run(boolean debug) {
        if (this.ran) {
            SimDistUtil.getClientPlayer().displayClientMessage((Component)Component.literal((String)"Set of scheduled sequences being re-run! See logs for more info").withStyle(ChatFormatting.RED), false);
            debug = true;
            Simulated.LOGGER.error("Trying to re-run scheduled sequences! Undefined behaviour ahead. A new instance should be made for running a new set of sequences");
        }
        this.ran = true;
        StringJoiner joiner = new StringJoiner("\n");
        boolean done = false;
        while (!done) {
            Sequence seq2;
            int i;
            done = true;
            boolean hasSyncing = false;
            int shortestIdle = Integer.MAX_VALUE;
            int shortestI = -1;
            boolean isDeadlocked = true;
            block1: for (i = 0; i < this.sequences.size(); ++i) {
                seq2 = this.sequences.get(i);
                while (!seq2.isDone()) {
                    int timeIdling = seq2.time - this.time;
                    if (timeIdling > 0) {
                        if (timeIdling < shortestIdle) {
                            shortestIdle = timeIdling;
                            shortestI = i;
                        }
                        done = false;
                        isDeadlocked = false;
                        continue block1;
                    }
                    if (seq2.isSyncIdle()) {
                        hasSyncing = true;
                        Object syncKey = seq2.getSyncKey();
                        Set<Sequence> sync = seq2.getSyncSet();
                        if (sync.remove((Object)seq2)) {
                            done = false;
                            joiner.add("(" + this.time + ") " + i + " awaiting sync " + String.valueOf(syncKey));
                        }
                        if (!sync.isEmpty()) continue block1;
                        isDeadlocked = false;
                        continue block1;
                    }
                    isDeadlocked = false;
                    int idle = seq2.getIdle();
                    if (idle > 0) {
                        seq2.time += idle;
                    } else {
                        joiner.add("(" + this.time + ") " + i + " action " + String.valueOf(seq2.queue.element().pInstruction));
                    }
                    done = false;
                    seq2.popAndTryRun();
                }
            }
            if (hasSyncing && isDeadlocked) {
                SimDistUtil.getClientPlayer().displayClientMessage((Component)Component.literal((String)"Ponder sequence deadlock! See logs for more info").withStyle(ChatFormatting.RED), false);
                debug = true;
                Simulated.LOGGER.error("Every sequence is awaiting syncs that will never happen");
                for (i = 0; i < this.sequences.size(); ++i) {
                    seq2 = this.sequences.get(i);
                    if (seq2.isDone()) continue;
                    Object sync = seq2.queue.element().sync;
                    Simulated.LOGGER.error("Sequence {} syncing {} (blocked by {})", new Object[]{i, sync, this.syncs.get(sync)});
                }
                this.sequences.forEach(seq -> {
                    if (!seq.isDone()) {
                        joiner.add("!! (" + this.time + ") " + seq.id + " skipping sync " + String.valueOf(seq.getSyncKey()));
                        seq.popAndTryRun();
                        seq.time = this.time;
                    }
                });
                done = false;
            }
            this.sequences.forEach(seq -> {
                if (!seq.isDone() && seq.isSyncIdle() && seq.getSyncSet().isEmpty()) {
                    seq.time = this.time;
                    seq.queue.remove();
                }
            });
            this.syncs.entrySet().removeIf(e -> {
                if (((Set)e.getValue()).isEmpty()) {
                    joiner.add("(" + this.time + ") Fully synced " + String.valueOf(e.getKey()));
                    return true;
                }
                return false;
            });
            if (shortestI == -1) continue;
            this.time += shortestIdle;
            this.builder.idle(shortestIdle);
            joiner.add("(" + this.time + ") " + shortestI + " idle " + shortestIdle);
        }
        if (debug) {
            Simulated.LOGGER.info("Finalized sequence:\n" + String.valueOf(joiner));
        }
    }

    public static class Sequence
    extends PonderSceneBuilder {
        private final Queue<Instruction> queue = new ArrayDeque<Instruction>();
        private final int id;
        private final SceneScheduler scheduler;
        private final SceneBuilder builder;
        private int time = 0;
        private boolean independent = true;
        private int duration = 0;

        private Sequence(int id, SceneScheduler scheduler, SceneBuilder builder) {
            super(builder.getScene());
            this.id = id;
            this.scheduler = scheduler;
            this.builder = builder;
        }

        public void addInstruction(PonderInstruction instruction) {
            this.queue.add(new Instruction(null, instruction, null));
        }

        public void addInstruction(Consumer<PonderScene> callback) {
            this.addInstruction(PonderInstruction.simple(callback));
        }

        public void idle(int ticks) {
            this.queue.add(new Instruction(ticks, null, null));
            this.duration += ticks;
        }

        public void sync(Object o) {
            this.scheduler.syncs.computeIfAbsent(o, k -> new HashSet()).add(this);
            this.queue.add(new Instruction(null, null, o));
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

        private Set<Sequence> getSyncSet() {
            return this.scheduler.syncs.get(this.queue.element().sync);
        }

        private void popAndTryRun() {
            Instruction i = this.queue.remove();
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
            Instruction i = this.queue.element();
            if (i.idle != null) {
                return "Sequence " + this.id + " waiting " + i.idle + " ticks";
            }
            if (i.pInstruction != null) {
                return "Sequence " + this.id + " running instruction " + String.valueOf(i.pInstruction);
            }
            return "Sequence " + this.id + " waiting for sync of " + String.valueOf(i.sync);
        }
    }

    private record Instruction(Integer idle, PonderInstruction pInstruction, Object sync) {
    }
}
