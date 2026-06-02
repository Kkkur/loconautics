/*
 * Decompiled with CFR 0.152.
 */
package dev.ryanhcode.sable.sublevel.render.fancy.task;

import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import dev.ryanhcode.sable.sublevel.render.fancy.SubLevelMeshBuilder;
import java.util.concurrent.CompletableFuture;

public static interface SubLevelTask.MeshUploader {
    public CompletableFuture<BucketRenderBuffer.Slice[]> upload(SubLevelMeshBuilder.QuadMesh var1);

    public SubLevelMeshBuilder getMeshBuilder();
}
