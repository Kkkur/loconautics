/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.AssemblyException
 *  com.simibubi.create.content.contraptions.Contraption
 *  com.simibubi.create.content.contraptions.ControlledContraptionEntity
 *  com.simibubi.create.content.contraptions.IControlContraption
 *  com.simibubi.create.content.contraptions.bearing.BearingContraption
 *  com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.content.kinetics.KineticNetwork
 *  com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  com.simibubi.create.foundation.item.TooltipHelper
 *  com.simibubi.create.foundation.utility.ServerSpeedProvider
 *  com.simibubi.create.infrastructure.config.AllConfigs
 *  dev.ryanhcode.sable.companion.math.BoundingBox3d
 *  dev.ryanhcode.sable.companion.math.BoundingBox3i
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  dev.simulated_team.simulated.api.BearingSlowdownController
 *  dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper
 *  dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
 *  javax.annotation.Nullable
 *  net.createmod.catnip.lang.FontHelper$Palette
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 */
package dev.ryanhcode.offroad.content.blocks.borehead_bearing;

import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.IControlContraption;
import com.simibubi.create.content.contraptions.bearing.BearingContraption;
import com.simibubi.create.content.contraptions.bearing.MechanicalBearingBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.ryanhcode.offroad.config.OffroadConfig;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlock;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlock;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import dev.ryanhcode.offroad.content.entities.BoreheadContraptionEntity;
import dev.ryanhcode.offroad.data.OffroadLang;
import dev.ryanhcode.offroad.data.OffroadTags;
import dev.ryanhcode.offroad.handlers.server.MultiMiningServerManager;
import dev.ryanhcode.offroad.handlers.server.MultiMiningSupplier;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.simulated_team.simulated.api.BearingSlowdownController;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class BoreheadBearingBlockEntity
extends MechanicalBearingBlockEntity
implements MultiMiningSupplier {
    private static final Vector3dc IMMUT_ZERO = new Vector3d();
    private static final BoundingBox3d TEMP_BOUNDING_BOX_DOUBLE = new BoundingBox3d();
    private static final BoundingBox3i TEMP_BOUNDING_BOX_INT = new BoundingBox3i();
    private static final BlockPos.MutableBlockPos TEMP_CURSOR = new BlockPos.MutableBlockPos();
    private static final Vector3d TEMP_POSITION = new Vector3d();
    private final BearingSlowdownController slowdownController = new BearingSlowdownController();
    private final ObjectArrayList<Vector3d> centerMiningPositions = new ObjectArrayList();
    private final Set<BlockPos> visitedPositions = new ObjectOpenHashSet();
    private final AtomicInteger nextAvailableIndex = new AtomicInteger();
    private boolean disassemblySlowdown = false;
    private float rotationSpeed;
    private LevelAccelerator accelerator;
    private int clientRockCutters = 0;
    private int clientBlockAmount = 0;
    private boolean initialized = false;
    private boolean stalled = false;
    private int stalledRecoveryTimer = 0;
    private boolean insideMainTick;

    public BoreheadBearingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.movementMode.setValue(2);
        behaviours.remove(this.movementMode);
    }

    public float calculateStressApplied() {
        float stress;
        this.lastStressApplied = stress = super.calculateStressApplied() * (float)Math.max(this.getRockCuttingAmount(0), 1);
        return stress;
    }

    public void initialize() {
        super.initialize();
        this.accelerator = new LevelAccelerator(this.getLevel());
    }

    public void tick() {
        this.updateSpeed();
        this.insideMainTick = true;
        super.tick();
        this.insideMainTick = false;
        if (this.isVirtual()) {
            float angularSpeed = this.getAngularSpeed();
            float newAngle = this.angle + angularSpeed;
            this.angle = newAngle % 360.0f;
            return;
        }
        if (this.movedContraption != null && !this.movedContraption.isAlive()) {
            this.movedContraption = null;
        }
        if (!this.level.isClientSide) {
            this.visitedPositions.clear();
        }
        if (this.movedContraption == null) {
            this.angle = 0.0f;
            this.setRotationSpeed(0.0f);
            this.disassemblySlowdown = false;
            return;
        }
        if (!this.level.isClientSide) {
            this.updateMiningBlocks();
        }
    }

    public void updateMiningBlocks() {
        assert (this.getLevel() != null);
        assert (this.level != null);
        int timerBefore = this.stalledRecoveryTimer;
        if (this.stalledRecoveryTimer > 0) {
            --this.stalledRecoveryTimer;
        }
        if (this.stalledRecoveryTimer == 0 && timerBefore != this.stalledRecoveryTimer) {
            this.sendData();
        }
        if (this.isActive() && Math.abs(this.getSpeed()) > 0.1f) {
            double searchRadius = (Double)OffroadConfig.server().blocks.boreheadBearingSearchRadius.get();
            Vector3d localPoint = new Vector3d();
            for (Vector3d pos : this.centerMiningPositions) {
                TEMP_BOUNDING_BOX_DOUBLE.set(pos.x - searchRadius * 2.0, pos.y - searchRadius * 2.0, pos.z - searchRadius * 2.0, pos.x + searchRadius * 2.0, pos.y + searchRadius * 2.0, pos.z + searchRadius * 2.0);
                TEMP_BOUNDING_BOX_INT.set(TEMP_BOUNDING_BOX_DOUBLE);
                for (int x = TEMP_BOUNDING_BOX_INT.minX(); x <= TEMP_BOUNDING_BOX_INT.maxX(); ++x) {
                    for (int z = TEMP_BOUNDING_BOX_INT.minZ(); z <= TEMP_BOUNDING_BOX_INT.maxZ(); ++z) {
                        for (int y = TEMP_BOUNDING_BOX_INT.minY(); y <= TEMP_BOUNDING_BOX_INT.maxY(); ++y) {
                            double r = 1.0;
                            TEMP_POSITION.set((double)x + 0.5, (double)y + 0.5, (double)z + 0.5).sub((Vector3dc)pos, TEMP_POSITION);
                            TEMP_POSITION.absolute(localPoint).sub(searchRadius, searchRadius, searchRadius, localPoint).add(r, r, r);
                            double boxSDF = localPoint.max(IMMUT_ZERO, TEMP_POSITION).length() + Math.min(Math.max(localPoint.x, Math.max(localPoint.y, localPoint.z)), 0.0);
                            if (boxSDF - r > 0.0) continue;
                            TEMP_CURSOR.set(x, y, z);
                            BlockState state = this.accelerator.getBlockState((BlockPos)TEMP_CURSOR);
                            if (!BlockBreakingKineticBlockEntity.isBreakable((BlockState)state, (float)state.getDestroySpeed((BlockGetter)this.accelerator, (BlockPos)TEMP_CURSOR))) continue;
                            BlockPos cursor = TEMP_CURSOR.immutable();
                            MultiMiningServerManager.addOrRefreshPos(this.level, cursor, this);
                            this.visitedPositions.add(cursor);
                        }
                    }
                }
            }
        }
        this.accelerator.clearCache();
    }

    @Override
    public float getBreakingSpeed(Level level, BlockPos pos, BlockState state) {
        if (this.isActive()) {
            int multiplier = state.is(OffroadTags.Blocks.BOREHEAD_SUPER_EFFECTIVE) ? 100 : (state.is(OffroadTags.Blocks.BOREHEAD_EFFECTIVE) ? 10 : 5);
            double baseMiningSpeed = Math.clamp((double)Math.abs(this.getRotationSpeed() * (float)multiplier) / 100.0, 0.01, 16.0);
            float blockAmount = (float)this.visitedPositions.size() / 50.0f;
            if (blockAmount != 0.0f) {
                float inversePercentage = Math.abs(this.getSpeed() / (float)((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get()).intValue());
                baseMiningSpeed /= (double)Math.max(blockAmount * inversePercentage, 1.0f);
            }
            return (float)baseMiningSpeed;
        }
        return 0.0f;
    }

    @Override
    public BlockPos getLocation() {
        return this.isActive() ? this.worldPosition : null;
    }

    @Override
    public boolean isActive() {
        return this.initialized && this.getSpeed() != 0.0f && !this.isSlowingDown() && !this.isRemoved() && !this.isStalled();
    }

    @Override
    public void itemCallback(ItemStack stack) {
        BoreheadContraptionEntity bce = this.getContraptionEntity();
        if (bce != null) {
            BoreheadBearingContraption contraption = bce.getContraption();
            BoreheadAttachedStorage attachedStorage = (BoreheadAttachedStorage)contraption.getStorage();
            attachedStorage.setInsertAllowed(true);
            int inserted = this.getContraptionWrappedInventory().insertGeneral(ItemInfoWrapper.generateFromStack((ItemStack)stack), stack.getCount(), false);
            if (inserted == 0 && ((Boolean)OffroadConfig.server().blocks.boreheadBearingStallingEnabled.get()).booleanValue()) {
                this.setStalled(true);
            } else {
                stack.shrink(inserted);
            }
            attachedStorage.setInsertAllowed(false);
        }
    }

    public void updateSpeed() {
        assert (this.level != null);
        if (this.isSlowingDown()) {
            if (this.slowdownController.stepGoal() && !this.level.isClientSide) {
                this.disassemble();
                return;
            }
            this.setRotationSpeed(this.slowdownController.getSpeed(0.0f));
            this.angle = this.slowdownController.getAngle(0.0f);
        } else {
            float targetSpeed = this.isVirtual() ? super.getAngularSpeed() / 4.0f : super.getAngularSpeed() / OffroadConfig.server().blocks.boreheadBearingRotationDivisor.getF();
            int currentRockCutters = this.getRockCuttingAmount(10);
            float fullMass = currentRockCutters * 5;
            if (this.isStalled()) {
                targetSpeed /= 4.0f;
                fullMass = currentRockCutters;
            } else if (this.getSpeed() == 0.0f) {
                targetSpeed = 0.0f;
                fullMass = currentRockCutters;
            }
            if ((double)Math.abs(this.rotationSpeed) <= 0.05 && this.getSpeed() == 0.0f) {
                this.rotationSpeed = 0.0f;
                return;
            }
            this.rotationSpeed = Mth.lerp((float)(1.0f / fullMass), (float)this.rotationSpeed, (float)targetSpeed);
        }
    }

    private int getRockCuttingAmount(int minimumRockcuttingWheelAmount) {
        int rockCuttingWheelAmount;
        assert (this.level != null);
        int n = rockCuttingWheelAmount = this.level.isClientSide ? this.clientRockCutters : this.centerMiningPositions.size();
        if (minimumRockcuttingWheelAmount > 0) {
            rockCuttingWheelAmount = Math.max(rockCuttingWheelAmount, minimumRockcuttingWheelAmount);
        }
        return rockCuttingWheelAmount;
    }

    public float getInterpolatedAngle(float partialTicks) {
        if (!this.isVirtual() && this.isSlowingDown()) {
            return this.slowdownController.getAngle(partialTicks);
        }
        if (this.isVirtual()) {
            float angSpeed = this.getAngularSpeed();
            return Mth.lerp((float)partialTicks, (float)this.angle, (float)(this.angle + angSpeed));
        }
        return super.getInterpolatedAngle(partialTicks);
    }

    public float getAngularSpeed() {
        assert (this.level != null);
        if (this.insideMainTick && this.disassemblySlowdown) {
            float slowDownSpeed = this.slowdownController.getSpeed(1.0f);
            if (this.level.isClientSide) {
                slowDownSpeed *= ServerSpeedProvider.get();
                slowDownSpeed += this.clientAngleDiff / 3.0f;
            }
            return slowDownSpeed;
        }
        return this.rotationSpeed;
    }

    public void startUnstalling() {
        if (this.stalled) {
            this.setStalled(false);
            this.stalledRecoveryTimer = (Integer)OffroadConfig.server().blocks.boreheadBearingStallRecoveryTicks.get();
        }
    }

    public BoreheadContraptionEntity getContraptionEntity() {
        ControlledContraptionEntity controlledContraptionEntity = this.movedContraption;
        if (controlledContraptionEntity instanceof BoreheadContraptionEntity) {
            BoreheadContraptionEntity bce = (BoreheadContraptionEntity)controlledContraptionEntity;
            return bce;
        }
        return null;
    }

    @Nullable
    public InventoryLoaderWrapper getContraptionWrappedInventory() {
        ControlledContraptionEntity controlledContraptionEntity = this.movedContraption;
        if (controlledContraptionEntity instanceof BoreheadContraptionEntity) {
            BoreheadContraptionEntity bce = (BoreheadContraptionEntity)controlledContraptionEntity;
            return bce.getContraption().getSimWrappedStorage();
        }
        return null;
    }

    public void setAssembleNextTick(boolean assembleNextTick) {
        this.assembleNextTick = assembleNextTick;
    }

    public void assemble() {
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof BoreheadBearingBlock)) {
            return;
        }
        Direction direction = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        BoreheadBearingContraption contraption = new BoreheadBearingContraption(direction);
        try {
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
        this.movedContraption = BoreheadContraptionEntity.create(this.level, (IControlContraption)this, (Contraption)contraption);
        BlockPos anchor = this.worldPosition.relative(direction);
        this.movedContraption.setPos((double)anchor.getX(), (double)anchor.getY(), (double)anchor.getZ());
        this.movedContraption.setRotationAxis(direction.getAxis());
        this.level.addFreshEntity((Entity)this.movedContraption);
        this.running = true;
        this.angle = 0.0f;
        this.setRotationSpeed(0.0f);
        this.initializeContraption();
        this.updateGeneratedRotation();
        this.sendData();
    }

    public void attach(ControlledContraptionEntity contraption) {
        assert (this.level != null);
        BlockState blockState = this.getBlockState();
        if (!(contraption.getContraption() instanceof BearingContraption)) {
            return;
        }
        if (!blockState.hasProperty((Property)BlockStateProperties.FACING)) {
            return;
        }
        this.movedContraption = contraption;
        this.setChanged();
        BlockPos anchor = this.worldPosition.relative((Direction)blockState.getValue((Property)BlockStateProperties.FACING));
        this.movedContraption.setPos((double)anchor.getX(), (double)anchor.getY(), (double)anchor.getZ());
        if (!this.level.isClientSide) {
            this.running = true;
            this.sendData();
        }
        this.initializeContraption();
    }

    public void initializeContraption() {
        Object object;
        assert (this.level != null);
        if (!this.level.isClientSide && (object = this.movedContraption) instanceof BoreheadContraptionEntity) {
            BoreheadContraptionEntity bce = (BoreheadContraptionEntity)((Object)object);
            ((BoreheadAttachedStorage)bce.getContraption().getStorage()).attachBlockEntity(this);
        }
        if (!this.initialized && this.movedContraption != null && !this.level.isClientSide) {
            this.resetCenterMiningInfo();
            Map blocks = this.movedContraption.getContraption().getBlocks();
            for (Map.Entry entry : blocks.entrySet()) {
                if (!(((StructureTemplate.StructureBlockInfo)entry.getValue()).state().getBlock() instanceof RockCuttingWheelBlock)) continue;
                this.centerMiningPositions.add((Object)new Vector3d());
            }
            this.initialized = true;
        }
    }

    public void disassemble() {
        this.assembleNextTick = false;
        if (this.running && this.movedContraption != null) {
            this.angle = 0.0f;
            this.applyRotation();
            super.disassemble();
        }
        this.resetCenterMiningInfo();
        this.initialized = false;
        this.setStalled(false);
        KineticNetwork network = this.getOrCreateNetwork();
        if (network != null) {
            network.updateStressFor((KineticBlockEntity)this, this.calculateStressApplied());
        }
        this.sendData();
    }

    private void resetCenterMiningInfo() {
        this.centerMiningPositions.clear();
        this.nextAvailableIndex.set(0);
        this.visitedPositions.clear();
    }

    public void startDisassemblySlowdown() {
        if (!this.isSlowingDown() && this.movedContraption != null) {
            int rockCuttingAmount = this.getRockCuttingAmount(12);
            this.slowdownController.generate(1.0f + 3.5f * (float)(rockCuttingAmount * rockCuttingAmount), this.getInterpolatedAngle(0.0f), this.getRotationSpeed(), (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING), (Contraption)this.getMovedContraption().getContraption());
            this.disassemblySlowdown = true;
            this.sendData();
        }
    }

    public float getRotationSpeed() {
        return this.rotationSpeed;
    }

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public boolean isSlowingDown() {
        return this.disassemblySlowdown;
    }

    public int requestNewIndexAndIncrement(MovementContext context) {
        if (context.state.getBlock() instanceof RockCuttingWheelBlock && this.nextAvailableIndex.get() < this.centerMiningPositions.size()) {
            return this.nextAvailableIndex.getAndIncrement();
        }
        return -1;
    }

    public void updatePosition(int index, Vec3 originPosition) {
        if (index < this.centerMiningPositions.size()) {
            ((Vector3d)this.centerMiningPositions.get(index)).set(originPosition.x, originPosition.y, originPosition.z);
        }
    }

    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putFloat("RotationSpeed", this.getRotationSpeed());
        if (clientPacket) {
            compound.putInt("BlockBreakingAmount", this.visitedPositions.size());
        }
        compound.putBoolean("DisassemblySlowdown", this.disassemblySlowdown);
        if (this.disassemblySlowdown) {
            this.slowdownController.serializeIntoNBT(compound);
        }
        compound.putBoolean("ContraptionInitialized", this.initialized);
        compound.putBoolean("Stalled", this.stalled);
        compound.putInt("StalledRecoveryTimer", this.stalledRecoveryTimer);
        if (this.initialized) {
            if (clientPacket) {
                compound.putInt("ClientRockCutterAmount", this.centerMiningPositions.size());
            } else {
                compound.putInt("OriginPositionSize", this.centerMiningPositions.size());
                ListTag originListTag = new ListTag();
                for (Vector3d originPos : this.centerMiningPositions) {
                    CompoundTag originCompoundTag = new CompoundTag();
                    originCompoundTag.putDouble("x", originPos.x);
                    originCompoundTag.putDouble("y", originPos.y);
                    originCompoundTag.putDouble("z", originPos.z);
                    originCompoundTag.putInt("indexPosition", this.centerMiningPositions.indexOf((Object)originPos));
                    originListTag.add((Object)originCompoundTag);
                }
                compound.put("OriginPositions", (Tag)originListTag);
                compound.putInt("NextAvailableIndex", this.nextAvailableIndex.get());
            }
        }
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.setRotationSpeed(compound.getFloat("RotationSpeed"));
        if (clientPacket) {
            this.clientBlockAmount = compound.getInt("BlockBreakingAmount");
        }
        this.disassemblySlowdown = compound.getBoolean("DisassemblySlowdown");
        if (this.disassemblySlowdown) {
            this.slowdownController.deserializeFromNBT(compound);
        }
        this.initialized = compound.getBoolean("ContraptionInitialized");
        this.setStalled(compound.getBoolean("Stalled"));
        this.stalledRecoveryTimer = compound.getInt("StalledRecoveryTimer");
        if (this.initialized) {
            if (clientPacket) {
                this.clientRockCutters = compound.getInt("ClientRockCutterAmount");
            } else {
                for (int i = 0; i < compound.getInt("OriginPositionSize"); ++i) {
                    this.centerMiningPositions.add((Object)new Vector3d());
                }
                ListTag originTagList = compound.getList("OriginPositions", 10);
                for (Tag tag : originTagList) {
                    CompoundTag originCompoundTag = (CompoundTag)tag;
                    int indexPos = originCompoundTag.getInt("indexPosition");
                    Vector3d originPos = (Vector3d)this.centerMiningPositions.get(indexPos);
                    originPos.set(originCompoundTag.getDouble("x"), originCompoundTag.getDouble("y"), originCompoundTag.getDouble("z"));
                }
                this.nextAvailableIndex.set(compound.getInt("NextAvailableIndex"));
            }
        }
    }

    public BoreheadContraptionEntity getMovedContraption() {
        return (BoreheadContraptionEntity)this.movedContraption;
    }

    public float handleAxisModification(Direction direction) {
        return direction.getAxisDirection().getStep();
    }

    public boolean isStalled() {
        return this.stalled || this.stalledRecoveryTimer > 0;
    }

    public void setStalled(boolean stalled) {
        this.stalled = stalled;
    }

    public boolean addExceptionToTooltip(List<Component> tooltip) {
        boolean original = super.addExceptionToTooltip(tooltip);
        if (!original && this.isStalled()) {
            if (!tooltip.isEmpty()) {
                tooltip.add(CommonComponents.EMPTY);
            }
            OffroadLang.translate("exceptions.borehead_bearing.too_many_items", new Object[0]).style(ChatFormatting.GOLD).forGoggles(tooltip);
            Arrays.stream(OffroadLang.translate("exceptions.borehead_bearing.too_many_items_description", new Object[0]).component().getString().split("\n")).forEach(l -> TooltipHelper.cutStringTextComponent((String)l, (FontHelper.Palette)FontHelper.Palette.GRAY_AND_WHITE).forEach(c -> OffroadLang.builder().add(c).forGoggles(tooltip)));
            return true;
        }
        return original;
    }
}
