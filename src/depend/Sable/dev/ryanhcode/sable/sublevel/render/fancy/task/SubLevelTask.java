/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.SectionBufferBuilderPack
 */
package dev.ryanhcode.sable.sublevel.render.fancy.task;

import dev.ryanhcode.sable.sublevel.render.fancy.BucketRenderBuffer;
import dev.ryanhcode.sable.sublevel.render.fancy.SubLevelMeshBuilder;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.renderer.SectionBufferBuilderPack;

@FunctionalInterface
public interface SubLevelTask {
    public void process(SectionBufferBuilderPack var1, MeshUploader var2);

    public static interface MeshUploader {
        public CompletableFuture<BucketRenderBuffer.Slice[]> upload(SubLevelMeshBuilder.QuadMesh var1);

        public SubLevelMeshBuilder getMeshBuilder();
    }
}
