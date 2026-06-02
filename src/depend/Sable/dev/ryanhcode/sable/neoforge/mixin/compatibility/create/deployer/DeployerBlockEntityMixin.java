/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  javax.annotation.Nullable
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyArg
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.deployer;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import javax.annotation.Nullable;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value={DeployerBlockEntity.class})
public abstract class DeployerBlockEntityMixin
extends SmartBlockEntity {
    public DeployerBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Redirect(method={"start"}, at=@At(value="INVOKE", target="Ljava/lang/Math;min(DD)D"))
    private double sable$deployerMin(double a, double b, @Local(ordinal=1) Vec3 rayOrigin, @Local(ordinal=0) BlockHitResult result) {
        return Math.min(Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(this.level, (Position)result.getLocation(), (Position)rayOrigin)), b);
    }

    @ModifyArg(method={"activate"}, at=@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/deployer/DeployerHandler;activate(Lcom/simibubi/create/content/kinetics/deployer/DeployerFakePlayer;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/Vec3;Lcom/simibubi/create/content/kinetics/deployer/DeployerBlockEntity$Mode;)V"), index=2, remap=false)
    private BlockPos sable$checkPositions(BlockPos pos) {
        Vec3 centerPos = Vec3.atCenterOf((Vec3i)pos);
        ActiveSableCompanion helper = Sable.HELPER;
        BlockPos gatheredPos = helper.runIncludingSubLevels(this.getLevel(), centerPos, true, helper.getContaining((BlockEntity)this), this::sable$getState);
        if (gatheredPos != null) {
            return gatheredPos;
        }
        return pos;
    }

    @Unique
    @Nullable
    private BlockPos sable$getState(SubLevel subLevel, BlockPos pos) {
        Level level = this.getLevel();
        assert (level != null);
        BlockState state = level.getBlockState(pos);
        if (!state.isAir()) {
            return pos;
        }
        for (Direction direction : Iterate.directions) {
            if (level.getBlockState(pos.relative(direction)).isAir()) continue;
            return pos;
        }
        return null;
    }
}
