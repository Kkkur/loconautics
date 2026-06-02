/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeInput
 *  net.minecraft.world.item.crafting.SingleRecipeInput
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.IItemHandlerModifiable
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  net.neoforged.neoforge.items.wrapper.RecipeWrapper
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.deployer.BeltDeployerCallbacks;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.kinetics.deployer.DeployerFilterSlot;
import com.simibubi.create.content.kinetics.deployer.DeployerHandler;
import com.simibubi.create.content.kinetics.deployer.DeployerItemHandler;
import com.simibubi.create.content.kinetics.deployer.DeployerRecipeSearchEvent;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SyncedBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class DeployerBlockEntity
extends KineticBlockEntity
implements Clearable {
    protected State state;
    protected Mode mode;
    protected ItemStack heldItem;
    protected DeployerFakePlayer player;
    protected int timer;
    protected float reach;
    protected boolean fistBump = false;
    protected List<ItemStack> overflowItems = new ArrayList<ItemStack>();
    protected FilteringBehaviour filtering;
    protected boolean redstoneLocked = false;
    protected UUID owner;
    private IItemHandlerModifiable invHandler;
    private ListTag deferredInventoryList;
    private LerpedFloat animatedOffset;
    public BeltProcessingBehaviour processingBehaviour;
    ItemStackHandler recipeInv = new ItemStackHandler(2);

    public DeployerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.state = State.WAITING;
        this.mode = Mode.USE;
        this.heldItem = ItemStack.EMPTY;
        this.animatedOffset = LerpedFloat.linear().startWithValue(0.0);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.DEPLOYER.get(), (be, context) -> {
            if (be.invHandler == null) {
                be.initHandler();
            }
            return be.invHandler;
        });
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.filtering = new FilteringBehaviour(this, new DeployerFilterSlot());
        behaviours.add(this.filtering);
        this.processingBehaviour = new BeltProcessingBehaviour(this).whenItemEnters((s, i) -> BeltDeployerCallbacks.onItemReceived(s, i, this)).whileItemHeld((s, i) -> BeltDeployerCallbacks.whenItemHeld(s, i, this));
        behaviours.add(this.processingBehaviour);
        this.registerAwardables(behaviours, AllAdvancements.TRAIN_CASING, AllAdvancements.ANDESITE_CASING, AllAdvancements.BRASS_CASING, AllAdvancements.COPPER_CASING, AllAdvancements.FIST_BUMP, AllAdvancements.DEPLOYER, AllAdvancements.SELF_DEPLOYING);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.initHandler();
    }

    private void initHandler() {
        if (this.invHandler != null) {
            return;
        }
        Level level = this.level;
        if (level instanceof ServerLevel) {
            ServerLevel sLevel = (ServerLevel)level;
            this.player = new DeployerFakePlayer(sLevel, this.owner);
            if (this.deferredInventoryList != null) {
                this.player.getInventory().load(this.deferredInventoryList);
                this.deferredInventoryList = null;
                this.heldItem = this.player.getMainHandItem();
                this.sendData();
            }
            Vec3 initialPos = VecHelper.getCenterOf((Vec3i)this.worldPosition.relative((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)));
            this.player.setPos(initialPos.x, initialPos.y, initialPos.z);
        }
        this.invHandler = this.createHandler();
    }

    protected void onExtract(ItemStack stack) {
        this.player.setItemInHand(InteractionHand.MAIN_HAND, stack.copy());
        this.sendData();
        this.setChanged();
    }

    protected int getTimerSpeed() {
        return (int)(this.getSpeed() == 0.0f ? 0.0f : Mth.clamp((float)Math.abs(this.getSpeed() * 2.0f), (float)8.0f, (float)512.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (!this.level.isClientSide && this.player != null && this.player.blockBreakingProgress != null && this.level.isEmptyBlock((BlockPos)this.player.blockBreakingProgress.getKey())) {
            this.level.destroyBlockProgress(this.player.getId(), (BlockPos)this.player.blockBreakingProgress.getKey(), -1);
            this.player.blockBreakingProgress = null;
        }
        if (this.timer > 0) {
            this.timer -= this.getTimerSpeed();
            return;
        }
        if (this.level.isClientSide) {
            return;
        }
        if (this.player == null) {
            return;
        }
        ItemStack stack = this.player.getMainHandItem();
        if (this.state == State.WAITING) {
            if (!this.overflowItems.isEmpty()) {
                this.timer = this.getTimerSpeed() * 10;
                return;
            }
            boolean changed = false;
            Inventory inventory = this.player.getInventory();
            for (int i = 0; i < inventory.getContainerSize() && this.overflowItems.size() <= 10; ++i) {
                ItemStack item = inventory.getItem(i);
                if (item.isEmpty() || item == stack && this.filtering.test(item)) continue;
                this.overflowItems.add(item);
                inventory.setItem(i, ItemStack.EMPTY);
                changed = true;
            }
            if (changed) {
                this.sendData();
                this.timer = this.getTimerSpeed() * 10;
                return;
            }
            Direction facing = (Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING);
            if (this.mode == Mode.USE && !DeployerHandler.shouldActivate(stack, this.level, this.worldPosition.relative(facing, 2), facing)) {
                this.timer = this.getTimerSpeed() * 10;
                return;
            }
            if (this.mode == Mode.PUNCH && !this.fistBump && this.startFistBump(facing)) {
                return;
            }
            if (this.redstoneLocked) {
                return;
            }
            this.start();
            return;
        }
        if (this.state == State.EXPANDING) {
            if (this.fistBump) {
                this.triggerFistBump();
            }
            this.activate();
            this.state = State.RETRACTING;
            this.timer = 1000;
            this.sendData();
            return;
        }
        if (this.state == State.RETRACTING) {
            this.state = State.WAITING;
            this.timer = 500;
            this.sendData();
            return;
        }
    }

    protected void start() {
        this.state = State.EXPANDING;
        Vec3 movementVector = this.getMovementVector();
        Vec3 rayOrigin = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(movementVector.scale(1.5));
        Vec3 rayTarget = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(movementVector.scale(2.5));
        ClipContext rayTraceContext = new ClipContext(rayOrigin, rayTarget, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)this.player);
        BlockHitResult result = this.level.clip(rayTraceContext);
        this.reach = (float)(0.5 + Math.min(result.getLocation().subtract(rayOrigin).length(), 0.75));
        this.timer = 1000;
        this.sendData();
    }

    public boolean startFistBump(Direction facing) {
        int i = 0;
        KineticBlockEntity partner = null;
        for (i = 2; i < 5; ++i) {
            BlockPos otherDeployer = this.worldPosition.relative(facing, i);
            if (!this.level.isLoaded(otherDeployer)) {
                return false;
            }
            BlockEntity other = this.level.getBlockEntity(otherDeployer);
            if (!(other instanceof DeployerBlockEntity)) continue;
            DeployerBlockEntity dpe = (DeployerBlockEntity)other;
            partner = dpe;
            break;
        }
        if (partner == null) {
            return false;
        }
        if (((Direction)this.level.getBlockState(partner.getBlockPos()).getValue((Property)DirectionalKineticBlock.FACING)).getOpposite() != facing || ((DeployerBlockEntity)partner).mode != Mode.PUNCH) {
            return false;
        }
        if (partner.getSpeed() == 0.0f) {
            return false;
        }
        for (DeployerBlockEntity be : Arrays.asList(this, partner)) {
            be.fistBump = true;
            be.reach = (float)(i - 2) * 0.5f;
            be.timer = 1000;
            be.state = State.EXPANDING;
            be.sendData();
        }
        return true;
    }

    public void triggerFistBump() {
        int i = 0;
        SyncedBlockEntity deployerBlockEntity = null;
        for (i = 2; i < 5; ++i) {
            BlockPos pos = this.worldPosition.relative((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING), i);
            if (!this.level.isLoaded(pos)) {
                return;
            }
            BlockEntity blockEntity = this.level.getBlockEntity(pos);
            if (!(blockEntity instanceof DeployerBlockEntity)) continue;
            DeployerBlockEntity dpe = (DeployerBlockEntity)blockEntity;
            deployerBlockEntity = dpe;
            break;
        }
        if (deployerBlockEntity == null) {
            return;
        }
        if (!((DeployerBlockEntity)deployerBlockEntity).fistBump || ((DeployerBlockEntity)deployerBlockEntity).state != State.EXPANDING) {
            return;
        }
        if (((DeployerBlockEntity)deployerBlockEntity).timer > 0) {
            return;
        }
        this.fistBump = false;
        ((DeployerBlockEntity)deployerBlockEntity).fistBump = false;
        ((DeployerBlockEntity)deployerBlockEntity).state = State.RETRACTING;
        ((DeployerBlockEntity)deployerBlockEntity).timer = 1000;
        deployerBlockEntity.sendData();
        this.award(AllAdvancements.FIST_BUMP);
        BlockPos soundLocation = BlockPos.containing((Position)Vec3.atCenterOf((Vec3i)this.worldPosition).add(Vec3.atCenterOf((Vec3i)deployerBlockEntity.getBlockPos())).scale(0.5));
        this.level.playSound(null, soundLocation, SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.BLOCKS, 0.75f, 0.75f);
    }

    protected void activate() {
        Vec3 movementVector = this.getMovementVector();
        Direction direction = (Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING);
        Vec3 center = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        BlockPos clickedPos = this.worldPosition.relative(direction, 2);
        this.player.setXRot(direction == Direction.UP ? -90.0f : (direction == Direction.DOWN ? 90.0f : 0.0f));
        this.player.setYRot(direction.toYRot());
        if (direction == Direction.DOWN && BlockEntityBehaviour.get((BlockGetter)this.level, clickedPos, TransportedItemStackHandlerBehaviour.TYPE) != null) {
            return;
        }
        DeployerHandler.activate(this.player, center, clickedPos, movementVector, this.mode);
        this.award(AllAdvancements.DEPLOYER);
        if (this.player != null) {
            int count = this.heldItem.getCount();
            this.heldItem = this.player.getMainHandItem();
            if (count != this.heldItem.getCount()) {
                this.setChanged();
            }
        }
    }

    protected Vec3 getMovementVector() {
        if (!AllBlocks.DEPLOYER.has(this.getBlockState())) {
            return Vec3.ZERO;
        }
        return Vec3.atLowerCornerOf((Vec3i)((Direction)this.getBlockState().getValue((Property)DirectionalKineticBlock.FACING)).getNormal());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        this.state = (State)NBTHelper.readEnum((CompoundTag)compound, (String)"State", State.class);
        this.mode = (Mode)NBTHelper.readEnum((CompoundTag)compound, (String)"Mode", Mode.class);
        this.timer = compound.getInt("Timer");
        this.redstoneLocked = compound.getBoolean("Powered");
        if (compound.contains("Owner")) {
            this.owner = compound.getUUID("Owner");
        }
        this.deferredInventoryList = compound.getList("Inventory", 10);
        this.overflowItems = NBTHelper.readItemList((ListTag)compound.getList("Overflow", 10), (HolderLookup.Provider)registries);
        if (compound.contains("HeldItem")) {
            this.heldItem = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compound.getCompound("HeldItem"));
        }
        super.read(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        this.fistBump = compound.getBoolean("Fistbump");
        this.reach = compound.getFloat("Reach");
        if (compound.contains("Particle")) {
            ItemStack particleStack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compound.getCompound("Particle"));
            SandPaperItem.spawnParticles(VecHelper.getCenterOf((Vec3i)this.worldPosition).add(this.getMovementVector().scale((double)(this.reach + 1.0f))), particleStack, this.level);
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        NBTHelper.writeEnum((CompoundTag)compound, (String)"Mode", (Enum)this.mode);
        NBTHelper.writeEnum((CompoundTag)compound, (String)"State", (Enum)this.state);
        compound.putInt("Timer", this.timer);
        compound.putBoolean("Powered", this.redstoneLocked);
        if (this.owner != null) {
            compound.putUUID("Owner", this.owner);
        }
        if (this.player != null) {
            ListTag invNBT = new ListTag();
            this.player.getInventory().save(invNBT);
            compound.put("Inventory", (Tag)invNBT);
            compound.put("HeldItem", this.player.getMainHandItem().saveOptional(registries));
            compound.put("Overflow", (Tag)NBTHelper.writeItemList(this.overflowItems, (HolderLookup.Provider)registries));
        } else if (this.deferredInventoryList != null) {
            compound.put("Inventory", (Tag)this.deferredInventoryList);
        }
        super.write(compound, registries, clientPacket);
        if (!clientPacket) {
            return;
        }
        compound.putBoolean("Fistbump", this.fistBump);
        compound.putFloat("Reach", this.reach);
        if (this.player == null) {
            return;
        }
        compound.put("HeldItem", this.player.getMainHandItem().saveOptional(registries));
        if (this.player.spawnedItemEffects != null) {
            compound.put("Particle", this.player.spawnedItemEffects.saveOptional(registries));
            this.player.spawnedItemEffects = null;
        }
    }

    @Override
    public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
        NBTHelper.writeEnum((CompoundTag)tag, (String)"Mode", (Enum)this.mode);
        super.writeSafe(tag, registries);
    }

    private IItemHandlerModifiable createHandler() {
        return new DeployerItemHandler(this);
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
    }

    @OnlyIn(value=Dist.CLIENT)
    public PartialModel getHandPose() {
        return this.mode == Mode.PUNCH ? AllPartialModels.DEPLOYER_HAND_PUNCHING : (this.heldItem.isEmpty() ? AllPartialModels.DEPLOYER_HAND_POINTING : AllPartialModels.DEPLOYER_HAND_HOLDING);
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(3.0);
    }

    public void discardPlayer() {
        if (this.player == null) {
            return;
        }
        this.player.getInventory().dropAll();
        this.overflowItems.forEach(itemstack -> this.player.drop((ItemStack)itemstack, true, false));
        this.player.discard();
        this.player = null;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (this.invHandler != null) {
            this.invalidateCapabilities();
        }
    }

    public void clearContent() {
        this.filtering.setFilter(ItemStack.EMPTY);
    }

    public void changeMode() {
        this.mode = this.mode == Mode.PUNCH ? Mode.USE : Mode.PUNCH;
        this.setChanged();
        this.sendData();
    }

    @Override
    public boolean addToTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        if (super.addToTooltip(tooltip, isPlayerSneaking)) {
            return true;
        }
        if (this.getSpeed() == 0.0f) {
            return false;
        }
        if (this.overflowItems.isEmpty()) {
            return false;
        }
        TooltipHelper.addHint(tooltip, "hint.full_deployer", new Object[0]);
        return true;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        CreateLang.translate("tooltip.deployer.header", new Object[0]).forGoggles(tooltip);
        CreateLang.translate("tooltip.deployer." + (this.mode == Mode.USE ? "using" : "punching"), new Object[0]).style(ChatFormatting.YELLOW).forGoggles(tooltip);
        if (!this.heldItem.isEmpty()) {
            CreateLang.translate("tooltip.deployer.contains", Component.translatable((String)this.heldItem.getDescriptionId()).getString(), this.heldItem.getCount()).style(ChatFormatting.GREEN).forGoggles(tooltip);
        }
        float stressAtBase = this.calculateStressApplied();
        if (IRotate.StressImpact.isEnabled() && !Mth.equal((float)stressAtBase, (float)0.0f)) {
            tooltip.add(CommonComponents.EMPTY);
            this.addStressImpactStats(tooltip, stressAtBase);
        }
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public float getHandOffset(float partialTicks) {
        if (this.isVirtual()) {
            return this.animatedOffset.getValue(partialTicks);
        }
        float progress = 0.0f;
        int timerSpeed = this.getTimerSpeed();
        PartialModel handPose = this.getHandPose();
        if (this.state == State.EXPANDING) {
            progress = 1.0f - ((float)this.timer - partialTicks * (float)timerSpeed) / 1000.0f;
            if (this.fistBump) {
                progress *= progress;
            }
        }
        if (this.state == State.RETRACTING) {
            progress = ((float)this.timer - partialTicks * (float)timerSpeed) / 1000.0f;
        }
        float handLength = handPose == AllPartialModels.DEPLOYER_HAND_POINTING ? 0.0f : (handPose == AllPartialModels.DEPLOYER_HAND_HOLDING ? 0.25f : 0.1875f);
        float distance = Math.min(Mth.clamp((float)progress, (float)0.0f, (float)1.0f) * (this.reach + handLength), 1.3125f);
        return distance;
    }

    public void setAnimatedOffset(float offset) {
        this.animatedOffset.setValue((double)offset);
    }

    @Nullable
    public RecipeHolder<? extends Recipe<? extends RecipeInput>> getRecipe(ItemStack stack) {
        Optional<RecipeHolder<Recipe<RecipeInput>>> polishingRecipe;
        if (this.player == null || this.level == null) {
            return null;
        }
        ItemStack heldItemMainhand = this.player.getMainHandItem();
        if (heldItemMainhand.getItem() instanceof SandPaperItem && (polishingRecipe = this.checkRecipe(AllRecipeTypes.SANDPAPER_POLISHING, (RecipeInput)new SingleRecipeInput(stack), this.level)).isPresent()) {
            return polishingRecipe.get();
        }
        this.recipeInv.setStackInSlot(0, stack);
        this.recipeInv.setStackInSlot(1, heldItemMainhand);
        DeployerRecipeSearchEvent event = new DeployerRecipeSearchEvent(this, new RecipeWrapper((IItemHandler)this.recipeInv));
        event.addRecipe(() -> SequencedAssemblyRecipe.getRecipe(this.level, event.getInventory(), AllRecipeTypes.DEPLOYING.getType(), DeployerApplicationRecipe.class), 100);
        event.addRecipe(() -> this.checkRecipe(AllRecipeTypes.DEPLOYING, (RecipeInput)event.getInventory(), this.level), 50);
        event.addRecipe(() -> this.checkRecipe(AllRecipeTypes.ITEM_APPLICATION, (RecipeInput)event.getInventory(), this.level), 50);
        NeoForge.EVENT_BUS.post((Event)event);
        return event.getRecipe();
    }

    private Optional<RecipeHolder<Recipe<RecipeInput>>> checkRecipe(AllRecipeTypes type, RecipeInput inv, Level level) {
        return type.find(inv, level).filter(AllRecipeTypes.CAN_BE_AUTOMATED);
    }

    public DeployerFakePlayer getPlayer() {
        return this.player;
    }

    static enum State {
        WAITING,
        EXPANDING,
        RETRACTING,
        DUMPING;

    }

    static enum Mode {
        PUNCH,
        USE;

    }
}
