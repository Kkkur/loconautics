/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateHolder
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.entity;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllContraptionTypes;
import com.simibubi.create.api.behaviour.interaction.ConductorBlockInteractionBehavior;
import com.simibubi.create.api.behaviour.interaction.MovingInteractionBehaviour;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.Contraption;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.simibubi.create.content.contraptions.actors.trainControls.ControlsBlock;
import com.simibubi.create.content.contraptions.minecart.TrainCargoManager;
import com.simibubi.create.content.contraptions.render.ClientContraption;
import com.simibubi.create.content.trains.bogey.AbstractBogeyBlock;
import com.simibubi.create.content.trains.entity.ArrivalSoundQueue;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.foundation.collision.CollisionList;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class CarriageContraption
extends Contraption {
    private Direction assemblyDirection;
    private boolean forwardControls;
    private boolean backwardControls;
    public Couple<Boolean> blockConductors;
    public Map<BlockPos, Couple<Boolean>> conductorSeats = new HashMap<BlockPos, Couple<Boolean>>();
    public ArrivalSoundQueue soundQueue;
    protected MountedStorageManager storageProxy;
    private int bogeys;
    private boolean sidewaysControls;
    private BlockPos secondBogeyPos;
    private List<BlockPos> assembledBlockConductors = new ArrayList<BlockPos>();
    public int portalCutoffMin;
    public int portalCutoffMax;
    static final MountedStorageManager fallbackStorage = new MountedStorageManager();

    public CarriageContraption() {
        this.blockConductors = Couple.create((Object)false, (Object)false);
        this.soundQueue = new ArrivalSoundQueue();
        this.portalCutoffMin = Integer.MIN_VALUE;
        this.portalCutoffMax = Integer.MAX_VALUE;
        this.storage = new TrainCargoManager();
    }

    public void setSoundQueueOffset(int offset) {
        this.soundQueue.offset = offset;
    }

    public CarriageContraption(Direction assemblyDirection) {
        this();
        this.assemblyDirection = assemblyDirection;
        this.bogeys = 0;
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!this.searchMovedStructure(world, pos, null)) {
            return false;
        }
        if (this.blocks.size() <= 1) {
            return false;
        }
        if (this.bogeys == 0) {
            return false;
        }
        if (this.bogeys > 2) {
            throw new AssemblyException((Component)CreateLang.translateDirect("train_assembly.too_many_bogeys", this.bogeys));
        }
        if (this.sidewaysControls) {
            throw new AssemblyException((Component)CreateLang.translateDirect("train_assembly.sideways_controls", new Object[0]));
        }
        for (BlockPos blazePos : this.assembledBlockConductors) {
            for (Direction direction : Iterate.directionsInAxis((Direction.Axis)this.assemblyDirection.getAxis())) {
                if (!this.inControl(blazePos, direction)) continue;
                this.blockConductors.set(direction != this.assemblyDirection, (Object)true);
            }
        }
        for (BlockPos seatPos : this.getSeats()) {
            for (Direction direction : Iterate.directionsInAxis((Direction.Axis)this.assemblyDirection.getAxis())) {
                if (!this.inControl(seatPos, direction)) continue;
                this.conductorSeats.computeIfAbsent(seatPos, p -> Couple.create((Object)false, (Object)false)).set(direction != this.assemblyDirection, (Object)true);
            }
        }
        return true;
    }

    public boolean inControl(BlockPos pos, Direction direction) {
        BlockPos controlsPos = pos.relative(direction);
        if (!this.blocks.containsKey(controlsPos)) {
            return false;
        }
        StructureTemplate.StructureBlockInfo info = (StructureTemplate.StructureBlockInfo)this.blocks.get(controlsPos);
        if (!AllBlocks.TRAIN_CONTROLS.has(info.state())) {
            return false;
        }
        return info.state().getValue((Property)ControlsBlock.FACING) == direction.getOpposite();
    }

    public void swapStorageAfterAssembly(CarriageContraptionEntity cce) {
        Carriage carriage = cce.getCarriage();
        if (carriage.storage == null) {
            carriage.storage = (TrainCargoManager)this.storage;
            this.storage = new MountedStorageManager();
        }
        this.storageProxy = carriage.storage;
    }

    public void returnStorageForDisassembly(MountedStorageManager storage) {
        this.storage = storage;
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return false;
    }

    @Override
    protected Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture(Level world, BlockPos pos) {
        ConductorBlockInteractionBehavior conductor;
        MovingInteractionBehaviour behaviour;
        BlockState blockState = world.getBlockState(pos);
        if (ArrivalSoundQueue.isPlayable(blockState)) {
            int anchorCoord = VecHelper.getCoordinate((Vec3i)this.anchor, (Direction.Axis)this.assemblyDirection.getAxis());
            int posCoord = VecHelper.getCoordinate((Vec3i)pos, (Direction.Axis)this.assemblyDirection.getAxis());
            this.soundQueue.add((posCoord - anchorCoord) * this.assemblyDirection.getAxisDirection().getStep(), this.toLocalPos(pos));
        }
        if (blockState.getBlock() instanceof AbstractBogeyBlock) {
            ++this.bogeys;
            if (this.bogeys == 2) {
                this.secondBogeyPos = pos;
            }
        }
        if ((behaviour = MovingInteractionBehaviour.REGISTRY.get((StateHolder<Block, ?>)blockState)) instanceof ConductorBlockInteractionBehavior && (conductor = (ConductorBlockInteractionBehavior)behaviour).isValidConductor(blockState)) {
            this.assembledBlockConductors.add(this.toLocalPos(pos));
        }
        if (AllBlocks.TRAIN_CONTROLS.has(blockState)) {
            Direction facing = (Direction)blockState.getValue((Property)ControlsBlock.FACING);
            if (facing.getAxis() != this.assemblyDirection.getAxis()) {
                this.sidewaysControls = true;
            } else {
                boolean forwards;
                boolean bl = forwards = facing == this.assemblyDirection;
                if (forwards) {
                    this.forwardControls = true;
                } else {
                    this.backwardControls = true;
                }
            }
        }
        return super.capture(world, pos);
    }

    @Override
    public CompoundTag writeNBT(HolderLookup.Provider registries, boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(registries, spawnPacket);
        NBTHelper.writeEnum((CompoundTag)tag, (String)"AssemblyDirection", (Enum)this.getAssemblyDirection());
        tag.putBoolean("FrontControls", this.forwardControls);
        tag.putBoolean("BackControls", this.backwardControls);
        tag.putBoolean("FrontBlazeConductor", ((Boolean)this.blockConductors.getFirst()).booleanValue());
        tag.putBoolean("BackBlazeConductor", ((Boolean)this.blockConductors.getSecond()).booleanValue());
        ListTag list = NBTHelper.writeCompoundList(this.conductorSeats.entrySet(), e -> {
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("Pos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)e.getKey())));
            compoundTag.putBoolean("Forward", ((Boolean)((Couple)e.getValue()).getFirst()).booleanValue());
            compoundTag.putBoolean("Backward", ((Boolean)((Couple)e.getValue()).getSecond()).booleanValue());
            return compoundTag;
        });
        tag.put("ConductorSeats", (Tag)list);
        this.soundQueue.serialize(tag);
        return tag;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        this.assemblyDirection = (Direction)NBTHelper.readEnum((CompoundTag)nbt, (String)"AssemblyDirection", Direction.class);
        this.forwardControls = nbt.getBoolean("FrontControls");
        this.backwardControls = nbt.getBoolean("BackControls");
        this.blockConductors = Couple.create((Object)nbt.getBoolean("FrontBlazeConductor"), (Object)nbt.getBoolean("BackBlazeConductor"));
        this.conductorSeats.clear();
        NBTHelper.iterateCompoundList((ListTag)nbt.getList("ConductorSeats", 10), c -> this.conductorSeats.put(NBTHelper.readBlockPos((CompoundTag)c, (String)"Pos"), (Couple<Boolean>)Couple.create((Object)c.getBoolean("Forward"), (Object)c.getBoolean("Backward"))));
        this.soundQueue.deserialize(nbt);
        super.readNBT(world, nbt, spawnData);
    }

    @Override
    public boolean canBeStabilized(Direction facing, BlockPos localPos) {
        return false;
    }

    @Override
    public ContraptionType getType() {
        return (ContraptionType)AllContraptionTypes.CARRIAGE.value();
    }

    public Direction getAssemblyDirection() {
        return this.assemblyDirection;
    }

    public boolean hasForwardControls() {
        return this.forwardControls;
    }

    public boolean hasBackwardControls() {
        return this.backwardControls;
    }

    public BlockPos getSecondBogeyPos() {
        return this.secondBogeyPos;
    }

    @Override
    @Nullable
    public CollisionList getSimplifiedEntityColliders() {
        if (this.notInPortal()) {
            return super.getSimplifiedEntityColliders();
        }
        return null;
    }

    @Override
    public boolean isHiddenInPortal(BlockPos localPos) {
        if (this.notInPortal()) {
            return super.isHiddenInPortal(localPos);
        }
        Direction facing = this.assemblyDirection;
        Direction.Axis axis = facing.getClockWise().getAxis();
        int coord = axis.choose(localPos.getZ(), localPos.getY(), localPos.getX()) * -facing.getAxisDirection().getStep();
        return !this.withinVisible(coord) || this.atSeam(coord);
    }

    public boolean isHiddenInPortal(int posAlongMovementAxis) {
        if (this.notInPortal()) {
            return false;
        }
        return !this.withinVisible(posAlongMovementAxis) || this.atSeam(posAlongMovementAxis);
    }

    public boolean notInPortal() {
        return this.portalCutoffMin == Integer.MIN_VALUE && this.portalCutoffMax == Integer.MAX_VALUE;
    }

    public boolean atSeam(BlockPos localPos) {
        Direction facing = this.assemblyDirection;
        Direction.Axis axis = facing.getClockWise().getAxis();
        int coord = axis.choose(localPos.getZ(), localPos.getY(), localPos.getX()) * -facing.getAxisDirection().getStep();
        return this.atSeam(coord);
    }

    public boolean withinVisible(BlockPos localPos) {
        Direction facing = this.assemblyDirection;
        Direction.Axis axis = facing.getClockWise().getAxis();
        int coord = axis.choose(localPos.getZ(), localPos.getY(), localPos.getX()) * -facing.getAxisDirection().getStep();
        return this.withinVisible(coord);
    }

    public boolean atSeam(int posAlongMovementAxis) {
        return posAlongMovementAxis == this.portalCutoffMin || posAlongMovementAxis == this.portalCutoffMax;
    }

    public boolean withinVisible(int posAlongMovementAxis) {
        return posAlongMovementAxis > this.portalCutoffMin && posAlongMovementAxis < this.portalCutoffMax;
    }

    @Override
    public MountedStorageManager getStorage() {
        return this.storageProxy == null ? fallbackStorage : this.storageProxy;
    }

    @Override
    public void writeStorage(CompoundTag nbt, HolderLookup.Provider registries, boolean spawnPacket) {
        if (!spawnPacket) {
            return;
        }
        if (this.storageProxy != null) {
            this.storageProxy.write(nbt, registries, spawnPacket);
        }
    }

    @Override
    protected ClientContraption createClientContraption() {
        return new CarriageClientContraption(this);
    }

    static {
        fallbackStorage.initialize();
    }

    public class CarriageClientContraption
    extends ClientContraption {
        public final BitSet scratchBlockEntitiesOutsidePortal;

        public CarriageClientContraption(CarriageContraption contraption) {
            super(contraption);
            this.scratchBlockEntitiesOutsidePortal = new BitSet();
        }

        @Override
        public ClientContraption.RenderedBlocks getRenderedBlocks() {
            if (CarriageContraption.this.notInPortal()) {
                return super.getRenderedBlocks();
            }
            HashMap values = new HashMap();
            CarriageContraption.this.blocks.forEach((pos, info) -> {
                if (CarriageContraption.this.withinVisible((BlockPos)pos)) {
                    values.put(pos, info.state());
                } else if (CarriageContraption.this.atSeam((BlockPos)pos)) {
                    values.put(pos, Blocks.PURPLE_STAINED_GLASS.defaultBlockState());
                }
            });
            return new ClientContraption.RenderedBlocks(pos -> values.getOrDefault(pos, Blocks.AIR.defaultBlockState()), values.keySet());
        }

        @Override
        public BlockEntity readBlockEntity(Level level, StructureTemplate.StructureBlockInfo info, boolean legacy) {
            AbstractBogeyBlock bogey;
            Block block = info.state().getBlock();
            if (block instanceof AbstractBogeyBlock && !(bogey = (AbstractBogeyBlock)block).captureBlockEntityForTrain()) {
                return null;
            }
            return super.readBlockEntity(level, info, legacy);
        }

        @Override
        public BitSet getAndAdjustShouldRenderBlockEntities() {
            if (CarriageContraption.this.notInPortal()) {
                return super.getAndAdjustShouldRenderBlockEntities();
            }
            this.scratchBlockEntitiesOutsidePortal.clear();
            this.scratchBlockEntitiesOutsidePortal.or(this.shouldRenderBlockEntities);
            for (int i = 0; i < this.renderedBlockEntityView.size(); ++i) {
                BlockEntity be = (BlockEntity)this.renderedBlockEntityView.get(i);
                if (!CarriageContraption.this.isHiddenInPortal(be.getBlockPos())) continue;
                this.scratchBlockEntitiesOutsidePortal.clear(i);
            }
            return this.scratchBlockEntitiesOutsidePortal;
        }
    }
}
