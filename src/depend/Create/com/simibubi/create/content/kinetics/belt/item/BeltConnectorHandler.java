/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.DustParticleOptions
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.simibubi.create.content.kinetics.belt.item;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class BeltConnectorHandler {
    public static void tick() {
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        if (player == null || level == null) {
            return;
        }
        if (Minecraft.getInstance().screen != null) {
            return;
        }
        RandomSource random = level.random;
        for (InteractionHand hand : InteractionHand.values()) {
            BlockPos first;
            ItemStack heldItem = player.getItemInHand(hand);
            if (!AllItems.BELT_CONNECTOR.isIn(heldItem) || !heldItem.has(AllDataComponents.BELT_FIRST_SHAFT) || !level.getBlockState(first = (BlockPos)heldItem.get(AllDataComponents.BELT_FIRST_SHAFT)).hasProperty((Property)BlockStateProperties.AXIS)) continue;
            Direction.Axis axis = (Direction.Axis)level.getBlockState(first).getValue((Property)BlockStateProperties.AXIS);
            HitResult rayTrace = Minecraft.getInstance().hitResult;
            if (rayTrace == null || !(rayTrace instanceof BlockHitResult)) {
                if (random.nextInt(50) == 0) {
                    level.addParticle((ParticleOptions)new DustParticleOptions(new Vector3f(0.3f, 0.9f, 0.5f), 1.0f), (double)((float)first.getX() + 0.5f + BeltConnectorHandler.randomOffset(random, 0.25f)), (double)((float)first.getY() + 0.5f + BeltConnectorHandler.randomOffset(random, 0.25f)), (double)((float)first.getZ() + 0.5f + BeltConnectorHandler.randomOffset(random, 0.25f)), 0.0, 0.0, 0.0);
                }
                return;
            }
            BlockPos selected = ((BlockHitResult)rayTrace).getBlockPos();
            if (level.getBlockState(selected).canBeReplaced()) {
                return;
            }
            if (!ShaftBlock.isShaft(level.getBlockState(selected))) {
                selected = selected.relative(((BlockHitResult)rayTrace).getDirection());
            }
            if (!selected.closerThan((Vec3i)first, (double)((Integer)AllConfigs.server().kinetics.maxBeltLength.get()).intValue())) {
                return;
            }
            boolean canConnect = BeltConnectorItem.validateAxis((Level)level, selected) && BeltConnectorItem.canConnect((Level)level, first, selected);
            Vec3 start = Vec3.atLowerCornerOf((Vec3i)first);
            Vec3 end = Vec3.atLowerCornerOf((Vec3i)selected);
            Vec3 actualDiff = end.subtract(start);
            end = end.subtract(axis.choose(actualDiff.x, 0.0, 0.0), axis.choose(0.0, actualDiff.y, 0.0), axis.choose(0.0, 0.0, actualDiff.z));
            Vec3 diff = end.subtract(start);
            double x = Math.abs(diff.x);
            double y = Math.abs(diff.y);
            double z = Math.abs(diff.z);
            float length = (float)Math.max(x, Math.max(y, z));
            Vec3 step = diff.normalize();
            int sames = (x == y ? 1 : 0) + (y == z ? 1 : 0) + (z == x ? 1 : 0);
            if (sames == 0) {
                LinkedList<Vec3> validDiffs = new LinkedList<Vec3>();
                for (int i = -1; i <= 1; ++i) {
                    for (int j = -1; j <= 1; ++j) {
                        for (int k = -1; k <= 1; ++k) {
                            if (axis.choose(i, j, k) != 0 || axis == Direction.Axis.Y && i != 0 && k != 0 || i == 0 && j == 0 && k == 0) continue;
                            validDiffs.add(new Vec3((double)i, (double)j, (double)k));
                        }
                    }
                }
                int closestIndex = 0;
                float closest = Float.MAX_VALUE;
                for (Vec3 validDiff : validDiffs) {
                    double distanceTo = step.distanceTo(validDiff);
                    if (!(distanceTo < (double)closest)) continue;
                    closest = (float)distanceTo;
                    closestIndex = validDiffs.indexOf(validDiff);
                }
                step = (Vec3)validDiffs.get(closestIndex);
            }
            if (axis == Direction.Axis.Y && step.x != 0.0 && step.z != 0.0) {
                return;
            }
            step = new Vec3(Math.signum(step.x), Math.signum(step.y), Math.signum(step.z));
            for (float f = 0.0f; f < length; f += 0.0625f) {
                Vec3 position = start.add(step.scale((double)f));
                if (random.nextInt(10) != 0) continue;
                level.addParticle((ParticleOptions)new DustParticleOptions(new Vector3f(canConnect ? 0.3f : 0.9f, canConnect ? 0.9f : 0.3f, 0.5f), 1.0f), position.x + 0.5, position.y + 0.5, position.z + 0.5, 0.0, 0.0, 0.0);
            }
            return;
        }
    }

    private static float randomOffset(RandomSource random, float range) {
        return (random.nextFloat() - 0.5f) * 2.0f * range;
    }
}
