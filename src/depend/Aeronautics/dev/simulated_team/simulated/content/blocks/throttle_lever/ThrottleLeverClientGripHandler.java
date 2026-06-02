/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3dc
 *  dev.ryanhcode.sable.sublevel.ClientSubLevel
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package dev.simulated_team.simulated.content.blocks.throttle_lever;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3dc;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlock;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverBlockEntity;
import dev.simulated_team.simulated.content.blocks.throttle_lever.ThrottleLeverRenderer;
import dev.simulated_team.simulated.index.SimBlocks;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class ThrottleLeverClientGripHandler {
    private static final PoseStack stack = new PoseStack();
    private static final Set<ThrottleLeverBlockEntity> nearbyThrottleLevers = new ObjectOpenHashSet();

    public static void tickGrip(ThrottleLeverBlockEntity blockEntity) {
        if (ThrottleLeverClientGripHandler.isInvalid(blockEntity)) {
            return;
        }
        nearbyThrottleLevers.add(blockEntity);
    }

    private static boolean isInvalid(ThrottleLeverBlockEntity blockEntity) {
        if (blockEntity.isRemoved()) {
            return true;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return true;
        }
        double reach = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue() + 2.0;
        BlockPos blockPos = blockEntity.getBlockPos();
        return player.distanceToSqr(blockPos.getCenter()) > reach * reach;
    }

    public static void clearNearbyThrottleLevers() {
        nearbyThrottleLevers.removeIf(ThrottleLeverClientGripHandler::isInvalid);
    }

    public static Collection<ThrottleLeverBlockEntity> getNearbyThrottleLevers() {
        return nearbyThrottleLevers;
    }

    public static Double raycastLever(Vec3 eyePosMoj, Vec3 viewVectorMoj, ThrottleLeverBlockEntity lever, float partialTicks) {
        Pose3dc pose;
        LocalPlayer player = Minecraft.getInstance().player;
        assert (player != null);
        BlockPos leverPos = lever.getBlockPos();
        Vector3d eyePos = JOMLConversion.toJOML((Position)eyePosMoj);
        Vector3d viewVector = JOMLConversion.toJOML((Position)viewVectorMoj);
        ClientSubLevel subLevel = Sable.HELPER.getContainingClient((BlockEntity)lever);
        if (subLevel != null) {
            pose = subLevel.renderPose(partialTicks);
            pose.transformPositionInverse(eyePos);
            pose.transformNormalInverse(viewVector);
        }
        stack.pushPose();
        stack.translate((double)leverPos.getX() - eyePos.x, (double)leverPos.getY() - eyePos.y, (double)leverPos.getZ() - eyePos.z);
        ThrottleLeverRenderer.transformHandleExternal(lever, partialTicks, stack);
        pose = stack.last().pose();
        pose.invert();
        stack.popPose();
        Vector3f localViewPosition = pose.transformPosition(new Vector3f());
        Vector3f localViewDirection = pose.transformDirection(new Vector3f((float)viewVector.x, (float)viewVector.y, (float)viewVector.z));
        VoxelShape leverShape = ((ThrottleLeverBlock)SimBlocks.THROTTLE_LEVER.get()).getHandleShape(SimBlocks.THROTTLE_LEVER.getDefaultState());
        eyePos.set((Vector3fc)localViewPosition);
        viewVector.set((Vector3fc)localViewDirection).mul(player.blockInteractionRange()).add((Vector3dc)eyePos);
        BlockHitResult hitResult = leverShape.clip(JOMLConversion.toMojang((Vector3dc)eyePos), JOMLConversion.toMojang((Vector3dc)viewVector), BlockPos.ZERO);
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
            return null;
        }
        Vec3 location = hitResult.getLocation();
        return eyePos.distanceSquared(location.x, location.y, location.z);
    }
}
