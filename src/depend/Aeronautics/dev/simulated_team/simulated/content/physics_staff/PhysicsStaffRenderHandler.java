/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack$Pose
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllSpecialTextures
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.sublevel.SubLevelContainer
 *  dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  foundry.veil.api.client.color.Color
 *  foundry.veil.api.client.render.MatrixStack
 *  foundry.veil.api.event.VeilRenderLevelStageEvent$Stage
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Camera
 *  net.minecraft.client.DeltaTracker
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource$BufferSource
 *  net.minecraft.client.renderer.culling.Frustum
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.physics_staff;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllSpecialTextures;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.clip_overwrite.LevelPoseProviderExtension;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffClientHandler;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimRenderTypes;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.render.MatrixStack;
import foundry.veil.api.event.VeilRenderLevelStageEvent;
import java.util.List;
import java.util.UUID;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

public class PhysicsStaffRenderHandler {
    @Nullable
    private static BlockPos hoverBlockPos = null;

    public static void renderSelectionBox(VeilRenderLevelStageEvent.Stage stage, LevelRenderer renderer, MultiBufferSource.BufferSource bufferSource, MatrixStack ps, Matrix4fc frustrumMat, Matrix4fc projectionMat, int renderTick, DeltaTracker tracker, Camera camera, Frustum frustrum) {
        if (stage != VeilRenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        if (Minecraft.getInstance().options.hideGui) {
            return;
        }
        ps.matrixPush();
        SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.onRender(ps.toPoseStack());
        ps.matrixPop();
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (!player.getItemInHand(InteractionHand.MAIN_HAND).is(SimItems.PHYSICS_STAFF) && !player.getItemInHand(InteractionHand.OFF_HAND).is(SimItems.PHYSICS_STAFF)) {
            return;
        }
        Vec3 cameraPos = camera.getPosition();
        Level level = player.level();
        PhysicsStaffRenderHandler.renderAllLocks(bufferSource, ps, level, cameraPos);
        PhysicsStaffRenderHandler.updateHoverPos(minecraft, player);
        if (hoverBlockPos != null) {
            Color color = new Color(0.7490196f, 0.7490196f, 0.7490196f, 1.0f);
            Outliner.getInstance().showCluster((Object)"physicsStaffSelection", List.of(hoverBlockPos)).colored(color.rgb()).disableLineNormals().lineWidth(0.03125f).withFaceTexture((BindableTexture)AllSpecialTextures.CHECKERED);
        }
    }

    private static void updateHoverPos(Minecraft minecraft, LocalPlayer player) {
        BlockHitResult blockHitResult;
        ClientLevel level = minecraft.level;
        float partialTicks = minecraft.getTimer().getGameTimeDeltaPartialTick(false);
        hoverBlockPos = null;
        PhysicsStaffClientHandler.ClientDragSession dragSession = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.getDragSession();
        if (dragSession != null) {
            Vector3dc localAnchor = dragSession.dragLocalAnchor();
            hoverBlockPos = BlockPos.containing((double)localAnchor.x(), (double)localAnchor.y(), (double)localAnchor.z());
            return;
        }
        LevelPoseProviderExtension extension = (LevelPoseProviderExtension)level;
        extension.sable$pushPoseSupplier(x -> ((ClientSubLevel)x).renderPose());
        HitResult hit = player.pick((double)PhysicsStaffItem.RANGE, partialTicks, false);
        extension.sable$popPoseSupplier();
        if (!(hit instanceof BlockHitResult) || (blockHitResult = (BlockHitResult)hit).getType() == HitResult.Type.MISS) {
            return;
        }
        Vec3 hitLocation = hit.getLocation();
        SubLevel subLevel = Sable.HELPER.getContaining((Level)level, (Position)hitLocation);
        if (subLevel == null) {
            return;
        }
        hoverBlockPos = blockHitResult.getBlockPos();
    }

    private static void renderAllLocks(MultiBufferSource.BufferSource bufferSource, MatrixStack ps, Level level, Vec3 cameraPos) {
        Minecraft client = Minecraft.getInstance();
        List<UUID> locks = SimulatedClient.PHYSICS_STAFF_CLIENT_HANDLER.getLocks(level);
        SubLevelContainer container = SubLevelContainer.getContainer((Level)level);
        for (UUID lock : locks) {
            SubLevel subLevel = container.getSubLevel(lock);
            if (!(subLevel instanceof ClientSubLevel)) continue;
            ClientSubLevel clientSubLevel = (ClientSubLevel)subLevel;
            ps.matrixPush();
            Vector3dc renderPos = clientSubLevel.renderPose().position();
            ps.translate(renderPos.x() - cameraPos.x(), renderPos.y() - cameraPos.y(), renderPos.z() - cameraPos.z());
            ps.rotate((Quaternionfc)client.getEntityRenderDispatcher().cameraOrientation());
            VertexConsumer buffer = bufferSource.getBuffer(SimRenderTypes.lock());
            PoseStack.Pose pose = ps.pose();
            int color = -1;
            buffer.addVertex(pose, -0.5f, -0.5f, 0.0f).setColor(-1).setUv(0.0f, 1.0f).setLight(0xF000F0);
            buffer.addVertex(pose, -0.5f, 0.5f, 0.0f).setColor(-1).setUv(0.0f, 0.0f).setLight(0xF000F0);
            buffer.addVertex(pose, 0.5f, 0.5f, 0.0f).setColor(-1).setUv(1.0f, 0.0f).setLight(0xF000F0);
            buffer.addVertex(pose, 0.5f, -0.5f, 0.0f).setColor(-1).setUv(1.0f, 1.0f).setLight(0xF000F0);
            ps.matrixPop();
        }
    }
}
