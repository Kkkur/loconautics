/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  io.netty.util.concurrent.FastThreadLocal
 *  it.unimi.dsi.fastutil.Function
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  net.minecraft.core.BlockPos
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaterniondc
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.api.schematic;

import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.sublevel.SubLevel;
import io.netty.util.concurrent.FastThreadLocal;
import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;

public class SubLevelSchematicSerializationContext {
    private static final FastThreadLocal<SubLevelSchematicSerializationContext> THREAD_LOCAL = new FastThreadLocal();
    private final Map<UUID, SchematicMapping> mappings = new Object2ObjectOpenHashMap();
    private Function<BlockPos, BlockPos> placeTransform;
    private Function<BlockPos, BlockPos> setupTransform;
    private final Type type;
    private final BoundingBox3i boundingBox;

    public SubLevelSchematicSerializationContext(Type type, BoundingBox3i boundingBox) {
        this.type = type;
        this.boundingBox = boundingBox;
    }

    public Type getType() {
        return this.type;
    }

    public BoundingBox3i getBoundingBox() {
        return this.boundingBox;
    }

    public static SubLevelSchematicSerializationContext getCurrentContext() {
        return (SubLevelSchematicSerializationContext)THREAD_LOCAL.get();
    }

    @ApiStatus.Internal
    public static void setCurrentContext(@Nullable SubLevelSchematicSerializationContext context) {
        THREAD_LOCAL.set((Object)context);
    }

    public Function<BlockPos, BlockPos> getPlaceTransform() {
        return this.placeTransform;
    }

    public Function<BlockPos, BlockPos> getSetupTransform() {
        return this.setupTransform;
    }

    @ApiStatus.Internal
    public void setPlaceTransform(Function<BlockPos, BlockPos> transform) {
        this.placeTransform = transform;
    }

    @ApiStatus.Internal
    public void setSetupTransform(Function<BlockPos, BlockPos> transform) {
        this.setupTransform = transform;
    }

    @Nullable
    public SchematicMapping getMapping(SubLevel subLevel) {
        return this.mappings.get(subLevel.getUniqueId());
    }

    @Nullable
    public SchematicMapping getMapping(UUID uuid) {
        return this.mappings.get(uuid);
    }

    @ApiStatus.Internal
    public Map<UUID, SchematicMapping> getMappings() {
        return this.mappings;
    }

    public static enum Type {
        PLACE,
        SAVE;

    }

    public record SchematicMapping(Vector3dc newCorner, Quaterniondc newOrientation, UUID newUUID, Function<BlockPos, BlockPos> transform) {
    }
}
