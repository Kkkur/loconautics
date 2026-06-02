/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.swivel_bearing;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencerInstructions;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlock;
import dev.simulated_team.simulated.content.blocks.swivel_bearing.SwivelBearingBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public static class SwivelBearingBlockEntity.SwivelBearingCogwheelBlockEntity
extends KineticBlockEntity
implements ExtraKinetics.ExtraKineticsBlockEntity {
    public static final ICogWheel EXTRA_COGWHEEL_CONFIG = new ICogWheel(){

        public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
            return false;
        }

        public Direction.Axis getRotationAxis(BlockState state) {
            return ((Direction)state.getValue((Property)SwivelBearingBlock.FACING)).getAxis();
        }
    };
    private final SwivelBearingBlockEntity parent;

    public SwivelBearingBlockEntity.SwivelBearingCogwheelBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state, SwivelBearingBlockEntity parent) {
        super(typeIn, (BlockPos)new ExtraBlockPos((Vec3i)pos), state);
        this.parent = parent;
    }

    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if ((double)this.speed != 0.0 && !this.parent.isAssembled()) {
            this.parent.assembleNextTick = true;
        }
        this.parent.sequencedAngleLimit = -1.0;
        if (this.sequenceContext != null && this.sequenceContext.instruction() == SequencerInstructions.TURN_ANGLE) {
            this.parent.sequencedAngleLimit = this.sequenceContext.getEffectiveValue((double)this.getTheoreticalSpeed());
        }
    }

    @Override
    public KineticBlockEntity getParentBlockEntity() {
        return this.parent;
    }

    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        super.addStressImpactStats(tooltip, stressAtBase);
    }

    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
        return true;
    }

    @Override
    public Component getKey() {
        return SimLang.translate("extra_kinetics.extra_cogwheel", new Object[0]).component();
    }
}
