/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.core.Position
 *  net.minecraft.util.AbortableIterationConsumer
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.entity.EntityAccess
 *  net.minecraft.world.level.entity.EntityTypeTest
 *  net.minecraft.world.level.entity.LevelEntityGetter
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4d
 */
package dev.ryanhcode.sable.util;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.core.Position;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;

public class SubLevelInclusiveLevelEntityGetter<T extends EntityAccess>
implements LevelEntityGetter<T> {
    public static final int MAX_GET_SIDE_LENGTH = 100000;
    private final Level level;
    private final LevelEntityGetter<T> delegate;

    public SubLevelInclusiveLevelEntityGetter(Level level, LevelEntityGetter<T> delegate) {
        this.level = level;
        this.delegate = delegate;
    }

    private static void logError(AABB aabb) {
        Sable.LOGGER.error("Aborting entity get for abnormally large AABB: {}", (Object)aabb, (Object)new Throwable("Stack Trace"));
    }

    @Nullable
    public T get(int i) {
        return (T)this.delegate.get(i);
    }

    @Nullable
    public T get(UUID uUID) {
        return (T)this.delegate.get(uUID);
    }

    @NotNull
    public Iterable<T> getAll() {
        return this.delegate.getAll();
    }

    public <U extends T> void get(EntityTypeTest<T, U> entityTypeTest, AbortableIterationConsumer<U> abortableIterationConsumer) {
        this.delegate.get(entityTypeTest, abortableIterationConsumer);
    }

    public void get(AABB aABB, Consumer<T> consumer) {
        if (aABB.getSize() > 100000.0) {
            SubLevelInclusiveLevelEntityGetter.logError(aABB);
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Position)aABB.getCenter());
        this.delegate.get(aABB, consumer);
        BoundingBox3d bb = new BoundingBox3d(aABB);
        Matrix4d bakedMatrix = new Matrix4d();
        if (subLevel != null) {
            aABB = bb.transform((Pose3dc)subLevel.logicalPose(), bb).toMojang();
            this.delegate.get(aABB, consumer);
        }
        Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)new BoundingBox3d((BoundingBox3dc)bb));
        for (SubLevel otherSubLevel : intersecting) {
            if (otherSubLevel == subLevel) continue;
            AABB localBounds = bb.set(aABB).transformInverse((Pose3dc)otherSubLevel.logicalPose(), bakedMatrix, bb).toMojang();
            this.delegate.get(localBounds, consumer);
        }
    }

    public <U extends T> void get(EntityTypeTest<T, U> entityTypeTest, AABB aABB, AbortableIterationConsumer<U> abortableIterationConsumer) {
        if (aABB.getSize() > 100000.0) {
            SubLevelInclusiveLevelEntityGetter.logError(aABB);
            return;
        }
        SubLevel subLevel = Sable.HELPER.getContaining(this.level, (Position)aABB.getCenter());
        this.delegate.get(entityTypeTest, aABB, abortableIterationConsumer);
        BoundingBox3d bb = new BoundingBox3d(aABB);
        if (subLevel != null) {
            aABB = bb.transform((Pose3dc)subLevel.logicalPose(), bb).toMojang();
            this.delegate.get(entityTypeTest, aABB, abortableIterationConsumer);
        }
        Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(this.level, (BoundingBox3dc)new BoundingBox3d((BoundingBox3dc)bb));
        for (SubLevel otherSubLevel : intersecting) {
            if (otherSubLevel == subLevel) continue;
            AABB localBounds = bb.set(aABB).transformInverse((Pose3dc)otherSubLevel.logicalPose(), bb).toMojang();
            this.delegate.get(entityTypeTest, localBounds, abortableIterationConsumer);
        }
    }

    public void getIgnoringSubLevels(AABB aABB, Consumer<T> consumer) {
        this.delegate.get(aABB, consumer);
    }

    public <U extends T> void getIgnoringSubLevels(EntityTypeTest<T, U> entityTypeTest, AABB aABB, AbortableIterationConsumer<U> abortableIterationConsumer) {
        this.delegate.get(entityTypeTest, aABB, abortableIterationConsumer);
    }
}
