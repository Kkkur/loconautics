/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  dev.ryanhcode.sable.api.physics.force.ForceGroup
 *  dev.ryanhcode.sable.api.physics.force.ForceGroups
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  io.netty.buffer.ByteBuf
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.resources.ResourceLocation
 */
package dev.simulated_team.simulated.content.entities.diagram;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.simulated_team.simulated.content.entities.diagram.DiagramEntity;
import dev.simulated_team.simulated.util.SimCodecUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public class DiagramConfig {
    public static final Codec<DiagramConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.list((Codec)ResourceLocation.CODEC).fieldOf("enabled_force_groups").forGetter(DiagramConfig::enabledForceGroups), (App)Codec.BOOL.fieldOf("display_center_of_mass").forGetter(DiagramConfig::displayCenterOfMass), (App)Codec.BOOL.fieldOf("merge_forces").forGetter(DiagramConfig::mergeForces), (App)Codec.DOUBLE.fieldOf("yaw").forGetter(DiagramConfig::yaw), (App)Codec.DOUBLE.fieldOf("pitch").forGetter(DiagramConfig::pitch), (App)NoteConfigs.NOTE_CONFIG_CODEC.fieldOf("note").forGetter(DiagramConfig::getNoteConfigs)).apply((Applicative)instance, DiagramConfig::new));
    public static final StreamCodec<ByteBuf, DiagramConfig> STREAM_CODEC = StreamCodec.composite((StreamCodec)ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), DiagramConfig::enabledForceGroups, (StreamCodec)ByteBufCodecs.BOOL, DiagramConfig::displayCenterOfMass, (StreamCodec)ByteBufCodecs.BOOL, DiagramConfig::mergeForces, (StreamCodec)ByteBufCodecs.DOUBLE, DiagramConfig::yaw, (StreamCodec)ByteBufCodecs.DOUBLE, DiagramConfig::pitch, NoteConfigs.NOTE_CONFIG_STREAM_CODEC, DiagramConfig::getNoteConfigs, DiagramConfig::new);
    private final List<ResourceLocation> enabledForceGroups;
    private boolean displayCenterOfMass;
    private boolean mergeForces;
    private double yaw;
    private double pitch;
    private final NoteConfigs noteConfig;

    public static DiagramConfig makeDefault(DiagramEntity entity) {
        ObjectArrayList enabledForceGroups = new ObjectArrayList();
        for (ResourceLocation groupId : ForceGroups.REGISTRY.keySet()) {
            if (!((ForceGroup)ForceGroups.REGISTRY.get(groupId)).defaultDisplayed()) continue;
            enabledForceGroups.add((Object)groupId);
        }
        NoteConfigs noteConfig = new NoteConfigs(new BoundingBox3d(), -entity.getYRot(), entity.getXRot(), false);
        return new DiagramConfig((List<ResourceLocation>)enabledForceGroups, false, false, -entity.getYRot(), entity.getXRot(), noteConfig);
    }

    public DiagramConfig(List<ResourceLocation> enabledForceGroups, boolean displayCenterOfMass, boolean mergeForces, double yaw, double pitch, NoteConfigs noteConfig) {
        this.enabledForceGroups = enabledForceGroups;
        this.displayCenterOfMass = displayCenterOfMass;
        this.mergeForces = mergeForces;
        this.yaw = yaw;
        this.pitch = pitch;
        this.noteConfig = noteConfig;
    }

    public List<ResourceLocation> enabledForceGroups() {
        return this.enabledForceGroups;
    }

    public boolean displayCenterOfMass() {
        return this.displayCenterOfMass;
    }

    public boolean mergeForces() {
        return this.mergeForces;
    }

    public double yaw() {
        return this.yaw;
    }

    public double pitch() {
        return this.pitch;
    }

    public void setDisplayCenterOfMass(boolean displayCenterOfMass) {
        this.displayCenterOfMass = displayCenterOfMass;
    }

    public void setMergeForces(boolean mergeForces) {
        this.mergeForces = mergeForces;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public NoteConfigs getNoteConfigs() {
        return this.noteConfig;
    }

    public static final class NoteConfigs {
        public static final Codec<NoteConfigs> NOTE_CONFIG_CODEC = RecordCodecBuilder.create(i -> i.group((App)BoundingBox3d.CODEC.fieldOf("note_scope").forGetter(NoteConfigs::getNoteScope), (App)Codec.DOUBLE.fieldOf("note_yaw").forGetter(NoteConfigs::getNoteYaw), (App)Codec.DOUBLE.fieldOf("note_pitch").forGetter(NoteConfigs::getNotePitch), (App)Codec.BOOL.fieldOf("note_active").forGetter(NoteConfigs::isActive)).apply((Applicative)i, NoteConfigs::new));
        public static final StreamCodec<ByteBuf, NoteConfigs> NOTE_CONFIG_STREAM_CODEC = StreamCodec.composite(SimCodecUtil.BOUNDING_BOX_3D_STREAM_CODEC, NoteConfigs::getNoteScope, (StreamCodec)ByteBufCodecs.DOUBLE, NoteConfigs::getNoteYaw, (StreamCodec)ByteBufCodecs.DOUBLE, NoteConfigs::getNotePitch, (StreamCodec)ByteBufCodecs.BOOL, NoteConfigs::isActive, NoteConfigs::new);
        private final BoundingBox3d noteScope;
        private double noteYaw;
        private double notePitch;
        private boolean active;

        public NoteConfigs(BoundingBox3d noteScope, double noteYaw, double notePitch, boolean active) {
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
}
