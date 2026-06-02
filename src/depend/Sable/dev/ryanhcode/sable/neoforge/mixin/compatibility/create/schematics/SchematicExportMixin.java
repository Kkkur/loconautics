/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  com.simibubi.create.content.schematics.SchematicAndQuillItem
 *  com.simibubi.create.content.schematics.SchematicExport
 *  com.simibubi.create.content.schematics.SchematicExport$SchematicExportResult
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.companion.math.BoundingBox3ic
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  it.unimi.dsi.fastutil.Function
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3i
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.schematics;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.simibubi.create.content.schematics.SchematicExport;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.schematic.SubLevelSchematicSerializationContext;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.util.SableNBTUtils;
import it.unimi.dsi.fastutil.Function;
import java.nio.file.Path;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SchematicExport.class})
public class SchematicExportMixin {
    @Inject(method={"saveSchematic"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate;fillFromWorld(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Vec3i;ZLnet/minecraft/world/level/block/Block;)V", shift=At.Shift.BEFORE)})
    private static void sable$saveSchematic(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second, CallbackInfoReturnable<SchematicExport.SchematicExportResult> cir, @Share(value="containingSubLevel") LocalRef<SubLevel> containingSubLevelRef, @Share(value="intersectingSubLevels") LocalRef<Iterable<SubLevel>> intersectingRef) {
        BoundingBox3d schematicBounds = new BoundingBox3d((double)first.getX(), (double)first.getY(), (double)first.getZ(), (double)(second.getX() + 1), (double)(second.getY() + 1), (double)(second.getZ() + 1));
        BoundingBox bb = BoundingBox.fromCorners((Vec3i)first, (Vec3i)second);
        BlockPos totalOrigin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel containingSubLevel = helper.getContaining(level, (Vector3dc)schematicBounds.center(new Vector3d()));
        if (containingSubLevel != null) {
            Pose3d containingPose = containingSubLevel.logicalPose();
            schematicBounds.transform((Pose3dc)containingPose, schematicBounds);
        }
        containingSubLevelRef.set((Object)containingSubLevel);
        Iterable<SubLevel> intersecting = helper.getAllIntersecting(level, (BoundingBox3dc)schematicBounds);
        intersectingRef.set(intersecting);
        SubLevelSchematicSerializationContext context = new SubLevelSchematicSerializationContext(SubLevelSchematicSerializationContext.Type.SAVE, new BoundingBox3i(first, second));
        context.setSetupTransform((Function<BlockPos, BlockPos>)((Function)block -> (BlockPos)block));
        context.setPlaceTransform((Function<BlockPos, BlockPos>)((Function)block -> ((BlockPos)block).subtract((Vec3i)totalOrigin)));
        for (SubLevel subLevel : intersecting) {
            if (subLevel == containingSubLevel) continue;
            BoundingBox3ic plotBounds = subLevel.getPlot().getBoundingBox();
            BlockPos origin = new BlockPos(plotBounds.minX(), plotBounds.minY(), plotBounds.minZ());
            Vec3 pos = subLevel.logicalPose().transformPosition(Vec3.atLowerCornerOf((Vec3i)origin));
            Quaterniond orientation = new Quaterniond((Quaterniondc)subLevel.logicalPose().orientation());
            if (containingSubLevel != null) {
                Pose3d containingPose = containingSubLevel.logicalPose();
                pos = containingPose.transformPositionInverse(pos);
                orientation.premul((Quaterniondc)containingPose.orientation().conjugate(new Quaterniond()));
            }
            Vector3d position = JOMLConversion.toJOML((Position)pos.subtract(Vec3.atLowerCornerOf((Vec3i)totalOrigin)));
            context.getMappings().put(subLevel.getUniqueId(), new SubLevelSchematicSerializationContext.SchematicMapping((Vector3dc)position, (Quaterniondc)orientation, UUID.randomUUID(), (Function<BlockPos, BlockPos>)((Function)block -> ((BlockPos)block).offset((Vec3i)origin.multiply(-1)))));
        }
        SubLevelSchematicSerializationContext.setCurrentContext(context);
    }

    @Inject(method={"saveSchematic"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/schematics/SchematicAndQuillItem;clampGlueBoxes(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/nbt/CompoundTag;)V", shift=At.Shift.AFTER)})
    private static void sable$saveSchematicPost(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second, CallbackInfoReturnable<SchematicExport.SchematicExportResult> cir, @Local CompoundTag data, @Share(value="containingSubLevel") LocalRef<SubLevel> containingSubLevelRef, @Share(value="intersectingSubLevels") LocalRef<Iterable<SubLevel>> intersectingRef) {
        ListTag list = new ListTag();
        SubLevel containingSubLevel = (SubLevel)containingSubLevelRef.get();
        SubLevelSchematicSerializationContext context = SubLevelSchematicSerializationContext.getCurrentContext();
        for (SubLevel subLevel : (Iterable)intersectingRef.get()) {
            if (subLevel == containingSubLevel) continue;
            BoundingBox3ic plotBounds = subLevel.getPlot().getBoundingBox();
            Vector3i size = plotBounds.size(new Vector3i());
            BlockPos origin = new BlockPos(plotBounds.minX(), plotBounds.minY(), plotBounds.minZ());
            BlockPos bounds = new BlockPos(size.x() + 1, size.y() + 1, size.z() + 1);
            StructureTemplate structure = new StructureTemplate();
            structure.fillFromWorld(level, origin, (Vec3i)bounds, true, Blocks.AIR);
            CompoundTag subLevelData = structure.save(new CompoundTag());
            SchematicAndQuillItem.replaceStructureVoidWithAir((CompoundTag)subLevelData);
            SchematicAndQuillItem.clampGlueBoxes((Level)level, (AABB)new AABB(Vec3.atLowerCornerOf((Vec3i)origin), Vec3.atLowerCornerOf((Vec3i)origin.offset((Vec3i)bounds))), (CompoundTag)subLevelData);
            SubLevelSchematicSerializationContext.SchematicMapping mapping = context.getMapping(subLevel);
            subLevelData.putUUID("uuid", mapping.newUUID());
            subLevelData.put("position", (Tag)SableNBTUtils.writeVector3d(mapping.newCorner()));
            subLevelData.put("orientation", (Tag)SableNBTUtils.writeQuaternion(mapping.newOrientation()));
            list.add((Object)subLevelData);
        }
        SubLevelSchematicSerializationContext.setCurrentContext(null);
        if (!list.isEmpty()) {
            data.put("sub_levels", (Tag)list);
        }
    }
}
