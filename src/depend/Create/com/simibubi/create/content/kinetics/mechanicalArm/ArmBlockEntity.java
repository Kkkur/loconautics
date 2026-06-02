/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.visualization.VisualizationHelper
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.SectionPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.JukeboxBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.chunk.ChunkSource
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmAngleTarget;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.visualization.VisualizationHelper;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ArmBlockEntity
extends KineticBlockEntity
implements TransformableBlockEntity {
    List<ArmInteractionPoint> inputs = new ArrayList<ArmInteractionPoint>();
    List<ArmInteractionPoint> outputs = new ArrayList<ArmInteractionPoint>();
    ListTag interactionPointTag = null;
    float chasedPointProgress;
    int chasedPointIndex;
    ItemStack heldItem = ItemStack.EMPTY;
    Phase phase = Phase.SEARCH_INPUTS;
    boolean goggles;
    ArmAngleTarget previousTarget = ArmAngleTarget.NO_TARGET;
    LerpedFloat lowerArmAngle;
    LerpedFloat upperArmAngle;
    LerpedFloat baseAngle = LerpedFloat.angular();
    LerpedFloat headAngle;
    LerpedFloat clawAngle;
    float previousBaseAngle;
    boolean updateInteractionPoints;
    int tooltipWarmup;
    protected ScrollOptionBehaviour<SelectionMode> selectionMode;
    protected int lastInputIndex = -1;
    protected int lastOutputIndex = -1;
    protected boolean redstoneLocked;

    public ArmBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.baseAngle.startWithValue((double)this.previousTarget.baseAngle);
        this.lowerArmAngle = LerpedFloat.angular();
        this.lowerArmAngle.startWithValue((double)this.previousTarget.lowerArmAngle);
        this.upperArmAngle = LerpedFloat.angular();
        this.upperArmAngle.startWithValue((double)this.previousTarget.upperArmAngle);
        this.headAngle = LerpedFloat.angular();
        this.headAngle.startWithValue((double)this.previousTarget.headAngle);
        this.clawAngle = LerpedFloat.angular();
        this.previousBaseAngle = this.previousTarget.baseAngle;
        this.updateInteractionPoints = true;
        this.redstoneLocked = false;
        this.tooltipWarmup = 15;
        this.goggles = false;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.selectionMode = new ScrollOptionBehaviour<SelectionMode>(SelectionMode.class, (Component)CreateLang.translateDirect("logistics.when_multiple_outputs_available", new Object[0]), this, new SelectionModeValueBox(this));
        behaviours.add(this.selectionMode);
        this.registerAwardables(behaviours, AllAdvancements.ARM_BLAZE_BURNER, AllAdvancements.ARM_MANY_TARGETS, AllAdvancements.MECHANICAL_ARM, AllAdvancements.MUSICAL_ARM);
    }

    @Override
    public void tick() {
        super.tick();
        this.initInteractionPoints();
        boolean targetReached = this.tickMovementProgress();
        if (this.tooltipWarmup > 0) {
            --this.tooltipWarmup;
        }
        if (this.chasedPointProgress < 1.0f) {
            ArmInteractionPoint point;
            if (this.phase == Phase.MOVE_TO_INPUT && (point = this.getTargetedInteractionPoint()) != null) {
                point.keepAlive();
            }
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        if (this.phase == Phase.MOVE_TO_INPUT) {
            this.collectItem();
        } else if (this.phase == Phase.MOVE_TO_OUTPUT) {
            this.depositItem();
        } else if (this.phase == Phase.SEARCH_INPUTS || this.phase == Phase.DANCING) {
            this.searchForItem();
        }
        if (targetReached) {
            this.lazyTick();
        }
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide) {
            return;
        }
        if (this.chasedPointProgress < 0.5f) {
            return;
        }
        if (this.phase == Phase.SEARCH_INPUTS || this.phase == Phase.DANCING) {
            this.checkForMusic();
        }
        if (this.phase == Phase.SEARCH_OUTPUTS) {
            this.searchForDestination();
        }
    }

    private void checkForMusic() {
        boolean hasMusic = this.checkForMusicAmong(this.inputs) || this.checkForMusicAmong(this.outputs);
        if (hasMusic != (this.phase == Phase.DANCING)) {
            this.phase = hasMusic ? Phase.DANCING : Phase.SEARCH_INPUTS;
            this.setChanged();
            this.sendData();
        }
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(3.0);
    }

    private boolean checkForMusicAmong(List<ArmInteractionPoint> list) {
        for (ArmInteractionPoint armInteractionPoint : list) {
            BlockState state;
            if (!(armInteractionPoint instanceof AllArmInteractionPointTypes.JukeboxPoint) || !(state = this.level.getBlockState(armInteractionPoint.getPos())).getOptionalValue((Property)JukeboxBlock.HAS_RECORD).orElse(false).booleanValue()) continue;
            return true;
        }
        return false;
    }

    private boolean tickMovementProgress() {
        boolean targetReachedPreviously = this.chasedPointProgress >= 1.0f;
        this.chasedPointProgress += Math.min(256.0f, Math.abs(this.getSpeed())) / 1024.0f;
        if (this.chasedPointProgress > 1.0f) {
            this.chasedPointProgress = 1.0f;
        }
        if (!this.level.isClientSide) {
            return !targetReachedPreviously && this.chasedPointProgress >= 1.0f;
        }
        ArmInteractionPoint targetedInteractionPoint = this.getTargetedInteractionPoint();
        ArmAngleTarget previousTarget = this.previousTarget;
        ArmAngleTarget target = targetedInteractionPoint == null ? ArmAngleTarget.NO_TARGET : targetedInteractionPoint.getTargetAngles(this.worldPosition, this.isOnCeiling());
        this.baseAngle.setValue((double)AngleHelper.angleLerp((double)this.chasedPointProgress, (double)this.previousBaseAngle, (double)(target == ArmAngleTarget.NO_TARGET ? (double)this.previousBaseAngle : (double)target.baseAngle)));
        if (this.chasedPointProgress < 0.5f) {
            target = ArmAngleTarget.NO_TARGET;
        } else {
            previousTarget = ArmAngleTarget.NO_TARGET;
        }
        float progress = this.chasedPointProgress == 1.0f ? 1.0f : this.chasedPointProgress % 0.5f * 2.0f;
        this.lowerArmAngle.setValue((double)Mth.lerp((float)progress, (float)previousTarget.lowerArmAngle, (float)target.lowerArmAngle));
        this.upperArmAngle.setValue((double)Mth.lerp((float)progress, (float)previousTarget.upperArmAngle, (float)target.upperArmAngle));
        this.headAngle.setValue((double)AngleHelper.angleLerp((double)progress, (double)(previousTarget.headAngle % 360.0f), (double)(target.headAngle % 360.0f)));
        return false;
    }

    protected boolean isOnCeiling() {
        BlockState state = this.getBlockState();
        return this.hasLevel() && state.getOptionalValue((Property)ArmBlock.CEILING).orElse(false) != false;
    }

    @Override
    public void destroy() {
        super.destroy();
        if (!this.heldItem.isEmpty()) {
            Block.popResource((Level)this.level, (BlockPos)this.worldPosition, (ItemStack)this.heldItem);
        }
    }

    @Nullable
    private ArmInteractionPoint getTargetedInteractionPoint() {
        if (this.chasedPointIndex == -1) {
            return null;
        }
        if (this.phase == Phase.MOVE_TO_INPUT && this.chasedPointIndex < this.inputs.size()) {
            return this.inputs.get(this.chasedPointIndex);
        }
        if (this.phase == Phase.MOVE_TO_OUTPUT && this.chasedPointIndex < this.outputs.size()) {
            return this.outputs.get(this.chasedPointIndex);
        }
        return null;
    }

    protected void searchForItem() {
        int scanRange;
        if (this.redstoneLocked) {
            return;
        }
        boolean foundInput = false;
        int startIndex = this.selectionMode.get() == SelectionMode.PREFER_FIRST ? 0 : this.lastInputIndex + 1;
        int n = scanRange = this.selectionMode.get() == SelectionMode.FORCED_ROUND_ROBIN ? this.lastInputIndex + 2 : this.inputs.size();
        if (scanRange > this.inputs.size()) {
            scanRange = this.inputs.size();
        }
        block0: for (int i = startIndex; i < scanRange; ++i) {
            ArmInteractionPoint armInteractionPoint = this.inputs.get(i);
            if (!armInteractionPoint.isValid()) continue;
            for (int j = 0; j < armInteractionPoint.getSlotCount(this); ++j) {
                if (this.getDistributableAmount(armInteractionPoint, j) == 0) continue;
                this.selectIndex(true, i);
                foundInput = true;
                break block0;
            }
        }
        if (!foundInput && this.selectionMode.get() == SelectionMode.ROUND_ROBIN) {
            this.lastInputIndex = -1;
        }
        if (this.lastInputIndex == this.inputs.size() - 1) {
            this.lastInputIndex = -1;
        }
    }

    protected void searchForDestination() {
        int scanRange;
        ItemStack held = this.heldItem.copy();
        boolean foundOutput = false;
        int startIndex = this.selectionMode.get() == SelectionMode.PREFER_FIRST ? 0 : this.lastOutputIndex + 1;
        int n = scanRange = this.selectionMode.get() == SelectionMode.FORCED_ROUND_ROBIN ? this.lastOutputIndex + 2 : this.outputs.size();
        if (scanRange > this.outputs.size()) {
            scanRange = this.outputs.size();
        }
        for (int i = startIndex; i < scanRange; ++i) {
            ItemStack remainder;
            ArmInteractionPoint armInteractionPoint = this.outputs.get(i);
            if (!armInteractionPoint.isValid() || ItemStack.matches((ItemStack)(remainder = armInteractionPoint.insert(this, held, true)), (ItemStack)this.heldItem)) continue;
            this.selectIndex(false, i);
            foundOutput = true;
            break;
        }
        if (!foundOutput && this.selectionMode.get() == SelectionMode.ROUND_ROBIN) {
            this.lastOutputIndex = -1;
        }
        if (this.lastOutputIndex == this.outputs.size() - 1) {
            this.lastOutputIndex = -1;
        }
    }

    private void selectIndex(boolean input, int index) {
        this.phase = input ? Phase.MOVE_TO_INPUT : Phase.MOVE_TO_OUTPUT;
        this.chasedPointIndex = index;
        this.chasedPointProgress = 0.0f;
        if (input) {
            this.lastInputIndex = index;
        } else {
            this.lastOutputIndex = index;
        }
        this.sendData();
        this.setChanged();
    }

    protected int getDistributableAmount(ArmInteractionPoint armInteractionPoint, int i) {
        ItemStack remainder;
        ItemStack stack = armInteractionPoint.extract(this, i, true);
        if (ItemStack.isSameItem((ItemStack)stack, (ItemStack)(remainder = this.simulateInsertion(stack)))) {
            return stack.getCount() - remainder.getCount();
        }
        return stack.getCount();
    }

    private ItemStack simulateInsertion(ItemStack stack) {
        for (ArmInteractionPoint armInteractionPoint : this.outputs) {
            if (armInteractionPoint.isValid()) {
                stack = armInteractionPoint.insert(this, stack, true);
            }
            if (!stack.isEmpty()) continue;
            break;
        }
        return stack;
    }

    protected void depositItem() {
        ArmInteractionPoint armInteractionPoint = this.getTargetedInteractionPoint();
        if (armInteractionPoint != null && armInteractionPoint.isValid()) {
            ItemStack remainder;
            ItemStack toInsert = this.heldItem.copy();
            this.heldItem = remainder = armInteractionPoint.insert(this, toInsert, false);
            if (armInteractionPoint instanceof AllArmInteractionPointTypes.JukeboxPoint && remainder.isEmpty()) {
                this.award(AllAdvancements.MUSICAL_ARM);
            }
        }
        this.phase = this.heldItem.isEmpty() ? Phase.SEARCH_INPUTS : Phase.SEARCH_OUTPUTS;
        this.chasedPointProgress = 0.0f;
        this.chasedPointIndex = -1;
        this.sendData();
        this.setChanged();
        if (!this.level.isClientSide) {
            this.award(AllAdvancements.MECHANICAL_ARM);
        }
    }

    protected void collectItem() {
        ArmInteractionPoint armInteractionPoint = this.getTargetedInteractionPoint();
        if (armInteractionPoint != null && armInteractionPoint.isValid()) {
            for (int i = 0; i < armInteractionPoint.getSlotCount(this); ++i) {
                int amountExtracted = this.getDistributableAmount(armInteractionPoint, i);
                if (amountExtracted == 0) continue;
                ItemStack prevHeld = this.heldItem;
                this.heldItem = armInteractionPoint.extract(this, i, amountExtracted, false);
                this.phase = Phase.SEARCH_OUTPUTS;
                this.chasedPointProgress = 0.0f;
                this.chasedPointIndex = -1;
                this.sendData();
                this.setChanged();
                if (!ItemStack.isSameItem((ItemStack)this.heldItem, (ItemStack)prevHeld)) {
                    this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.125f, 0.5f + Create.RANDOM.nextFloat() * 0.25f);
                }
                return;
            }
        }
        this.phase = Phase.SEARCH_INPUTS;
        this.chasedPointProgress = 0.0f;
        this.chasedPointIndex = -1;
        this.sendData();
        this.setChanged();
    }

    public void redstoneUpdate() {
        if (this.level.isClientSide) {
            return;
        }
        boolean blockPowered = this.level.hasNeighborSignal(this.worldPosition);
        if (blockPowered == this.redstoneLocked) {
            return;
        }
        this.redstoneLocked = blockPowered;
        this.sendData();
        if (!this.redstoneLocked) {
            this.searchForItem();
        }
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        if (this.interactionPointTag == null) {
            return;
        }
        for (Tag tag : this.interactionPointTag) {
            ArmInteractionPoint.transformPos((CompoundTag)tag, transform);
        }
        this.notifyUpdate();
    }

    protected boolean isAreaActuallyLoaded(BlockPos center, int range) {
        if (!this.level.isAreaLoaded(center, range)) {
            return false;
        }
        if (this.level.isClientSide) {
            int minY = center.getY() - range;
            int maxY = center.getY() + range;
            if (maxY < this.level.getMinBuildHeight() || minY >= this.level.getMaxBuildHeight()) {
                return false;
            }
            int minX = center.getX() - range;
            int minZ = center.getZ() - range;
            int maxX = center.getX() + range;
            int maxZ = center.getZ() + range;
            int minChunkX = SectionPos.blockToSectionCoord((int)minX);
            int maxChunkX = SectionPos.blockToSectionCoord((int)maxX);
            int minChunkZ = SectionPos.blockToSectionCoord((int)minZ);
            int maxChunkZ = SectionPos.blockToSectionCoord((int)maxZ);
            ChunkSource chunkSource = this.level.getChunkSource();
            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX) {
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ) {
                    if (chunkSource.hasChunk(chunkX, chunkZ)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    protected void initInteractionPoints() {
        if (!this.updateInteractionPoints || this.interactionPointTag == null) {
            return;
        }
        if (!this.isAreaActuallyLoaded(this.worldPosition, ArmBlockEntity.getRange() + 1)) {
            return;
        }
        this.inputs.clear();
        this.outputs.clear();
        boolean hasBlazeBurner = false;
        for (Tag tag : this.interactionPointTag) {
            ArmInteractionPoint point = ArmInteractionPoint.deserialize((CompoundTag)tag, this.level, this.worldPosition);
            if (point == null) continue;
            if (point.getMode() == ArmInteractionPoint.Mode.DEPOSIT) {
                this.outputs.add(point);
            } else if (point.getMode() == ArmInteractionPoint.Mode.TAKE) {
                this.inputs.add(point);
            }
            hasBlazeBurner |= point instanceof AllArmInteractionPointTypes.BlazeBurnerPoint;
        }
        if (!this.level.isClientSide) {
            if (this.outputs.size() >= 10) {
                this.award(AllAdvancements.ARM_MANY_TARGETS);
            }
            if (hasBlazeBurner) {
                this.award(AllAdvancements.ARM_BLAZE_BURNER);
            }
        }
        this.updateInteractionPoints = false;
        this.sendData();
        this.setChanged();
    }

    public void writeInteractionPoints(CompoundTag compound) {
        if (this.updateInteractionPoints && this.interactionPointTag != null) {
            compound.put("InteractionPoints", (Tag)this.interactionPointTag);
        } else {
            ListTag pointsNBT = new ListTag();
            this.inputs.stream().map(aip -> aip.serialize(this.worldPosition)).forEach(arg_0 -> pointsNBT.add(arg_0));
            this.outputs.stream().map(aip -> aip.serialize(this.worldPosition)).forEach(arg_0 -> pointsNBT.add(arg_0));
            compound.put("InteractionPoints", (Tag)pointsNBT);
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        this.writeInteractionPoints(compound);
        NBTHelper.writeEnum((CompoundTag)compound, (String)"Phase", (Enum)this.phase);
        compound.putBoolean("Powered", this.redstoneLocked);
        compound.putBoolean("Goggles", this.goggles);
        compound.put("HeldItem", this.heldItem.saveOptional(registries));
        compound.putInt("TargetPointIndex", this.chasedPointIndex);
        compound.putFloat("MovementProgress", this.chasedPointProgress);
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        this.writeInteractionPoints(tag);
    }

    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        int previousIndex = this.chasedPointIndex;
        Phase previousPhase = this.phase;
        ListTag interactionPointTagBefore = this.interactionPointTag;
        super.read(tag, registries, clientPacket);
        this.heldItem = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("HeldItem"));
        this.phase = (Phase)NBTHelper.readEnum((CompoundTag)tag, (String)"Phase", Phase.class);
        this.chasedPointIndex = tag.getInt("TargetPointIndex");
        this.chasedPointProgress = tag.getFloat("MovementProgress");
        this.interactionPointTag = tag.getList("InteractionPoints", 10);
        this.redstoneLocked = tag.getBoolean("Powered");
        boolean hadGoggles = this.goggles;
        this.goggles = tag.getBoolean("Goggles");
        if (!clientPacket) {
            return;
        }
        if (hadGoggles != this.goggles && CatnipServices.PLATFORM.getEnv().isClient()) {
            Client.queueUpdate(this);
        }
        boolean ceiling = this.isOnCeiling();
        if (interactionPointTagBefore == null || interactionPointTagBefore.size() != this.interactionPointTag.size()) {
            this.updateInteractionPoints = true;
        }
        if (previousIndex != this.chasedPointIndex || previousPhase != this.phase) {
            ArmInteractionPoint targetedPoint;
            ArmInteractionPoint previousPoint = null;
            if (previousPhase == Phase.MOVE_TO_INPUT && previousIndex < this.inputs.size()) {
                previousPoint = this.inputs.get(previousIndex);
            }
            if (previousPhase == Phase.MOVE_TO_OUTPUT && previousIndex < this.outputs.size()) {
                previousPoint = this.outputs.get(previousIndex);
            }
            ArmAngleTarget armAngleTarget = this.previousTarget = previousPoint == null ? ArmAngleTarget.NO_TARGET : previousPoint.getTargetAngles(this.worldPosition, ceiling);
            if (previousPoint != null) {
                this.previousBaseAngle = this.previousTarget.baseAngle;
            }
            if ((targetedPoint = this.getTargetedInteractionPoint()) != null) {
                targetedPoint.updateCachedState();
            }
        }
    }

    public static int getRange() {
        return (Integer)AllConfigs.server().logistics.mechanicalArmRange.get();
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        if (isPlayerSneaking) {
            return false;
        }
        if (this.tooltipWarmup > 0) {
            return false;
        }
        if (!this.inputs.isEmpty()) {
            return false;
        }
        if (!this.outputs.isEmpty()) {
            return false;
        }
        TooltipHelper.addHint(tooltip, "hint.mechanical_arm_no_targets", new Object[0]);
        return true;
    }

    public void setLevel(Level level) {
        super.setLevel(level);
        for (ArmInteractionPoint input : this.inputs) {
            input.setLevel(level);
        }
        for (ArmInteractionPoint output : this.outputs) {
            output.setLevel(level);
        }
    }

    public static enum Phase {
        SEARCH_INPUTS,
        MOVE_TO_INPUT,
        SEARCH_OUTPUTS,
        MOVE_TO_OUTPUT,
        DANCING;

    }

    public static enum SelectionMode implements INamedIconOptions
    {
        ROUND_ROBIN(AllIcons.I_ARM_ROUND_ROBIN),
        FORCED_ROUND_ROBIN(AllIcons.I_ARM_FORCED_ROUND_ROBIN),
        PREFER_FIRST(AllIcons.I_ARM_PREFER_FIRST);

        private final String translationKey;
        private final AllIcons icon;

        private SelectionMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.mechanical_arm.selection_mode." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }

    private class SelectionModeValueBox
    extends CenteredSideValueBoxTransform {
        public SelectionModeValueBox(ArmBlockEntity armBlockEntity) {
            super((BlockState blockState, Direction direction) -> !direction.getAxis().isVertical());
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            int yPos = (Boolean)state.getValue((Property)ArmBlock.CEILING) != false ? 13 : 3;
            Vec3 location = VecHelper.voxelSpace((double)8.0, (double)yPos, (double)15.5);
            location = VecHelper.rotateCentered((Vec3)location, (double)AngleHelper.horizontalAngle((Direction)this.getSide()), (Direction.Axis)Direction.Axis.Y);
            return location;
        }

        @Override
        public float getScale() {
            return super.getScale();
        }
    }

    private static class Client {
        private Client() {
        }

        private static void queueUpdate(BlockEntity be) {
            VisualizationHelper.queueUpdate((BlockEntity)be);
        }
    }
}
