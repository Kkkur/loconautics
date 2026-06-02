/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.analog_transmission;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlock;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static class AnalogTransmissionBlockEntity.AnalogTransmissionCogwheel
extends KineticBlockEntity
implements ExtraKinetics.ExtraKineticsBlockEntity {
    public static final ICogWheel EXTRA_COGWHEEL_CONFIG = new ICogWheel(){

        public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
            return false;
        }

        public Direction.Axis getRotationAxis(BlockState state) {
            return (Direction.Axis)state.getValue((Property)AnalogTransmissionBlock.AXIS);
        }
    };
    private final KineticBlockEntity parentBlockEntity;

    public AnalogTransmissionBlockEntity.AnalogTransmissionCogwheel(BlockEntityType<?> typeIn, ExtraBlockPos pos, BlockState state, KineticBlockEntity parentBlockEntity) {
        super(typeIn, (BlockPos)pos, state);
        this.parentBlockEntity = parentBlockEntity;
    }

    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        return this.parentBlockEntity.propagateRotationTo(target, stateFrom, stateTo, diff, connectedViaAxes, connectedViaCogs);
    }

    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
        return true;
    }

    @Override
    public KineticBlockEntity getParentBlockEntity() {
        return this.parentBlockEntity;
    }
}
