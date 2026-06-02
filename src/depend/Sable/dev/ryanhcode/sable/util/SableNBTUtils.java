/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.util;

import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3d;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class SableNBTUtils {
    public static CompoundTag writePose3d(Pose3d pose) {
        CompoundTag tag = new CompoundTag();
        tag.put("position", (Tag)SableNBTUtils.writeVector3d((Vector3dc)pose.position()));
        tag.put("orientation", (Tag)SableNBTUtils.writeQuaternion((Quaterniondc)pose.orientation()));
        tag.put("rotation_point", (Tag)SableNBTUtils.writeVector3d((Vector3dc)pose.rotationPoint()));
        return tag;
    }

    public static Pose3d readPose3d(CompoundTag tag) {
        return new Pose3d(SableNBTUtils.readVector3d(tag.getCompound("position")), SableNBTUtils.readQuaternion(tag.getCompound("orientation")), SableNBTUtils.readVector3d(tag.getCompound("rotation_point")), new Vector3d(1.0));
    }

    public static CompoundTag writeQuaternion(Quaterniondc quat) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", quat.x());
        tag.putDouble("y", quat.y());
        tag.putDouble("z", quat.z());
        tag.putDouble("w", quat.w());
        return tag;
    }

    public static Quaterniond readQuaternion(CompoundTag tag) {
        return new Quaterniond(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"), tag.getDouble("w"));
    }

    public static CompoundTag writeVector3d(Vector3dc vector) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("x", vector.x());
        tag.putDouble("y", vector.y());
        tag.putDouble("z", vector.z());
        return tag;
    }

    public static Vector3d readVector3d(CompoundTag tag) {
        return new Vector3d(tag.getDouble("x"), tag.getDouble("y"), tag.getDouble("z"));
    }

    public static CompoundTag writeBoundingBox(BoundingBox3dc bounds) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("minX", bounds.minX());
        tag.putDouble("minY", bounds.minY());
        tag.putDouble("minZ", bounds.minZ());
        tag.putDouble("maxX", bounds.maxX());
        tag.putDouble("maxY", bounds.maxY());
        tag.putDouble("maxZ", bounds.maxZ());
        return tag;
    }

    public static BoundingBox3d readBoundingBox(CompoundTag tag) {
        return new BoundingBox3d(tag.getDouble("minX"), tag.getDouble("minY"), tag.getDouble("minZ"), tag.getDouble("maxX"), tag.getDouble("maxY"), tag.getDouble("maxZ"));
    }
}
