/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.vertex.MutableVertexList
 *  dev.engine_room.flywheel.lib.model.QuadMesh
 *  net.minecraft.client.renderer.texture.OverlayTexture
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.util.Mth
 *  org.joml.Vector4f
 *  org.joml.Vector4fc
 */
package com.simibubi.create.content.fluids;

import dev.engine_room.flywheel.api.vertex.MutableVertexList;
import dev.engine_room.flywheel.lib.model.QuadMesh;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public record FluidMesh.FluidSurfaceMesh(TextureAtlasSprite texture, float width) implements QuadMesh
{
    public int vertexCount() {
        int quadWidth = Mth.ceil((float)this.width) - Mth.floor((float)(-this.width));
        return 4 * quadWidth * quadWidth;
    }

    public void write(MutableVertexList vertexList) {
        for (int i = 0; i < this.vertexCount(); ++i) {
            vertexList.r(i, 1.0f);
            vertexList.g(i, 1.0f);
            vertexList.b(i, 1.0f);
            vertexList.a(i, 1.0f);
            vertexList.light(i, 0);
            vertexList.overlay(i, OverlayTexture.NO_OVERLAY);
            vertexList.normalX(i, 0.0f);
            vertexList.normalY(i, 1.0f);
            vertexList.normalZ(i, 0.0f);
            vertexList.y(i, 0.0f);
        }
        float textureScale = 0.0625f;
        float left = -this.width;
        float right = this.width;
        float down = -this.width;
        float up = this.width;
        int vertex = 0;
        float shrink = this.texture.uvShrinkRatio() * 0.25f * textureScale;
        float centerU = this.texture.getU0() + (this.texture.getU1() - this.texture.getU0()) * 0.5f;
        float centerV = this.texture.getV0() + (this.texture.getV1() - this.texture.getV0()) * 0.5f;
        float x1 = left;
        while (x1 < right) {
            float x1floor = Mth.floor((float)x1);
            float x2 = Math.min(x1floor + 1.0f, right);
            float u1 = this.texture.getU((x1 - x1floor) * 16.0f * textureScale);
            float u2 = this.texture.getU((x2 - x1floor) * 16.0f * textureScale);
            u1 = Mth.lerp((float)shrink, (float)u1, (float)centerU);
            u2 = Mth.lerp((float)shrink, (float)u2, (float)centerU);
            float y1 = down;
            while (y1 < up) {
                float y1floor = Mth.floor((float)y1);
                float y2 = Math.min(y1floor + 1.0f, up);
                float v1 = this.texture.getV((y1 - y1floor) * 16.0f * textureScale);
                float v2 = this.texture.getV((y2 - y1floor) * 16.0f * textureScale);
                v1 = Mth.lerp((float)shrink, (float)v1, (float)centerV);
                v2 = Mth.lerp((float)shrink, (float)v2, (float)centerV);
                vertexList.x(vertex, x1);
                vertexList.z(vertex, y1);
                vertexList.u(vertex, u1);
                vertexList.v(vertex, v1);
                vertexList.x(vertex + 1, x1);
                vertexList.z(vertex + 1, y2);
                vertexList.u(vertex + 1, u1);
                vertexList.v(vertex + 1, v2);
                vertexList.x(vertex + 2, x2);
                vertexList.z(vertex + 2, y2);
                vertexList.u(vertex + 2, u2);
                vertexList.v(vertex + 2, v2);
                vertexList.x(vertex + 3, x2);
                vertexList.z(vertex + 3, y1);
                vertexList.u(vertex + 3, u2);
                vertexList.v(vertex + 3, v1);
                vertex += 4;
                y1 = y2;
            }
            x1 = x2;
        }
    }

    public Vector4fc boundingSphere() {
        return new Vector4f(0.0f, 0.0f, 0.0f, this.width / Mth.SQRT_OF_TWO);
    }
}
