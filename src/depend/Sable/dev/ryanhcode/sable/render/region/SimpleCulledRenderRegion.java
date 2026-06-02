/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexBuffer
 *  com.mojang.blaze3d.vertex.VertexBuffer$Usage
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ShaderInstance
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3i
 *  org.joml.Vector3ic
 */
package dev.ryanhcode.sable.render.region;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.render.region.SimpleCulledRenderRegionBuilder;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3i;
import org.joml.Vector3ic;

@ApiStatus.Internal
public abstract class SimpleCulledRenderRegion {
    private Collection<BlockPos> unbuiltData;
    private boolean built = false;
    private VertexBuffer buffer;
    private Vec3 origin;

    public SimpleCulledRenderRegion(Collection<BlockPos> blocks) {
        this.unbuiltData = blocks;
    }

    public void render(Matrix4f modelView, Matrix4f projectionMatrix) {
        if (!this.built) {
            this.build();
        }
        ShaderInstance shader = RenderSystem.getShader();
        assert (shader != null);
        Minecraft client = Minecraft.getInstance();
        SubLevel subLevel = Sable.HELPER.getContaining((Level)client.level, (Position)this.origin);
        Vec3 globalOrigin = this.origin;
        Quaternionf globalOrientation = new Quaternionf();
        if (subLevel instanceof ClientSubLevel) {
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            Pose3dc renderPose = clientSubLevel.renderPose();
            globalOrigin = renderPose.transformPosition(globalOrigin);
            globalOrientation.set(renderPose.orientation());
        }
        Vec3 relativePos = globalOrigin.subtract(client.gameRenderer.getMainCamera().getPosition());
        Matrix4f modelViewMatrix = new Matrix4f((Matrix4fc)modelView).setTranslation(0.0f, 0.0f, 0.0f).translate((float)relativePos.x, (float)relativePos.y, (float)relativePos.z).rotate((Quaternionfc)globalOrientation);
        shader.setDefaultUniforms(VertexFormat.Mode.QUADS, modelViewMatrix, projectionMatrix, client.getWindow());
        shader.apply();
        this.buffer.bind();
        this.buffer.draw();
        VertexBuffer.unbind();
    }

    public void build() {
        BlockPos firstBlock = this.unbuiltData.stream().findFirst().orElseThrow();
        Vector3i minBlock = new Vector3i(firstBlock.getX(), firstBlock.getY(), firstBlock.getZ());
        Vector3i maxBlock = new Vector3i(firstBlock.getX(), firstBlock.getY(), firstBlock.getZ());
        Vector3i currentBlock = new Vector3i();
        for (BlockPos block : this.unbuiltData) {
            currentBlock.set(block.getX(), block.getY(), block.getZ());
            minBlock.min((Vector3ic)currentBlock);
            maxBlock.max((Vector3ic)currentBlock);
        }
        int gridSize = maxBlock.x() - minBlock.x() + 1;
        gridSize = Math.max(gridSize, maxBlock.y() - minBlock.y() + 1);
        gridSize = Math.max(gridSize, maxBlock.z() - minBlock.z() + 1);
        BlockPos originBlock = new BlockPos(minBlock.x(), minBlock.y(), minBlock.z());
        this.origin = Vec3.atLowerCornerOf((Vec3i)originBlock);
        SimpleCulledRenderRegionBuilder builder = this.createMeshBuilder(gridSize);
        for (BlockPos blockPos : this.unbuiltData) {
            builder.add(blockPos.getX() - originBlock.getX(), blockPos.getY() - originBlock.getY(), blockPos.getZ() - originBlock.getZ());
        }
        builder.buildNoGreedy();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, this.getVertexFormat());
        builder.render(new Matrix4f(), (VertexConsumer)bufferBuilder);
        this.unbuiltData = null;
        this.buffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.buffer.bind();
        this.buffer.upload(bufferBuilder.buildOrThrow());
        this.built = true;
    }

    public Vec3 getOrigin() {
        return this.origin;
    }

    public abstract SimpleCulledRenderRegionBuilder createMeshBuilder(int var1);

    public abstract VertexFormat getVertexFormat();

    public void free() {
        if (this.built) {
            this.buffer.close();
        }
    }
}
