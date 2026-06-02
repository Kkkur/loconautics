/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  foundry.veil.Veil
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.ryanhcode.sable.neoforge.compatibility.flywheel;

import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.Veil;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FlywheelCompatNeoForge {
    public static boolean FLYWHEEL_LOADED = Veil.platform().isModLoaded("flywheel");
    private static final Long2ObjectMap<SubLevelFlwRenderState> RENDER_POSES = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());

    public static void tryAddVisual(BlockEntity blockEntity) {
        VisualizationHelper.tryAddBlockEntity((BlockEntity)blockEntity);
    }

    public static void preVisualizationFrame(Level level, float partialTicks) {
        ClientSubLevelContainer container = (ClientSubLevelContainer)SubLevelContainer.getContainer(level);
        if (container == null) {
            RENDER_POSES.clear();
            return;
        }
        ObjectIterator iter = RENDER_POSES.long2ObjectEntrySet().iterator();
        while (iter.hasNext()) {
            int plotZ;
            Long2ObjectMap.Entry entry = (Long2ObjectMap.Entry)iter.next();
            long pos = entry.getLongKey();
            SubLevelFlwRenderState poseEntry = (SubLevelFlwRenderState)entry.getValue();
            int plotX = ChunkPos.getX((long)pos);
            SubLevel subLevel = container.getSubLevel(plotX, plotZ = ChunkPos.getZ((long)pos));
            if (subLevel == null || !Objects.equals(subLevel.getUniqueId(), poseEntry.subLevelID)) {
                iter.remove();
                continue;
            }
            FlywheelCompatNeoForge.updateEntry(container, (ClientSubLevel)subLevel, poseEntry, partialTicks);
        }
    }

    public static SubLevelFlwRenderState getInfo(long plotCoord) {
        return (SubLevelFlwRenderState)RENDER_POSES.get(plotCoord);
    }

    private static void updateEntry(ClientSubLevelContainer container, ClientSubLevel clientSubLevel, SubLevelFlwRenderState poseEntry, float partialTicks) {
        poseEntry.sceneID = container.getLightingSceneId(clientSubLevel);
        poseEntry.subLevelID = clientSubLevel.getUniqueId();
        poseEntry.renderPose.set(clientSubLevel.renderPose(partialTicks));
        poseEntry.latestSkyLightScale = clientSubLevel.getLatestSkyLightScale();
        poseEntry.centerChunk = clientSubLevel.getPlot().getCenterChunk();
    }

    public static void createRenderInfo(Level level, SubLevel subLevel) {
        ClientSubLevelContainer container = (ClientSubLevelContainer)SubLevelContainer.getContainer(level);
        if (container == null) {
            return;
        }
        ChunkPos plotPos = subLevel.getPlot().plotPos;
        long plotCoord = ChunkPos.asLong((int)(plotPos.x - container.getOrigin().x), (int)(plotPos.z - container.getOrigin().y));
        RENDER_POSES.computeIfAbsent(plotCoord, x -> {
            SubLevelFlwRenderState renderState = new SubLevelFlwRenderState();
            FlywheelCompatNeoForge.updateEntry(container, (ClientSubLevel)subLevel, renderState, 1.0f);
            return renderState;
        });
    }

    public static class SubLevelFlwRenderState {
        public int sceneID;
        public final Pose3d renderPose = new Pose3d();
        public UUID subLevelID;
        public float latestSkyLightScale;
        public ChunkPos centerChunk;
    }
}
