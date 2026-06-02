/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.simibubi.create.content.logistics.chute.ChuteBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.inventory_manipulation;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={ChuteBlockEntity.class})
public abstract class ChuteBlockEntityMixin
extends SmartBlockEntity {
    public ChuteBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @WrapMethod(method={"grabCapability"})
    public IItemHandler sable$grabCap(Direction side, Operation<IItemHandler> original) {
        IItemHandler handler = (IItemHandler)original.call(new Object[]{side});
        if (handler != null) {
            return handler;
        }
        Level level = this.getLevel();
        assert (level != null);
        BlockPos checkPos = this.worldPosition.relative(side);
        Direction opposite = side.getOpposite();
        Vector3d mut = new Vector3d((double)opposite.getStepX(), (double)opposite.getStepY(), (double)opposite.getStepZ());
        ActiveSableCompanion helper = Sable.HELPER;
        SubLevel parentSublevel = helper.getContaining(level, (Vec3i)checkPos);
        if (parentSublevel != null) {
            parentSublevel.logicalPose().transformNormalInverse(mut);
        }
        Vector3d includSublevelDir = new Vector3d((Vector3dc)mut);
        return helper.runIncludingSubLevels(level, checkPos.getCenter(), false, parentSublevel, (sublevel, pos) -> {
            includSublevelDir.set((Vector3dc)mut);
            if (sublevel != null) {
                sublevel.logicalPose().transformNormal(includSublevelDir);
            }
            return (IItemHandler)level.getCapability(Capabilities.ItemHandler.BLOCK, pos, (Object)Direction.getNearest((double)includSublevelDir.x, (double)includSublevelDir.y, (double)includSublevelDir.z));
        });
    }
}
