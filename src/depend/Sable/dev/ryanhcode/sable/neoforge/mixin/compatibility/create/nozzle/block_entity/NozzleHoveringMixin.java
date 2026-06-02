/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.fan.NozzleBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.nozzle.block_entity;

import com.simibubi.create.content.kinetics.fan.NozzleBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.api.physics.force.ForceGroup;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.nozzles.NozzleHoveringHelper;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NozzleBlockEntity.class})
public abstract class NozzleHoveringMixin
extends SmartBlockEntity
implements BlockEntitySubLevelActor {
    @Shadow
    private boolean pushing;
    @Shadow
    private float range;
    @Unique
    private List<Couple<Vec3>> sable$rayPoints = null;

    public NozzleHoveringMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method={"<init>"}, at={@At(value="TAIL")})
    public void sable$generateRays(BlockEntityType type, BlockPos pos, BlockState state, CallbackInfo ci) {
        this.sable$rayPoints = NozzleHoveringHelper.gatherRaycastPoints(state);
    }

    @Inject(method={"tick"}, at={@At(value="INVOKE", target="Lnet/minecraft/util/RandomSource;nextInt(I)I")})
    private void addPhysicsParticles(CallbackInfo ci) {
        ActiveSableCompanion helper = Sable.HELPER;
        if (helper.getContaining((BlockEntity)this) != null && this.pushing) {
            Vec3 blockCorner = Vec3.atLowerCornerOf((Vec3i)this.getBlockPos());
            Couple<Vec3> ray = this.sable$rayPoints.get(this.level.random.nextInt(this.sable$rayPoints.size()));
            Vec3 start = ((Vec3)ray.getFirst()).add(blockCorner);
            Vec3 end = ((Vec3)ray.getSecond()).add(blockCorner);
            ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.ANY, CollisionContext.empty());
            BlockHitResult clip = this.level.clip(context);
            NozzleHoveringHelper.spawnWindHitParticle(this.level, helper.getContaining((BlockEntity)this), clip, (Vector3dc)JOMLConversion.toJOML((Position)start), this.range / 40.0f);
        }
    }

    @Override
    public void sable$physicsTick(ServerSubLevel subLevel, RigidBodyHandle handle, double timeStep) {
        Vector3d force = NozzleHoveringHelper.gatherForceFromRays(subLevel, timeStep, this.getLevel(), this.getBlockPos(), (NozzleBlockEntity)this, this.sable$rayPoints);
        if (force != null) {
            QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup((ForceGroup)ForceGroups.PROPULSION.get());
            forceGroup.applyAndRecordPointForce((Vector3dc)JOMLConversion.toJOML((Position)Vec3.atCenterOf((Vec3i)this.getBlockPos())), (Vector3dc)force);
        }
    }
}
