/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllBlocks
 *  com.simibubi.create.AllSoundEvents
 *  com.simibubi.create.AllTags$AllBlockTags
 *  com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation
 *  com.simibubi.create.api.stress.BlockStressValues
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.Contraption
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.IControlContraption
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
 *  com.simibubi.create.content.contraptions.bearing.BearingContraption
 *  com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions
 *  com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour
 *  com.simibubi.create.foundation.gui.AllIcons
 *  com.simibubi.create.foundation.utility.ServerSpeedProvider
 *  dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller
 *  dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor
 *  dev.simulated_team.simulated.api.BearingSlowdownController
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderGetter
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.contraption.PropellerBearingContraptionEntity;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.BearingContraptionExtension;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.MechanicalBearingTileEntityExtension;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlock;
import dev.eriksonn.aeronautics.content.blocks.propeller.behaviour.PropellerActorBehaviour;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.util.AeroSoundDistUtil;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import dev.simulated_team.simulated.api.BearingSlowdownController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class PropellerBearingBlockEntity
extends MechanicalBearingBlockEntity
implements MechanicalBearingTileEntityExtension,
BlockEntitySubLevelPropellerActor,
IHaveGoggleInformation,
BlockEntityPropeller {
    private static final MutableComponent SCROLL_OPTION_TITLE = AeroLang.translate("scroll_option.thrust_direction", new Object[0]).component();
    public final Vector3d thrustDirection;
    public final Vector3d facingDirection = new Vector3d();
    public float totalSailPower;
    public boolean disassemblySlowdown = false;
    public float prevAngle;
    public BearingSlowdownController slowdownController = new BearingSlowdownController();
    protected PropellerActorBehaviour behavior;
    protected List<BlockPos> sailPositions = new ArrayList<BlockPos>();
    protected float lastGeneratedSpeed;
    private ScrollOptionBehaviour<ThrustDirection> thrustDirectionOption;
    private float rotationSpeed = 0.0f;
    private boolean insideMainTick = false;
    @Nullable
    private Object currentSoundInstance;

    public PropellerBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.thrustDirection = new Vector3d();
        this.behavior.setThrustDirection((Vector3dc)this.thrustDirection);
    }

    private static double getConfigAirflowMult() {
        return (Double)AeroConfig.server().physics.propellerBearingAirflowMult.get();
    }

    private static double getConfigThrust() {
        return (Double)AeroConfig.server().physics.propellerBearingThrust.get();
    }

    public float calculateStressApplied() {
        float stress;
        if (!this.running || this.disassemblySlowdown) {
            this.lastStressApplied = 0.0f;
            return 0.0f;
        }
        int sails = 0;
        if (this.movedContraption != null) {
            sails = ((BearingContraption)this.movedContraption.getContraption()).getSailBlocks();
        }
        sails = Math.max(sails, 2);
        this.lastStressApplied = stress = (float)sails * (float)BlockStressValues.getImpact((Block)this.getStressConfigKey());
        return stress;
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.movementMode.setValue(2);
        behaviours.remove(this.movementMode);
        this.thrustDirectionOption = new ScrollOptionBehaviour(ThrustDirection.class, (Component)SCROLL_OPTION_TITLE, (SmartBlockEntity)this, this.getMovementModeSlot());
        this.getThrustDirectionOption().withCallback($ -> this.onDirectionChanged());
        behaviours.add((BlockEntityBehaviour)this.getThrustDirectionOption());
        this.behavior = this.getAndPreparePropBehaviour();
        behaviours.add(this.behavior);
    }

    public PropellerActorBehaviour createProp() {
        return new PropellerActorBehaviour((SmartBlockEntity)this, this);
    }

    public PropellerActorBehaviour getAndPreparePropBehaviour() {
        PropellerActorBehaviour prop = this.createProp();
        prop.setParticleAmountUpdater(() -> 0.02 * (double)Math.abs(this.getClampedRotationRate()) * (double)this.totalSailPower);
        prop.setParticleCountProperties(50, 10.0);
        prop.setParticlePositionUpdater((v, random) -> this.getRandomSailPosition((RandomSource)random, (Vector3d)v).add((Vector3dc)this.facingDirection));
        return prop;
    }

    public Direction getBlockDirection() {
        return (Direction)this.getBlockState().getValue((Property)PropellerBearingBlock.FACING);
    }

    public double getThrust() {
        return Math.pow(this.totalSailPower, 1.5) * (double)this.getDirectionIndependentSpeed() * PropellerBearingBlockEntity.getConfigThrust();
    }

    public boolean isActive() {
        return Math.abs(this.rotationSpeed) > 0.01f && this.movedContraption != null;
    }

    public double getAirflow() {
        return Math.sqrt(this.totalSailPower) * (double)this.getDirectionIndependentSpeed() * PropellerBearingBlockEntity.getConfigAirflowMult();
    }

    public float getDirectionIndependentSpeed() {
        return (float)((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getAxisDirection().getStep() * this.getClampedRotationRate() * 3.3333333f * (float)(this.getThrustDirectionOption().value == 1 ? -1 : 1);
    }

    public BlockEntityPropeller getPropeller() {
        return this;
    }

    public void tick() {
        this.prevAngle = this.angle;
        Vec3i normal = ((Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING)).getNormal();
        this.facingDirection.set((double)normal.getX(), (double)normal.getY(), (double)normal.getZ());
        if (this.disassemblySlowdown) {
            this.updateSlowdownSpeed();
        } else {
            this.updateRotationSpeed();
        }
        this.insideMainTick = true;
        super.tick();
        this.insideMainTick = false;
        if (this.movedContraption != null && !this.movedContraption.isAlive()) {
            this.movedContraption = null;
        }
        if (this.movedContraption == null && !this.isVirtual()) {
            this.angle = 0.0f;
            this.setRotationSpeed(0.0f);
            this.disassemblySlowdown = false;
        }
        if (this.speed != 0.0f) {
            this.lastGeneratedSpeed = this.speed;
        }
        if (this.isActive()) {
            this.activeTick();
        }
    }

    public void activeTick() {
        this.behavior.pushEntities();
        if (this.level.isClientSide) {
            this.behavior.spawnParticles();
        }
    }

    public void onDirectionChanged() {
        if (!this.level.isClientSide && this.running) {
            this.updateGeneratedRotation();
        }
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putFloat("LastGenerated", this.lastGeneratedSpeed);
        compound.putFloat("RotationSpeed", this.getRotationSpeed());
        compound.putBoolean("DisassemblySlowdown", this.disassemblySlowdown);
        if (this.disassemblySlowdown) {
            this.slowdownController.serializeIntoNBT(compound);
        }
        super.write(compound, registries, clientPacket);
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        if (!this.wasMoved) {
            this.lastGeneratedSpeed = compound.getFloat("LastGenerated");
        }
        this.setRotationSpeed(compound.getFloat("RotationSpeed"));
        this.disassemblySlowdown = compound.getBoolean("DisassemblySlowdown");
        if (this.disassemblySlowdown) {
            this.slowdownController.deserializeFromNBT(compound);
        }
        super.read(compound, registries, clientPacket);
    }

    public float getInterpolatedAngle(float partialTicks) {
        if (this.isVirtual()) {
            return Mth.lerp((float)(partialTicks + 0.5f), (float)this.prevAngle, (float)this.angle);
        }
        if (this.movedContraption == null || this.movedContraption.isStalled() || !this.running) {
            partialTicks = 0.0f;
        }
        if (this.disassemblySlowdown) {
            return this.slowdownController.getAngle(partialTicks);
        }
        return Mth.lerp((float)partialTicks, (float)this.angle, (float)(this.angle + this.getAngularSpeed()));
    }

    public float getAngularSpeed() {
        float speed = this.getRotationSpeed();
        if (this.insideMainTick && this.disassemblySlowdown) {
            speed = this.slowdownController.getSpeed(1.0f);
        }
        if (this.level.isClientSide) {
            speed *= ServerSpeedProvider.get();
            speed += this.clientAngleDiff / 3.0f;
        }
        return speed;
    }

    private void updateRotationSpeed() {
        float nextSpeed = PropellerBearingBlockEntity.convertToAngular((float)this.getSpeed());
        if (this.isVirtual()) {
            this.setRotationSpeed(nextSpeed);
        }
        if (this.getSpeed() == 0.0f) {
            nextSpeed = 0.0f;
        }
        if (this.totalSailPower > 0.0f) {
            this.setRotationSpeed(Mth.lerp((float)(0.4f / (float)Math.sqrt(this.totalSailPower)), (float)this.getRotationSpeed(), (float)nextSpeed));
        } else {
            this.setRotationSpeed(nextSpeed);
        }
    }

    private void updateSlowdownSpeed() {
        if (this.slowdownController.stepGoal() && !this.level.isClientSide) {
            this.disassemble();
            return;
        }
        this.setRotationSpeed(this.slowdownController.getSpeed(0.0f));
        this.angle = this.slowdownController.getAngle(0.0f);
    }

    public void attach(ControlledContraptionEntity contraption) {
        super.attach(contraption);
        this.contraptionInitialize();
        if (this.level.isClientSide) {
            this.currentSoundInstance = AeroSoundDistUtil.tickPropellerSounds(this, this.currentSoundInstance);
        }
    }

    public void assemble() {
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof BearingBlock)) {
            return;
        }
        Direction direction = (Direction)this.getBlockState().getValue((Property)PropellerBearingBlock.FACING);
        BearingContraption contraption = new BearingContraption(this.isWindmill(), direction);
        try {
            if (this.isPropeller()) {
                ((BearingContraptionExtension)contraption).aeronautics$setPropeller();
            }
            if (!contraption.assemble(this.level, this.worldPosition)) {
                return;
            }
            this.lastException = null;
        }
        catch (AssemblyException e) {
            this.lastException = e;
            this.sendData();
            return;
        }
        contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
        this.movedContraption = PropellerBearingContraptionEntity.create(this.level, (IControlContraption)this, (Contraption)contraption);
        BlockPos anchor = this.worldPosition.relative(direction);
        this.movedContraption.setPos((double)anchor.getX(), (double)anchor.getY(), (double)anchor.getZ());
        this.movedContraption.setRotationAxis(direction.getAxis());
        this.level.addFreshEntity((Entity)this.movedContraption);
        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(this.level, (Vec3i)this.worldPosition);
        AeroAdvancements.IN_THRUST_WE_TRUST.awardToNearby(this.getBlockPos(), this.getLevel());
        this.running = true;
        this.angle = 0.0f;
        this.updateGeneratedRotation();
        this.setRotationSpeed(0.0f);
        this.contraptionInitialize();
        this.sendData();
    }

    public void disassemble() {
        if (this.running && this.movedContraption != null) {
            this.angle = 0.0f;
            this.behavior.getLayers().clear();
            this.applyRotation();
            super.disassemble();
        }
    }

    public void setAssembleNextTick(boolean value) {
        this.assembleNextTick = value;
    }

    public void startDisassemblySlowdown() {
        if (!this.disassemblySlowdown && this.movedContraption != null) {
            this.slowdownController.generate(1.0f + 3.5f * (float)Math.sqrt(this.totalSailPower), this.getInterpolatedAngle(0.0f), this.getRotationSpeed(), (Direction)this.getBlockState().getValue((Property)PropellerBearingBlock.FACING), this.getMovedContraption().getContraption());
            this.disassemblySlowdown = true;
            this.updateGeneratedRotation();
            this.sendData();
        }
    }

    public void contraptionInitialize() {
        Direction direction = (Direction)this.getBlockState().getValue((Property)PropellerBearingBlock.FACING);
        this.thrustDirection.set((double)direction.getStepX(), (double)direction.getStepY(), (double)direction.getStepZ());
        this.findSails();
    }

    public float getSailPower(StructureTemplate.StructureBlockInfo info) {
        BlockState newState;
        BlockState state = info.state();
        if (AllBlocks.COPYCAT_PANEL.has(state) && !(newState = NbtUtils.readBlockState((HolderGetter)this.blockHolderGetter(), (CompoundTag)info.nbt().getCompound("Material"))).isAir()) {
            state = newState;
        }
        float power = 0.0f;
        if (state.is(AllTags.AllBlockTags.WINDMILL_SAILS.tag)) {
            power += 1.0f;
        }
        return power;
    }

    public void findSails() {
        this.sailPositions = new ArrayList<BlockPos>();
        this.totalSailPower = 0.0f;
        this.behavior.getLayers().clear();
        if (this.movedContraption != null) {
            Map Blocks = this.movedContraption.getContraption().getBlocks();
            Vec3i direction = ((Direction)this.getBlockState().getValue((Property)PropellerBearingBlock.FACING)).getNormal();
            HashMap<Integer, Tuple> layerHashMap = new HashMap<Integer, Tuple>();
            for (Map.Entry entry : Blocks.entrySet()) {
                float sailPower = this.getSailPower((StructureTemplate.StructureBlockInfo)entry.getValue());
                if (!(sailPower > 0.0f)) continue;
                BlockPos currentPos = (BlockPos)entry.getKey();
                this.sailPositions.add(currentPos);
                int offset = direction.getX() * currentPos.getX() + direction.getY() * currentPos.getY() + direction.getZ() * currentPos.getZ();
                this.totalSailPower += sailPower;
                currentPos = currentPos.offset(direction.multiply(-offset));
                int radius = currentPos.getX() * currentPos.getX() + currentPos.getY() * currentPos.getY() + currentPos.getZ() * currentPos.getZ();
                if (layerHashMap.containsKey(offset)) {
                    Tuple tuple = (Tuple)layerHashMap.get(offset);
                    if (radius < (Integer)tuple.getA()) {
                        tuple.setA((Object)radius);
                    }
                    if (radius <= (Integer)tuple.getB()) continue;
                    tuple.setB((Object)radius);
                    continue;
                }
                layerHashMap.put(offset, new Tuple((Object)radius, (Object)radius));
            }
            for (Map.Entry entry : layerHashMap.entrySet()) {
                Tuple tuple = (Tuple)entry.getValue();
                double inner = Math.max(Math.sqrt(((Integer)tuple.getA()).intValue()) - 0.5, 0.0);
                double outer = Math.sqrt(((Integer)tuple.getB()).intValue()) + 0.5;
                this.behavior.addPropellerLayer(new PropellerActorBehaviour.PropellerLayer((Integer)entry.getKey() + 1, inner, outer));
            }
        }
    }

    private Vector3d getRandomSailPosition(RandomSource random, Vector3d pos) {
        BlockPos sailPos = this.sailPositions.get(random.nextInt(this.sailPositions.size()));
        Vec3 floatPos = new Vec3((double)sailPos.getX(), (double)sailPos.getY(), (double)sailPos.getZ());
        floatPos = this.movedContraption.applyRotation(floatPos, 0.0f);
        pos.set(random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0, random.nextDouble() * 2.0 - 1.0).mul(0.5);
        pos.fma(-this.thrustDirection.dot((Vector3dc)pos), (Vector3dc)this.thrustDirection);
        pos.add(floatPos.x, floatPos.y, floatPos.z);
        return pos;
    }

    public float getClampedRotationRate() {
        if (this.disassemblySlowdown) {
            float max = Math.max(this.slowdownController.getInitialVelocity(), 0.0f);
            float min = Math.min(this.slowdownController.getInitialVelocity(), 0.0f);
            return Math.min(Math.max(this.getRotationSpeed(), min), max);
        }
        return this.getRotationSpeed();
    }

    public boolean isWoodenTop() {
        return false;
    }

    @Override
    public boolean isPropeller() {
        return true;
    }

    public float getRotationSpeed() {
        return this.rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public ScrollOptionBehaviour<ThrustDirection> getThrustDirectionOption() {
        return this.thrustDirectionOption;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (!super.addToGoggleTooltip(tooltip, isPlayerSneaking)) {
            return false;
        }
        return this.behavior.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public PropellerBearingContraptionEntity getMovedContraption() {
        ControlledContraptionEntity controlledContraptionEntity = this.movedContraption;
        if (controlledContraptionEntity instanceof PropellerBearingContraptionEntity) {
            PropellerBearingContraptionEntity propellerBearingContraptionEntity = (PropellerBearingContraptionEntity)controlledContraptionEntity;
            return propellerBearingContraptionEntity;
        }
        return null;
    }

    public static enum ThrustDirection implements INamedIconOptions
    {
        RIGHT_HANDED(AllIcons.I_REFRESH, "pull_when_clockwise"),
        LEFT_HANDED(AllIcons.I_ROTATE_CCW, "push_when_clockwise");

        private final String translationKey;
        private final AllIcons icon;

        private ThrustDirection(AllIcons icon, String name) {
            this.icon = icon;
            this.translationKey = "aeronautics.generic." + name;
        }

        public AllIcons getIcon() {
            return this.icon;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
