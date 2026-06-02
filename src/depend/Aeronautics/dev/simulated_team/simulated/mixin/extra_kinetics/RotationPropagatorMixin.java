/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.ModifyReceiver
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.RotationPropagator
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.Redirect
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.extra_kinetics;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={RotationPropagator.class})
public abstract class RotationPropagatorMixin {
    @Redirect(method={"handleRemoved", "propagateMissingSource", "findConnectedNeighbour"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/level/Level;getBlockEntity(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/entity/BlockEntity;"))
    private static BlockEntity simulated$accountForExtraKinetics(Level level, BlockPos pos) {
        return RotationPropagatorMixin.simulated$getBlockEntityAccountingExtraKinetics(level, pos);
    }

    @WrapOperation(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=0), @At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=3)})
    private static boolean testSmallCogFrom1(BlockState state, Operation<Boolean> original, @Local(argsOnly=true, ordinal=0) KineticBlockEntity fromBE) {
        return RotationPropagatorMixin.simulated$checkCogStateSmall((Boolean)original.call(new Object[]{state}), fromBE);
    }

    @WrapOperation(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=1), @At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isSmallCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=2)})
    private static boolean testSmallCogTo(BlockState state, Operation<Boolean> original, @Local(argsOnly=true, ordinal=1) KineticBlockEntity toBE) {
        return RotationPropagatorMixin.simulated$checkCogStateSmall((Boolean)original.call(new Object[]{state}), toBE);
    }

    @WrapOperation(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=0)})
    private static boolean testLargeCogFrom(BlockState state, Operation<Boolean> original, @Local(argsOnly=true, ordinal=0) KineticBlockEntity fromBE) {
        return RotationPropagatorMixin.simulated$checkCogStateLarge((Boolean)original.call(new Object[]{state}), fromBE);
    }

    @WrapOperation(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=1), @At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/simpleRelays/ICogWheel;isLargeCog(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=2)})
    private static boolean testLargeCogTo(BlockState state, Operation<Boolean> original, @Local(argsOnly=true, ordinal=1) KineticBlockEntity toBE) {
        return RotationPropagatorMixin.simulated$checkCogStateLarge((Boolean)original.call(new Object[]{state}), toBE);
    }

    @ModifyReceiver(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", ordinal=0)})
    private static IRotate changeIRotateShaftFrom(IRotate instance, LevelReader levelReader, BlockPos blockPos, BlockState state, Direction direction, @Local(argsOnly=true, ordinal=0) KineticBlockEntity fromBE) {
        return RotationPropagatorMixin.simulated$getNewIRotate(instance, fromBE);
    }

    @ModifyReceiver(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;hasShaftTowards(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", ordinal=1)})
    private static IRotate changeIRotateShaftTo(IRotate instance, LevelReader levelReader, BlockPos blockPos, BlockState state, Direction direction, @Local(argsOnly=true, ordinal=1) KineticBlockEntity toBe) {
        return RotationPropagatorMixin.simulated$getNewIRotate(instance, toBe);
    }

    @ModifyReceiver(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal=0), @At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal=1)})
    private static IRotate changeIRotateAxisFrom(IRotate instance, BlockState state, @Local(argsOnly=true, ordinal=0) KineticBlockEntity fromBE) {
        return RotationPropagatorMixin.simulated$getNewIRotate(instance, fromBE);
    }

    @ModifyReceiver(method={"getRotationSpeedModifier"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/base/IRotate;getRotationAxis(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/core/Direction$Axis;", ordinal=2)})
    private static IRotate changeIRotateAxisTo(IRotate instance, BlockState state, @Local(argsOnly=true, ordinal=1) KineticBlockEntity toBe) {
        return RotationPropagatorMixin.simulated$getNewIRotate(instance, toBe);
    }

    @Inject(method={"getConnectedNeighbours"}, at={@At(value="TAIL")}, remap=false)
    private static void simulated$addExtraKineticsBlockEntities(KineticBlockEntity be, CallbackInfoReturnable<List<KineticBlockEntity>> cir) {
        KineticBlockEntity extraKinetics;
        ExtraKinetics ek;
        List list = (List)cir.getReturnValue();
        if (be instanceof ExtraKinetics.ExtraKineticsBlockEntity) {
            ExtraKinetics.ExtraKineticsBlockEntity ekbe = (ExtraKinetics.ExtraKineticsBlockEntity)be;
            KineticBlockEntity parent = ekbe.getParentBlockEntity();
            if (parent != null && ((ExtraKinetics)parent).shouldConnectExtraKinetics()) {
                list.add(parent);
            }
        } else if (be instanceof ExtraKinetics && (ek = (ExtraKinetics)be).shouldConnectExtraKinetics() && (extraKinetics = ek.getExtraKinetics()) != null) {
            list.add(extraKinetics);
        }
    }

    @Inject(method={"getPotentialNeighbourLocations"}, at={@At(value="TAIL")}, remap=false)
    private static void simulated$getExtraKineticsBlockPositions(KineticBlockEntity be, CallbackInfoReturnable<List<BlockPos>> cir) {
        List list = (List)cir.getReturnValue();
        ArrayList<ExtraBlockPos> extraKinetics = new ArrayList<ExtraBlockPos>();
        Level level = be.getLevel();
        for (BlockPos pos : list) {
            Block block = level.getBlockState(pos).getBlock();
            if (!(block instanceof ExtraKinetics.ExtraKineticsBlock)) continue;
            extraKinetics.add(new ExtraBlockPos((Vec3i)pos));
        }
        list.addAll(extraKinetics);
    }

    @Unique
    @Nullable
    private static BlockEntity simulated$getBlockEntityAccountingExtraKinetics(Level level, BlockPos blockPos) {
        BlockEntity be = level.getBlockEntity(blockPos);
        if (be instanceof ExtraKinetics) {
            ExtraKinetics ek = (ExtraKinetics)be;
            if (blockPos instanceof ExtraBlockPos) {
                return ek.getExtraKinetics();
            }
        }
        return be;
    }

    @Unique
    private static boolean simulated$checkCogStateSmall(boolean original, KineticBlockEntity be) {
        Block block;
        if (original) {
            return true;
        }
        if (be.getBlockPos() instanceof ExtraBlockPos && (block = be.getBlockState().getBlock()) instanceof ExtraKinetics.ExtraKineticsBlock) {
            ICogWheel ic;
            ExtraKinetics.ExtraKineticsBlock ekb = (ExtraKinetics.ExtraKineticsBlock)block;
            IRotate iRotate = ekb.getExtraKineticsRotationConfiguration();
            return iRotate instanceof ICogWheel && (ic = (ICogWheel)iRotate).isSmallCog();
        }
        return false;
    }

    @Unique
    private static boolean simulated$checkCogStateLarge(boolean original, KineticBlockEntity be) {
        Block block;
        if (original) {
            return true;
        }
        if (be.getBlockPos() instanceof ExtraBlockPos && (block = be.getBlockState().getBlock()) instanceof ExtraKinetics.ExtraKineticsBlock) {
            ICogWheel ic;
            ExtraKinetics.ExtraKineticsBlock ekb = (ExtraKinetics.ExtraKineticsBlock)block;
            IRotate iRotate = ekb.getExtraKineticsRotationConfiguration();
            return iRotate instanceof ICogWheel && (ic = (ICogWheel)iRotate).isLargeCog();
        }
        return false;
    }

    @Unique
    private static IRotate simulated$getNewIRotate(IRotate currentRotate, KineticBlockEntity be) {
        Block block;
        if (be.getBlockPos() instanceof ExtraBlockPos && (block = be.getBlockState().getBlock()) instanceof ExtraKinetics.ExtraKineticsBlock) {
            ExtraKinetics.ExtraKineticsBlock ekb = (ExtraKinetics.ExtraKineticsBlock)block;
            return ekb.getExtraKineticsRotationConfiguration();
        }
        return currentRotate;
    }
}
