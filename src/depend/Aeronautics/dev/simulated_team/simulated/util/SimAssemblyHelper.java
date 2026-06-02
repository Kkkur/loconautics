/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.Contraption
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.StructureTransform
 *  com.simibubi.create.content.contraptions.glue.SuperGlueEntity
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper
 *  dev.ryanhcode.sable.api.SubLevelAssemblyHelper$AssemblyTransform
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.sublevel.ServerSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.LevelPlot
 *  dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder
 *  dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot
 *  dev.ryanhcode.sable.sublevel.plot.heat.SubLevelHeatMapManager
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.entity.EntitySection
 *  net.minecraft.world.level.entity.PersistentEntitySectionManager
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 */
package dev.simulated_team.simulated.util;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.contraptions.glue.SuperGlueEntity;
import dev.ryanhcode.sable.api.SubLevelAssemblyHelper;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.plot.ServerLevelPlot;
import dev.ryanhcode.sable.sublevel.plot.heat.SubLevelHeatMapManager;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.mixin.accessor.ContraptionAccessor;
import dev.simulated_team.simulated.mixin.accessor.ControlledContraptionEntityAccessor;
import dev.simulated_team.simulated.mixin_interface.create_assembly.IControlContraptionExtension;
import dev.simulated_team.simulated.util.assembly.SimAssemblyContraption;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class SimAssemblyHelper {
    public static void disassembleSubLevel(@NotNull Level level, @NotNull SubLevel toDisassemble, @NotNull BlockPos subLevelAnchor, @NotNull BlockPos disassemblyGoal, @NotNull Rotation rotation, @NotNull boolean playSound) {
        if (playSound) {
            level.playSound(null, subLevelAnchor, SimSoundEvents.SIMULATED_CONTRAPTION_STOPS.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        BoundingBox3i plotBounds = new BoundingBox3i(toDisassemble.getPlot().getBoundingBox());
        SubLevelAssemblyHelper.AssemblyTransform transform = new SubLevelAssemblyHelper.AssemblyTransform(subLevelAnchor, disassemblyGoal, rotation == Rotation.NONE ? 0 : 4 - rotation.ordinal(), rotation, (ServerLevel)level);
        ObjectArrayList blocks = new ObjectArrayList();
        LevelPlot plot = toDisassemble.getPlot();
        for (PlotChunkHolder chunk : plot.getLoadedChunks()) {
            BoundingBox3ic localChunkBounds = chunk.getBoundingBox();
            if (localChunkBounds == null || localChunkBounds == BoundingBox3i.EMPTY) continue;
            for (int x = localChunkBounds.minX(); x <= localChunkBounds.maxX(); ++x) {
                for (int y = localChunkBounds.minY(); y <= localChunkBounds.maxY(); ++y) {
                    for (int z = localChunkBounds.minZ(); z <= localChunkBounds.maxZ(); ++z) {
                        BlockPos pos = new BlockPos(x + chunk.getPos().getMinBlockX(), y, z + chunk.getPos().getMinBlockZ());
                        BlockState state = level.getBlockState(pos);
                        if (state.isAir()) continue;
                        blocks.add((Object)pos);
                    }
                }
            }
        }
        SimAssemblyHelper.disassembleAndAddCreateContraptions(level, plot.getBoundingBox(), (Collection<BlockPos>)blocks, false, null);
        PersistentEntitySectionManager manager = ((ServerLevel)toDisassemble.getLevel()).entityManager;
        for (PlotChunkHolder chunk : toDisassemble.getPlot().getLoadedChunks()) {
            Stream sections = manager.sectionStorage.getExistingSectionsInChunk(chunk.getPos().toLong());
            for (EntitySection section : sections.toList()) {
                List entities = section.getEntities().toList();
                for (Entity entity : entities) {
                    AABB box = entity.getBoundingBox();
                    box = new AABB(transform.apply(new Vec3(box.minX, box.minY, box.minZ)), transform.apply(new Vec3(box.maxX, box.maxY, box.maxZ)));
                    if (entity instanceof SuperGlueEntity) {
                        entity.remove(Entity.RemovalReason.KILLED);
                        level.addFreshEntity((Entity)new SuperGlueEntity(level, box));
                        continue;
                    }
                    if (entity instanceof HoneyGlueEntity) {
                        entity.remove(Entity.RemovalReason.KILLED);
                        HoneyGlueEntity newHoneyGlue = new HoneyGlueEntity(level, box);
                        level.addFreshEntity((Entity)newHoneyGlue);
                        newHoneyGlue.setBoundsAndSync(box);
                        continue;
                    }
                    Vec3 newPos = transform.apply(entity.position());
                    entity.setPos(newPos);
                    entity.setYRot(entity.rotate(transform.getRotation()));
                    entity.yRotO = entity.getYRot();
                    if (entity instanceof HangingEntity) {
                        HangingEntity hangingEntity = (HangingEntity)entity;
                        hangingEntity.recalculateBoundingBox();
                    }
                    entity.levelCallback.onRemove(Entity.RemovalReason.CHANGED_DIMENSION);
                    ((ServerLevel)level).addDuringTeleport(entity);
                }
            }
        }
        if (!blocks.isEmpty()) {
            ((ServerLevelPlot)toDisassemble.getPlot()).kickAllEntities();
            SubLevelAssemblyHelper.moveBlocks((ServerLevel)((ServerLevel)level), (SubLevelAssemblyHelper.AssemblyTransform)transform, (Iterable)blocks);
        }
        SubLevelAssemblyHelper.moveTrackingPoints((ServerLevel)((ServerLevel)level), (BoundingBox3ic)plotBounds, null, (SubLevelAssemblyHelper.AssemblyTransform)transform);
    }

    public static AssemblyResult assembleFromSingleBlock(Level level, BlockPos selfPos, BlockPos toAssemble, boolean includeStart, boolean includeEncasingGlue) throws AssemblyException {
        if (level.getBlockState(toAssemble).isAir()) {
            return null;
        }
        SimAssemblyContraption contraption = new SimAssemblyContraption(includeStart ? null : selfPos, !includeEncasingGlue);
        contraption.searchMovedStructure(level, toAssemble);
        Collection<BlockPos> blocks = contraption.getBlocks();
        if (!blocks.isEmpty()) {
            BoundingBox3i bounds = BoundingBox3i.from(blocks);
            Collection<SuperGlueEntity> superGlues = contraption.getGlues();
            Collection<HoneyGlueEntity> honeyGlues = contraption.getHoneyGlues();
            ObjectArrayList collectedContraptionGlues = new ObjectArrayList();
            SimAssemblyHelper.disassembleAndAddCreateContraptions(level, (BoundingBox3ic)bounds, blocks, true, (List<AABB>)collectedContraptionGlues);
            BlockPos anchor = blocks.stream().findFirst().get();
            ServerSubLevel subLevel = SubLevelAssemblyHelper.assembleBlocks((ServerLevel)((ServerLevel)level), (BlockPos)anchor, blocks, (BoundingBox3ic)bounds);
            if (subLevel != null) {
                BlockPos offsetBlocks = subLevel.getPlot().getCenterBlock().subtract((Vec3i)anchor);
                for (AABB aABB : collectedContraptionGlues) {
                    level.addFreshEntity((Entity)new SuperGlueEntity(level, aABB.move(Vec3.atLowerCornerOf((Vec3i)offsetBlocks))));
                }
                for (SuperGlueEntity superGlueEntity : superGlues) {
                    superGlueEntity.remove(Entity.RemovalReason.KILLED);
                    level.addFreshEntity((Entity)new SuperGlueEntity(level, superGlueEntity.getBoundingBox().move(Vec3.atLowerCornerOf((Vec3i)offsetBlocks))));
                }
                for (HoneyGlueEntity honeyGlueEntity : honeyGlues) {
                    honeyGlueEntity.remove(Entity.RemovalReason.KILLED);
                    AABB newBB = honeyGlueEntity.getBoundingBox().move(Vec3.atLowerCornerOf((Vec3i)offsetBlocks));
                    HoneyGlueEntity entity = new HoneyGlueEntity(level, newBB);
                    level.addFreshEntity((Entity)entity);
                    entity.setBoundsAndSync(newBB);
                }
                level.playSound(null, selfPos, SimSoundEvents.SIMULATED_CONTRAPTION_MOVES.event(), SoundSource.BLOCKS, 1.0f, 1.0f);
                return new AssemblyResult((SubLevel)subLevel, offsetBlocks);
            }
        }
        return null;
    }

    private static void disassembleAndAddCreateContraptions(Level level, BoundingBox3ic assemblyBounds, Collection<BlockPos> blocks, boolean passGluesBack, List<AABB> collectedGlues) {
        assert (assemblyBounds != null);
        AABB assemblyBoundsD = new AABB((double)assemblyBounds.minX(), (double)assemblyBounds.minY(), (double)assemblyBounds.minZ(), (double)(assemblyBounds.maxX() + 1), (double)(assemblyBounds.maxY() + 1), (double)(assemblyBounds.maxZ() + 1));
        List intersectingContraptions = level.getEntitiesOfClass(ControlledContraptionEntity.class, assemblyBoundsD.inflate(2.0));
        for (ControlledContraptionEntity contraptionEntity : intersectingContraptions) {
            ControlledContraptionEntityAccessor accessor = (ControlledContraptionEntityAccessor)contraptionEntity;
            BlockPos controllerPos = accessor.getControllerPos();
            if (!blocks.contains(controllerPos)) continue;
            Contraption contraption = contraptionEntity.getContraption();
            StructureTransform transform = accessor.invokeMakeStructureTransform();
            for (Object contraptionBlock : contraption.getBlocks().keySet()) {
                BlockPos targetPos = transform.apply((BlockPos)contraptionBlock);
                blocks.add(targetPos);
            }
            if (passGluesBack) {
                Object contraptionBlock;
                List<AABB> superGlue = ((ContraptionAccessor)contraption).getSuperGlue();
                contraptionBlock = superGlue.iterator();
                while (contraptionBlock.hasNext()) {
                    AABB aabb = (AABB)contraptionBlock.next();
                    aabb = new AABB(transform.apply(new Vec3(aabb.minX, aabb.minY, aabb.minZ)), transform.apply(new Vec3(aabb.maxX, aabb.maxY, aabb.maxZ)));
                    collectedGlues.add(aabb);
                }
                superGlue.clear();
            }
            contraptionEntity.disassemble();
            BlockEntity blockEntity = level.getBlockEntity(controllerPos);
            if (!(blockEntity instanceof IControlContraptionExtension)) continue;
            IControlContraptionExtension controlContraption = (IControlContraptionExtension)blockEntity;
            controlContraption.sable$disassemble();
        }
    }

    public static Rotation rotationFrom90DegRots(int rots) {
        return switch (Math.floorMod(rots, 4)) {
            case 0 -> Rotation.NONE;
            case 1 -> Rotation.COUNTERCLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.CLOCKWISE_90;
            default -> throw new AssertionError();
        };
    }

    public static void register() {
        SubLevelHeatMapManager.addSplitListener(SimAssemblyHelper::addSplitBlocks);
    }

    private static void addSplitBlocks(Level level, BoundingBox3ic boundingBox3ic, Collection<BlockPos> blocks) {
        SimAssemblyHelper.disassembleAndAddCreateContraptions(level, boundingBox3ic, blocks, false, null);
    }

    public record AssemblyResult(SubLevel subLevel, BlockPos offset) {
    }
}
