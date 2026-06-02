/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.vertex.MutableVertexList
 *  dev.engine_room.flywheel.lib.model.QuadMesh
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Mth
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package com.simibubi.create.content.fluids;

import dev.engine_room.flywheel.api.vertex.MutableVertexList;
import dev.engine_room.flywheel.lib.model.QuadMesh;
import net.createmod.catnip.data.Iterate;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public record FluidMesh.FluidStreamMesh(TextureAtlasSprite texture) implements QuadMesh
{
    public int vertexCount() {
        return 32;
    }

    public void write(MutableVertexList vertexList) {
        for (int i = 0; i < this.vertexCount(); ++i) {
            vertexList.r(i, 1.0f);
            vertexList.g(i, 1.0f);
            vertexList.b(i, 1.0f);
            vertexList.a(i, 1.0f);
            vertexList.light(i, 0);
            vertexList.overlay(i, OverlayTexture.NO_OVERLAY);
            vertexList.v(i, 0.0f);
        }
        float textureScale = 0.03125f;
        float shrink = this.texture.uvShrinkRatio() * 0.25f * textureScale;
        float centerU = this.texture.getU0() + (this.texture.getU1() - this.texture.getU0()) * 0.5f;
        float radius = 0.1875f;
        float left = -radius;
        float right = radius;
        int vertex = 0;
        for (Direction horizontalDirection : Iterate.horizontalDirections) {
            float x1 = left;
            while (x1 < right) {
                float x1floor = Mth.floor((float)x1);
                float x2 = Math.min(x1floor + 1.0f, right);
                float u1 = this.texture.getU((x1 - x1floor) * 16.0f * textureScale);
                float u2 = this.texture.getU((x2 - x1floor) * 16.0f * textureScale);
                u1 = Mth.lerp((float)shrink, (float)u1, (float)centerU);
                u2 = Mth.lerp((float)shrink, (float)u2, (float)centerU);
                FluidMesh.FluidStreamMesh.putQuad(vertexList, vertex, horizontalDirection, radius, x1, x2, u1, u2);
                vertex += 4;
                x1 = x2;
            }
        }
    }

    private static void putQuad(MutableVertexList vertexList, int i, Direction horizontal, float radius, float p0, float p1, float u0, float u1) {
        float zStart;
        float zEnd;
        float xEnd;
        float xStart;
        switch (horizontal) {
            case NORTH: {
                xStart = p1;
                xEnd = p0;
                zStart = zEnd = -radius;
                break;
            }
            case SOUTH: {
                xStart = p0;
                xEnd = p1;
                zStart = zEnd = radius;
                break;
            }
            case WEST: {
                zStart = p0;
                zEnd = p1;
                xStart = xEnd = -radius;
                break;
            }
            case EAST: {
                zStart = p1;
                zEnd = p0;
                xStart = xEnd = radius;
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected value: " + String.valueOf(horizontal));
            }
        }
        vertexList.x(i, xStart);
        vertexList.y(i, 1.0f);
        vertexList.z(i, zStart);
        vertexList.u(i, u0);
        vertexList.x(i + 1, xStart);
        vertexList.y(i + 1, 0.0f);
        vertexList.z(i + 1, zStart);
        vertexList.u(i + 1, u0);
        vertexList.x(i + 2, xEnd);
        vertexList.y(i + 2, 0.0f);
        vertexList.z(i + 2, zEnd);
        vertexList.u(i + 2, u1);
        vertexList.x(i + 3, xEnd);
        vertexList.y(i + 3, 1.0f);
        vertexList.z(i + 3, zEnd);
        vertexList.u(i + 3, u1);
        for (int j = 0; j < 4; ++j) {
            vertexList.normalX(i + j, (float)horizontal.getStepX());
            vertexList.normalY(i + j, (float)horizontal.getStepY());
            vertexList.normalZ(i + j, (float)horizontal.getStepZ());
        }
    }

    public Vector4fc boundingSphere() {
        return new Vector4f(0.0f, 0.5f, 0.0f, 1.0f);
    }
}
