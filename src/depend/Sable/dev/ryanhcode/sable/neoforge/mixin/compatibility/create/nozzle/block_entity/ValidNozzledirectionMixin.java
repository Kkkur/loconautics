/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.fan.NozzleBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.nozzle.block_entity;

import com.simibubi.create.content.kinetics.fan.NozzleBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dev.ryanhcode.sable.neoforge.mixinterface.compatibility.create.NozzleBlockEntityExtension;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={NozzleBlockEntity.class})
public abstract class ValidNozzledirectionMixin
extends SmartBlockEntity
implements NozzleBlockEntityExtension {
    @Unique
    private final EnumSet<Direction> sable$validDirections = EnumSet.noneOf(Direction.class);

    public ValidNozzledirectionMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public EnumSet<Direction> sable$getValidDirections() {
        return this.sable$validDirections;
    }

    @Inject(method={"tick"}, at={@At(value="HEAD")})
    public void sable$updateValidDirections(CallbackInfo ci) {
        this.sable$validDirections.clear();
        if (this.getLevel() != null) {
            for (Direction value : Direction.values()) {
                BlockState state = this.getLevel().getBlockState(this.getBlockPos().relative(value));
                if (!state.canBeReplaced()) continue;
                this.sable$validDirections.add(value);
            }
        }
    }
}
