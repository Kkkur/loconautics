/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity$ConnectedPort
 *  com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage
 *  com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.frogports;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorPackage;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.sable.Sable;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={ChainConveyorBlockEntity.class})
public abstract class ChainConveyorBlockEntityMixin
extends SmartBlockEntity {
    public ChainConveyorBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @WrapOperation(method={"exportToPort"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/logistics/packagePort/frogport/FrogportBlockEntity;isBackedUp()Z")})
    public boolean sable$testSublevelDistance(FrogportBlockEntity instance, Operation<Boolean> original, @Local(argsOnly=true) ChainConveyorPackage chainPackage) {
        Vec3 packagePos = chainPackage.worldPosition;
        if (packagePos == null) {
            return (Boolean)original.call(new Object[]{instance});
        }
        Vec3 frogPos = instance.getBlockPos().getCenter();
        int maxRange = (Integer)AllConfigs.server().logistics.packagePortRange.get() + 2;
        return (Boolean)original.call(new Object[]{instance}) != false || Sable.HELPER.distanceSquaredWithSubLevels(instance.getLevel(), (Position)packagePos, (Position)frogPos) > (double)(maxRange * maxRange);
    }

    @WrapOperation(method={"tick"}, at={@At(value="INVOKE", target="Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;notifyPortToAnticipate(Lnet/minecraft/core/BlockPos;)V")})
    public void sable$testSublevelDistance1(ChainConveyorBlockEntity instance, BlockPos blockPos, Operation<Void> original, @Local(name={"portEntry"}) Map.Entry<BlockPos, ChainConveyorBlockEntity.ConnectedPort> entry, @Local ChainConveyorPackage chainPackage) {
        Vec3 packagePos = chainPackage.worldPosition;
        if (packagePos == null) {
            original.call(new Object[]{instance, blockPos});
            return;
        }
        Vec3 frogPos = this.worldPosition.offset((Vec3i)entry.getKey()).getCenter();
        int maxRange = (Integer)AllConfigs.server().logistics.packagePortRange.get() + 2;
        if (Sable.HELPER.distanceSquaredWithSubLevels(this.getLevel(), (Position)packagePos, (Position)frogPos) < (double)(maxRange * maxRange)) {
            original.call(new Object[]{instance, blockPos});
        }
    }
}
