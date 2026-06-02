/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.VisibleForTesting
 */
package dev.simulated_team.simulated.content.blocks.analog_transmission;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.simulated_team.simulated.content.blocks.analog_transmission.AnalogTransmissionBlock;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.mixin_interface.extra_kinetics.KineticBlockEntityExtension;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraBlockPos;
import dev.simulated_team.simulated.util.extra_kinetics.ExtraKinetics;
import java.util.List;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

public class AnalogTransmissionBlockEntity
extends KineticBlockEntity
implements ExtraKinetics {
    private final AnalogTransmissionCogwheel extraWheel;
    private int signal = 0;
    private boolean oversaturated = false;
    boolean alreadySentEffects = false;

    public AnalogTransmissionBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.extraWheel = new AnalogTransmissionCogwheel(typeIn, new ExtraBlockPos((Vec3i)pos), state, this);
    }

    public void tick() {
        int bestNeighborSignal = this.getLevel().getBestNeighborSignal(this.getBlockPos());
        if (!this.getLevel().isClientSide) {
            if (bestNeighborSignal != this.signal) {
                this.detachKinetics();
                this.extraWheel.detachKinetics();
                this.removeSource();
                this.extraWheel.removeSource();
                this.signal = bestNeighborSignal;
                this.getLevel().setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)AnalogTransmissionBlock.POWERED, (Comparable)Boolean.valueOf(this.signal > 0)));
                if (((KineticBlockEntityExtension)((Object)this)).simulated$getConnectedToExtraKinetics()) {
                    this.attachKinetics();
                    this.extraWheel.attachKinetics();
                } else {
                    this.extraWheel.attachKinetics();
                    this.attachKinetics();
                }
            }
        } else if (this.oversaturated) {
            if (!this.alreadySentEffects) {
                this.alreadySentEffects = true;
                this.effects.triggerOverStressedEffect();
            }
        } else {
            this.alreadySentEffects = false;
        }
        this.extraWheel.tick();
        super.tick();
    }

    @VisibleForTesting
    public float getRotationModifier() {
        return 1.0f - (float)(this.signal + 1) / 16.0f;
    }

    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        float gatheredRotationModifier = 0.0f;
        if (this.signal != 15) {
            if (target == this.extraWheel) {
                float f = gatheredRotationModifier = this.signal == 0 ? 1.0f : this.getRotationModifier();
                if (this.oversaturated) {
                    return 0.0f;
                }
            } else if (target == this) {
                float f = gatheredRotationModifier = this.signal == 0 ? 1.0f : 1.0f / this.getRotationModifier();
                if (Math.abs(this.extraWheel.getTheoreticalSpeed() * gatheredRotationModifier) > (float)((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).intValue()) {
                    this.oversaturated = true;
                    return 0.0f;
                }
                this.oversaturated = false;
            }
        } else {
            this.oversaturated = false;
        }
        return gatheredRotationModifier;
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("Signal", this.signal);
        compound.putBoolean("Oversaturated", this.oversaturated);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.signal = compound.getInt("Signal");
        this.oversaturated = compound.getBoolean("Oversaturated");
    }

    public boolean isOverStressed() {
        if (this.level.isClientSide) {
            return this.oversaturated || this.overStressed;
        }
        return super.isOverStressed();
    }

    @Override
    @NotNull
    public KineticBlockEntity getExtraKinetics() {
        return this.extraWheel;
    }

    @Override
    public boolean shouldConnectExtraKinetics() {
        return true;
    }

    @Override
    public String getExtraKineticsSaveName() {
        return "ExtraCogwheel";
    }

    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (this.oversaturated) {
            SimLang.translate("analog_transmission.too_fast", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
            MutableComponent component = SimLang.translate("analog_transmission.too_fast_error", new Object[0]).component();
            List cutString = TooltipHelper.cutTextComponent((Component)component, (FontHelper.Palette)FontHelper.Palette.GRAY_AND_WHITE);
            tooltip.addAll(cutString);
            return true;
        }
        return super.addToTooltip(tooltip, isPlayerSneaking);
    }

    public static class AnalogTransmissionCogwheel
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

        public AnalogTransmissionCogwheel(BlockEntityType<?> typeIn, ExtraBlockPos pos, BlockState state, KineticBlockEntity parentBlockEntity) {
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
}
