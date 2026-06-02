/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllTags$AllItemTags
 *  com.simibubi.create.foundation.utility.RaycastHelper
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.math.OrientedBoundingBox3d
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  foundry.veil.api.network.VeilPacketManager
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.KeyMapping
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.resources.sounds.SimpleSoundInstance
 *  net.minecraft.client.resources.sounds.SoundInstance
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.strand.client;

import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.RaycastHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.math.OrientedBoundingBox3d;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientLevelRopeManager;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopePoint;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.network.packets.RopeBreakPacket;
import dev.simulated_team.simulated.network.packets.RopeRidingPacket;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimMathUtils;
import dev.simulated_team.simulated.util.click_interactions.InteractCallback;
import foundry.veil.api.network.VeilPacketManager;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import java.util.UUID;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class ZiplineClientManager
implements InteractCallback {
    private static final double CONTINUOUS_STEP_SIZE = 0.25;
    private static final double HALF_THICKNESS = 0.25;
    public static UUID ridingRope = null;
    public static UUID hoveringRope = null;
    private static int groundedTimer = 0;

    public static void tick() {
        if (ridingRope != null) {
            ZiplineClientManager.ridingTick();
        } else {
            groundedTimer = 0;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!ZiplineClientManager.isRopeInteractable(mc.player.getMainHandItem())) {
            hoveringRope = null;
            return;
        }
        double maxRange = mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        HitResult hitResult = mc.hitResult;
        ClientLevelRopeManager ropeManager = ClientLevelRopeManager.getOrCreate((Level)mc.level);
        Vector3d from = JOMLConversion.toJOML((Position)mc.player.getEyePosition());
        Vector3d to = JOMLConversion.toJOML((Position)RaycastHelper.getTraceTarget((Player)mc.player, (double)maxRange, (Vec3)JOMLConversion.toMojang((Vector3dc)from)));
        double bestDiffSqr = hitResult == null ? 3.4028234663852886E38 : Sable.HELPER.projectOutOfSubLevel((Level)mc.level, hitResult.getLocation()).distanceToSqr(from.x, from.y, from.z);
        hoveringRope = ZiplineClientManager.raycastRope(ropeManager, (Vector3dc)from, (Vector3dc)to, bestDiffSqr, 0.25);
        if (ridingRope == null && hoveringRope == null && mc.options.keyUse.isDown() && !mc.player.isShiftKeyDown()) {
            ZiplineClientManager.holdUseSearch(ropeManager, (Player)mc.player, from, to, bestDiffSqr);
        }
    }

    @Nullable
    public static UUID raycastRope(ClientLevelRopeManager ropeManager, Vector3dc from, Vector3dc to, double bestDiffSqr, double halfThickness) {
        Vector3d localFrom = new Vector3d();
        Vector3d localTo = new Vector3d();
        Vector3d normal = new Vector3d();
        UUID bestHovering = null;
        for (ClientRopeStrand strand : ropeManager.getAllStrands()) {
            ObjectArrayList<ClientRopePoint> points = strand.getPoints();
            for (int i = 0; i < points.size() - 1; ++i) {
                double distanceToSqr;
                ClientRopePoint point0 = (ClientRopePoint)points.get(i);
                ClientRopePoint point1 = (ClientRopePoint)points.get(i + 1);
                point1.position().sub((Vector3dc)point0.position(), normal).normalize();
                AABB bounds = new AABB(-halfThickness, 0.0, -halfThickness, halfThickness, point0.position().distance((Vector3dc)point1.position()), halfThickness);
                Quaternionf rot = SimMathUtils.getQuaternionfFromVectorRotation(OrientedBoundingBox3d.UP, (Vector3dc)normal);
                rot.transformInverse(localFrom.set(from).sub((Vector3dc)point0.position()));
                rot.transformInverse(localTo.set(to).sub((Vector3dc)point0.position()));
                Optional clip = bounds.clip(JOMLConversion.toMojang((Vector3dc)localFrom), JOMLConversion.toMojang((Vector3dc)localTo));
                if (clip.isEmpty() || (distanceToSqr = ((Vec3)clip.get()).distanceToSqr(localFrom.x, localFrom.y, localFrom.z)) > bestDiffSqr) continue;
                bestDiffSqr = distanceToSqr;
                bestHovering = strand.getUuid();
            }
        }
        return bestHovering;
    }

    private static void holdUseSearch(ClientLevelRopeManager ropeManager, Player player, Vector3d from, Vector3d to, double bestDiffSqr) {
        Vec3 oldPlayerPosition = new Vec3(player.xo, player.yo, player.zo);
        Vec3 playerMovement = player.position().subtract(oldPlayerPosition);
        double length = Math.min(playerMovement.length(), 15.0);
        if (length > 1.0E-4) {
            playerMovement = playerMovement.normalize();
        }
        Vector3d offsetFrom = new Vector3d();
        Vector3d offsetTo = new Vector3d();
        Vector3d offset = new Vector3d();
        for (double i = 0.0; i < Math.max(length, 0.01); i += 0.25) {
            ClosestQuery query;
            ClientRopeStrand strand;
            JOMLConversion.toJOML((Position)playerMovement, (Vector3d)offset).mul(-i);
            offsetFrom.set((Vector3dc)from).add((Vector3dc)offset);
            offsetTo.set((Vector3dc)to).add((Vector3dc)offset);
            UUID foundStrand = ZiplineClientManager.raycastRope(ropeManager, (Vector3dc)offsetFrom, (Vector3dc)offsetTo, bestDiffSqr, 0.25);
            if (foundStrand == null || (strand = ropeManager.getStrand(foundStrand)) == null || !ZiplineClientManager.canStartRiding(query = ZiplineClientManager.getClosestPointOnStrand(strand, player), player, true)) continue;
            ZiplineClientManager.embark(foundStrand);
            player.swing(InteractionHand.MAIN_HAND);
            return;
        }
    }

    private static void ridingTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.isPaused()) {
            return;
        }
        if (!AllTags.AllItemTags.CHAIN_RIDEABLE.matches(mc.player.getMainHandItem())) {
            ZiplineClientManager.disembark();
            return;
        }
        ClientLevelRopeManager ropeHandler = ClientLevelRopeManager.getOrCreate((Level)mc.level);
        ClientRopeStrand strand = ropeHandler.getStrand(ridingRope);
        groundedTimer = mc.player.onGround() ? ++groundedTimer : 0;
        if (groundedTimer > 5 || mc.player.isShiftKeyDown() || mc.player.getAbilities().flying || strand == null) {
            ZiplineClientManager.disembark();
            return;
        }
        float chainYOffset = 0.5f * mc.player.getScale();
        Vec3 playerPosition = mc.player.position().add(0.0, mc.player.getBoundingBox().getYsize() + (double)chainYOffset, 0.0);
        ClosestQuery query = ZiplineClientManager.getClosestPointOnStrand(strand, playerPosition);
        boolean isEnd = query.position().distanceSquared((Vector3dc)((ClientRopePoint)strand.getPoints().getLast()).position()) < 0.25;
        boolean isStart = query.position().distanceSquared((Vector3dc)((ClientRopePoint)strand.getPoints().getFirst()).position()) < 0.25;
        Vec3 mojNormal = new Vec3(query.normal.x, query.normal.y, query.normal.z);
        double exitThreshold = 0.6;
        Vec3 exitingMovement = mc.player.getDeltaMovement();
        if (exitingMovement.lengthSqr() > 1.0E-8 && (isEnd && exitingMovement.normalize().dot(mojNormal) > 0.6 || isStart && exitingMovement.normalize().dot(mojNormal) < -0.6)) {
            ZiplineClientManager.disembark();
            return;
        }
        Vec3 target = JOMLConversion.toMojang((Vector3dc)query.position());
        Vec3 diff = target.subtract(playerPosition);
        Vec3 normal = JOMLConversion.toMojang((Vector3dc)query.normal());
        Vec3 assistanceForce = normal.scale(mc.player.getDeltaMovement().dot(normal)).scale(0.04);
        double reach = mc.player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        if (diff.lengthSqr() > reach * reach) {
            ZiplineClientManager.disembark();
            return;
        }
        Vec3 dampingForce = mc.player.getDeltaMovement().scale(-0.6);
        dampingForce = dampingForce.subtract(normal.scale(normal.dot(dampingForce)));
        float diffLength = diff.lengthSqr() > 0.0 ? Mth.sqrt((float)((float)diff.length())) : 0.0f;
        mc.player.setDeltaMovement(mc.player.getDeltaMovement().add(dampingForce).add(assistanceForce).add(diff.scale((double)diffLength * 0.3)));
        mc.player.fallDistance = 0.0f;
        if (AnimationTickHolder.getTicks() % 10 == 0) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new RopeRidingPacket(ridingRope, false)});
        }
    }

    public static boolean canStartRidingDistance(ClosestQuery query, Player player) {
        double reach = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        return query.position.distanceSquared((Vector3dc)JOMLConversion.toJOML((Position)player.position())) <= reach * reach;
    }

    public static boolean canStartRidingSteepness(ClosestQuery query, Player player) {
        double verticalDot = query.normal.dot((Vector3dc)new Vector3d(0.0, 1.0, 0.0));
        return Math.abs(verticalDot) <= Math.sin(Math.toRadians(SimConfigService.INSTANCE.server().blocks.maxRopeZiplineAngle.getF()));
    }

    public static boolean canStartRiding(ClosestQuery query, Player player, boolean sendMessage) {
        if (!ZiplineClientManager.canStartRidingDistance(query, player)) {
            if (sendMessage) {
                player.displayClientMessage((Component)SimLang.translate("zipline.too_far", new Object[0]).color(SimColors.NUH_UH_RED).component(), true);
            }
            return false;
        }
        if (!ZiplineClientManager.canStartRidingSteepness(query, player)) {
            if (sendMessage) {
                player.displayClientMessage((Component)SimLang.translate("zipline.too_steep", new Object[0]).color(SimColors.NUH_UH_RED).component(), true);
            }
            return false;
        }
        return true;
    }

    public static ClosestQuery getClosestPointOnStrand(ClientRopeStrand strand, Vec3 playerPosition) {
        ObjectArrayList<ClientRopePoint> points = strand.getPoints();
        double minDistanceSquared = Double.MAX_VALUE;
        Vector3d minPoint = new Vector3d();
        Vector3d minNormal = new Vector3d();
        Vector3d point = new Vector3d();
        Vector3d diff = new Vector3d();
        Vector3d normalizedDiff = new Vector3d();
        for (int i = 0; i < points.size() - 1; ++i) {
            Vector3d pointA = ((ClientRopePoint)points.get(i)).position();
            Vector3d pointB = ((ClientRopePoint)points.get(i + 1)).position();
            pointB.sub((Vector3dc)pointA, diff);
            diff.normalize(normalizedDiff);
            point.set(playerPosition.x, playerPosition.y, playerPosition.z).sub((Vector3dc)pointA);
            double along = point.dot((Vector3dc)normalizedDiff);
            along = Mth.clamp((double)along, (double)0.0, (double)diff.length());
            point.set((Vector3dc)pointA).fma(along, (Vector3dc)normalizedDiff);
            double distance = point.distanceSquared(playerPosition.x, playerPosition.y, playerPosition.z);
            if (!(distance < minDistanceSquared)) continue;
            minPoint.set((Vector3dc)point);
            minNormal.set((Vector3dc)normalizedDiff);
            minDistanceSquared = distance;
        }
        return new ClosestQuery(minPoint, minNormal);
    }

    public static ClosestQuery getClosestPointOnStrand(ClientRopeStrand strand, Player player) {
        float chainYOffset = 0.5f * player.getScale();
        Vec3 playerPosition = player.position().add(0.0, player.getBoundingBox().getYsize() + (double)chainYOffset, 0.0);
        return ZiplineClientManager.getClosestPointOnStrand(strand, playerPosition);
    }

    public static boolean isRopeInteractable(ItemStack stack) {
        return AllTags.AllItemTags.CHAIN_RIDEABLE.matches(stack) || stack.is(SimTags.Items.DESTROYS_ROPE);
    }

    public static void embark(UUID rope) {
        Minecraft mc = Minecraft.getInstance();
        MutableComponent component = Component.translatable((String)"mount.onboard", (Object[])new Object[]{mc.options.keyShift.getTranslatedKeyMessage()});
        mc.gui.setOverlayMessage((Component)component, false);
        mc.getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.WOOL_HIT, (float)1.0f, (float)0.5f));
        ridingRope = rope;
        mc.player.getAbilities().flying = false;
        mc.player.stopFallFlying();
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new RopeRidingPacket(ridingRope, false)});
    }

    public static void disembark() {
        if (ridingRope != null) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new RopeRidingPacket(ridingRope, true)});
        }
        ridingRope = null;
        Minecraft.getInstance().getSoundManager().play((SoundInstance)SimpleSoundInstance.forUI((SoundEvent)SoundEvents.WOOL_HIT, (float)0.75f, (float)0.35f));
    }

    @Override
    public InteractCallback.Result onUse(int modifiers, int action, KeyMapping rightKey) {
        if (action == 0 || hoveringRope == null || ridingRope == hoveringRope) {
            return InteractCallback.Result.empty();
        }
        Minecraft mc = Minecraft.getInstance();
        ItemStack mainHandItem = mc.player.getMainHandItem();
        boolean isWrench = AllTags.AllItemTags.CHAIN_RIDEABLE.matches(mainHandItem);
        boolean isDestroyer = mainHandItem.is(SimTags.Items.DESTROYS_ROPE);
        if (isWrench && !mc.player.isShiftKeyDown()) {
            ClientLevelRopeManager ropeManager = ClientLevelRopeManager.getOrCreate(mc.player.level());
            ClientRopeStrand strand = ropeManager.getStrand(hoveringRope);
            if (strand == null) {
                return InteractCallback.Result.empty();
            }
            ClosestQuery query = ZiplineClientManager.getClosestPointOnStrand(strand, (Player)mc.player);
            if (!ZiplineClientManager.canStartRiding(query, (Player)mc.player, true)) {
                return InteractCallback.Result.empty();
            }
            ZiplineClientManager.embark(hoveringRope);
            mc.player.swing(InteractionHand.MAIN_HAND);
            return new InteractCallback.Result(true);
        }
        if (isDestroyer || isWrench && mc.player.isShiftKeyDown()) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new RopeBreakPacket(hoveringRope)});
            mc.player.swing(InteractionHand.MAIN_HAND);
            return new InteractCallback.Result(true);
        }
        return InteractCallback.Result.empty();
    }

    public record ClosestQuery(Vector3d position, Vector3d normal) {
    }
}
