/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller
 *  dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.small;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.eriksonn.aeronautics.content.blocks.propeller.behaviour.PropellerActorBehaviour;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.BasePropellerBlock;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3dc;

public abstract class BasePropellerBlockEntity
extends KineticBlockEntity
implements BlockEntitySubLevelPropellerActor,
BlockEntityPropeller {
    private final Quaternionf rot = new Quaternionf();
    public float rotationSpeed = 0.0f;
    public PropellerActorBehaviour prop;
    private float previousAngle;
    private float angle;

    public BasePropellerBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.prop = this.createBehavior();
        behaviours.add(this.prop);
    }

    public PropellerActorBehaviour createBehavior() {
        PropellerActorBehaviour prop = new PropellerActorBehaviour((SmartBlockEntity)this, this);
        prop.setThrustDirection((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atLowerCornerOf((Vec3i)this.getBlockDirection().getNormal())));
        prop.setParticleAmountUpdater(() -> 0.12 * (double)Math.abs(this.rotationSpeed));
        prop.setParticleCountProperties(5, 2.0);
        prop.addSimpleLayer(this.getOffset(), this.getRadius());
        prop.setParticlePositionUpdater((v, random) -> {
            PropellerActorBehaviour.PropellerLayer layer = prop.getLayers().get(random.nextInt(prop.getLayers().size()));
            double R = Math.sqrt(Mth.lerp((double)random.nextFloat(), (double)layer.innerRadiusSquared(), (double)layer.outerRadiusSquared()));
            double angle = Math.PI * 2 * (double)random.nextFloat();
            v.set(Math.cos(angle) * R, layer.offset(), Math.sin(angle) * R);
            this.rot.transform(v);
        });
        return prop;
    }

    public BlockEntityPropeller getPropeller() {
        return this;
    }

    public abstract double getConfigThrust();

    public abstract double getConfigAirflow();

    public abstract float getRadius();

    public float getOffset() {
        return 0.0f;
    }

    public void tick() {
        this.updateRotationSpeed();
        this.setPreviousAngle(this.getAngle());
        this.setAngle(this.getAngle() + this.rotationSpeed);
        this.rot.set((Quaternionfc)this.getBlockDirection().getRotation());
        super.tick();
        if (this.isActive() && !this.isVirtual()) {
            this.onActiveTick();
        }
    }

    public void onActiveTick() {
        this.prop.pushEntities();
        this.prop.spawnParticles();
    }

    protected float getDirectionIndependentSpeed() {
        return (float)this.getBlockDirection().getAxisDirection().getStep() * this.rotationSpeed * 3.3333333f * (float)((Boolean)this.getBlockState().getValue((Property)BasePropellerBlock.REVERSED) != false ? -1 : 1);
    }

    private void updateRotationSpeed() {
        float nextSpeed = BasePropellerBlockEntity.convertToAngular((float)this.getSpeed());
        if (this.getSpeed() == 0.0f) {
            nextSpeed = 0.0f;
        }
        float lerpAmount = 0.15f;
        this.rotationSpeed = Mth.lerp((float)0.15f, (float)this.rotationSpeed, (float)nextSpeed);
    }

    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if (Math.abs(this.getSpeed()) > 0.0f) {
            AeroAdvancements.FOR_EVERY_ACTION.awardToNearby(this.getBlockPos(), this.getLevel());
        }
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("RotationSpeed", this.rotationSpeed);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.rotationSpeed = compound.getFloat("RotationSpeed");
    }

    public float getPreviousAngle() {
        return this.previousAngle;
    }

    public void setPreviousAngle(float previousAngle) {
        this.previousAngle = previousAngle;
    }

    public float getAngle() {
        return this.angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Direction getBlockDirection() {
        return (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
    }

    public double getAirflow() {
        return this.getConfigAirflow() * (double)this.getDirectionIndependentSpeed();
    }

    public double getThrust() {
        return this.getConfigThrust() * (double)this.getDirectionIndependentSpeed();
    }

    public boolean isActive() {
        return Math.abs(this.rotationSpeed) > 0.01f;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            return false;
        }
        return this.prop.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }
}
