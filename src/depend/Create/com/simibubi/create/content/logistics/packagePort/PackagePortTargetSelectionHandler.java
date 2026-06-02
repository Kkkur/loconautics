/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.logistics.packagePort;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.logistics.packagePort.PackagePortBlockEntity;
import com.simibubi.create.content.logistics.packagePort.PackagePortPlacementPacket;
import com.simibubi.create.content.logistics.packagePort.PackagePortTarget;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;

public class PackagePortTargetSelectionHandler {
    public static PackagePortTarget activePackageTarget;
    public static Vec3 exactPositionOfTarget;
    public static boolean isPostbox;

    public static void flushSettings(BlockPos pos) {
        if (activePackageTarget == null) {
            CreateLang.translate("gui.package_port.not_targeting_anything", new Object[0]).sendStatus((Player)Minecraft.getInstance().player);
            return;
        }
        if (PackagePortTargetSelectionHandler.validateDiff(exactPositionOfTarget, pos) == null) {
            PackagePortTargetSelectionHandler.activePackageTarget.relativePos = PackagePortTargetSelectionHandler.activePackageTarget.relativePos.subtract((Vec3i)pos);
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new PackagePortPlacementPacket(activePackageTarget, pos));
        }
        activePackageTarget = null;
        isPostbox = false;
    }

    public static boolean onUse() {
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;
        ItemStack mainHandItem = mc.player.getMainHandItem();
        if (hitResult == null || hitResult.getType() == HitResult.Type.MISS) {
            return false;
        }
        if (!(hitResult instanceof BlockHitResult)) {
            return false;
        }
        BlockHitResult bhr = (BlockHitResult)hitResult;
        BlockPos pos = bhr.getBlockPos();
        BlockEntity blockEntity = mc.level.getBlockEntity(pos);
        if (!(blockEntity instanceof StationBlockEntity)) {
            return false;
        }
        StationBlockEntity sbe = (StationBlockEntity)blockEntity;
        if (sbe.edgePoint == null) {
            return false;
        }
        if (!AllTags.AllItemTags.POSTBOXES.matches(mainHandItem)) {
            return false;
        }
        exactPositionOfTarget = Vec3.atCenterOf((Vec3i)pos);
        activePackageTarget = new PackagePortTarget.TrainStationFrogportTarget(pos);
        isPostbox = true;
        return true;
    }

    public static void tick() {
        String validateDiff;
        HitResult objectMouseOver;
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        boolean isPostbox = AllTags.AllItemTags.POSTBOXES.matches(player.getMainHandItem());
        boolean isWrench = player.getMainHandItem().is(Tags.Items.TOOLS_WRENCH);
        if (!isWrench) {
            if (activePackageTarget == null) {
                return;
            }
            if (!AllBlocks.PACKAGE_FROGPORT.isIn(player.getMainHandItem()) && !isPostbox) {
                return;
            }
        }
        if (!((objectMouseOver = mc.hitResult) instanceof BlockHitResult)) {
            return;
        }
        BlockHitResult blockRayTraceResult = (BlockHitResult)objectMouseOver;
        if (isWrench) {
            if (blockRayTraceResult.getType() == HitResult.Type.MISS) {
                return;
            }
            BlockPos pos = blockRayTraceResult.getBlockPos();
            BlockEntity blockEntity = mc.level.getBlockEntity(pos);
            if (!(blockEntity instanceof PackagePortBlockEntity)) {
                return;
            }
            PackagePortBlockEntity ppbe = (PackagePortBlockEntity)blockEntity;
            if (ppbe.target == null) {
                return;
            }
            Vec3 source = Vec3.atBottomCenterOf((Vec3i)pos);
            Vec3 target = ppbe.target.getExactTargetLocation(ppbe, (LevelAccessor)mc.level, pos);
            if (target == Vec3.ZERO) {
                return;
            }
            Color color = new Color(10411635);
            PackagePortTargetSelectionHandler.animateConnection(mc, source, target, color);
            Outliner.getInstance().chaseAABB((Object)"ChainPointSelected", new AABB(target, target)).colored(color).lineWidth(0.2f).disableLineNormals();
            return;
        }
        Vec3 target = exactPositionOfTarget;
        if (blockRayTraceResult.getType() == HitResult.Type.MISS) {
            Outliner.getInstance().chaseAABB((Object)"ChainPointSelected", new AABB(target, target)).colored(10411635).lineWidth(0.2f).disableLineNormals();
            return;
        }
        BlockPos pos = blockRayTraceResult.getBlockPos();
        if (!mc.level.getBlockState(pos).canBeReplaced()) {
            pos = pos.relative(blockRayTraceResult.getDirection());
        }
        boolean valid = (validateDiff = PackagePortTargetSelectionHandler.validateDiff(target, pos)) == null;
        Color color = new Color(valid ? 10411635 : 0xFF7171);
        Vec3 source = Vec3.atBottomCenterOf((Vec3i)pos);
        CreateLang.translate(validateDiff != null ? validateDiff : "package_port.valid", new Object[0]).color(color.getRGB()).sendStatus((Player)player);
        Outliner.getInstance().chaseAABB((Object)"ChainPointSelected", new AABB(target, target)).colored(color).lineWidth(0.2f).disableLineNormals();
        if (!mc.level.getBlockState(pos).canBeReplaced()) {
            return;
        }
        Outliner.getInstance().chaseAABB((Object)"TargetedFrogPos", new AABB(pos).contract(0.0, 1.0, 0.0).deflate(0.125, 0.0, 0.125)).colored(color).lineWidth(0.0625f).disableLineNormals();
        PackagePortTargetSelectionHandler.animateConnection(mc, source, target, color);
    }

    public static void animateConnection(Minecraft mc, Vec3 source, Vec3 target, Color color) {
        DustParticleOptions data = new DustParticleOptions(color.asVectorF(), 1.0f);
        ClientLevel world = mc.level;
        double totalFlyingTicks = 10.0;
        int segments = (int)totalFlyingTicks / 3 + 1;
        double tickOffset = totalFlyingTicks / (double)segments;
        for (int i = 0; i < segments; ++i) {
            double ticks = (double)(AnimationTickHolder.getRenderTime() / 3.0f) % tickOffset + (double)i * tickOffset;
            Vec3 vec = source.lerp(target, ticks / totalFlyingTicks);
            world.addParticle((ParticleOptions)data, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
        }
    }

    public static String validateDiff(Vec3 target, BlockPos placedPos) {
        Vec3 source = Vec3.atBottomCenterOf((Vec3i)placedPos);
        Vec3 diff = target.subtract(source);
        if (diff.y < 0.0 && !isPostbox) {
            return "package_port.cannot_reach_down";
        }
        if (diff.length() > (double)((Integer)AllConfigs.server().logistics.packagePortRange.get()).intValue()) {
            return "package_port.too_far";
        }
        return null;
    }
}
