/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.pulley;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;
import com.simibubi.create.content.contraptions.pulley.PulleyBlock;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.simibubi.create.content.redstone.thresholdSwitch.ThresholdSwitchObservable;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class PulleyBlockEntity
extends LinearActuatorBlockEntity
implements ThresholdSwitchObservable {
    protected int initialOffset;
    private float prevAnimatedOffset;
    protected BlockPos mirrorParent;
    protected List<BlockPos> mirrorChildren;
    public WeakReference<AbstractContraptionEntity> sharedMirrorContraption;

    public PulleyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        AbstractContraptionEntity ace;
        double expandY = -this.offset;
        if (this.sharedMirrorContraption != null && (ace = (AbstractContraptionEntity)((Object)this.sharedMirrorContraption.get())) != null) {
            expandY = ace.getY() - (double)this.worldPosition.getY();
        }
        return super.createRenderBoundingBox().expandTowards(0.0, expandY, 0.0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.PULLEY_MAXED);
    }

    @Override
    public void tick() {
        float prevOffset = this.offset;
        super.tick();
        if (this.level.isClientSide() && this.mirrorParent != null && (this.sharedMirrorContraption == null || this.sharedMirrorContraption.get() == null || !((AbstractContraptionEntity)((Object)this.sharedMirrorContraption.get())).isAlive())) {
            this.sharedMirrorContraption = null;
            BlockEntity blockEntity = this.level.getBlockEntity(this.mirrorParent);
            if (blockEntity instanceof PulleyBlockEntity) {
                PulleyBlockEntity pte = (PulleyBlockEntity)blockEntity;
                if (pte.movedContraption != null) {
                    this.sharedMirrorContraption = new WeakReference<AbstractContraptionEntity>(pte.movedContraption);
                }
            }
        }
        if (this.isVirtual()) {
            this.prevAnimatedOffset = this.offset;
        }
        this.invalidateRenderBoundingBox();
        if (prevOffset < 200.0f && this.offset >= 200.0f) {
            this.award(AllAdvancements.PULLEY_MAXED);
        }
    }

    @Override
    protected boolean isPassive() {
        return this.mirrorParent != null;
    }

    @Nullable
    public AbstractContraptionEntity getAttachedContraption() {
        return this.mirrorParent != null && this.sharedMirrorContraption != null ? (AbstractContraptionEntity)((Object)this.sharedMirrorContraption.get()) : this.movedContraption;
    }

    @Override
    protected void assemble() throws AssemblyException {
        BlockPos ropePos;
        BlockState ropeState;
        int i;
        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof PulleyBlock)) {
            return;
        }
        if (this.speed == 0.0f && this.mirrorParent == null) {
            return;
        }
        int maxLength = (Integer)AllConfigs.server().kinetics.maxRopeLength.get();
        for (i = 1; i <= maxLength && (AllBlocks.ROPE.has(ropeState = this.level.getBlockState(ropePos = this.worldPosition.below(i))) || AllBlocks.PULLEY_MAGNET.has(ropeState)); ++i) {
        }
        this.offset = i - 1;
        if (this.offset >= (float)this.getExtensionRange() && this.getSpeed() > 0.0f) {
            return;
        }
        if (this.offset <= 0.0f && this.getSpeed() < 0.0f) {
            return;
        }
        if (!this.level.isClientSide && this.mirrorParent == null) {
            this.needsContraption = false;
            BlockPos anchor = this.worldPosition.below(Mth.floor((float)(this.offset + 1.0f)));
            this.initialOffset = Mth.floor((float)this.offset);
            PulleyContraption contraption = new PulleyContraption(this.initialOffset);
            boolean canAssembleStructure = contraption.assemble(this.level, anchor);
            if (canAssembleStructure) {
                Direction movementDirection;
                Direction direction = movementDirection = this.getSpeed() > 0.0f ? Direction.DOWN : Direction.UP;
                if (ContraptionCollider.isCollidingWithWorld(this.level, contraption, anchor.relative(movementDirection), movementDirection)) {
                    canAssembleStructure = false;
                }
            }
            if (!canAssembleStructure && this.getSpeed() > 0.0f) {
                return;
            }
            this.removeRopes();
            if (!contraption.getBlocks().isEmpty()) {
                contraption.removeBlocksFromWorld(this.level, BlockPos.ZERO);
                this.movedContraption = ControlledContraptionEntity.create(this.level, this, contraption);
                this.movedContraption.setPos(anchor.getX(), anchor.getY(), anchor.getZ());
                this.level.addFreshEntity((Entity)this.movedContraption);
                this.forceMove = true;
                this.needsContraption = true;
                if (contraption.containsBlockBreakers()) {
                    this.award(AllAdvancements.CONTRAPTION_ACTORS);
                }
                for (BlockPos pos : contraption.createColliders(this.level, Direction.UP)) {
                    BlockEntity blockEntity;
                    if (pos.getY() != 0 || !((blockEntity = this.level.getBlockEntity(new BlockPos((pos = pos.offset((Vec3i)anchor)).getX(), this.worldPosition.getY(), pos.getZ()))) instanceof PulleyBlockEntity)) continue;
                    PulleyBlockEntity pbe = (PulleyBlockEntity)blockEntity;
                    pbe.startMirroringOther(this.worldPosition);
                }
            }
        }
        if (this.mirrorParent != null) {
            this.removeRopes();
        }
        this.clientOffsetDiff = 0.0f;
        this.running = true;
        this.sendData();
    }

    private void removeRopes() {
        for (int i = (int)this.offset; i > 0; --i) {
            BlockPos offset = this.worldPosition.below(i);
            BlockState oldState = this.level.getBlockState(offset);
            this.level.setBlock(offset, oldState.getFluidState().createLegacyBlock(), 66);
        }
    }

    @Override
    public void disassemble() {
        if (!this.running && this.movedContraption == null && this.mirrorParent == null) {
            return;
        }
        this.offset = this.getGridOffset(this.offset);
        if (this.movedContraption != null) {
            this.resetContraptionToOffset();
        }
        if (!this.level.isClientSide) {
            if (this.shouldCreateRopes()) {
                if (this.offset > 0.0f) {
                    BlockPos magnetPos = this.worldPosition.below((int)this.offset);
                    FluidState ifluidstate = this.level.getFluidState(magnetPos);
                    if (this.level.getBlockState(magnetPos).getDestroySpeed((BlockGetter)this.level, magnetPos) != -1.0f) {
                        this.level.destroyBlock(magnetPos, this.level.getBlockState(magnetPos).getCollisionShape((BlockGetter)this.level, magnetPos).isEmpty());
                        this.level.setBlock(magnetPos, (BlockState)AllBlocks.PULLEY_MAGNET.getDefaultState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(ifluidstate.getType() == Fluids.WATER)), 66);
                    }
                }
                boolean[] waterlog = new boolean[(int)this.offset];
                for (boolean destroyPass : Iterate.trueAndFalse) {
                    for (int i = 1; i <= (int)this.offset - 1; ++i) {
                        BlockPos ropePos = this.worldPosition.below(i);
                        if (this.level.getBlockState(ropePos).getDestroySpeed((BlockGetter)this.level, ropePos) == -1.0f) continue;
                        if (destroyPass) {
                            FluidState ifluidstate = this.level.getFluidState(ropePos);
                            waterlog[i] = ifluidstate.getType() == Fluids.WATER;
                            this.level.destroyBlock(ropePos, this.level.getBlockState(ropePos).getCollisionShape((BlockGetter)this.level, ropePos).isEmpty());
                            continue;
                        }
                        this.level.setBlock(this.worldPosition.below(i), (BlockState)AllBlocks.ROPE.getDefaultState().setValue((Property)BlockStateProperties.WATERLOGGED, (Comparable)Boolean.valueOf(waterlog[i])), 66);
                    }
                }
            }
            if (this.movedContraption != null && this.mirrorParent == null) {
                this.movedContraption.disassemble();
            }
            this.notifyMirrorsOfDisassembly();
        }
        if (this.movedContraption != null) {
            this.movedContraption.discard();
        }
        this.movedContraption = null;
        this.initialOffset = 0;
        this.running = false;
        this.sendData();
    }

    protected boolean shouldCreateRopes() {
        return !this.remove;
    }

    @Override
    protected Vec3 toPosition(float offset) {
        Contraption contraption = this.movedContraption.getContraption();
        if (contraption instanceof PulleyContraption) {
            PulleyContraption contraption2 = (PulleyContraption)contraption;
            return Vec3.atLowerCornerOf((Vec3i)contraption2.anchor).add(0.0, (double)((float)contraption2.getInitialOffset() - offset), 0.0);
        }
        return Vec3.ZERO;
    }

    @Override
    protected void visitNewPosition() {
        super.visitNewPosition();
        if (this.level.isClientSide) {
            return;
        }
        if (this.movedContraption != null) {
            return;
        }
        if (this.getSpeed() <= 0.0f) {
            return;
        }
        BlockPos posBelow = this.worldPosition.below((int)(this.offset + this.getMovementSpeed()) + 1);
        BlockState state = this.level.getBlockState(posBelow);
        if (!BlockMovementChecks.isMovementNecessary(state, this.level, posBelow)) {
            return;
        }
        if (BlockMovementChecks.isBrittle(state)) {
            return;
        }
        this.disassemble();
        this.assembleNextTick = true;
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.initialOffset = compound.getInt("InitialOffset");
        this.needsContraption = compound.getBoolean("NeedsContraption");
        super.read(compound, registries, clientPacket);
        BlockPos prevMirrorParent = this.mirrorParent;
        this.mirrorParent = null;
        if (compound.contains("MirrorParent")) {
            this.mirrorParent = NBTHelper.readBlockPos((CompoundTag)compound, (String)"MirrorParent");
        }
        this.mirrorChildren = null;
        if (compound.contains("MirrorChildren")) {
            this.mirrorChildren = NBTHelper.readCompoundList((ListTag)compound.getList("MirrorChildren", 10), t -> NBTHelper.readBlockPos((CompoundTag)t, (String)"Pos"));
        }
        if (this.mirrorParent != null) {
            this.offset = 0.0f;
            if (prevMirrorParent == null || !prevMirrorParent.equals((Object)this.mirrorParent)) {
                this.sharedMirrorContraption = null;
            }
        }
        if (this.mirrorParent == null) {
            this.sharedMirrorContraption = null;
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.putInt("InitialOffset", this.initialOffset);
        super.write(compound, registries, clientPacket);
        if (this.mirrorParent != null) {
            compound.put("MirrorParent", NbtUtils.writeBlockPos((BlockPos)this.mirrorParent));
        }
        if (this.mirrorChildren != null) {
            compound.put("MirrorChildren", (Tag)NBTHelper.writeCompoundList(this.mirrorChildren, p -> {
                CompoundTag tag = new CompoundTag();
                tag.put("Pos", NbtUtils.writeBlockPos((BlockPos)p));
                return tag;
            }));
        }
    }

    public void startMirroringOther(BlockPos parent) {
        if (parent.equals((Object)this.worldPosition)) {
            return;
        }
        BlockEntity blockEntity = this.level.getBlockEntity(parent);
        if (!(blockEntity instanceof PulleyBlockEntity)) {
            return;
        }
        PulleyBlockEntity pbe = (PulleyBlockEntity)blockEntity;
        if (pbe.getType() != this.getType()) {
            return;
        }
        if (pbe.mirrorChildren == null) {
            pbe.mirrorChildren = new ArrayList<BlockPos>();
        }
        pbe.mirrorChildren.add(this.worldPosition);
        pbe.notifyUpdate();
        this.mirrorParent = parent;
        try {
            this.assemble();
        }
        catch (AssemblyException assemblyException) {
            // empty catch block
        }
        this.notifyUpdate();
    }

    public void notifyMirrorsOfDisassembly() {
        if (this.mirrorChildren == null) {
            return;
        }
        for (BlockPos blockPos : this.mirrorChildren) {
            BlockEntity blockEntity = this.level.getBlockEntity(blockPos);
            if (!(blockEntity instanceof PulleyBlockEntity)) continue;
            PulleyBlockEntity pbe = (PulleyBlockEntity)blockEntity;
            pbe.offset = this.offset;
            pbe.disassemble();
            pbe.mirrorParent = null;
            pbe.notifyUpdate();
        }
        this.mirrorChildren.clear();
        this.notifyUpdate();
    }

    @Override
    protected int getExtensionRange() {
        return Math.max(0, Math.min((Integer)AllConfigs.server().kinetics.maxRopeLength.get(), this.worldPosition.getY() - 1 - this.level.getMinBuildHeight()));
    }

    @Override
    protected int getInitialOffset() {
        return this.initialOffset;
    }

    @Override
    protected Vec3 toMotionVector(float speed) {
        return new Vec3(0.0, (double)(-speed), 0.0);
    }

    @Override
    protected ValueBoxTransform getMovementModeSlot() {
        return new CenteredSideValueBoxTransform((state, d) -> d == Direction.UP);
    }

    @Override
    public float getInterpolatedOffset(float partialTicks) {
        if (this.isVirtual()) {
            return Mth.lerp((float)partialTicks, (float)this.prevAnimatedOffset, (float)this.offset);
        }
        boolean moving = this.running && (this.movedContraption == null || !this.movedContraption.isStalled());
        return super.getInterpolatedOffset(moving ? partialTicks : 0.5f);
    }

    public void animateOffset(float forcedOffset) {
        this.offset = forcedOffset;
    }

    public BlockPos getMirrorParent() {
        return this.mirrorParent;
    }

    @Override
    public int getCurrentValue() {
        return this.worldPosition.getY() - (int)this.getInterpolatedOffset(0.5f);
    }

    @Override
    public int getMinValue() {
        return this.level.getMinBuildHeight();
    }

    @Override
    public int getMaxValue() {
        return this.worldPosition.getY();
    }

    @Override
    public MutableComponent format(int value) {
        return CreateLang.translateDirect("gui.threshold_switch.pulley_y_level", value);
    }
}
