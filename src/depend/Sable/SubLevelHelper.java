/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.commands.arguments.EntityAnchorArgument$Anchor
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.mixinterface.EntityExtension;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.function.BiFunction;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector3d;
import org.joml.Vector3dc;

@ApiStatus.Internal
public final class SubLevelHelper {
    private static final ThreadLocal<EntityRot> oldRot = ThreadLocal.withInitial(EntityRot::new);
    private static final ObjectList<BiFunction<Vector3dc, Level, Vector3dc>> windProviders = new ObjectArrayList();

    public static void pushEntityLocal(SubLevel subLevel, Entity entity) {
        SubLevelHelper.pushEntityLocal(subLevel, entity, EntityAnchorArgument.Anchor.FEET);
    }

    public static void popEntityLocal(SubLevel subLevel, Entity player) {
        SubLevelHelper.popEntityLocal(subLevel, player, EntityAnchorArgument.Anchor.FEET);
    }

    public static void pushEntityLocal(SubLevel subLevel, Entity entity, EntityAnchorArgument.Anchor anchor) {
        if (anchor == EntityAnchorArgument.Anchor.FEET) {
            ((EntityExtension)entity).sable$setPosSuperRaw(subLevel.logicalPose().transformPositionInverse(entity.position()));
        } else {
            ((EntityExtension)entity).sable$setPosSuperRaw(subLevel.logicalPose().transformPositionInverse(entity.getEyePosition()).add(0.0, (double)(-entity.getEyeHeight()), 0.0));
        }
        Vec3 playerLookAngle = entity.getLookAngle();
        playerLookAngle = subLevel.logicalPose().transformNormalInverse(playerLookAngle);
        oldRot.get().copy(entity);
        Vec3 pTarget = entity.getEyePosition().add(playerLookAngle);
        Vec3 vec3 = entity.getEyePosition();
        double d0 = pTarget.x - vec3.x;
        double d1 = pTarget.y - vec3.y;
        double d2 = pTarget.z - vec3.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        entity.setXRot(Mth.wrapDegrees((float)((float)(-(Mth.atan2((double)d1, (double)d3) * 57.2957763671875)))));
        entity.setYRot(Mth.wrapDegrees((float)((float)(Mth.atan2((double)d2, (double)d0) * 57.2957763671875) - 90.0f)));
        entity.setYHeadRot(entity.getYRot());
        entity.setDeltaMovement(subLevel.logicalPose().transformNormalInverse(entity.getDeltaMovement()));
    }

    public static void popEntityLocal(SubLevel subLevel, Entity entity, EntityAnchorArgument.Anchor anchor) {
        if (anchor == EntityAnchorArgument.Anchor.FEET) {
            ((EntityExtension)entity).sable$setPosSuperRaw(subLevel.logicalPose().transformPosition(entity.position()));
        } else {
            ((EntityExtension)entity).sable$setPosSuperRaw(subLevel.logicalPose().transformPosition(entity.getEyePosition()).add(0.0, (double)(-entity.getEyeHeight()), 0.0));
        }
        oldRot.get().apply(entity);
        entity.setDeltaMovement(subLevel.logicalPose().transformNormal(entity.getDeltaMovement()));
    }

    public static Vector3d getVelocityRelativeToAir(Level level, Vector3dc pos, Vector3d dest) {
        Vector3d probePos = new Vector3d(pos);
        Vector3d velocity = Sable.HELPER.getVelocity(level, pos, dest);
        for (BiFunction windProvider : windProviders) {
            Vector3dc airVelocity = (Vector3dc)windProvider.apply(probePos, level);
            if (airVelocity == null) continue;
            velocity.sub(airVelocity);
        }
        return velocity;
    }

    public static void registerWindProvider(BiFunction<Vector3dc, Level, Vector3dc> function) {
        windProviders.add(function);
    }

    public static Collection<ServerSubLevel> getLoadingDependencyChain(ServerSubLevel subLevel) {
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        ObjectOpenHashSet frontier = new ObjectOpenHashSet();
        frontier.add((Object)subLevel);
        while (!frontier.isEmpty()) {
            ServerSubLevel current = (ServerSubLevel)frontier.iterator().next();
            frontier.remove((Object)current);
            visited.add((Object)current);
            Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting((Level)current.getLevel(), (BoundingBox3dc)new BoundingBox3d(current.boundingBox()));
            for (SubLevel neighbor : intersecting) {
                ServerSubLevel serverNeighbor = (ServerSubLevel)neighbor;
                if (visited.contains((Object)serverNeighbor)) continue;
                frontier.add((Object)serverNeighbor);
            }
            for (BlockEntitySubLevelActor actor : current.getPlot().getBlockEntityActors()) {
                Iterable<SubLevel> loadingDependencies = actor.sable$getLoadingDependencies();
                if (loadingDependencies == null) continue;
                for (SubLevel dependency : loadingDependencies) {
                    ServerSubLevel serverDependency = (ServerSubLevel)dependency;
                    if (visited.contains((Object)serverDependency)) continue;
                    frontier.add((Object)serverDependency);
                }
            }
        }
        return visited;
    }

    public static Collection<SubLevel> getConnectedChain(SubLevel subLevel) {
        ObjectOpenHashSet visited = new ObjectOpenHashSet();
        ObjectOpenHashSet frontier = new ObjectOpenHashSet();
        frontier.add((Object)subLevel);
        while (!frontier.isEmpty()) {
            SubLevel current = (SubLevel)frontier.iterator().next();
            frontier.remove((Object)current);
            visited.add((Object)current);
            for (BlockEntitySubLevelActor actor : current.getPlot().getBlockEntityActors()) {
                Iterable<SubLevel> dependencies = actor.sable$getConnectionDependencies();
                if (dependencies == null) continue;
                for (SubLevel dependency : dependencies) {
                    SubLevel serverDependency = dependency;
                    if (visited.contains((Object)serverDependency)) continue;
                    frontier.add((Object)serverDependency);
                }
            }
        }
        return visited;
    }

    private static class EntityRot {
        private float xRot;
        private float yRot;
        private float yHeadRot;

        private EntityRot() {
        }

        public void apply(Entity entity) {
            entity.setXRot(this.xRot);
            entity.setYRot(this.yRot);
            entity.setYHeadRot(this.yHeadRot);
        }

        public void copy(Entity entity) {
            this.xRot = entity.getXRot();
            this.yRot = entity.getYRot();
            this.yHeadRot = entity.getYHeadRot();
        }
    }
}
