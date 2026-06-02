/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.schematics;

import java.nio.file.Path;
import net.minecraft.core.BlockPos;

public record SchematicExport.SchematicExportResult(Path file, Path dir, String fileName, boolean overwritten, BlockPos origin, BlockPos bounds) {
}
