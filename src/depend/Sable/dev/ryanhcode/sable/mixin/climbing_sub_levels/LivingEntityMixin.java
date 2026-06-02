/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Share
 *  com.llamalad7.mixinextras.sugar.ref.LocalRef
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3dc
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  org.joml.Vector3d
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.mixin.climbing_sub_levels;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3dc;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.mixinterface.entity.entity_sublevel_collision.EntityMovementExtension;
import dev.ryanhcode.sable.platform.SablePlatform;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
extends Entity {
    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method={"onClimbable"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;blockPosition()Lnet/minecraft/core/BlockPos;"))
    private BlockPos sable$redirectPos(LivingEntity instance, @Share(value="subLevelBlockState") LocalRef<BlockState> subLevelBlockState) {
        Level level = this.level();
        LivingEntity self = (LivingEntity)this;
        BlockPos defaultPos = ((EntityMovementExtension)((Object)this)).sable$getInBlockStatePos();
        BlockState defaultState = this.getInBlockState();
        if (defaultState.is(BlockTags.CLIMBABLE) && SablePlatform.INSTANCE.isBlockstateLadder(defaultState, level, defaultPos, self)) {
            return defaultPos;
        }
        Vector3d position = new Vector3d();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (SubLevel subLevel : Sable.HELPER.getAllIntersecting(level, (BoundingBox3dc)new BoundingBox3d(this.getBoundingBox()))) {
            subLevel.logicalPose().transformPositionInverse(JOMLConversion.toJOML((Position)this.position(), (Vector3d)position));
            pos.set(position.x, position.y, position.z);
            BlockState state = level.getBlockState((BlockPos)pos);
            if (!state.is(BlockTags.CLIMBABLE) || !SablePlatform.INSTANCE.isBlockstateLadder(state, level, (BlockPos)pos, self)) continue;
            subLevelBlockState.set((Object)state);
            return pos.immutable();
        }
        return defaultPos;
    }

    @WrapOperation(method={"onClimbable"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;getInBlockState()Lnet/minecraft/world/level/block/state/BlockState;")})
    private BlockState getInBlockState(LivingEntity instance, Operation<BlockState> original, @Share(value="subLevelBlockState") LocalRef<BlockState> subLevelBlockState) {
        BlockState state = (BlockState)subLevelBlockState.get();
        return state != null ? state : (BlockState)original.call(new Object[]{instance});
    }
}
