/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.createmod.catnip.platform.CatnipServices
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
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ElytraItem
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.ObserverBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.ItemStackHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.depot;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.box.PackageEntity;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.EjectorAwardPacket;
import com.simibubi.create.content.logistics.depot.EjectorBlock;
import com.simibubi.create.content.logistics.depot.EjectorElytraPacket;
import com.simibubi.create.content.logistics.depot.EntityLauncher;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollValueBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class EjectorBlockEntity
extends KineticBlockEntity {
    List<IntAttached<ItemStack>> launchedItems;
    ScrollValueBehaviour maxStackSize;
    DepotBehaviour depotBehaviour;
    EntityLauncher launcher = new EntityLauncher(1, 0);
    LerpedFloat lidProgress = LerpedFloat.linear().startWithValue(1.0);
    boolean powered = false;
    boolean launch;
    State state = State.RETRACTING;
    @Nullable
    Pair<Vec3, BlockPos> earlyTarget;
    float earlyTargetTime;
    int scanCooldown;
    ItemStack trackedItem;

    public EjectorBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.launchedItems = new ArrayList<IntAttached<ItemStack>>();
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.WEIGHTED_EJECTOR.get(), (be, context) -> be.depotBehaviour.itemHandler);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.depotBehaviour = new DepotBehaviour(this);
        behaviours.add(this.depotBehaviour);
        this.maxStackSize = new ScrollValueBehaviour((Component)CreateLang.translateDirect("weighted_ejector.stack_size", new Object[0]), this, new EjectorSlot()).between(0, 64).withFormatter(i -> i == 0 ? "*" : String.valueOf(i));
        behaviours.add(this.maxStackSize);
        this.depotBehaviour.maxStackSize = () -> this.maxStackSize.getValue();
        this.depotBehaviour.canAcceptItems = () -> this.state == State.CHARGED;
        this.depotBehaviour.canFunnelsPullFrom = side -> side != this.getFacing();
        this.depotBehaviour.enableMerging();
        this.depotBehaviour.addSubBehaviours(behaviours);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.updateSignal();
    }

    public void activate() {
        this.launch = true;
        this.nudgeEntities();
    }

    protected boolean cannotLaunch() {
        return this.state != State.CHARGED && (!this.level.isClientSide || this.state != State.LAUNCHING);
    }

    public void activateDeferred() {
        boolean doLogic;
        if (this.cannotLaunch()) {
            return;
        }
        Direction facing = this.getFacing();
        List entities = this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition).inflate(-0.0625, 0.0, -0.0625));
        boolean bl = doLogic = !this.level.isClientSide || this.isVirtual();
        if (doLogic) {
            this.launchItems();
        }
        for (Entity entity : entities) {
            boolean isPlayerEntity = entity instanceof Player;
            if (!entity.isAlive() || entity instanceof ItemEntity || entity instanceof PackageEntity || entity.getPistonPushReaction() == PushReaction.IGNORE) continue;
            entity.setOnGround(false);
            if (isPlayerEntity != this.level.isClientSide) continue;
            entity.setPos((double)((float)this.worldPosition.getX() + 0.5f), (double)(this.worldPosition.getY() + 1), (double)((float)this.worldPosition.getZ() + 0.5f));
            this.launcher.applyMotion(entity, facing);
            if (!isPlayerEntity) continue;
            Player playerEntity = (Player)entity;
            if (this.launcher.getHorizontalDistance() * this.launcher.getHorizontalDistance() + this.launcher.getVerticalDistance() * this.launcher.getVerticalDistance() >= 625) {
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new EjectorAwardPacket(this.worldPosition));
            }
            if (!(playerEntity.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof ElytraItem)) continue;
            playerEntity.setXRot(-35.0f);
            playerEntity.setYRot(facing.toYRot());
            playerEntity.setDeltaMovement(playerEntity.getDeltaMovement().scale(0.75));
            this.deployElytra(playerEntity);
            CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new EjectorElytraPacket(this.worldPosition));
        }
        if (doLogic) {
            this.lidProgress.chase(1.0, (double)0.8f, LerpedFloat.Chaser.EXP);
            this.state = State.LAUNCHING;
            if (!this.level.isClientSide) {
                this.level.playSound(null, this.worldPosition, SoundEvents.WOODEN_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 0.35f, 1.0f);
                this.level.playSound(null, this.worldPosition, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 0.1f, 1.4f);
            }
        }
    }

    public void deployElytra(Player playerEntity) {
        EntityHack.setElytraFlying((Entity)playerEntity);
    }

    protected void launchItems() {
        ItemStack heldItemStack = this.depotBehaviour.getHeldItemStack();
        Direction funnelFacing = this.getFacing().getOpposite();
        if (AbstractFunnelBlock.getFunnelFacing(this.level.getBlockState(this.worldPosition.above())) == funnelFacing) {
            ItemStack remainder;
            DirectBeltInputBehaviour directOutput = this.getBehaviour(DirectBeltInputBehaviour.TYPE);
            if (this.depotBehaviour.heldItem != null && (remainder = directOutput.tryExportingToBeltFunnel(heldItemStack, funnelFacing, false)) != null) {
                if (remainder.isEmpty()) {
                    this.depotBehaviour.removeHeldItem();
                } else if (remainder.getCount() != heldItemStack.getCount()) {
                    this.depotBehaviour.heldItem.stack = remainder;
                }
            }
            Iterator<TransportedItemStack> iterator = this.depotBehaviour.incoming.iterator();
            while (iterator.hasNext()) {
                TransportedItemStack transportedItemStack = iterator.next();
                ItemStack stack = transportedItemStack.stack;
                ItemStack remainder2 = directOutput.tryExportingToBeltFunnel(stack, funnelFacing, false);
                if (remainder2 == null) continue;
                if (remainder2.isEmpty()) {
                    iterator.remove();
                    continue;
                }
                if (ItemStack.isSameItem((ItemStack)remainder2, (ItemStack)stack)) continue;
                transportedItemStack.stack = remainder2;
            }
            ItemStackHandler outputs = this.depotBehaviour.processingOutputBuffer;
            for (int i = 0; i < outputs.getSlots(); ++i) {
                ItemStack remainder3 = directOutput.tryExportingToBeltFunnel(outputs.getStackInSlot(i), funnelFacing, false);
                if (remainder3 == null) continue;
                outputs.setStackInSlot(i, remainder3);
            }
            return;
        }
        if (!this.level.isClientSide) {
            for (Direction d : Iterate.directions) {
                BlockState blockState = this.level.getBlockState(this.worldPosition.relative(d));
                if (!(blockState.getBlock() instanceof ObserverBlock) || blockState.getValue((Property)ObserverBlock.FACING) != d.getOpposite()) continue;
                blockState.updateShape(d.getOpposite(), blockState, (LevelAccessor)this.level, this.worldPosition.relative(d), this.worldPosition);
            }
        }
        if (this.depotBehaviour.heldItem != null) {
            this.addToLaunchedItems(heldItemStack);
            this.depotBehaviour.removeHeldItem();
        }
        for (TransportedItemStack transportedItemStack : this.depotBehaviour.incoming) {
            this.addToLaunchedItems(transportedItemStack.stack);
        }
        this.depotBehaviour.incoming.clear();
        ItemStackHandler outputs = this.depotBehaviour.processingOutputBuffer;
        for (int i = 0; i < outputs.getSlots(); ++i) {
            ItemStack extractItem = outputs.extractItem(i, 64, false);
            if (extractItem.isEmpty()) continue;
            this.addToLaunchedItems(extractItem);
        }
    }

    protected boolean addToLaunchedItems(ItemStack stack) {
        if ((!this.level.isClientSide || this.isVirtual()) && this.trackedItem == null && this.scanCooldown == 0) {
            this.scanCooldown = (Integer)AllConfigs.server().kinetics.ejectorScanInterval.get();
            this.trackedItem = stack;
        }
        return this.launchedItems.add((IntAttached<ItemStack>)IntAttached.withZero((Object)stack));
    }

    protected Direction getFacing() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.WEIGHTED_EJECTOR.has(blockState)) {
            return Direction.UP;
        }
        Direction facing = (Direction)blockState.getValue(EjectorBlock.HORIZONTAL_FACING);
        return facing;
    }

    @Override
    public void tick() {
        super.tick();
        boolean doLogic = !this.level.isClientSide || this.isVirtual();
        State prevState = this.state;
        float totalTime = Math.max(3.0f, (float)this.launcher.getTotalFlyingTicks());
        if (this.scanCooldown > 0) {
            --this.scanCooldown;
        }
        if (this.launch) {
            this.launch = false;
            this.activateDeferred();
        }
        Iterator<IntAttached<ItemStack>> iterator = this.launchedItems.iterator();
        while (iterator.hasNext()) {
            float maxTime;
            IntAttached<ItemStack> intAttached = iterator.next();
            boolean hit = false;
            if (intAttached.getSecond() == this.trackedItem) {
                hit = this.scanTrajectoryForObstacles((Integer)intAttached.getFirst());
            }
            float f = maxTime = this.earlyTarget != null ? Math.min(this.earlyTargetTime, totalTime) : totalTime;
            if (hit || intAttached.exceeds((int)maxTime)) {
                this.placeItemAtTarget(doLogic, maxTime, intAttached);
                iterator.remove();
            }
            intAttached.increment();
        }
        if (this.state == State.LAUNCHING) {
            this.lidProgress.chase(1.0, (double)0.8f, LerpedFloat.Chaser.EXP);
            this.lidProgress.tickChaser();
            if (this.lidProgress.getValue() > 0.9375f && doLogic) {
                this.state = State.RETRACTING;
                this.lidProgress.setValue(1.0);
            }
        }
        if (this.state == State.CHARGED) {
            this.lidProgress.setValue(0.0);
            this.lidProgress.updateChaseSpeed(0.0);
            if (doLogic) {
                this.ejectIfTriggered();
            }
        }
        if (this.state == State.RETRACTING) {
            if (this.lidProgress.getChaseTarget() == 1.0f && !this.lidProgress.settled()) {
                this.lidProgress.tickChaser();
            } else {
                this.lidProgress.updateChaseTarget(0.0f);
                this.lidProgress.updateChaseSpeed(0.0);
                if (this.lidProgress.getValue() == 0.0f && doLogic) {
                    this.state = State.CHARGED;
                    this.lidProgress.setValue(0.0);
                    this.sendData();
                }
                float value = Mth.clamp((float)(this.lidProgress.getValue() - this.getWindUpSpeed()), (float)0.0f, (float)1.0f);
                this.lidProgress.setValue((double)value);
                int soundRate = (int)(1.0f / (this.getWindUpSpeed() * 5.0f)) + 1;
                float volume = 0.125f;
                float pitch = 1.5f - this.lidProgress.getValue();
                if ((int)this.level.getGameTime() % soundRate == 0 && doLogic) {
                    this.level.playSound(null, this.worldPosition, SoundEvents.WOODEN_BUTTON_CLICK_OFF, SoundSource.BLOCKS, volume, pitch);
                }
            }
        }
        if (this.state != prevState) {
            this.notifyUpdate();
        }
    }

    private boolean scanTrajectoryForObstacles(int time) {
        BlockState blockState;
        boolean miss;
        Vec3 target;
        if (time <= 2) {
            return false;
        }
        Vec3 source = this.getLaunchedItemLocation(time);
        BlockHitResult rayTraceBlocks = this.level.clip(new ClipContext(source, target = this.getLaunchedItemLocation(time + 1), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        boolean bl = miss = rayTraceBlocks.getType() == HitResult.Type.MISS;
        if (!miss && rayTraceBlocks.getType() == HitResult.Type.BLOCK && FunnelBlock.isFunnel(blockState = this.level.getBlockState(rayTraceBlocks.getBlockPos())) && blockState.hasProperty((Property)FunnelBlock.EXTRACTING) && ((Boolean)blockState.getValue((Property)FunnelBlock.EXTRACTING)).booleanValue()) {
            miss = true;
        }
        if (miss) {
            if (this.earlyTarget != null && this.earlyTargetTime < (float)(time + 1)) {
                this.earlyTarget = null;
                this.earlyTargetTime = 0.0f;
            }
            return false;
        }
        Vec3 vec = rayTraceBlocks.getLocation();
        this.earlyTarget = Pair.of((Object)vec.add(Vec3.atLowerCornerOf((Vec3i)rayTraceBlocks.getDirection().getNormal()).scale(0.25)), (Object)rayTraceBlocks.getBlockPos());
        this.earlyTargetTime = (float)((double)time + source.distanceTo(vec) / source.distanceTo(target));
        this.sendData();
        return true;
    }

    protected void nudgeEntities() {
        for (Entity entity : this.level.getEntitiesOfClass(Entity.class, new AABB(this.worldPosition).inflate(-0.0625, 0.0, -0.0625))) {
            if (!entity.isAlive() || entity.getPistonPushReaction() == PushReaction.IGNORE || entity instanceof Player) continue;
            entity.setPos(entity.getX(), entity.getY() + 0.125, entity.getZ());
        }
    }

    protected void ejectIfTriggered() {
        DirectBeltInputBehaviour targetOpenInv;
        if (this.powered) {
            return;
        }
        int presentStackSize = this.depotBehaviour.getPresentStackSize();
        if (presentStackSize == 0) {
            return;
        }
        if (presentStackSize < this.maxStackSize.getValue()) {
            return;
        }
        if (this.depotBehaviour.heldItem != null && this.depotBehaviour.heldItem.beltPosition < 0.49f) {
            return;
        }
        Direction funnelFacing = this.getFacing().getOpposite();
        ItemStack held = this.depotBehaviour.getHeldItemStack();
        if (AbstractFunnelBlock.getFunnelFacing(this.level.getBlockState(this.worldPosition.above())) == funnelFacing) {
            ItemStack tryFunnel;
            DirectBeltInputBehaviour directOutput = this.getBehaviour(DirectBeltInputBehaviour.TYPE);
            if (!(this.depotBehaviour.heldItem == null || (tryFunnel = directOutput.tryExportingToBeltFunnel(held, funnelFacing, true)) != null && tryFunnel.isEmpty())) {
                return;
            }
        }
        if ((targetOpenInv = this.getTargetOpenInv()) != null && this.depotBehaviour.heldItem != null && targetOpenInv.handleInsertion(held, Direction.UP, true).getCount() == held.getCount()) {
            return;
        }
        this.activate();
        this.notifyUpdate();
    }

    protected void placeItemAtTarget(boolean doLogic, float maxTime, IntAttached<ItemStack> intAttached) {
        DirectBeltInputBehaviour targetOpenInv;
        if (!doLogic) {
            return;
        }
        if (intAttached.getSecond() == this.trackedItem) {
            this.trackedItem = null;
        }
        if ((targetOpenInv = this.getTargetOpenInv()) != null) {
            ItemStack remainder = targetOpenInv.handleInsertion((ItemStack)intAttached.getValue(), Direction.UP, false);
            intAttached.setSecond((Object)remainder);
        }
        if (((ItemStack)intAttached.getValue()).isEmpty()) {
            return;
        }
        Vec3 ejectVec = this.earlyTarget != null ? (Vec3)this.earlyTarget.getFirst() : this.getLaunchedItemLocation(maxTime);
        Vec3 ejectMotionVec = this.getLaunchedItemMotion(maxTime);
        ItemEntity item = new ItemEntity(this.level, ejectVec.x, ejectVec.y, ejectVec.z, (ItemStack)intAttached.getValue());
        item.setDeltaMovement(ejectMotionVec);
        item.setDefaultPickUpDelay();
        this.level.addFreshEntity((Entity)item);
    }

    public DirectBeltInputBehaviour getTargetOpenInv() {
        BlockPos targetPos = this.earlyTarget != null ? (BlockPos)this.earlyTarget.getSecond() : this.worldPosition.above(this.launcher.getVerticalDistance()).relative(this.getFacing(), Math.max(1, this.launcher.getHorizontalDistance()));
        return BlockEntityBehaviour.get((BlockGetter)this.level, targetPos, DirectBeltInputBehaviour.TYPE);
    }

    public Vec3 getLaunchedItemLocation(float time) {
        return this.launcher.getGlobalPos(time, this.getFacing().getOpposite(), this.worldPosition);
    }

    public Vec3 getLaunchedItemMotion(float time) {
        return this.launcher.getGlobalVelocity(time, this.getFacing().getOpposite(), this.worldPosition).scale(0.5);
    }

    @Override
    public void destroy() {
        super.destroy();
        this.dropFlyingItems();
    }

    public void dropFlyingItems() {
        for (IntAttached<ItemStack> intAttached : this.launchedItems) {
            Vec3 ejectVec = this.getLaunchedItemLocation(((Integer)intAttached.getFirst()).intValue());
            Vec3 ejectMotionVec = this.getLaunchedItemMotion(((Integer)intAttached.getFirst()).intValue());
            ItemEntity item = new ItemEntity(this.level, 0.0, 0.0, 0.0, (ItemStack)intAttached.getValue());
            item.setPosRaw(ejectVec.x, ejectVec.y, ejectVec.z);
            item.setDeltaMovement(ejectMotionVec);
            item.setDefaultPickUpDelay();
            this.level.addFreshEntity((Entity)item);
        }
        this.launchedItems.clear();
    }

    public float getWindUpSpeed() {
        int hd = this.launcher.getHorizontalDistance();
        int vd = this.launcher.getVerticalDistance();
        float speedFactor = Math.abs(this.getSpeed()) / 256.0f;
        float distanceFactor = hd == 0 && vd == 0 ? 1.0f : 1.0f * Mth.sqrt((float)(hd * hd + vd * vd));
        return speedFactor / distanceFactor;
    }

    @Override
    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.putInt("HorizontalDistance", this.launcher.getHorizontalDistance());
        compound.putInt("VerticalDistance", this.launcher.getVerticalDistance());
        compound.putBoolean("Powered", this.powered);
        NBTHelper.writeEnum((CompoundTag)compound, (String)"State", (Enum)this.state);
        compound.put("Lid", (Tag)this.lidProgress.writeNBT());
        compound.put("LaunchedItems", (Tag)NBTHelper.writeCompoundList(this.launchedItems, ia -> ia.serializeNBT(s -> (CompoundTag)s.saveOptional(registries))));
        if (this.earlyTarget != null) {
            compound.put("EarlyTarget", (Tag)VecHelper.writeNBT((Vec3)((Vec3)this.earlyTarget.getFirst())));
            compound.put("EarlyTargetPos", NbtUtils.writeBlockPos((BlockPos)((BlockPos)this.earlyTarget.getSecond())));
            compound.putFloat("EarlyTargetTime", this.earlyTargetTime);
        }
    }

    @Override
    public void writeSafe(CompoundTag compound, HolderLookup.Provider registries) {
        super.writeSafe(compound, registries);
        compound.putInt("HorizontalDistance", this.launcher.getHorizontalDistance());
        compound.putInt("VerticalDistance", this.launcher.getVerticalDistance());
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        int horizontalDistance = compound.getInt("HorizontalDistance");
        int verticalDistance = compound.getInt("VerticalDistance");
        if (this.launcher.getHorizontalDistance() != horizontalDistance || this.launcher.getVerticalDistance() != verticalDistance) {
            this.launcher.set(horizontalDistance, verticalDistance);
            this.launcher.clamp((Integer)AllConfigs.server().kinetics.maxEjectorDistance.get());
        }
        this.powered = compound.getBoolean("Powered");
        this.state = (State)NBTHelper.readEnum((CompoundTag)compound, (String)"State", State.class);
        this.lidProgress.readNBT(compound.getCompound("Lid"), false);
        this.launchedItems = NBTHelper.readCompoundList((ListTag)compound.getList("LaunchedItems", 10), nbt -> IntAttached.read((CompoundTag)nbt, t -> ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)t)));
        this.earlyTarget = null;
        this.earlyTargetTime = 0.0f;
        if (compound.contains("EarlyTarget")) {
            this.earlyTarget = Pair.of((Object)VecHelper.readNBT((ListTag)compound.getList("EarlyTarget", 6)), (Object)NBTHelper.readBlockPos((CompoundTag)compound, (String)"EarlyTargetPos"));
            this.earlyTargetTime = compound.getFloat("EarlyTargetTime");
        }
        if (compound.contains("ForceAngle")) {
            this.lidProgress.startWithValue((double)compound.getFloat("ForceAngle"));
        }
    }

    public void updateSignal() {
        boolean shoudPower = this.level.hasNeighborSignal(this.worldPosition);
        if (shoudPower == this.powered) {
            return;
        }
        this.powered = shoudPower;
        this.sendData();
    }

    public void setTarget(int horizontalDistance, int verticalDistance) {
        this.launcher.set(Math.max(1, horizontalDistance), verticalDistance);
        this.sendData();
    }

    public BlockPos getTargetPosition() {
        BlockState blockState = this.getBlockState();
        if (!AllBlocks.WEIGHTED_EJECTOR.has(blockState)) {
            return this.worldPosition;
        }
        Direction facing = (Direction)blockState.getValue(EjectorBlock.HORIZONTAL_FACING);
        return this.worldPosition.relative(facing, this.launcher.getHorizontalDistance()).above(this.launcher.getVerticalDistance());
    }

    public float getLidProgress(float pt) {
        return this.lidProgress.getValue(pt);
    }

    public State getState() {
        return this.state;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return AABB.INFINITE;
    }

    public static enum State {
        CHARGED,
        LAUNCHING,
        RETRACTING;

    }

    private class EjectorSlot
    extends ValueBoxTransform.Sided {
        private EjectorSlot() {
        }

        @Override
        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            if (this.direction != Direction.UP) {
                return super.getLocalOffset(level, pos, state);
            }
            return new Vec3(0.5, 0.65625, 0.5).add(VecHelper.rotate((Vec3)VecHelper.voxelSpace((double)0.0, (double)0.0, (double)-5.0), (double)this.angle(state), (Direction.Axis)Direction.Axis.Y));
        }

        @Override
        public void rotate(LevelAccessor level, BlockPos pos, BlockState state, PoseStack ms) {
            if (this.direction != Direction.UP) {
                super.rotate(level, pos, state, ms);
                return;
            }
            ((PoseTransformStack)TransformStack.of((PoseStack)ms).rotateYDegrees(this.angle(state))).rotateXDegrees(90.0f);
        }

        protected float angle(BlockState state) {
            float horizontalAngle = AllBlocks.WEIGHTED_EJECTOR.has(state) ? AngleHelper.horizontalAngle((Direction)((Direction)state.getValue(EjectorBlock.HORIZONTAL_FACING))) : 0.0f;
            return horizontalAngle;
        }

        @Override
        protected boolean isSideActive(BlockState state, Direction direction) {
            return direction.getAxis() == ((Direction)state.getValue(EjectorBlock.HORIZONTAL_FACING)).getAxis() || direction == Direction.UP && EjectorBlockEntity.this.state != State.CHARGED;
        }

        @Override
        protected Vec3 getSouthLocation() {
            return this.direction == Direction.UP ? Vec3.ZERO : VecHelper.voxelSpace((double)8.0, (double)6.0, (double)15.5);
        }
    }

    private static abstract class EntityHack
    extends Entity {
        public EntityHack(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
            super(p_i48580_1_, p_i48580_2_);
        }

        public static void setElytraFlying(Entity e) {
            SynchedEntityData data = e.getEntityData();
            data.set(DATA_SHARED_FLAGS_ID, (Object)((byte)((Byte)data.get(DATA_SHARED_FLAGS_ID) | 0x80)));
        }
    }
}
