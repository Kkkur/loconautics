/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.flywheel;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.flywheel.FlywheelBlockEntity;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelReactionWheel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={FlywheelBlockEntity.class})
public abstract class FlywheelBlockEntityMixin
extends KineticBlockEntity
implements BlockEntitySubLevelReactionWheel {
    @Unique
    float sable$smoothedSpeed = 0.0f;

    public FlywheelBlockEntityMixin(BlockEntityType<?> arg, BlockPos arg2, BlockState arg3) {
        super(arg, arg2, arg3);
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void sable$tick(CallbackInfo ci) {
        this.sable$smoothedSpeed += (this.speed - this.sable$smoothedSpeed) / 32.0f;
    }

    @Inject(method={"write"}, at={@At(value="TAIL")})
    public void sable$write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        compound.putFloat("SmoothedSpeed", this.sable$smoothedSpeed);
    }

    @Inject(method={"read"}, at={@At(value="TAIL")})
    public void sable$read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket, CallbackInfo ci) {
        this.sable$smoothedSpeed = compound.getFloat("SmoothedSpeed");
    }

    @Override
    public void sable$getAngularVelocity(Vector3d v) {
        Direction.Axis axis = ((IRotate)this.getBlockState().getBlock()).getRotationAxis(this.getBlockState());
        Direction dir = Direction.get((Direction.AxisDirection)Direction.AxisDirection.NEGATIVE, (Direction.Axis)axis);
        float angularSpeed = this.sable$smoothedSpeed * ((float)Math.PI * 2) / 60.0f;
        v.set((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ()).mul((double)angularSpeed);
    }
}
