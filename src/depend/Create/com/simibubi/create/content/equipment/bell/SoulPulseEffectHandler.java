/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.equipment.bell;

import com.simibubi.create.content.equipment.bell.SoulPulseEffect;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class SoulPulseEffectHandler {
    private List<SoulPulseEffect> pulses = new ArrayList<SoulPulseEffect>();
    private Set<BlockPos> occupied = new HashSet<BlockPos>();

    public void tick(Level world) {
        for (SoulPulseEffect pulse : this.pulses) {
            List<BlockPos> spawns = pulse.tick(world);
            if (spawns == null) continue;
            if (pulse.canOverlap()) {
                for (BlockPos pos : spawns) {
                    pulse.spawnParticles(world, pos);
                }
                continue;
            }
            for (BlockPos pos : spawns) {
                if (this.occupied.contains(pos)) continue;
                pulse.spawnParticles(world, pos);
                pulse.added.add(pos);
                this.occupied.add(pos);
            }
        }
        for (SoulPulseEffect pulse : this.pulses) {
            if (!pulse.finished() || pulse.canOverlap()) continue;
            this.occupied.removeAll(pulse.added);
        }
        this.pulses.removeIf(SoulPulseEffect::finished);
    }

    public void addPulse(SoulPulseEffect pulse) {
        this.pulses.add(pulse);
    }

    public void refresh() {
        this.pulses.clear();
        this.occupied.clear();
    }
}
