/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 */
package dev.simulated_team.simulated.content.entities.diagram;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.simulated_team.simulated.util.SimCodecUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public static final class DiagramConfig.NoteConfigs {
    public static final Codec<DiagramConfig.NoteConfigs> NOTE_CONFIG_CODEC = RecordCodecBuilder.create(i -> i.group((App)BoundingBox3d.CODEC.fieldOf("note_scope").forGetter(DiagramConfig.NoteConfigs::getNoteScope), (App)Codec.DOUBLE.fieldOf("note_yaw").forGetter(DiagramConfig.NoteConfigs::getNoteYaw), (App)Codec.DOUBLE.fieldOf("note_pitch").forGetter(DiagramConfig.NoteConfigs::getNotePitch), (App)Codec.BOOL.fieldOf("note_active").forGetter(DiagramConfig.NoteConfigs::isActive)).apply((Applicative)i, DiagramConfig.NoteConfigs::new));
    public static final StreamCodec<ByteBuf, DiagramConfig.NoteConfigs> NOTE_CONFIG_STREAM_CODEC = StreamCodec.composite(SimCodecUtil.BOUNDING_BOX_3D_STREAM_CODEC, DiagramConfig.NoteConfigs::getNoteScope, (StreamCodec)ByteBufCodecs.DOUBLE, DiagramConfig.NoteConfigs::getNoteYaw, (StreamCodec)ByteBufCodecs.DOUBLE, DiagramConfig.NoteConfigs::getNotePitch, (StreamCodec)ByteBufCodecs.BOOL, DiagramConfig.NoteConfigs::isActive, DiagramConfig.NoteConfigs::new);
    private final BoundingBox3d noteScope;
    private double noteYaw;
    private double notePitch;
    private boolean active;

    public DiagramConfig.NoteConfigs(BoundingBox3d noteScope, double noteYaw, double notePitch, boolean active) {
        this.noteScope = noteScope;
        this.noteYaw = noteYaw;
        this.notePitch = notePitch;
        this.active = active;
    }

    public BoundingBox3d getNoteScope() {
        return this.noteScope;
    }

    public double getNotePitch() {
        return this.notePitch;
    }

    public double getNoteYaw() {
        return this.noteYaw;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNotePitch(double notePitch) {
        this.notePitch = notePitch;
    }

    public void setNoteYaw(double noteYaw) {
        this.noteYaw = noteYaw;
    }
}
