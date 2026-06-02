/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.levelWrappers.SchematicLevel
 *  net.createmod.catnip.math.BBHelper
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
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BedPart
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 */
package com.simibubi.create.content.schematics;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.Create;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.schematics.SchematicItem;
import com.simibubi.create.content.schematics.cannon.MaterialChecklist;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.blockEntity.IMergeableBE;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import net.createmod.catnip.levelWrappers.SchematicLevel;
import net.createmod.catnip.math.BBHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class SchematicPrinter {
    private boolean schematicLoaded;
    private boolean isErrored;
    private SchematicLevel blockReader;
    private BlockPos schematicAnchor;
    private BlockPos currentPos;
    private int printingEntityIndex = -1;
    private PrintStage printStage = PrintStage.BLOCKS;
    private List<BlockPos> deferredBlocks = new LinkedList<BlockPos>();

    public void fromTag(CompoundTag compound, boolean clientPacket) {
        if (compound.contains("CurrentPos")) {
            this.currentPos = NBTHelper.readBlockPos((CompoundTag)compound, (String)"CurrentPos");
        }
        if (clientPacket) {
            this.schematicLoaded = false;
            if (compound.contains("Anchor")) {
                this.schematicAnchor = NBTHelper.readBlockPos((CompoundTag)compound, (String)"Anchor");
                this.schematicLoaded = true;
            }
        }
        this.printingEntityIndex = compound.getInt("EntityProgress");
        this.printStage = PrintStage.valueOf(compound.getString("PrintStage"));
        compound.getList("DeferredBlocks", 10).stream().map(p -> NBTHelper.readBlockPos((CompoundTag)((CompoundTag)p), (String)"Pos")).collect(Collectors.toCollection(() -> this.deferredBlocks));
    }

    public void write(CompoundTag compound) {
        if (this.currentPos != null) {
            compound.put("CurrentPos", NbtUtils.writeBlockPos((BlockPos)this.currentPos));
        }
        if (this.schematicAnchor != null) {
            compound.put("Anchor", NbtUtils.writeBlockPos((BlockPos)this.schematicAnchor));
        }
        compound.putInt("EntityProgress", this.printingEntityIndex);
        compound.putString("PrintStage", this.printStage.name());
        ListTag tagDeferredBlocks = new ListTag();
        for (BlockPos p : this.deferredBlocks) {
            CompoundTag tag = new CompoundTag();
            tag.put("Pos", NbtUtils.writeBlockPos((BlockPos)p));
            tagDeferredBlocks.add((Object)tag);
        }
        compound.put("DeferredBlocks", (Tag)tagDeferredBlocks);
    }

    public void loadSchematic(ItemStack blueprint, Level originalWorld, boolean processNBT) {
        if (!blueprint.has(AllDataComponents.SCHEMATIC_ANCHOR) || !blueprint.has(AllDataComponents.SCHEMATIC_DEPLOYED)) {
            return;
        }
        StructureTemplate activeTemplate = SchematicItem.loadSchematic(originalWorld, blueprint);
        StructurePlaceSettings settings = SchematicItem.getSettings(blueprint, processNBT);
        this.schematicAnchor = (BlockPos)blueprint.get(AllDataComponents.SCHEMATIC_ANCHOR);
        this.blockReader = new SchematicLevel(this.schematicAnchor, originalWorld);
        try {
            activeTemplate.placeInWorld((ServerLevelAccessor)this.blockReader, this.schematicAnchor, this.schematicAnchor, settings, this.blockReader.getRandom(), 2);
        }
        catch (Exception e) {
            Create.LOGGER.error("Failed to load Schematic for Printing", (Throwable)e);
            this.schematicLoaded = true;
            this.isErrored = true;
            return;
        }
        BlockPos extraBounds = StructureTemplate.calculateRelativePosition((StructurePlaceSettings)settings, (BlockPos)new BlockPos(activeTemplate.getSize()).offset(-1, -1, -1));
        this.blockReader.setBounds(BBHelper.encapsulate((BoundingBox)this.blockReader.getBounds(), (BlockPos)extraBounds));
        StructureTransform transform = new StructureTransform(settings.getRotationPivot(), Direction.Axis.Y, settings.getRotation(), settings.getMirror());
        for (BlockEntity be : this.blockReader.getBlockEntities()) {
            transform.apply(be);
        }
        this.printingEntityIndex = -1;
        this.printStage = PrintStage.BLOCKS;
        this.deferredBlocks.clear();
        BoundingBox bounds = this.blockReader.getBounds();
        this.currentPos = new BlockPos(bounds.minX() - 1, bounds.minY(), bounds.minZ());
        this.schematicLoaded = true;
    }

    public void resetSchematic() {
        this.schematicLoaded = false;
        this.schematicAnchor = null;
        this.isErrored = false;
        this.currentPos = null;
        this.blockReader = null;
        this.printingEntityIndex = -1;
        this.printStage = PrintStage.BLOCKS;
        this.deferredBlocks.clear();
    }

    public boolean isLoaded() {
        return this.schematicLoaded;
    }

    public boolean isErrored() {
        return this.isErrored;
    }

    public BlockPos getCurrentTarget() {
        if (!this.isLoaded() || this.isErrored()) {
            return null;
        }
        return this.schematicAnchor.offset((Vec3i)this.currentPos);
    }

    public PrintStage getPrintStage() {
        return this.printStage;
    }

    public BlockPos getAnchor() {
        return this.schematicAnchor;
    }

    public boolean isWorldEmpty() {
        return this.blockReader.getAllPositions().isEmpty();
    }

    public void handleCurrentTarget(BlockTargetHandler blockHandler, EntityTargetHandler entityHandler) {
        BlockPos target = this.getCurrentTarget();
        if (this.printStage == PrintStage.ENTITIES) {
            Entity entity = (Entity)this.blockReader.getEntityList().get(this.printingEntityIndex);
            entityHandler.handle(target, entity);
        } else {
            BlockState blockState = BlockHelper.setZeroAge(this.blockReader.getBlockState(target));
            BlockEntity blockEntity = this.blockReader.getBlockEntity(target);
            blockHandler.handle(target, blockState, blockEntity);
        }
    }

    public boolean shouldPlaceCurrent(Level world) {
        return this.shouldPlaceCurrent(world, (a, b, c, d, e, f) -> true);
    }

    public boolean shouldPlaceCurrent(Level world, PlacementPredicate predicate) {
        if (world == null) {
            return false;
        }
        if (this.printStage == PrintStage.ENTITIES) {
            return true;
        }
        return this.shouldPlaceBlock(world, predicate, this.getCurrentTarget());
    }

    /*
     * Unable to fully structure code
     */
    public boolean shouldPlaceBlock(Level world, PlacementPredicate predicate, BlockPos pos) {
        state = BlockHelper.setZeroAge(this.blockReader.getBlockState(pos));
        blockEntity = this.blockReader.getBlockEntity(pos);
        toReplace = world.getBlockState(pos);
        toReplaceBE = world.getBlockEntity(pos);
        toReplaceOther = null;
        if (state.hasProperty((Property)BlockStateProperties.BED_PART) && state.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING) && state.getValue((Property)BlockStateProperties.BED_PART) == BedPart.FOOT) {
            toReplaceOther = world.getBlockState(pos.relative((Direction)state.getValue((Property)BlockStateProperties.HORIZONTAL_FACING)));
        }
        if (state.hasProperty((Property)BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue((Property)BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            toReplaceOther = world.getBlockState(pos.above());
        }
        if (blockEntity == null || !(toReplaceBE instanceof IMergeableBE)) ** GOTO lbl-1000
        mergeBE = (IMergeableBE)toReplaceBE;
        if (toReplaceBE.getType().equals(blockEntity.getType())) {
            v0 = true;
        } else lbl-1000:
        // 2 sources

        {
            v0 = mergeTEs = false;
        }
        if (!world.isLoaded(pos)) {
            return false;
        }
        if (!world.getWorldBorder().isWithinBounds(pos)) {
            return false;
        }
        if (toReplace == state && !mergeTEs) {
            return false;
        }
        if (toReplace.getDestroySpeed((BlockGetter)world, pos) == -1.0f || toReplaceOther != null && toReplaceOther.getDestroySpeed((BlockGetter)world, pos) == -1.0f) {
            return false;
        }
        isNormalCube = state.isRedstoneConductor((BlockGetter)this.blockReader, this.currentPos);
        return predicate.shouldPlace(pos, state, blockEntity, toReplace, toReplaceOther, isNormalCube);
    }

    public ItemRequirement getCurrentRequirement() {
        if (this.printStage == PrintStage.ENTITIES) {
            return ItemRequirement.of((Entity)this.blockReader.getEntityList().get(this.printingEntityIndex));
        }
        BlockPos target = this.getCurrentTarget();
        BlockState blockState = BlockHelper.setZeroAge(this.blockReader.getBlockState(target));
        BlockEntity blockEntity = null;
        if (blockState.hasBlockEntity()) {
            blockEntity = ((EntityBlock)blockState.getBlock()).newBlockEntity(target, blockState);
            CompoundTag data = BlockHelper.prepareBlockEntityData((Level)this.blockReader, blockState, this.blockReader.getBlockEntity(target));
            if (blockEntity != null && data != null) {
                blockEntity.loadWithComponents(data, (HolderLookup.Provider)this.blockReader.registryAccess());
            }
        }
        return ItemRequirement.of(blockState, blockEntity);
    }

    public int markAllBlockRequirements(MaterialChecklist checklist, Level world, PlacementPredicate predicate) {
        int blocksToPlace = 0;
        for (BlockPos pos : this.blockReader.getAllPositions()) {
            ItemRequirement requirement;
            BlockPos relPos = pos.offset((Vec3i)this.schematicAnchor);
            BlockState required = this.blockReader.getBlockState(relPos);
            BlockEntity requiredBE = this.blockReader.getBlockEntity(relPos);
            if (!world.isLoaded(pos.offset((Vec3i)this.schematicAnchor))) {
                checklist.warnBlockNotLoaded();
                continue;
            }
            if (!this.shouldPlaceBlock(world, predicate, relPos) || (requirement = ItemRequirement.of(required, requiredBE)).isEmpty() || requirement.isInvalid()) continue;
            checklist.require(requirement);
            ++blocksToPlace;
        }
        return blocksToPlace;
    }

    public void markAllEntityRequirements(MaterialChecklist checklist) {
        for (Entity entity : this.blockReader.getEntityList()) {
            ItemRequirement requirement = ItemRequirement.of(entity);
            if (requirement.isEmpty()) {
                return;
            }
            if (requirement.isInvalid()) {
                return;
            }
            checklist.require(requirement);
        }
    }

    public boolean advanceCurrentPos() {
        List entities = this.blockReader.getEntityList();
        do {
            if (this.printStage == PrintStage.BLOCKS) {
                while (this.tryAdvanceCurrentPos()) {
                    this.deferredBlocks.add(this.currentPos);
                }
            }
            if (this.printStage == PrintStage.DEFERRED_BLOCKS) {
                if (this.deferredBlocks.isEmpty()) {
                    this.printStage = PrintStage.ENTITIES;
                } else {
                    this.currentPos = this.deferredBlocks.remove(0);
                }
            }
            if (this.printStage != PrintStage.ENTITIES) continue;
            if (this.printingEntityIndex + 1 < entities.size()) {
                ++this.printingEntityIndex;
                this.currentPos = ((Entity)entities.get(this.printingEntityIndex)).blockPosition().subtract((Vec3i)this.schematicAnchor);
                continue;
            }
            return false;
        } while (!this.blockReader.getBounds().isInside((Vec3i)this.currentPos));
        return true;
    }

    public boolean tryAdvanceCurrentPos() {
        this.currentPos = this.currentPos.relative(Direction.EAST);
        BoundingBox bounds = this.blockReader.getBounds();
        BlockPos posInBounds = this.currentPos.offset(-bounds.minX(), -bounds.minY(), -bounds.minZ());
        if (posInBounds.getX() > bounds.getXSpan()) {
            this.currentPos = new BlockPos(bounds.minX(), this.currentPos.getY(), this.currentPos.getZ() + 1).west();
        }
        if (posInBounds.getZ() > bounds.getZSpan()) {
            this.currentPos = new BlockPos(this.currentPos.getX(), this.currentPos.getY() + 1, bounds.minZ()).west();
        }
        if (this.currentPos.getY() > bounds.getYSpan()) {
            this.printStage = PrintStage.DEFERRED_BLOCKS;
            return false;
        }
        return SchematicPrinter.shouldDeferBlock(this.blockReader.getBlockState(this.getCurrentTarget()));
    }

    public static boolean shouldDeferBlock(BlockState state) {
        return AllBlocks.GANTRY_CARRIAGE.has(state) || AllBlocks.MECHANICAL_ARM.has(state) || BlockMovementChecks.isBrittle(state);
    }

    public void sendBlockUpdates(Level level) {
        BoundingBox bounds = this.blockReader.getBounds();
        BlockPos.betweenClosedStream((BoundingBox)bounds.inflatedBy(1)).filter(pos -> !bounds.isInside((Vec3i)pos)).filter(pos -> level.isLoaded(pos.offset((Vec3i)this.schematicAnchor)) && level.getFluidState(pos.offset((Vec3i)this.schematicAnchor)).is((Fluid)Fluids.WATER)).forEach(pos -> level.scheduleTick(pos.offset((Vec3i)this.schematicAnchor), (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay((LevelReader)level)));
    }

    public static enum PrintStage {
        BLOCKS,
        DEFERRED_BLOCKS,
        ENTITIES;

    }

    @FunctionalInterface
    public static interface EntityTargetHandler {
        public void handle(BlockPos var1, Entity var2);
    }

    @FunctionalInterface
    public static interface BlockTargetHandler {
        public void handle(BlockPos var1, BlockState var2, BlockEntity var3);
    }

    @FunctionalInterface
    public static interface PlacementPredicate {
        public boolean shouldPlace(BlockPos var1, BlockState var2, BlockEntity var3, BlockState var4, BlockState var5, boolean var6);
    }
}
