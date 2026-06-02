/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  dev.ryanhcode.sable.companion.math.Pose3d
 *  foundry.veil.api.network.handler.PacketContext
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.FluidTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.sable.network.packets.tcp;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.SableConfig;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.companion.math.Pose3d;
import dev.ryanhcode.sable.index.SableAttributes;
import dev.ryanhcode.sable.network.tcp.SableTCPPacket;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import dev.ryanhcode.sable.util.SableBufferUtils;
import foundry.veil.api.network.handler.PacketContext;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record ServerboundPunchSubLevelPacket(BlockPos punchedBlock, Vector3dc localPosition, Vector3dc direction) implements SableTCPPacket
{
    public static final CustomPacketPayload.Type<ServerboundPunchSubLevelPacket> TYPE = new CustomPacketPayload.Type(Sable.sablePath("punch_sub_level"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundPunchSubLevelPacket> CODEC = StreamCodec.of((buf, value) -> value.write((FriendlyByteBuf)buf), ServerboundPunchSubLevelPacket::read);

    private static ServerboundPunchSubLevelPacket read(FriendlyByteBuf buf) {
        return new ServerboundPunchSubLevelPacket(buf.readBlockPos(), (Vector3dc)SableBufferUtils.read((ByteBuf)buf, new Vector3d()), (Vector3dc)SableBufferUtils.read((ByteBuf)buf, new Vector3d()));
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.punchedBlock);
        SableBufferUtils.write((ByteBuf)buf, this.localPosition);
        SableBufferUtils.write((ByteBuf)buf, this.direction);
    }

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handle(PacketContext context) {
        BlockState blockState;
        ServerLevel level = (ServerLevel)context.level();
        ServerSubLevelContainer container = SubLevelContainer.getContainer(level);
        if (container == null) {
            Sable.LOGGER.error("Received a sub-level punch packet for a level without a sub-level container");
            return;
        }
        Player player = context.player();
        if (!(player.onGround() || player.isInWater() || player.getAbilities().flying || player.onClimbable())) {
            return;
        }
        ServerSubLevel standingSubLevel = (ServerSubLevel)Sable.HELPER.getTrackingSubLevel((Entity)player);
        SubLevelPhysicsSystem physicsSystem = container.physicsSystem();
        SubLevel targetSubLevel = Sable.HELPER.getContaining((Level)level, (Vec3i)this.punchedBlock);
        if (standingSubLevel == targetSubLevel) {
            return;
        }
        Vector3d localHitPosition = new Vector3d(this.localPosition);
        Vector3d globalDirection = new Vector3d(this.direction).normalize();
        if (targetSubLevel != null) {
            localHitPosition.add((Vector3dc)targetSubLevel.logicalPose().position());
            targetSubLevel.logicalPose().transformPositionInverse(localHitPosition);
        }
        if (standingSubLevel != null) {
            standingSubLevel.logicalPose().transformNormal(globalDirection);
        }
        double attributeStrength = Objects.requireNonNull(player.getAttribute(SableAttributes.PUNCH_STRENGTH)).getValue();
        int customCooldown = SableAttributes.getPushCooldownTicks((LivingEntity)player);
        if (!physicsSystem.tryPunch(player.getGameProfile().getId(), customCooldown)) {
            return;
        }
        player.getCooldowns().addCooldown(player.getMainHandItem().getItem(), customCooldown);
        double downwardStrengthMultiplier = SableConfig.SUB_LEVEL_PUNCH_DOWNWARD_STRENGTH_MULTIPLIER.getAsDouble();
        if (globalDirection.y < 0.0) {
            globalDirection.mul(1.0, downwardStrengthMultiplier, 1.0);
        }
        if (!(targetSubLevel instanceof ServerSubLevel)) {
            if (standingSubLevel != null) {
                Pose3d pose = standingSubLevel.logicalPose();
                Vector3d localPosition = pose.transformPositionInverse(JOMLConversion.toJOML((Position)player.position()));
                Vector3d localDirection = pose.transformNormalInverse(globalDirection);
                localDirection.negate();
                double strengthScalar = ServerboundPunchSubLevelPacket.computeStrengthScalar(standingSubLevel, (Vector3dc)localPosition, (Vector3dc)localDirection);
                physicsSystem.getPipeline().applyImpulse(standingSubLevel, (Vector3dc)localPosition, (Vector3dc)localDirection.mul(attributeStrength * strengthScalar, new Vector3d()));
            }
        } else {
            double strengthScalar;
            ServerSubLevel punchedSubLevel = (ServerSubLevel)targetSubLevel;
            Vector3d localHitDirection = punchedSubLevel.logicalPose().transformNormalInverse((Vector3dc)globalDirection, new Vector3d());
            if (standingSubLevel == null) {
                strengthScalar = ServerboundPunchSubLevelPacket.computeStrengthScalar(punchedSubLevel, (Vector3dc)localHitPosition, (Vector3dc)localHitDirection);
            } else {
                Vector3d localPosition = standingSubLevel.logicalPose().transformPositionInverse(JOMLConversion.toJOML((Position)player.position()));
                Vector3d localDirection = standingSubLevel.logicalPose().transformNormalInverse(new Vector3d((Vector3dc)globalDirection));
                double standingStrength = ServerboundPunchSubLevelPacket.computeStrengthScalar(standingSubLevel, (Vector3dc)localPosition, (Vector3dc)localDirection);
                double punchedSubLevelScale = ServerboundPunchSubLevelPacket.computeStrengthScalar(punchedSubLevel, (Vector3dc)localHitPosition, (Vector3dc)localHitDirection);
                strengthScalar = Math.min(punchedSubLevelScale, standingStrength);
                localDirection.negate();
                physicsSystem.getPipeline().applyImpulse(standingSubLevel, (Vector3dc)localPosition, (Vector3dc)localDirection.mul(attributeStrength * strengthScalar));
            }
            physicsSystem.getPipeline().applyImpulse(punchedSubLevel, (Vector3dc)localHitPosition, (Vector3dc)localHitDirection.mul(attributeStrength * strengthScalar));
        }
        if ((blockState = level.getBlockState(this.punchedBlock)).getFluidState().isEmpty()) {
            Vector3d particlePos = new Vector3d((Vector3dc)localHitPosition).fma(-0.1, (Vector3dc)globalDirection);
            level.sendParticles((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockState), particlePos.x(), particlePos.y(), particlePos.z(), (int)(Math.random() * 3.0), 0.0, 0.0, 0.0, 0.0);
        } else {
            this.sendFluidParticles(level, blockState, (Vector3dc)globalDirection);
        }
    }

    private void sendFluidParticles(ServerLevel level, BlockState blockState, Vector3dc transformedDirection) {
        if (blockState.getFluidState().is(FluidTags.WATER)) {
            Vector3d particlePos = new Vector3d(this.localPosition).fma(0.1, transformedDirection);
            level.sendParticles((ParticleOptions)ParticleTypes.SPLASH, particlePos.x(), particlePos.y(), particlePos.z(), 10, 0.2, 0.2, 0.2, 0.0);
            particlePos.fma(0.2, transformedDirection);
            level.sendParticles((ParticleOptions)ParticleTypes.BUBBLE, particlePos.x(), particlePos.y(), particlePos.z(), 5, 0.2, 0.1, 0.2, 0.0);
            level.playSound(null, particlePos.x(), particlePos.y(), particlePos.z(), SoundEvents.PLAYER_SWIM, SoundSource.BLOCKS, 0.2f, 1.0f);
        } else {
            Vector3d particlePos = new Vector3d(this.localPosition).fma(0.1, transformedDirection);
            level.sendParticles((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockState), particlePos.x(), particlePos.y(), particlePos.z(), (int)(Math.random() * 3.0), 0.2, 0.2, 0.2, 0.0);
        }
    }

    public static double punchCurve(double x) {
        double S = 2.0;
        double E = 0.5;
        double k = 0.8;
        double p = 1.75;
        double u = x - 1.0;
        double g = 0.8;
        if (x < 1.0) {
            return ((0.5499999999999998 * u + 0.8 - 1.0) * u + 1.0) * x;
        }
        double inverseE = -2.0;
        return 2.0 * (Math.pow(u + Math.pow(0.8, -2.0), 0.5) - Math.pow(0.8, -1.0)) + 1.0;
    }

    private static double computeStrengthScalar(ServerSubLevel standingSubLevel, Vector3dc localPosition, Vector3dc localDirection) {
        MassData massTracker = standingSubLevel.getMassTracker();
        double generalizedInverseMass = massTracker.getInverseNormalMass(localPosition, localDirection);
        double mass = 1.0 / generalizedInverseMass;
        double strengthMultiplier = SableConfig.SUB_LEVEL_PUNCH_STRENGTH_MULTIPLIER.getAsDouble();
        return ServerboundPunchSubLevelPacket.punchCurve(mass) * strengthMultiplier;
    }
}
