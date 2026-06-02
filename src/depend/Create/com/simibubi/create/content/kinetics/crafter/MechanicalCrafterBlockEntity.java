/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.common.Tags$Items
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.crafter;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.crafter.ConnectedInputHandler;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.RecipeGridHandler;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.edgeInteraction.EdgeInteractionBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import com.simibubi.create.foundation.item.SmartInventory;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.math.Pointing;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class MechanicalCrafterBlockEntity
extends KineticBlockEntity
implements TransformableBlockEntity {
    protected Inventory inventory;
    protected RecipeGridHandler.GroupedItems groupedItems = new RecipeGridHandler.GroupedItems();
    protected ConnectedInputHandler.ConnectedInput input = new ConnectedInputHandler.ConnectedInput();
    @Nullable
    protected IItemHandler invCap;
    protected boolean reRender;
    protected Phase phase;
    protected int countDown;
    protected boolean covered;
    protected boolean wasPoweredBefore;
    protected RecipeGridHandler.GroupedItems groupedItemsBeforeCraft;
    private InvManipulationBehaviour inserting;
    private EdgeInteractionBehaviour connectivity;
    private ItemStack scriptedResult = ItemStack.EMPTY;

    public MechanicalCrafterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.setLazyTickRate(20);
        this.phase = Phase.IDLE;
        this.groupedItemsBeforeCraft = new RecipeGridHandler.GroupedItems();
        this.inventory = new Inventory(this);
        this.wasPoweredBefore = true;
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.MECHANICAL_CRAFTER.get(), (be, context) -> be.getInvCapability());
    }

    protected IItemHandler getInvCapability() {
        if (this.invCap == null) {
            this.invCap = this.input.getItemHandler(this.getLevel(), this.getBlockPos());
        }
        return this.invCap;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.inserting = new InvManipulationBehaviour(this, this::getTargetFace);
        this.connectivity = new EdgeInteractionBehaviour(this, ConnectedInputHandler::toggleConnection).connectivity(ConnectedInputHandler::shouldConnect).require(item -> item.builtInRegistryHolder().is(Tags.Items.TOOLS_WRENCH));
        behaviours.add(this.inserting);
        behaviours.add(this.connectivity);
        this.registerAwardables(behaviours, AllAdvancements.CRAFTER, AllAdvancements.CRAFTER_LAZY);
    }

    @Override
    public void onSpeedChanged(float previousSpeed) {
        super.onSpeedChanged(previousSpeed);
        if (!Mth.equal((float)this.getSpeed(), (float)0.0f)) {
            this.award(AllAdvancements.CRAFTER);
            if (Math.abs(this.getSpeed()) < 5.0f) {
                this.award(AllAdvancements.CRAFTER_LAZY);
            }
        }
    }

    public void blockChanged() {
        this.removeBehaviour(InvManipulationBehaviour.TYPE);
        this.inserting = new InvManipulationBehaviour(this, this::getTargetFace);
        this.attachBehaviourLate(this.inserting);
    }

    public BlockFace getTargetFace(Level world, BlockPos pos, BlockState state) {
        return new BlockFace(pos, MechanicalCrafterBlock.getTargetDirection(state));
    }

    public Direction getTargetDirection() {
        return MechanicalCrafterBlock.getTargetDirection(this.getBlockState());
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        super.writeSafe(tag, registries);
        if (this.input == null) {
            return;
        }
        CompoundTag inputNBT = new CompoundTag();
        this.input.write(inputNBT);
        tag.put("ConnectedInput", (Tag)inputNBT);
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("Inventory", (Tag)this.inventory.serializeNBT(registries));
        CompoundTag inputNBT = new CompoundTag();
        this.input.write(inputNBT);
        compound.put("ConnectedInput", (Tag)inputNBT);
        CompoundTag groupedItemsNBT = new CompoundTag();
        this.groupedItems.write(groupedItemsNBT, registries);
        compound.put("GroupedItems", (Tag)groupedItemsNBT);
        compound.putString("Phase", this.phase.name());
        compound.putInt("CountDown", this.countDown);
        compound.putBoolean("Cover", this.covered);
        super.write(compound, registries, clientPacket);
        if (clientPacket && this.reRender) {
            compound.putBoolean("Redraw", true);
            this.reRender = false;
        }
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        Phase phaseBefore = this.phase;
        RecipeGridHandler.GroupedItems before = this.groupedItems;
        this.inventory.deserializeNBT(registries, compound.getCompound("Inventory"));
        this.input.read(compound.getCompound("ConnectedInput"));
        this.groupedItems = RecipeGridHandler.GroupedItems.read(compound.getCompound("GroupedItems"), registries);
        this.phase = Phase.IDLE;
        String name = compound.getString("Phase");
        for (Phase phase : Phase.values()) {
            if (!phase.name().equals(name)) continue;
            this.phase = phase;
        }
        this.countDown = compound.getInt("CountDown");
        this.covered = compound.getBoolean("Cover");
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        if (compound.contains("Redraw")) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 16);
        }
        if (phaseBefore != this.phase && this.phase == Phase.CRAFTING) {
            this.groupedItemsBeforeCraft = before;
        }
        if (phaseBefore == Phase.EXPORTING && this.phase == Phase.WAITING) {
            if (before.onlyEmptyItems()) {
                return;
            }
            Direction facing = (Direction)this.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
            Vec3 vec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal()).scale(0.75).add(VecHelper.getCenterOf((Vec3i)this.worldPosition));
            Direction targetDirection = MechanicalCrafterBlock.getTargetDirection(this.getBlockState());
            vec = vec.add(Vec3.atLowerCornerOf((Vec3i)targetDirection.getNormal()).scale(1.0));
            this.level.addParticle((ParticleOptions)ParticleTypes.CRIT, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.invalidateCapabilities();
    }

    public int getCountDownSpeed() {
        if (this.getSpeed() == 0.0f) {
            return 0;
        }
        return Mth.clamp((int)((int)Math.abs(this.getSpeed())), (int)4, (int)250);
    }

    @Override
    public void tick() {
        boolean runLogic;
        super.tick();
        if (this.phase == Phase.ACCEPTING) {
            return;
        }
        boolean onClient = this.level.isClientSide;
        boolean bl = runLogic = !onClient || this.isVirtual();
        if (this.wasPoweredBefore != this.level.hasNeighborSignal(this.worldPosition)) {
            this.wasPoweredBefore = this.level.hasNeighborSignal(this.worldPosition);
            if (this.wasPoweredBefore) {
                if (!runLogic) {
                    return;
                }
                this.checkCompletedRecipe(true);
            }
        }
        if (this.phase == Phase.ASSEMBLING) {
            this.countDown -= this.getCountDownSpeed();
            if (this.countDown < 0) {
                ItemStack result;
                this.countDown = 0;
                if (!runLogic) {
                    return;
                }
                if (RecipeGridHandler.getTargetingCrafter(this) != null) {
                    this.phase = Phase.EXPORTING;
                    this.countDown = this.groupedItems.onlyEmptyItems() ? 0 : 1000;
                    this.sendData();
                    return;
                }
                ItemStack itemStack = result = this.isVirtual() ? this.scriptedResult : RecipeGridHandler.tryToApplyRecipe(this.level, this.groupedItems);
                if (result != null) {
                    ArrayList containers = new ArrayList();
                    this.groupedItems.grid.values().forEach(stack -> {
                        if (stack.hasCraftingRemainingItem()) {
                            containers.add(stack.getCraftingRemainingItem().copy());
                        }
                    });
                    if (this.isVirtual()) {
                        this.groupedItemsBeforeCraft = this.groupedItems;
                    }
                    this.groupedItems = new RecipeGridHandler.GroupedItems(result);
                    for (int i = 0; i < containers.size(); ++i) {
                        ItemStack stack2 = (ItemStack)containers.get(i);
                        RecipeGridHandler.GroupedItems container = new RecipeGridHandler.GroupedItems();
                        container.grid.put((Pair<Integer, Integer>)Pair.of((Object)i, (Object)0), stack2);
                        container.mergeOnto(this.groupedItems, Pointing.LEFT);
                    }
                    this.phase = Phase.CRAFTING;
                    this.countDown = 2000;
                    this.sendData();
                    return;
                }
                this.ejectWholeGrid();
                return;
            }
        }
        if (this.phase == Phase.EXPORTING) {
            this.countDown -= this.getCountDownSpeed();
            if (this.countDown < 0) {
                this.countDown = 0;
                if (!runLogic) {
                    return;
                }
                MechanicalCrafterBlockEntity targetingCrafter = RecipeGridHandler.getTargetingCrafter(this);
                if (targetingCrafter == null) {
                    this.ejectWholeGrid();
                    return;
                }
                boolean empty = this.groupedItems.onlyEmptyItems();
                Pointing pointing = (Pointing)this.getBlockState().getValue(MechanicalCrafterBlock.POINTING);
                this.groupedItems.mergeOnto(targetingCrafter.groupedItems, pointing);
                this.groupedItems = new RecipeGridHandler.GroupedItems();
                float pitch = (float)(targetingCrafter.groupedItems.grid.size() * 1) / 16.0f + 0.5f;
                if (!empty) {
                    AllSoundEvents.CRAFTER_CLICK.playOnServer(this.level, (Vec3i)this.worldPosition, 1.0f, pitch);
                }
                this.phase = Phase.WAITING;
                this.countDown = 0;
                this.sendData();
                targetingCrafter.continueIfAllPrecedingFinished();
                targetingCrafter.sendData();
                return;
            }
        }
        if (this.phase == Phase.CRAFTING) {
            if (onClient) {
                Direction facing = (Direction)this.getBlockState().getValue(MechanicalCrafterBlock.HORIZONTAL_FACING);
                float progress = (float)this.countDown / 2000.0f;
                Vec3 facingVec = Vec3.atLowerCornerOf((Vec3i)facing.getNormal());
                Vec3 vec = facingVec.scale(0.65).add(VecHelper.getCenterOf((Vec3i)this.worldPosition));
                Vec3 offset = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)0.125f).multiply(VecHelper.axisAlingedPlaneOf((Vec3)facingVec)).normalize().scale((double)(progress * 0.5f)).add(vec);
                if (progress > 0.5f) {
                    this.level.addParticle((ParticleOptions)ParticleTypes.CRIT, offset.x, offset.y, offset.z, 0.0, 0.0, 0.0);
                }
                if (!this.groupedItemsBeforeCraft.grid.isEmpty() && progress < 0.5f && this.groupedItems.grid.containsKey(Pair.of((Object)0, (Object)0))) {
                    ItemStack stack3 = this.groupedItems.grid.get(Pair.of((Object)0, (Object)0));
                    this.groupedItemsBeforeCraft = new RecipeGridHandler.GroupedItems();
                    for (int i = 0; i < 10; ++i) {
                        Vec3 randVec = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)this.level.random, (float)0.125f).multiply(VecHelper.axisAlingedPlaneOf((Vec3)facingVec)).normalize().scale(0.25);
                        Vec3 offset2 = randVec.add(vec);
                        randVec = randVec.scale((double)0.35f);
                        this.level.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, stack3), offset2.x, offset2.y, offset2.z, randVec.x, randVec.y, randVec.z);
                    }
                }
            }
            int prev = this.countDown;
            this.countDown -= this.getCountDownSpeed();
            if (this.countDown < 1000 && prev >= 1000) {
                AllSoundEvents.CRAFTER_CLICK.playOnServer(this.level, (Vec3i)this.worldPosition, 1.0f, 2.0f);
                AllSoundEvents.CRAFTER_CRAFT.playOnServer(this.level, (Vec3i)this.worldPosition);
            }
            if (this.countDown < 0) {
                this.countDown = 0;
                if (!runLogic) {
                    return;
                }
                this.tryInsert();
                return;
            }
        }
        if (this.phase == Phase.INSERTING) {
            if (runLogic && this.isTargetingBelt()) {
                this.tryInsert();
            }
            return;
        }
    }

    protected boolean isTargetingBelt() {
        DirectBeltInputBehaviour behaviour = this.getTargetingBelt();
        return behaviour != null && behaviour.canInsertFromSide(this.getTargetDirection());
    }

    protected DirectBeltInputBehaviour getTargetingBelt() {
        BlockPos targetPos = this.worldPosition.relative(this.getTargetDirection());
        return BlockEntityBehaviour.get((BlockGetter)this.level, targetPos, DirectBeltInputBehaviour.TYPE);
    }

    public void tryInsert() {
        if (!this.inserting.hasInventory() && !this.isTargetingBelt()) {
            this.ejectWholeGrid();
            return;
        }
        boolean chagedPhase = this.phase != Phase.INSERTING;
        LinkedList<Pair<Integer, Integer>> inserted = new LinkedList<Pair<Integer, Integer>>();
        DirectBeltInputBehaviour behaviour = this.getTargetingBelt();
        for (Map.Entry<Pair<Integer, Integer>, ItemStack> entry : this.groupedItems.grid.entrySet()) {
            ItemStack remainder;
            Pair<Integer, Integer> pair = entry.getKey();
            ItemStack stack = entry.getValue();
            BlockFace face = this.getTargetFace(this.level, this.worldPosition, this.getBlockState());
            ItemStack itemStack = remainder = behaviour == null ? this.inserting.insert(stack.copy()) : behaviour.handleInsertion(stack, face.getFace(), false);
            if (!remainder.isEmpty()) {
                stack.setCount(remainder.getCount());
                continue;
            }
            inserted.add(pair);
        }
        inserted.forEach(this.groupedItems.grid::remove);
        if (this.groupedItems.grid.isEmpty()) {
            this.ejectWholeGrid();
        } else {
            this.phase = Phase.INSERTING;
        }
        if (!inserted.isEmpty() || chagedPhase) {
            this.sendData();
        }
    }

    public void ejectWholeGrid() {
        List<MechanicalCrafterBlockEntity> chain = RecipeGridHandler.getAllCraftersOfChain(this);
        if (chain == null) {
            return;
        }
        chain.forEach(MechanicalCrafterBlockEntity::eject);
    }

    public void eject() {
        BlockState blockState = this.getBlockState();
        boolean present = AllBlocks.MECHANICAL_CRAFTER.has(blockState);
        Vec3 vec = present ? Vec3.atLowerCornerOf((Vec3i)((Direction)blockState.getValue(HorizontalKineticBlock.HORIZONTAL_FACING)).getNormal()).scale(0.75) : Vec3.ZERO;
        Vec3 ejectPos = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(vec);
        this.groupedItems.grid.forEach((pair, stack) -> this.dropItem(ejectPos, (ItemStack)stack));
        if (!this.inventory.getItem(0).isEmpty()) {
            this.dropItem(ejectPos, this.inventory.getItem(0));
        }
        this.phase = Phase.IDLE;
        this.groupedItems = new RecipeGridHandler.GroupedItems();
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        this.sendData();
    }

    public void dropItem(Vec3 ejectPos, ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.level, ejectPos.x, ejectPos.y, ejectPos.z, stack);
        itemEntity.setDefaultPickUpDelay();
        this.level.addFreshEntity((Entity)itemEntity);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        if (this.phase == Phase.IDLE && this.craftingItemPresent()) {
            this.checkCompletedRecipe(false);
        }
        if (this.phase == Phase.INSERTING) {
            this.tryInsert();
        }
    }

    public boolean craftingItemPresent() {
        return !this.inventory.getItem(0).isEmpty();
    }

    public boolean craftingItemOrCoverPresent() {
        return !this.inventory.getItem(0).isEmpty() || this.covered;
    }

    public void checkCompletedRecipe(boolean poweredStart) {
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (this.level.isClientSide && !this.isVirtual()) {
            return;
        }
        List<MechanicalCrafterBlockEntity> chain = RecipeGridHandler.getAllCraftersOfChainIf(this, poweredStart ? MechanicalCrafterBlockEntity::craftingItemPresent : MechanicalCrafterBlockEntity::craftingItemOrCoverPresent, poweredStart);
        if (chain == null) {
            return;
        }
        chain.forEach(MechanicalCrafterBlockEntity::begin);
    }

    protected void begin() {
        this.phase = Phase.ACCEPTING;
        this.groupedItems = new RecipeGridHandler.GroupedItems(this.inventory.getItem(0));
        this.inventory.setStackInSlot(0, ItemStack.EMPTY);
        if (RecipeGridHandler.getPrecedingCrafters(this).isEmpty()) {
            this.phase = Phase.ASSEMBLING;
            this.countDown = 1;
        }
        this.sendData();
    }

    protected void continueIfAllPrecedingFinished() {
        List<MechanicalCrafterBlockEntity> preceding = RecipeGridHandler.getPrecedingCrafters(this);
        if (preceding == null) {
            this.ejectWholeGrid();
            return;
        }
        for (MechanicalCrafterBlockEntity blockEntity : preceding) {
            if (blockEntity.phase == Phase.WAITING) continue;
            return;
        }
        this.phase = Phase.ASSEMBLING;
        this.countDown = 1;
    }

    public void connectivityChanged() {
        this.reRender = true;
        this.sendData();
        this.invCap = null;
        this.invalidateCapabilities();
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public void setScriptedResult(ItemStack scriptedResult) {
        this.scriptedResult = scriptedResult;
    }

    public ConnectedInputHandler.ConnectedInput getInput() {
        return this.input;
    }

    @Override
    public void transform(BlockEntity be, StructureTransform transform) {
        this.input.data.replaceAll(transform::applyWithoutOffset);
        this.notifyUpdate();
    }

    static enum Phase {
        IDLE,
        ACCEPTING,
        ASSEMBLING,
        EXPORTING,
        WAITING,
        CRAFTING,
        INSERTING;

    }

    public static class Inventory
    extends SmartInventory {
        private MechanicalCrafterBlockEntity blockEntity;

        public Inventory(MechanicalCrafterBlockEntity blockEntity) {
            super(1, blockEntity, 1, false);
            this.blockEntity = blockEntity;
            this.forbidExtraction();
            this.whenContentsChanged(slot -> {
                if (this.getItem((int)slot).isEmpty()) {
                    return;
                }
                if (blockEntity.phase == Phase.IDLE) {
                    blockEntity.checkCompletedRecipe(false);
                }
            });
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (this.blockEntity.phase != Phase.IDLE) {
                return stack;
            }
            if (this.blockEntity.covered) {
                return stack;
            }
            ItemStack insertItem = super.insertItem(slot, stack, simulate);
            if (insertItem.getCount() != stack.getCount() && !simulate) {
                this.blockEntity.getLevel().playSound(null, this.blockEntity.getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.25f, 0.5f);
            }
            return insertItem;
        }
    }
}
