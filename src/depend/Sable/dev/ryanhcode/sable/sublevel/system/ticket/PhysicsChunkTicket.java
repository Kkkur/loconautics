/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.SectionPos
 */
package dev.ryanhcode.sable.sublevel.system.ticket;

import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Collection;
import java.util.Objects;
import net.minecraft.core.SectionPos;

public final class PhysicsChunkTicket {
    private final SectionPos pos;
    private final Collection<SubLevel> residentSubLevels;
    private long lastInhabitedTick;

    public PhysicsChunkTicket(SectionPos pos, long lastInhabitedTick, Collection<SubLevel> residentSubLevels) {
        this.pos = pos;
        this.lastInhabitedTick = lastInhabitedTick;
        this.residentSubLevels = residentSubLevels;
    }

    public SectionPos pos() {
        return this.pos;
    }

    public long lastInhabitedTick() {
        return this.lastInhabitedTick;
    }

    public void setLastInhabitedTick(long lastInhabitedTick) {
        this.lastInhabitedTick = lastInhabitedTick;
    }

    public Collection<SubLevel> residentSubLevels() {
        return this.residentSubLevels;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        PhysicsChunkTicket that = (PhysicsChunkTicket)obj;
        return Objects.equals(this.pos, that.pos);
    }

    public int hashCode() {
        return Objects.hash(this.pos, this.lastInhabitedTick);
    }

    public String toString() {
        return "PhysicsChunkTicket[pos=" + String.valueOf(this.pos) + ", lastInhabitedTick=" + this.lastInhabitedTick + ", residentSubLevels=" + String.valueOf(this.residentSubLevels) + "]";
    }
}
