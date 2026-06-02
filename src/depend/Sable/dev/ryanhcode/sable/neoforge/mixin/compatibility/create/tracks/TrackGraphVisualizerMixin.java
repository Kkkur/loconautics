/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.trains.graph.TrackGraphBounds
 *  com.simibubi.create.content.trains.graph.TrackGraphVisualizer
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.tracks;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.trains.graph.TrackGraphBounds;
import com.simibubi.create.content.trains.graph.TrackGraphVisualizer;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={TrackGraphVisualizer.class})
public class TrackGraphVisualizerMixin {
    @WrapOperation(method={"debugViewGraph"}, at={@At(value="FIELD", target="Lcom/simibubi/create/content/trains/graph/TrackGraphBounds;box:Lnet/minecraft/world/phys/AABB;")})
    private static AABB debugViewGraph(TrackGraphBounds instance, Operation<AABB> original) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return (AABB)original.call(new Object[]{instance});
        }
        Vec3 center = instance.box.getCenter();
        SubLevel containing = Sable.HELPER.getContaining((Level)level, (Position)center);
        if (containing == null) {
            return (AABB)original.call(new Object[]{instance});
        }
        return new BoundingBox3d(instance.box).transform((Pose3dc)containing.logicalPose()).toMojang();
    }

    @WrapOperation(method={"debugViewGraph"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/phys/Vec3;distanceTo(Lnet/minecraft/world/phys/Vec3;)D")})
    private static double debugViewGraph(Vec3 location, Vec3 camera, Operation<Double> original) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return (Double)original.call(new Object[]{location, camera});
        }
        return Sable.HELPER.projectOutOfSubLevel((Level)level, location).distanceTo(camera);
    }
}
