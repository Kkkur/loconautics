/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.render.ShadedBlockSbbBuilder
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.RandomSource
 */
package com.simibubi.create.content.schematics.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

private static class SchematicRenderer.ThreadLocalObjects {
    public final PoseStack poseStack = new PoseStack();
    public final RandomSource random = RandomSource.createNewThreadLocalInstance();
    public final BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
    public final ShadedBlockSbbBuilder sbbBuilder = ShadedBlockSbbBuilder.create();

    private SchematicRenderer.ThreadLocalObjects() {
    }
}
