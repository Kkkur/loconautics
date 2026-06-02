/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtIo
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.schematics;

import com.simibubi.create.Create;
import com.simibubi.create.content.schematics.SchematicAndQuillItem;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.FilesHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class SchematicExport {
    @Nullable
    public static SchematicExportResult saveSchematic(Path dir, String fileName, boolean overwrite, Level level, BlockPos first, BlockPos second) {
        BoundingBox bb = BoundingBox.fromCorners((Vec3i)first, (Vec3i)second);
        BlockPos origin = new BlockPos(bb.minX(), bb.minY(), bb.minZ());
        BlockPos bounds = new BlockPos(bb.getXSpan(), bb.getYSpan(), bb.getZSpan());
        StructureTemplate structure = new StructureTemplate();
        structure.fillFromWorld(level, origin, (Vec3i)bounds, true, Blocks.AIR);
        CompoundTag data = structure.save(new CompoundTag());
        SchematicAndQuillItem.replaceStructureVoidWithAir(data);
        SchematicAndQuillItem.clampGlueBoxes(level, new AABB(Vec3.atLowerCornerOf((Vec3i)origin), Vec3.atLowerCornerOf((Vec3i)origin.offset((Vec3i)bounds))), data);
        if (((String)fileName).isEmpty()) {
            fileName = CreateLang.translateDirect("schematicAndQuill.fallbackName", new Object[0]).getString();
        }
        if (!overwrite) {
            fileName = FilesHelper.findFirstValidFilename((String)fileName, dir, "nbt");
        }
        if (!((String)fileName).endsWith(".nbt")) {
            fileName = (String)fileName + ".nbt";
        }
        Path file = dir.resolve((String)fileName).toAbsolutePath();
        try {
            Files.createDirectories(dir, new FileAttribute[0]);
            boolean overwritten = Files.deleteIfExists(file);
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE);){
                NbtIo.writeCompressed((CompoundTag)data, (OutputStream)out);
            }
            return new SchematicExportResult(file, dir, (String)fileName, overwritten, origin, bounds);
        }
        catch (IOException e) {
            Create.LOGGER.error("An error occurred while saving schematic [" + (String)fileName + "]", (Throwable)e);
            return null;
        }
    }

    public record SchematicExportResult(Path file, Path dir, String fileName, boolean overwritten, BlockPos origin, BlockPos bounds) {
    }
}
