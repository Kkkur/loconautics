/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.schematics;

import java.io.OutputStream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public static class ServerSchematicLoader.SchematicUploadEntry {
    public Level world;
    public BlockPos tablePos;
    public OutputStream stream;
    public long bytesUploaded;
    public long totalBytes;
    public int idleTime;

    public ServerSchematicLoader.SchematicUploadEntry(OutputStream stream, long totalBytes, Level world, BlockPos tablePos) {
        this.stream = stream;
        this.totalBytes = totalBytes;
        this.tablePos = tablePos;
        this.world = world;
        this.bytesUploaded = 0L;
        this.idleTime = 0;
    }
}
