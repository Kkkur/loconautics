/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.simulated_team.simulated.content.blocks.rope.rope_winch;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.config.server.blocks.SimBlockConfigs;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBehavior;
import dev.simulated_team.simulated.content.blocks.rope.RopeStrandHolderBlockEntity;
import dev.simulated_team.simulated.content.blocks.rope.strand.client.ClientRopeStrand;
import dev.simulated_team.simulated.content.blocks.rope.strand.server.ServerRopeStrand;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.service.SimConfigService;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class RopeWinchBlockEntity
extends KineticBlockEntity
implements RopeStrandHolderBlockEntity,
ThresholdSwitchObservable {
    private RopeStrandHolderBehavior ropeHolder;
    private int stretchTimer = 0;
    private boolean stretched;
    private boolean stretchedLastTick;
    protected LerpedFloat clientAngle = LerpedFloat.linear();

    public RopeWinchBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public RopeStrandHolderBehavior getRopeHolder() {
        return this.ropeHolder;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.ropeHolder = new RopeStrandHolderBehavior((SmartBlockEntity)this);
        behaviours.add(this.ropeHolder);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
    }

    public void tick() {
        boolean hasRope;
        super.tick();
        ServerRopeStrand strand = this.ropeHolder.getOwnedStrand();
        boolean bl = hasRope = strand != null;
        if (this.level.isClientSide) {
            this.invalidateRenderBoundingBox();
            this.clientAngle.setValue((double)(this.clientAngle.getValue() + this.getMovementSpeed()));
        }
        if (!this.level.isClientSide && hasRope && this.ropeHolder.ownsRope()) {
            this.updateRopeStrandExtension(strand);
        }
        if (this.stretchTimer > 0) {
            --this.stretchTimer;
        }
    }

    private void updateRopeStrandExtension(ServerRopeStrand strand) {
        SimBlockConfigs config = SimConfigService.INSTANCE.server().blocks;
        float movementSpeed = this.getMovementSpeed();
        double desiredExtension = strand.getExtension() + (double)(strand.getPoints().size() - 2) * 1.0;
        double currentExtension = strand.getCurrentExtension();
        boolean bl = this.stretched = currentExtension > desiredExtension * (1.0 + (Double)config.maxRopeStretchAllowed.get() / 100.0);
        if (this.stretched) {
            if (!this.stretchedLastTick) {
                this.effects.triggerOverStressedEffect();
            }
            if (this.stretchTimer == 0) {
                this.stretchTimer = this.level.random.nextIntBetweenInclusive(100, 300);
                this.level.playSound(null, this.getBlockPos(), SimSoundEvents.ROPE_WINCH_STRETCH.event(), SoundSource.BLOCKS, 0.1f, 0.8f + this.level.random.nextFloat() * 0.2f);
            }
            movementSpeed = Math.max(0.0f, movementSpeed);
        }
        this.stretchedLastTick = this.stretched;
        if (currentExtension > (Double)config.maxRopeRange.get()) {
            movementSpeed = Math.min(0.0f, movementSpeed);
        }
        double extension = strand.getExtension();
        extension += (double)movementSpeed;
        int minPointCount = 2;
        if (extension < 1.0 && strand.getPoints().size() == 2) {
            extension = 1.0;
        } else {
            while (extension < 0.0) {
                strand.removeFirstPoint();
                if (!((extension += 1.0) < 1.0) || strand.getPoints().size() != 2) continue;
                extension = 1.0;
                break;
            }
            while (extension > 1.0) {
                Vector3d point = JOMLConversion.toJOML((Position)Sable.HELPER.projectOutOfSubLevel(this.level, this.ropeHolder.getAttachmentPoint()));
                strand.addPoint((Vector3dc)point);
                extension -= 1.0;
            }
            if (extension < 1.0 && strand.getPoints().size() <= 2) {
                extension = 1.0;
            }
        }
        strand.updateFirstSegmentExtension(extension);
    }

    public AABB getRenderBoundingBox() {
        ClientRopeStrand rope = this.ropeHolder.getClientStrand();
        if (rope != null && this.ropeHolder.ownsRope()) {
            AABB bounds = rope.getBounds();
            if (bounds == null) {
                return super.getRenderBoundingBox();
            }
            return bounds.inflate(3.0);
        }
        return super.getRenderBoundingBox();
    }

    public float getMovementSpeed() {
        return Mth.clamp((float)RopeWinchBlockEntity.convertToLinear((float)this.getSpeed()), (float)-0.49f, (float)0.49f);
    }

    @Override
    public RopeStrandHolderBehavior getBehavior() {
        return this.ropeHolder;
    }

    @Override
    public Vec3 getAttachmentPoint(BlockPos pos, BlockState state) {
        return pos.getCenter();
    }

    public int getMaxValue() {
        return (int)SimConfigService.INSTANCE.server().blocks.maxRopeRange.getF();
    }

    public int getMinValue() {
        return 0;
    }

    public int getCurrentValue() {
        ServerRopeStrand strand = this.ropeHolder.getOwnedStrand();
        if (strand != null) {
            return (int)strand.getCurrentExtension();
        }
        return 0;
    }

    public MutableComponent format(int value) {
        return SimLang.translate("gui.threshold_switch.rope_winch_length", value).component();
    }
}
