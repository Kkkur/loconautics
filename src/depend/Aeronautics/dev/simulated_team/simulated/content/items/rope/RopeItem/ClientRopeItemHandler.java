/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.Sable
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.items.rope.RopeItem;

import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.config.server.blocks.SimBlockConfigs;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.rope_winch.RopeWinchBlockEntity;
import dev.simulated_team.simulated.content.items.rope.RopeItem.RopeItem;
import dev.simulated_team.simulated.index.SimDataComponents;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.service.SimConfigService;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ClientRopeItemHandler {
    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return;
        }
        if (Minecraft.getInstance().screen != null) {
            return;
        }
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (!SimItems.ROPE_COUPLING.isIn(heldItem) || !heldItem.has(SimDataComponents.ROPE_FIRST_CONNECTION)) continue;
            BlockPos firstBlock = (BlockPos)heldItem.get(SimDataComponents.ROPE_FIRST_CONNECTION);
            HitResult rayTrace = Minecraft.getInstance().hitResult;
            if (rayTrace instanceof BlockHitResult) {
                Vec3 target;
                BlockHitResult hitResult = (BlockHitResult)rayTrace;
                BlockPos hitBlock = hitResult.getBlockPos();
                Vec3 firstPoint = firstBlock.getCenter();
                SimBlockConfigs blockConfig = SimConfigService.INSTANCE.server().blocks;
                double maxRopeRange = (Double)blockConfig.maxRopeRange.get();
                boolean inRange = Sable.HELPER.distanceSquaredWithSubLevels((Level)level, (Position)firstPoint, (Position)hitResult.getLocation()) < maxRopeRange * maxRopeRange;
                boolean valid = RopeItem.isValidRopeAttachment((Level)level, hitBlock) && !hitBlock.equals((Object)firstBlock) && inRange;
                RopeStrandHolderBehavior holderA = RopeItem.getRopeHolder((Level)level, hitBlock);
                RopeStrandHolderBehavior holderB = RopeItem.getRopeHolder((Level)level, firstBlock);
                if (valid && holderA != null && holderA.blockEntity instanceof RopeWinchBlockEntity && holderB != null && holderB.blockEntity instanceof RopeWinchBlockEntity) {
                    valid = false;
                }
                Vec3 vec3 = target = valid ? hitBlock.getCenter() : hitResult.getLocation();
                Color color = valid ? new Color(SimColors.SUCCESS_LIME) : new Color(inRange ? SimColors.PERCHANCE_ORANGE : SimColors.NUH_UH_RED);
                Outliner.getInstance().chaseAABB((Object)"FirstRopeAttachmentPoint", new AABB(firstPoint, firstPoint)).colored(color).lineWidth(0.33333334f).disableLineNormals();
                Vec3 globalFirstPoint = Sable.HELPER.projectOutOfSubLevel((Level)level, firstPoint);
                Vec3 globalTarget = Sable.HELPER.projectOutOfSubLevel((Level)level, target);
                if (valid) {
                    Outliner.getInstance().chaseAABB((Object)"SecondRopeAttachmentPoint", new AABB(target, target)).colored(color).lineWidth(0.33333334f).disableLineNormals();
                    double points = Math.floor(globalFirstPoint.distanceTo(globalTarget));
                    Vec3 backwardsDiff = globalFirstPoint.subtract(globalTarget).normalize();
                    int i = 0;
                    while ((double)i < points) {
                        Vec3 point = globalTarget.add(backwardsDiff.scale((double)i));
                        Outliner.getInstance().chaseAABB((Object)("RopePoint" + i), new AABB(point, point)).colored(color).lineWidth(0.125f).disableLineNormals();
                        ++i;
                    }
                } else if (!inRange) {
                    globalTarget = globalTarget.subtract(globalFirstPoint).normalize().scale(maxRopeRange - 0.5).add(globalFirstPoint);
                    Outliner.getInstance().chaseAABB((Object)"SecondRopeAttachmentPoint", new AABB(globalTarget, globalTarget)).colored(color).lineWidth(0.33333334f).disableLineNormals();
                }
                DustParticleOptions data = new DustParticleOptions(color.asVectorF(), 1.0f);
                double totalFlyingTicks = 10.0;
                int segments = 4;
                for (int i = 0; i < 4; ++i) {
                    Vec3 vec = globalFirstPoint.lerp(globalTarget, (double)level.random.nextFloat());
                    level.addParticle((ParticleOptions)data, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
                }
            }
            return;
        }
    }
}
