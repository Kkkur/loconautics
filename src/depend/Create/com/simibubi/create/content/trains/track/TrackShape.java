/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.Direction
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.trains.track;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;

public enum TrackShape implements StringRepresentable
{
    NONE("", Vec3.ZERO),
    ZO("z_ortho", new Vec3(0.0, 0.0, 1.0)),
    XO("x_ortho", new Vec3(1.0, 0.0, 0.0)),
    PD("diag", new Vec3(1.0, 0.0, 1.0)),
    ND("diag_2", new Vec3(-1.0, 0.0, 1.0)),
    AN("ascending", 180, new Vec3(0.0, 1.0, -1.0), new Vec3(0.0, 1.0, 1.0)),
    AS("ascending", 0, new Vec3(0.0, 1.0, 1.0), new Vec3(0.0, 1.0, -1.0)),
    AE("ascending", 270, new Vec3(1.0, 1.0, 0.0), new Vec3(-1.0, 1.0, 0.0)),
    AW("ascending", 90, new Vec3(-1.0, 1.0, 0.0), new Vec3(1.0, 1.0, 0.0)),
    TN("teleport", 180, new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 1.0, 0.0)),
    TS("teleport", 0, new Vec3(0.0, 0.0, 1.0), new Vec3(0.0, 1.0, 0.0)),
    TE("teleport", 270, new Vec3(1.0, 0.0, 0.0), new Vec3(0.0, 1.0, 0.0)),
    TW("teleport", 90, new Vec3(-1.0, 0.0, 0.0), new Vec3(0.0, 1.0, 0.0)),
    CR_O("cross_ortho", new Vec3(0.0, 0.0, 1.0), new Vec3(1.0, 0.0, 0.0)),
    CR_D("cross_diag", new Vec3(1.0, 0.0, 1.0), new Vec3(-1.0, 0.0, 1.0)),
    CR_PDX("cross_d1_xo", new Vec3(1.0, 0.0, 0.0), new Vec3(1.0, 0.0, 1.0)),
    CR_PDZ("cross_d1_zo", new Vec3(0.0, 0.0, 1.0), new Vec3(1.0, 0.0, 1.0)),
    CR_NDX("cross_d2_xo", new Vec3(1.0, 0.0, 0.0), new Vec3(-1.0, 0.0, 1.0)),
    CR_NDZ("cross_d2_zo", new Vec3(0.0, 0.0, 1.0), new Vec3(-1.0, 0.0, 1.0));

    private String model;
    private List<Vec3> axes;
    private int modelRotation;
    private Vec3 normal;
    static EnumMap<TrackShape, TrackShape> zMirror;
    static EnumMap<TrackShape, TrackShape> xMirror;
    static EnumMap<TrackShape, TrackShape> clockwise;

    private TrackShape(String model, Vec3 axis) {
        this(model, 0, axis, new Vec3(0.0, 1.0, 0.0));
    }

    private TrackShape(String model, Vec3 axis, Vec3 secondAxis) {
        this.model = model;
        this.modelRotation = 0;
        this.normal = new Vec3(0.0, 1.0, 0.0);
        this.axes = ImmutableList.of((Object)axis, (Object)secondAxis);
    }

    private TrackShape(String model, int modelRotation, Vec3 axis, Vec3 normal) {
        this.model = model;
        this.modelRotation = modelRotation;
        this.normal = normal.normalize();
        this.axes = ImmutableList.of((Object)axis);
    }

    public String getSerializedName() {
        return Lang.asId((String)this.name());
    }

    public String getModel() {
        return this.model;
    }

    public List<Vec3> getAxes() {
        return this.axes;
    }

    public boolean isJunction() {
        return this.axes.size() > 1;
    }

    public boolean isPortal() {
        switch (this.ordinal()) {
            case 9: 
            case 10: 
            case 11: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    public static TrackShape asPortal(Direction horizontalFacing) {
        switch (horizontalFacing) {
            case EAST: {
                return TE;
            }
            case NORTH: {
                return TN;
            }
            case SOUTH: {
                return TS;
            }
        }
        return TW;
    }

    public Vec3 getNormal() {
        return this.normal;
    }

    public int getModelRotation() {
        return this.modelRotation;
    }

    public TrackShape mirror(Mirror mirror) {
        return mirror == Mirror.NONE ? this : (mirror == Mirror.FRONT_BACK ? xMirror.getOrDefault((Object)this, this) : zMirror.getOrDefault((Object)this, this));
    }

    public TrackShape rotate(Rotation rotation) {
        TrackShape shape = this;
        for (int i = 0; i < rotation.ordinal(); ++i) {
            shape = clockwise.getOrDefault((Object)shape, shape);
        }
        return shape;
    }

    static {
        zMirror = new EnumMap(TrackShape.class);
        xMirror = new EnumMap(TrackShape.class);
        clockwise = new EnumMap(TrackShape.class);
        zMirror.putAll((Map<TrackShape, TrackShape>)ImmutableMap.builder().put((Object)PD, (Object)ND).put((Object)ND, (Object)PD).put((Object)AN, (Object)AS).put((Object)AS, (Object)AN).put((Object)CR_PDX, (Object)CR_NDX).put((Object)CR_NDX, (Object)CR_PDX).put((Object)CR_PDZ, (Object)CR_NDZ).put((Object)CR_NDZ, (Object)CR_PDZ).build());
        xMirror.putAll((Map<TrackShape, TrackShape>)ImmutableMap.builder().put((Object)PD, (Object)ND).put((Object)ND, (Object)PD).put((Object)AE, (Object)AW).put((Object)AW, (Object)AE).put((Object)CR_PDX, (Object)CR_NDX).put((Object)CR_NDX, (Object)CR_PDX).put((Object)CR_PDZ, (Object)CR_NDZ).put((Object)CR_NDZ, (Object)CR_PDZ).build());
        clockwise.putAll((Map<TrackShape, TrackShape>)ImmutableMap.builder().put((Object)PD, (Object)ND).put((Object)ND, (Object)PD).put((Object)XO, (Object)ZO).put((Object)ZO, (Object)XO).put((Object)AE, (Object)AS).put((Object)AS, (Object)AW).put((Object)AW, (Object)AN).put((Object)AN, (Object)AE).put((Object)CR_PDX, (Object)CR_NDZ).put((Object)CR_NDX, (Object)CR_PDZ).put((Object)CR_PDZ, (Object)CR_NDX).put((Object)CR_NDZ, (Object)CR_PDX).build());
    }
}
