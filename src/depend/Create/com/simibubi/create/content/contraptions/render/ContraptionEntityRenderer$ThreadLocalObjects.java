/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.createmod.catnip.render.ShadedBlockSbbBuilder
 *  net.minecraft.util.RandomSource
 */
package com.simibubi.create.content.contraptions.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.createmod.catnip.render.ShadedBlockSbbBuilder;
import net.minecraft.util.RandomSource;

private static class ContraptionEntityRenderer.ThreadLocalObjects {
    public final PoseStack poseStack = new PoseStack();
    public final RandomSource random = RandomSource.createNewThreadLocalInstance();
    public final ShadedBlockSbbBuilder sbbBuilder = ShadedBlockSbbBuilder.create();

    private ContraptionEntityRenderer.ThreadLocalObjects() {
    }
}
