/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.network.VeilPacketManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.network.packets.SteeringWheelPacket;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class SteeringWheelHandler
extends BlockHoldInteraction {
    private static SteeringWheelBlockEntity blockEntity = null;
    private static boolean updated = false;
    private static float rawAngle = 0.0f;
    private static float effectiveAngle = 0.0f;
    private static boolean wasShiftKeyDown = false;
    private static int angleSgn = 1;

    @Override
    public void startHold(Level level, Player player, BlockPos blockPos) {
        super.startHold(level, player, blockPos);
        blockEntity = (SteeringWheelBlockEntity)((Object)level.getBlockEntity(blockPos, (BlockEntityType)SimBlockEntityTypes.STEERING_WHEEL.get()).orElseThrow());
        rawAngle = blockEntity.getInteractionAngle(Minecraft.getInstance().getTimer().getGameTimeDeltaTicks());
        angleSgn = (int)blockEntity.directionConvert(1.0f);
        updated = true;
        this.setTargetAngle(rawAngle);
    }

    @Override
    public void stop() {
        if (blockEntity != null && !blockEntity.isRemoved()) {
            SteeringWheelHandler.blockEntity.held = false;
            blockEntity = null;
        }
        VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new SteeringWheelPacket(true, effectiveAngle, this.getInteractionPos())});
        super.stop();
    }

    @Override
    public boolean activeOnMouseMove(double yaw, double pitch) {
        if (yaw != 0.0) {
            float oldAngle = rawAngle;
            rawAngle += (float)(yaw / 10.0 * (double)angleSgn);
            updated |= oldAngle != (rawAngle = Mth.clamp((float)rawAngle, (float)(-SteeringWheelHandler.blockEntity.angleInput.getValue()), (float)SteeringWheelHandler.blockEntity.angleInput.getValue()));
        }
        return true;
    }

    @Override
    public boolean activeTick(Level level, LocalPlayer player) {
        effectiveAngle = rawAngle;
        if (HoldInteractionManager.unblockedShift()) {
            effectiveAngle = Mth.clamp((int)(Math.round(effectiveAngle / 45.0f) * 45), (int)(-SteeringWheelHandler.blockEntity.angleInput.getValue()), (int)SteeringWheelHandler.blockEntity.angleInput.getValue());
            if (!wasShiftKeyDown) {
                updated = true;
            }
            wasShiftKeyDown = true;
        } else {
            if (wasShiftKeyDown) {
                updated = true;
            }
            wasShiftKeyDown = false;
        }
        this.setTargetAngle(effectiveAngle);
        return !BlockHoldInteraction.inInteractionRange((Player)player, (Position)this.getInteractionPos().getCenter());
    }

    @Override
    public boolean isBlockActive(BlockPos pos) {
        return super.isBlockActive(pos) && !Float.isNaN(rawAngle);
    }

    public void setTargetAngle(float targetAngle) {
        if (updated) {
            VeilPacketManager.server().sendPacket(new CustomPacketPayload[]{new SteeringWheelPacket(false, targetAngle, this.getInteractionPos())});
            updated = false;
            SteeringWheelHandler.blockEntity.targetAngleToUpdate = targetAngle;
            SteeringWheelHandler.blockEntity.held = !Float.isNaN(targetAngle);
        }
    }

    @Override
    public int getCrouchBlockingTicks() {
        return 6;
    }
}
