/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.base;

import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.equipment.goggles.IHaveHoveringInformation;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.compat.computercraft.events.KineticsChangeEvent;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.RotationPropagator;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import com.simibubi.create.content.kinetics.gearbox.GearboxBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.sound.SoundScapes;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class KineticBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation,
IHaveHoveringInformation {
    @Nullable
    public Long network;
    @Nullable
    public BlockPos source;
    public boolean networkDirty;
    public boolean updateSpeed = true;
    public int preventSpeedUpdate;
    protected KineticEffectHandler effects = new KineticEffectHandler(this);
    protected float speed;
    protected float capacity;
    protected float stress;
    protected boolean overStressed;
    protected boolean wasMoved;
    private int flickerTally;
    private int networkSize;
    private int validationCountdown;
    protected float lastStressApplied;
    protected float lastCapacityProvided;
    public SequencedGearshiftBlockEntity.SequenceContext sequenceContext;

    public KineticBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    @Override
    public void initialize() {
        if (this.hasNetwork() && !this.level.isClientSide) {
            KineticNetwork network = this.getOrCreateNetwork();
            if (!network.initialized) {
                network.initFromTE(this.capacity, this.stress, this.networkSize);
            }
            network.addSilently(this, this.lastCapacityProvided, this.lastStressApplied);
        }
        super.initialize();
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide && this.needsSpeedUpdate()) {
            this.attachKinetics();
        }
        super.tick();
        this.effects.tick();
        this.preventSpeedUpdate = 0;
        if (this.level.isClientSide) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> this.tickAudio());
            return;
        }
        if (this.validationCountdown-- <= 0) {
            this.validationCountdown = (Integer)AllConfigs.server().kinetics.kineticValidationFrequency.get();
            this.validateKinetics();
        }
        if (this.getFlickerScore() > 0) {
            this.flickerTally = this.getFlickerScore() - 1;
        }
        if (this.networkDirty) {
            if (this.hasNetwork()) {
                this.getOrCreateNetwork().updateNetwork();
            }
            this.networkDirty = false;
        }
    }

    private void validateKinetics() {
        if (this.hasSource()) {
            KineticBlockEntity sourceBE;
            if (!this.hasNetwork()) {
                this.removeSource();
                return;
            }
            if (!this.level.isLoaded(this.source)) {
                return;
            }
            BlockEntity blockEntity = this.level.getBlockEntity(this.source);
            KineticBlockEntity kineticBlockEntity = sourceBE = blockEntity instanceof KineticBlockEntity ? (KineticBlockEntity)blockEntity : null;
            if (sourceBE == null || sourceBE.speed == 0.0f) {
                this.removeSource();
                this.detachKinetics();
                return;
            }
            return;
        }
        if (this.speed != 0.0f && this.getGeneratedSpeed() == 0.0f) {
            this.speed = 0.0f;
        }
    }

    public void updateFromNetwork(float maxStress, float currentStress, int networkSize) {
        this.networkDirty = false;
        this.capacity = maxStress;
        this.stress = currentStress;
        this.networkSize = networkSize;
        boolean overStressed = maxStress < currentStress && IRotate.StressImpact.isEnabled();
        this.setChanged();
        if (overStressed != this.overStressed) {
            float prevSpeed = this.getSpeed();
            this.overStressed = overStressed;
            this.onSpeedChanged(prevSpeed);
            this.sendData();
        }
    }

    protected KineticsChangeEvent makeComputerKineticsChangeEvent() {
        return new KineticsChangeEvent(this.speed, this.capacity, this.stress, this.overStressed);
    }

    protected Block getStressConfigKey() {
        return this.getBlockState().getBlock();
    }

    public float calculateStressApplied() {
        float impact;
        this.lastStressApplied = impact = (float)BlockStressValues.getImpact(this.getStressConfigKey());
        return impact;
    }

    public float calculateAddedStressCapacity() {
        float capacity;
        this.lastCapacityProvided = capacity = (float)BlockStressValues.getCapacity(this.getStressConfigKey());
        return capacity;
    }

    public void onSpeedChanged(float previousSpeed) {
        boolean directionSwap;
        boolean fromOrToZero = previousSpeed == 0.0f != (this.getSpeed() == 0.0f);
        boolean bl = directionSwap = !fromOrToZero && Math.signum(previousSpeed) != Math.signum(this.getSpeed());
        if (fromOrToZero || directionSwap) {
            this.flickerTally = this.getFlickerScore() + 5;
        }
        this.setChanged();
    }

    @Override
    public void remove() {
        if (!this.level.isClientSide) {
            if (this.hasNetwork()) {
                this.getOrCreateNetwork().remove(this);
            }
            this.detachKinetics();
        }
        super.remove();
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("Speed", this.speed);
        if (this.sequenceContext != null && (!clientPacket || this.syncSequenceContext())) {
            compound.put("Sequence", (Tag)this.sequenceContext.serializeNBT());
        }
        if (this.needsSpeedUpdate()) {
            compound.putBoolean("NeedsSpeedUpdate", true);
        }
        if (this.hasSource()) {
            compound.put("Source", NbtUtils.writeBlockPos((BlockPos)this.source));
        }
        if (this.hasNetwork()) {
            CompoundTag networkTag = new CompoundTag();
            networkTag.putLong("Id", this.network.longValue());
            networkTag.putFloat("Stress", this.stress);
            networkTag.putFloat("Capacity", this.capacity);
            networkTag.putInt("Size", this.networkSize);
            if (this.lastStressApplied != 0.0f) {
                networkTag.putFloat("AddedStress", this.lastStressApplied);
            }
            if (this.lastCapacityProvided != 0.0f) {
                networkTag.putFloat("AddedCapacity", this.lastCapacityProvided);
            }
            compound.put("Network", (Tag)networkTag);
        }
        super.write(compound, registries, clientPacket);
    }

    public boolean needsSpeedUpdate() {
        return this.updateSpeed;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        boolean overStressedBefore = this.overStressed;
        this.clearKineticInformation();
        if (this.wasMoved) {
            super.read(compound, registries, clientPacket);
            return;
        }
        this.speed = compound.getFloat("Speed");
        this.sequenceContext = SequencedGearshiftBlockEntity.SequenceContext.fromNBT(compound.getCompound("Sequence"));
        this.source = null;
        if (compound.contains("Source")) {
            this.source = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Source");
        }
        if (compound.contains("Network")) {
            CompoundTag networkTag = compound.getCompound("Network");
            this.network = networkTag.getLong("Id");
            this.stress = networkTag.getFloat("Stress");
            this.capacity = networkTag.getFloat("Capacity");
            this.networkSize = networkTag.getInt("Size");
            this.lastStressApplied = networkTag.getFloat("AddedStress");
            this.lastCapacityProvided = networkTag.getFloat("AddedCapacity");
            this.overStressed = this.capacity < this.stress && IRotate.StressImpact.isEnabled();
        }
        super.read(compound, registries, clientPacket);
        if (clientPacket && overStressedBefore != this.overStressed && this.speed != 0.0f) {
            this.effects.triggerOverStressedEffect();
        }
        if (clientPacket) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
        }
    }

    public float getGeneratedSpeed() {
        return 0.0f;
    }

    public boolean isSource() {
        return this.getGeneratedSpeed() != 0.0f;
    }

    public float getSpeed() {
        if (this.overStressed || this.level != null && this.level.tickRateManager().isFrozen()) {
            return 0.0f;
        }
        return this.getTheoreticalSpeed();
    }

    public float getTheoreticalSpeed() {
        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean hasSource() {
        return this.source != null;
    }

    public void setSource(BlockPos source) {
        this.source = source;
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(source);
        if (!(blockEntity instanceof KineticBlockEntity)) {
            this.removeSource();
            return;
        }
        KineticBlockEntity sourceBE = (KineticBlockEntity)blockEntity;
        this.setNetwork(sourceBE.network);
        this.copySequenceContextFrom(sourceBE);
    }

    protected void copySequenceContextFrom(KineticBlockEntity sourceBE) {
        this.sequenceContext = sourceBE.sequenceContext;
    }

    public void removeSource() {
        float prevSpeed = this.getSpeed();
        this.speed = 0.0f;
        this.source = null;
        this.setNetwork(null);
        this.sequenceContext = null;
        this.onSpeedChanged(prevSpeed);
    }

    public void setNetwork(@Nullable Long networkIn) {
        if (Objects.equals(this.network, networkIn)) {
            return;
        }
        if (this.network != null) {
            this.getOrCreateNetwork().remove(this);
        }
        this.network = networkIn;
        this.setChanged();
        if (networkIn == null) {
            return;
        }
        this.network = networkIn;
        KineticNetwork network = this.getOrCreateNetwork();
        network.initialized = true;
        network.add(this);
    }

    public KineticNetwork getOrCreateNetwork() {
        return Create.TORQUE_PROPAGATOR.getOrCreateNetworkFor(this);
    }

    public boolean hasNetwork() {
        return this.network != null;
    }

    public void attachKinetics() {
        this.updateSpeed = false;
        RotationPropagator.handleAdded(this.level, this.worldPosition, this);
    }

    public void detachKinetics() {
        RotationPropagator.handleRemoved(this.level, this.worldPosition, this);
    }

    public boolean isSpeedRequirementFulfilled() {
        BlockState state = this.getBlockState();
        if (!(this.getBlockState().getBlock() instanceof IRotate)) {
            return true;
        }
        IRotate def = (IRotate)state.getBlock();
        IRotate.SpeedLevel minimumRequiredSpeedLevel = def.getMinimumRequiredSpeedLevel();
        return Math.abs(this.getSpeed()) >= minimumRequiredSpeedLevel.getSpeedValue();
    }

    public static void switchToBlockState(Level world, BlockPos pos, BlockState state) {
        if (world.isClientSide) {
            return;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        BlockState currentState = world.getBlockState(pos);
        boolean isKinetic = blockEntity instanceof KineticBlockEntity;
        if (currentState == state) {
            return;
        }
        if (blockEntity == null || !isKinetic) {
            world.setBlock(pos, state, 3);
            return;
        }
        KineticBlockEntity kineticBlockEntity = (KineticBlockEntity)blockEntity;
        if (state.getBlock() instanceof KineticBlock && !((KineticBlock)state.getBlock()).areStatesKineticallyEquivalent(currentState, state)) {
            if (kineticBlockEntity.hasNetwork()) {
                kineticBlockEntity.getOrCreateNetwork().remove(kineticBlockEntity);
            }
            kineticBlockEntity.detachKinetics();
            kineticBlockEntity.removeSource();
        }
        if (blockEntity instanceof GeneratingKineticBlockEntity) {
            GeneratingKineticBlockEntity generatingBlockEntity = (GeneratingKineticBlockEntity)blockEntity;
            generatingBlockEntity.reActivateSource = true;
        }
        world.setBlock(pos, state, 3);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean notFastEnough;
        boolean bl = notFastEnough = !this.isSpeedRequirementFulfilled() && this.getSpeed() != 0.0f;
        if (this.overStressed && ((Boolean)AllConfigs.client().enableOverstressedTooltip.get()).booleanValue()) {
            CreateLang.translate("gui.stressometer.overstressed", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
            MutableComponent hint = CreateLang.translateDirect("gui.contraptions.network_overstressed", new Object[0]);
            List<Component> cutString = TooltipHelper.cutTextComponent((Component)hint, FontHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString) {
                CreateLang.builder().add(component.copy()).forGoggles(tooltip);
            }
            return true;
        }
        if (notFastEnough) {
            CreateLang.translate("tooltip.speedRequirement", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
            MutableComponent hint = CreateLang.translateDirect("gui.contraptions.not_fast_enough", I18n.get((String)this.getBlockState().getBlock().getDescriptionId(), (Object[])new Object[0]));
            List<Component> cutString = TooltipHelper.cutTextComponent((Component)hint, FontHelper.Palette.GRAY_AND_WHITE);
            for (Component component : cutString) {
                CreateLang.builder().add(component.copy()).forGoggles(tooltip);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean added = false;
        if (!IRotate.StressImpact.isEnabled()) {
            return added;
        }
        float stressAtBase = this.calculateStressApplied();
        if (Mth.equal((float)stressAtBase, (float)0.0f)) {
            return added;
        }
        CreateLang.translate("gui.goggles.kinetic_stats", new Object[0]).forGoggles(tooltip);
        this.addStressImpactStats(tooltip, stressAtBase);
        return true;
    }

    protected void addStressImpactStats(List<Component> tooltip, float stressAtBase) {
        CreateLang.translate("tooltip.stressImpact", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        float stressTotal = stressAtBase * Math.abs(this.getTheoreticalSpeed());
        CreateLang.number(stressTotal).translate("generic.unit.stress", new Object[0]).style(ChatFormatting.AQUA).space().add(CreateLang.translate("gui.goggles.at_current_speed", new Object[0]).style(ChatFormatting.DARK_GRAY)).forGoggles(tooltip, 1);
    }

    public void clearKineticInformation() {
        this.speed = 0.0f;
        this.source = null;
        this.network = null;
        this.overStressed = false;
        this.stress = 0.0f;
        this.capacity = 0.0f;
        this.lastStressApplied = 0.0f;
        this.lastCapacityProvided = 0.0f;
    }

    public void warnOfMovement() {
        this.wasMoved = true;
    }

    public int getFlickerScore() {
        return this.flickerTally;
    }

    public static float convertToDirection(float axisSpeed, Direction d) {
        return d.getAxisDirection() == Direction.AxisDirection.POSITIVE ? axisSpeed : -axisSpeed;
    }

    public static float convertToLinear(float speed) {
        return speed / 512.0f;
    }

    public static float convertToAngular(float speed) {
        return speed * 360.0f / 60.0f / 20.0f;
    }

    public boolean isOverStressed() {
        return this.overStressed;
    }

    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        return 0.0f;
    }

    public List<BlockPos> addPropagationLocations(IRotate block, BlockState state, List<BlockPos> neighbours) {
        if (!this.canPropagateDiagonally(block, state)) {
            return neighbours;
        }
        Direction.Axis axis = block.getRotationAxis(state);
        BlockPos.betweenClosedStream((BlockPos)new BlockPos(-1, -1, -1), (BlockPos)new BlockPos(1, 1, 1)).forEach(offset -> {
            if (axis.choose(offset.getX(), offset.getY(), offset.getZ()) != 0) {
                return;
            }
            if (offset.distSqr((Vec3i)BlockPos.ZERO) != 2.0) {
                return;
            }
            neighbours.add(this.worldPosition.offset((Vec3i)offset));
        });
        return neighbours;
    }

    public boolean isCustomConnection(KineticBlockEntity other, BlockState state, BlockState otherState) {
        return false;
    }

    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
        return ICogWheel.isSmallCog(state);
    }

    public void requestModelDataUpdate() {
        super.requestModelDataUpdate();
        if (!this.remove) {
            CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> VisualizationHelper.queueUpdate((BlockEntity)this));
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void tickAudio() {
        Block block;
        float componentSpeed = Math.abs(this.getSpeed());
        if (componentSpeed == 0.0f) {
            return;
        }
        float pitch = Mth.clamp((float)(componentSpeed / 256.0f + 0.45f), (float)0.85f, (float)1.0f);
        if (this.isNoisy()) {
            SoundScapes.play(SoundScapes.AmbienceGroup.KINETIC, this.worldPosition, pitch);
        }
        if (ICogWheel.isSmallCog(block = this.getBlockState().getBlock()) || ICogWheel.isLargeCog(block) || block instanceof GearboxBlock) {
            SoundScapes.play(SoundScapes.AmbienceGroup.COG, this.worldPosition, pitch);
        }
    }

    protected boolean isNoisy() {
        return true;
    }

    public int getRotationAngleOffset(Direction.Axis axis) {
        return 0;
    }

    protected boolean syncSequenceContext() {
        return false;
    }
}
