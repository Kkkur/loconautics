/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.BufferBuilder
 *  com.mojang.blaze3d.vertex.BufferUploader
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.Tesselator
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  com.mojang.math.Axis
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.simulated_team.simulated.util.SimColors
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.shader.program.ShaderProgram
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.ponder.api.level.PonderLevel
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.core.BlockPos
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix4f
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlock;
import dev.eriksonn.aeronautics.content.blocks.hot_air.hot_air_burner.HotAirBurnerBlockEntity;
import dev.eriksonn.aeronautics.index.AeroPartialModels;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.util.SimColors;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.program.ShaderProgram;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class HotAirBurnerRenderer
extends SmartBlockEntityRenderer<HotAirBurnerBlockEntity> {
    private static final ResourceLocation BURNER_FLAME_SHADER = Aeronautics.path("burner_flame");

    public HotAirBurnerRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(HotAirBurnerBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        SubLevel sublevel;
        float signalStrength = Math.max(0.0f, (float)be.getSignalStrength() / 15.0f);
        SuperByteBuffer indicator = CachedBuffers.partial((PartialModel)AeroPartialModels.HOT_AIR_BURNER_INDICATOR, (BlockState)be.getBlockState());
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());
        indicator.light(light).color(SimColors.redstone((float)signalStrength)).renderInto(ms, vb);
        if ((double)signalStrength <= 0.0) {
            return;
        }
        ms.pushPose();
        ms.translate(-0.5, 0.35, 0.5);
        BlockPos pos = be.getBlockPos();
        Vec3 center = pos.getCenter();
        Minecraft minecraft = Minecraft.getInstance();
        Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
        if (be.getLevel() instanceof PonderLevel) {
            camera = minecraft.getCameraEntity().getPosition(partialTicks);
        }
        if ((sublevel = Sable.HELPER.getContaining((BlockEntity)be)) != null) {
            camera = sublevel.logicalPose().transformPositionInverse(camera);
        }
        float angle = (float)Math.atan2(camera.z() - center.z(), camera.x() - center.x());
        HotAirBurnerBlock.Variant variant = (HotAirBurnerBlock.Variant)((Object)be.getBlockState().getValue(HotAirBurnerBlock.VARIANT));
        float palette = variant == HotAirBurnerBlock.Variant.FIRE ? 0.25f : 0.75f;
        ShaderProgram shader = VeilRenderSystem.setShader((ResourceLocation)BURNER_FLAME_SHADER);
        float flameRenderTime = (float)Mth.lerp((double)partialTicks, (double)be.lastRenderTime, (double)be.renderTime) + be.getTimeOffset();
        shader.getUniformSafe((CharSequence)"FlameRenderTime").setFloat(flameRenderTime);
        shader.getUniformSafe((CharSequence)"Intensity").setFloat(be.getFlameIntensity(partialTicks));
        shader.getUniformSafe((CharSequence)"Palette").setFloat(palette);
        ms.rotateAround(Axis.YP.rotation((float)((double)(-angle) + 1.5707963267948966)), 1.0f, 0.0f, 0.0f);
        HotAirBurnerRenderer.renderFlame(ms);
        ms.popPose();
        super.renderSafe((SmartBlockEntity)be, partialTicks, ms, buffer, light, overlay);
    }

    private static void renderFlame(PoseStack poseStack) {
        float size = 2.0f;
        BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        Matrix4f pose = poseStack.last().pose();
        builder.addVertex(pose, 0.0f, 0.0f, 0.0f).setUv(0.0f, 1.0f);
        builder.addVertex(pose, 2.0f, 0.0f, 0.0f).setUv(1.0f, 1.0f);
        builder.addVertex(pose, 2.0f, 2.0f, 0.0f).setUv(1.0f, 0.0f);
        builder.addVertex(pose, 0.0f, 2.0f, 0.0f).setUv(0.0f, 0.0f);
        BufferUploader.drawWithShader((MeshData)builder.buildOrThrow());
        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
    }
}
