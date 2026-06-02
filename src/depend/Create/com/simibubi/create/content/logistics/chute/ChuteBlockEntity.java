/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.Containers
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.capabilities.BlockCapability
 *  net.neoforged.neoforge.capabilities.BlockCapabilityCache
 *  net.neoforged.neoforge.capabilities.Capabilities$ItemHandler
 *  net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.chute;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteBlock;
import com.simibubi.create.content.logistics.chute.ChuteItemHandler;
import com.simibubi.create.content.logistics.chute.SmartChuteBlockEntity;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.VersionedInventoryTrackerBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ChuteBlockEntity
extends SmartBlockEntity
implements IHaveGoggleInformation,
Clearable {
    float pull;
    float push;
    ItemStack item;
    LerpedFloat itemPosition;
    ChuteItemHandler itemHandler;
    boolean canPickUpItems = false;
    float bottomPullDistance = 0.0f;
    float beltBelowOffset;
    TransportedItemStackHandlerBehaviour beltBelow;
    boolean updateAirFlow = true;
    int airCurrentUpdateCooldown;
    int entitySearchCooldown;
    VersionedInventoryTrackerBehaviour invVersionTracker;
    private final EnumMap<Direction, BlockCapabilityCache<IItemHandler, @Nullable Direction>> capCaches = new EnumMap(Direction.class);

    public ChuteBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.item = ItemStack.EMPTY;
        this.itemPosition = LerpedFloat.linear();
        this.itemHandler = new ChuteItemHandler(this);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, (BlockEntityType)AllBlockEntityTypes.CHUTE.get(), (be, context) -> be.itemHandler);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        behaviours.add(new DirectBeltInputBehaviour(this).onlyInsertWhen(d -> this.canDirectlyInsertCached()));
        this.invVersionTracker = new VersionedInventoryTrackerBehaviour(this);
        behaviours.add(this.invVersionTracker);
        this.registerAwardables(behaviours, AllAdvancements.CHUTE);
    }

    public boolean canDirectlyInsertCached() {
        return this.canPickUpItems;
    }

    private boolean canDirectlyInsert() {
        BlockState blockState = this.getBlockState();
        BlockState blockStateAbove = this.level.getBlockState(this.worldPosition.above());
        if (!AbstractChuteBlock.isChute(blockState)) {
            return false;
        }
        if (AbstractChuteBlock.getChuteFacing(blockStateAbove) == Direction.DOWN) {
            return false;
        }
        if (this.getItemMotion() > 0.0f && this.getInputChutes().isEmpty()) {
            return false;
        }
        return AbstractChuteBlock.isOpenChute(blockState);
    }

    @Override
    public void initialize() {
        super.initialize();
        this.onAdded();
    }

    @Override
    protected AABB createRenderBoundingBox() {
        return new AABB(this.worldPosition).expandTowards(0.0, -3.0, 0.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.canPickUpItems = this.canDirectlyInsert();
        }
        boolean clientSide = this.level != null && this.level.isClientSide && !this.isVirtual();
        float itemMotion = this.getItemMotion();
        if (itemMotion != 0.0f && this.level != null && this.level.isClientSide) {
            this.spawnParticles(itemMotion);
        }
        this.tickAirStreams(itemMotion);
        if (this.item.isEmpty() && !clientSide) {
            if (itemMotion < 0.0f) {
                this.handleInputFromAbove();
            }
            if (itemMotion > 0.0f) {
                this.handleInputFromBelow();
            }
            return;
        }
        float nextOffset = this.itemPosition.getValue() + itemMotion;
        if (itemMotion < 0.0f) {
            if (nextOffset < 0.5f) {
                if (!this.handleDownwardOutput(true)) {
                    nextOffset = 0.5f;
                } else if (nextOffset < 0.0f) {
                    this.handleDownwardOutput(clientSide);
                    nextOffset = this.itemPosition.getValue();
                }
            }
        } else if (itemMotion > 0.0f && nextOffset > 0.5f) {
            if (!this.handleUpwardOutput(true)) {
                nextOffset = 0.5f;
            } else if (nextOffset > 1.0f) {
                this.handleUpwardOutput(clientSide);
                nextOffset = this.itemPosition.getValue();
            }
        }
        this.itemPosition.setValue((double)nextOffset);
    }

    private void updateAirFlow(float itemSpeed) {
        this.updateAirFlow = false;
        if (itemSpeed > 0.0f && this.level != null && !this.level.isClientSide) {
            float flowLimit;
            float speed = this.pull - this.push;
            this.beltBelow = null;
            float maxPullDistance = speed >= 128.0f ? 3.0f : (speed >= 64.0f ? 2.0f : (speed >= 32.0f ? 1.0f : Mth.lerp((float)(speed / 32.0f), (float)0.0f, (float)1.0f)));
            if (AbstractChuteBlock.isChute(this.level.getBlockState(this.worldPosition.below()))) {
                maxPullDistance = 0.0f;
            }
            if ((flowLimit = maxPullDistance) > 0.0f) {
                flowLimit = AirCurrent.getFlowLimit(this.level, this.worldPosition, maxPullDistance, Direction.DOWN);
            }
            int i = 1;
            while ((float)i <= flowLimit + 1.0f) {
                TransportedItemStackHandlerBehaviour behaviour = BlockEntityBehaviour.get((BlockGetter)this.level, this.worldPosition.below(i), TransportedItemStackHandlerBehaviour.TYPE);
                if (behaviour != null) {
                    this.beltBelow = behaviour;
                    this.beltBelowOffset = i - 1;
                    break;
                }
                ++i;
            }
            this.bottomPullDistance = Math.max(0.0f, flowLimit);
        }
        this.sendData();
    }

    private void findEntities(float itemSpeed) {
        if (this.bottomPullDistance <= 0.0f && !this.getItem().isEmpty() || itemSpeed <= 0.0f || this.level == null || this.level.isClientSide) {
            return;
        }
        if (!this.canActivate()) {
            return;
        }
        Vec3 center = VecHelper.getCenterOf((Vec3i)this.worldPosition);
        AABB searchArea = new AABB(center.add(0.0, (double)(-this.bottomPullDistance) - 0.5, 0.0), center.add(0.0, -0.5, 0.0)).inflate((double)0.45f);
        for (ItemEntity itemEntity : this.level.getEntitiesOfClass(ItemEntity.class, searchArea)) {
            ItemStack entityItem;
            if (!itemEntity.isAlive() || !this.canAcceptItem(entityItem = itemEntity.getItem())) continue;
            this.setItem(entityItem.copy(), (float)(itemEntity.getBoundingBox().getCenter().y - (double)this.worldPosition.getY()));
            itemEntity.discard();
            break;
        }
    }

    private void extractFromBelt(float itemSpeed) {
        if (itemSpeed <= 0.0f || this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.getItem().isEmpty() && this.beltBelow != null) {
            this.beltBelow.handleCenteredProcessingOnAllItems(0.5f, ts -> {
                if (this.canAcceptItem(ts.stack)) {
                    this.setItem(ts.stack.copy(), -this.beltBelowOffset);
                    return TransportedItemStackHandlerBehaviour.TransportedResult.removeItem();
                }
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            });
        }
    }

    private void tickAirStreams(float itemSpeed) {
        if (!this.level.isClientSide && this.airCurrentUpdateCooldown-- <= 0) {
            this.airCurrentUpdateCooldown = (Integer)AllConfigs.server().kinetics.fanBlockCheckRate.get();
            this.updateAirFlow = true;
        }
        if (this.updateAirFlow) {
            this.updateAirFlow(itemSpeed);
        }
        if (this.entitySearchCooldown-- <= 0 && this.item.isEmpty()) {
            this.entitySearchCooldown = 5;
            this.findEntities(itemSpeed);
        }
        this.extractFromBelt(itemSpeed);
    }

    public void blockBelowChanged() {
        this.updateAirFlow = true;
    }

    private void spawnParticles(float itemMotion) {
        float absMotion;
        if (this.level == null) {
            return;
        }
        BlockState blockState = this.getBlockState();
        boolean up = itemMotion > 0.0f;
        float f = absMotion = up ? itemMotion : -itemMotion;
        if (blockState == null || !AbstractChuteBlock.isChute(blockState)) {
            return;
        }
        if (this.push == 0.0f && this.pull == 0.0f) {
            return;
        }
        if (up && AbstractChuteBlock.isOpenChute(blockState) && BlockHelper.noCollisionInSpace((BlockGetter)this.level, this.worldPosition.above())) {
            this.spawnAirFlow(1.0f, 2.0f, absMotion, 0.5f);
        }
        if (AbstractChuteBlock.getChuteFacing(blockState) != Direction.DOWN) {
            return;
        }
        if (AbstractChuteBlock.isTransparentChute(blockState)) {
            this.spawnAirFlow(up ? 0.0f : 1.0f, up ? 1.0f : 0.0f, absMotion, 1.0f);
        }
        if (!up && BlockHelper.noCollisionInSpace((BlockGetter)this.level, this.worldPosition.below())) {
            this.spawnAirFlow(0.0f, -1.0f, absMotion, 0.5f);
        }
        if (up && this.canActivate() && this.bottomPullDistance > 0.0f) {
            this.spawnAirFlow(-this.bottomPullDistance, 0.0f, absMotion, 2.0f);
            this.spawnAirFlow(-this.bottomPullDistance, 0.0f, absMotion, 2.0f);
        }
    }

    private void spawnAirFlow(float verticalStart, float verticalEnd, float motion, float drag) {
        if (this.level == null) {
            return;
        }
        AirParticleData airParticleData = new AirParticleData(drag, motion);
        Vec3 origin = Vec3.atLowerCornerOf((Vec3i)this.worldPosition);
        float xOff = Create.RANDOM.nextFloat() * 0.5f + 0.25f;
        float zOff = Create.RANDOM.nextFloat() * 0.5f + 0.25f;
        Vec3 v = origin.add((double)xOff, (double)verticalStart, (double)zOff);
        Vec3 d = origin.add((double)xOff, (double)verticalEnd, (double)zOff).subtract(v);
        if (Create.RANDOM.nextFloat() < 2.0f * motion) {
            this.level.addAlwaysVisibleParticle((ParticleOptions)airParticleData, v.x, v.y, v.z, d.x, d.y, d.z);
        }
    }

    private void handleInputFromAbove() {
        this.handleInput(this.grabCapability(Direction.UP), 1.0f);
    }

    private void handleInputFromBelow() {
        this.handleInput(this.grabCapability(Direction.DOWN), 0.0f);
    }

    private void handleInput(@Nullable IItemHandler inv, float startLocation) {
        ItemStack extracted;
        if (inv == null) {
            return;
        }
        if (!this.canActivate()) {
            return;
        }
        if (this.invVersionTracker.stillWaiting(inv)) {
            return;
        }
        Predicate<ItemStack> canAccept = this::canAcceptItem;
        int count = this.getExtractionAmount();
        ItemHelper.ExtractionCountMode mode = this.getExtractionMode();
        if (!(mode != ItemHelper.ExtractionCountMode.UPTO && ItemHelper.extract(inv, canAccept, mode, count, true).isEmpty() || (extracted = ItemHelper.extract(inv, canAccept, mode, count, false)).isEmpty())) {
            this.setItem(extracted, startLocation);
            return;
        }
        this.invVersionTracker.awaitNewVersion(inv);
    }

    private boolean handleDownwardOutput(boolean simulate) {
        BlockState blockState = this.getBlockState();
        ChuteBlockEntity targetChute = this.getTargetChute(blockState);
        Direction direction = AbstractChuteBlock.getChuteFacing(blockState);
        if (this.level == null || direction == null || !this.canActivate()) {
            return false;
        }
        IItemHandler capBelow = this.grabCapability(Direction.DOWN);
        if (capBelow != null) {
            if (this.level.isClientSide && !this.isVirtual()) {
                return false;
            }
            if (this.invVersionTracker.stillWaiting(capBelow)) {
                return false;
            }
            ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)capBelow, (ItemStack)this.item, (boolean)simulate);
            ItemStack held = this.getItem();
            if (!simulate) {
                this.setItem(remainder, this.itemPosition.getValue(0.0f));
            }
            if (remainder.getCount() != held.getCount()) {
                return true;
            }
            this.invVersionTracker.awaitNewVersion(capBelow);
            if (direction == Direction.DOWN) {
                return false;
            }
        }
        if (targetChute != null) {
            boolean canInsert = targetChute.canAcceptItem(this.item);
            if (!simulate && canInsert) {
                targetChute.setItem(this.item, direction == Direction.DOWN ? 1.0f : 0.51f);
                this.setItem(ItemStack.EMPTY);
            }
            return canInsert;
        }
        if (direction.getAxis().isHorizontal()) {
            return false;
        }
        if (FunnelBlock.getFunnelFacing(this.level.getBlockState(this.worldPosition.below())) == Direction.DOWN) {
            return false;
        }
        if (Block.canSupportRigidBlock((BlockGetter)this.level, (BlockPos)this.worldPosition.below())) {
            return false;
        }
        if (!simulate) {
            Vec3 dropVec = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(0.0, -0.75, 0.0);
            ItemEntity dropped = new ItemEntity(this.level, dropVec.x, dropVec.y, dropVec.z, this.item.copy());
            dropped.setDefaultPickUpDelay();
            dropped.setDeltaMovement(0.0, -0.25, 0.0);
            this.level.addFreshEntity((Entity)dropped);
            this.setItem(ItemStack.EMPTY);
        }
        return true;
    }

    private boolean handleUpwardOutput(boolean simulate) {
        IItemHandler capAbove;
        BlockState stateAbove = this.level.getBlockState(this.worldPosition.above());
        if (this.level == null || !this.canActivate()) {
            return false;
        }
        if (AbstractChuteBlock.isOpenChute(this.getBlockState()) && (capAbove = this.grabCapability(Direction.UP)) != null) {
            if (this.level.isClientSide && !this.isVirtual() && !ChuteBlock.isChute(stateAbove)) {
                return false;
            }
            int countBefore = this.item.getCount();
            if (this.invVersionTracker.stillWaiting(capAbove)) {
                return false;
            }
            ItemStack remainder = ItemHandlerHelper.insertItemStacked((IItemHandler)capAbove, (ItemStack)this.item, (boolean)simulate);
            if (!simulate) {
                this.item = remainder;
            }
            if (countBefore != remainder.getCount()) {
                return true;
            }
            this.invVersionTracker.awaitNewVersion(capAbove);
            return false;
        }
        ChuteBlockEntity bestOutput = null;
        List<ChuteBlockEntity> inputChutes = this.getInputChutes();
        for (ChuteBlockEntity targetChute : inputChutes) {
            float itemMotion;
            if (!targetChute.canAcceptItem(this.item) || (itemMotion = targetChute.getItemMotion()) < 0.0f || bestOutput != null && !(bestOutput.getItemMotion() < itemMotion)) continue;
            bestOutput = targetChute;
        }
        if (bestOutput != null) {
            if (!simulate) {
                bestOutput.setItem(this.item, 0.0f);
                this.setItem(ItemStack.EMPTY);
            }
            return true;
        }
        if (FunnelBlock.getFunnelFacing(this.level.getBlockState(this.worldPosition.above())) == Direction.UP) {
            return false;
        }
        if (BlockHelper.hasBlockSolidSide(stateAbove, (BlockGetter)this.level, this.worldPosition.above(), Direction.DOWN)) {
            return false;
        }
        if (!inputChutes.isEmpty()) {
            return false;
        }
        if (!simulate) {
            Vec3 dropVec = VecHelper.getCenterOf((Vec3i)this.worldPosition).add(0.0, 0.5, 0.0);
            ItemEntity dropped = new ItemEntity(this.level, dropVec.x, dropVec.y, dropVec.z, this.item.copy());
            dropped.setDefaultPickUpDelay();
            dropped.setDeltaMovement(0.0, (double)(this.getItemMotion() * 2.0f), 0.0);
            this.level.addFreshEntity((Entity)dropped);
            this.setItem(ItemStack.EMPTY);
        }
        return true;
    }

    protected boolean canAcceptItem(ItemStack stack) {
        return this.item.isEmpty();
    }

    protected int getExtractionAmount() {
        return 16;
    }

    protected ItemHelper.ExtractionCountMode getExtractionMode() {
        return ItemHelper.ExtractionCountMode.UPTO;
    }

    protected boolean canActivate() {
        return true;
    }

    /*
     * Issues handling annotations - annotations may be inaccurate
     */
    @Nullable
    private IItemHandler grabCapability(@NotNull Direction side) {
        BlockPos pos = this.worldPosition.relative(side);
        if (this.level == null) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(pos);
        if (be instanceof ChuteBlockEntity && (side != Direction.DOWN || !(be instanceof SmartChuteBlockEntity) || this.getItemMotion() > 0.0f)) {
            return null;
        }
        if (this.capCaches.get(side) == null) {
            Level level = this.level;
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                @Nullable BlockCapabilityCache cache = BlockCapabilityCache.create((BlockCapability)Capabilities.ItemHandler.BLOCK, (ServerLevel)serverLevel, (BlockPos)pos, (Object)side.getOpposite());
                this.capCaches.put(side, (BlockCapabilityCache<IItemHandler, Direction>)cache);
                return (IItemHandler)cache.getCapability();
            }
            return (IItemHandler)this.level.getCapability(Capabilities.ItemHandler.BLOCK, pos, (Object)side.getOpposite());
        }
        return (IItemHandler)this.capCaches.get(side).getCapability();
    }

    public void setItem(ItemStack stack) {
        this.setItem(stack, this.getItemMotion() < 0.0f ? 1.0f : 0.0f);
    }

    public void setItem(ItemStack stack, float insertionPos) {
        this.item = stack;
        this.itemPosition.startWithValue((double)insertionPos);
        this.invVersionTracker.reset();
        if (!this.level.isClientSide) {
            this.notifyUpdate();
            this.award(AllAdvancements.CHUTE);
        }
    }

    @Override
    public void invalidate() {
        if (this.itemHandler != null) {
            this.invalidateCapabilities();
        }
        this.capCaches.clear();
        super.invalidate();
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        compound.put("Item", this.item.saveOptional(registries));
        compound.putFloat("ItemPosition", this.itemPosition.getValue());
        compound.putFloat("Pull", this.pull);
        compound.putFloat("Push", this.push);
        compound.putFloat("BottomAirFlowDistance", this.bottomPullDistance);
        super.write(compound, registries, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        ItemStack previousItem = this.item;
        this.item = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)compound.getCompound("Item"));
        this.itemPosition.startWithValue((double)compound.getFloat("ItemPosition"));
        this.pull = compound.getFloat("Pull");
        this.push = compound.getFloat("Push");
        this.bottomPullDistance = compound.getFloat("BottomAirFlowDistance");
        super.read(compound, registries, clientPacket);
        if (this.hasLevel() && this.level != null && this.level.isClientSide && !ItemStack.matches((ItemStack)previousItem, (ItemStack)this.item) && !this.item.isEmpty()) {
            if (this.level.random.nextInt(3) != 0) {
                return;
            }
            Vec3 p = VecHelper.getCenterOf((Vec3i)this.worldPosition);
            p = VecHelper.offsetRandomly((Vec3)p, (RandomSource)this.level.random, (float)0.5f);
            Vec3 m = Vec3.ZERO;
            this.level.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, this.item), p.x, p.y, p.z, m.x, m.y, m.z);
        }
    }

    public float getItemMotion() {
        float fanSpeedModifier = 0.015625f;
        float maxItemSpeed = 20.0f;
        float gravity = 4.0f;
        float motion = (this.push + this.pull) * 0.015625f;
        return (Mth.clamp((float)motion, (float)-20.0f, (float)20.0f) + (motion <= 0.0f ? -4.0f : 0.0f)) / 20.0f;
    }

    public void clearContent() {
        this.item = ItemStack.EMPTY;
    }

    @Override
    public void destroy() {
        super.destroy();
        ChuteBlockEntity targetChute = this.getTargetChute(this.getBlockState());
        List<ChuteBlockEntity> inputChutes = this.getInputChutes();
        if (!this.item.isEmpty() && this.level != null) {
            Containers.dropItemStack((Level)this.level, (double)this.worldPosition.getX(), (double)this.worldPosition.getY(), (double)this.worldPosition.getZ(), (ItemStack)this.item);
        }
        this.setRemoved();
        if (targetChute != null) {
            targetChute.updatePull();
            targetChute.propagatePush();
        }
        inputChutes.forEach(c -> c.updatePush(inputChutes.size()));
    }

    public void onAdded() {
        this.refreshBlockState();
        this.updatePull();
        ChuteBlockEntity targetChute = this.getTargetChute(this.getBlockState());
        if (targetChute != null) {
            targetChute.propagatePush();
        } else {
            this.updatePush(1);
        }
    }

    public void updatePull() {
        float totalPull = this.calculatePull();
        if (this.pull == totalPull) {
            return;
        }
        this.pull = totalPull;
        this.updateAirFlow = true;
        this.sendData();
        ChuteBlockEntity targetChute = this.getTargetChute(this.getBlockState());
        if (targetChute != null) {
            targetChute.updatePull();
        }
    }

    public void updatePush(int branchCount) {
        float totalPush = this.calculatePush(branchCount);
        if (this.push == totalPush) {
            return;
        }
        this.updateAirFlow = true;
        this.push = totalPush;
        this.sendData();
        this.propagatePush();
    }

    public void propagatePush() {
        List<ChuteBlockEntity> inputs = this.getInputChutes();
        inputs.forEach(c -> c.updatePush(inputs.size()));
    }

    protected float calculatePull() {
        BlockEntity be;
        BlockState blockStateAbove = this.level.getBlockState(this.worldPosition.above());
        if (AllBlocks.ENCASED_FAN.has(blockStateAbove) && blockStateAbove.getValue((Property)EncasedFanBlock.FACING) == Direction.DOWN && (be = this.level.getBlockEntity(this.worldPosition.above())) instanceof EncasedFanBlockEntity) {
            EncasedFanBlockEntity fan = (EncasedFanBlockEntity)be;
            if (!be.isRemoved()) {
                return fan.getSpeed();
            }
        }
        float totalPull = 0.0f;
        for (Direction d : Iterate.directions) {
            ChuteBlockEntity inputChute = this.getInputChute(d);
            if (inputChute == null) continue;
            totalPull += inputChute.pull;
        }
        return totalPull;
    }

    protected float calculatePush(int branchCount) {
        ChuteBlockEntity targetChute;
        BlockEntity be;
        if (this.level == null) {
            return 0.0f;
        }
        BlockState blockStateBelow = this.level.getBlockState(this.worldPosition.below());
        if (AllBlocks.ENCASED_FAN.has(blockStateBelow) && blockStateBelow.getValue((Property)EncasedFanBlock.FACING) == Direction.UP && (be = this.level.getBlockEntity(this.worldPosition.below())) instanceof EncasedFanBlockEntity) {
            EncasedFanBlockEntity fan = (EncasedFanBlockEntity)be;
            if (!be.isRemoved()) {
                return fan.getSpeed();
            }
        }
        if ((targetChute = this.getTargetChute(this.getBlockState())) == null) {
            return 0.0f;
        }
        return targetChute.push / (float)branchCount;
    }

    @Nullable
    private ChuteBlockEntity getTargetChute(BlockState state) {
        BlockState chuteState;
        if (this.level == null) {
            return null;
        }
        Direction targetDirection = AbstractChuteBlock.getChuteFacing(state);
        if (targetDirection == null) {
            return null;
        }
        BlockPos chutePos = this.worldPosition.below();
        if (targetDirection.getAxis().isHorizontal()) {
            chutePos = chutePos.relative(targetDirection.getOpposite());
        }
        if (!AbstractChuteBlock.isChute(chuteState = this.level.getBlockState(chutePos))) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(chutePos);
        if (be instanceof ChuteBlockEntity) {
            return (ChuteBlockEntity)be;
        }
        return null;
    }

    private List<ChuteBlockEntity> getInputChutes() {
        LinkedList<ChuteBlockEntity> inputs = new LinkedList<ChuteBlockEntity>();
        for (Direction d : Iterate.directions) {
            ChuteBlockEntity inputChute = this.getInputChute(d);
            if (inputChute == null) continue;
            inputs.add(inputChute);
        }
        return inputs;
    }

    @Nullable
    private ChuteBlockEntity getInputChute(Direction direction) {
        BlockState chuteState;
        Direction chuteFacing;
        if (this.level == null || direction == Direction.DOWN) {
            return null;
        }
        direction = direction.getOpposite();
        BlockPos chutePos = this.worldPosition.above();
        if (direction.getAxis().isHorizontal()) {
            chutePos = chutePos.relative(direction);
        }
        if ((chuteFacing = AbstractChuteBlock.getChuteFacing(chuteState = this.level.getBlockState(chutePos))) != direction) {
            return null;
        }
        BlockEntity be = this.level.getBlockEntity(chutePos);
        if (be instanceof ChuteBlockEntity && !be.isRemoved()) {
            return (ChuteBlockEntity)be;
        }
        return null;
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        boolean downward = this.getItemMotion() < 0.0f;
        CreateLang.translate("tooltip.chute.header", new Object[0]).forGoggles(tooltip);
        if (this.pull == 0.0f && this.push == 0.0f) {
            CreateLang.translate("tooltip.chute.no_fans_attached", new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        }
        if (this.pull != 0.0f) {
            CreateLang.translate("tooltip.chute.fans_" + (this.pull > 0.0f ? "pull_up" : "push_down"), new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        }
        if (this.push != 0.0f) {
            CreateLang.translate("tooltip.chute.fans_" + (this.push > 0.0f ? "push_up" : "pull_down"), new Object[0]).style(ChatFormatting.GRAY).forGoggles(tooltip);
        }
        CreateLang.text("-> ").add(CreateLang.translate("tooltip.chute.items_move_" + (downward ? "down" : "up"), new Object[0])).style(ChatFormatting.YELLOW).forGoggles(tooltip);
        if (!this.item.isEmpty()) {
            CreateLang.translate("tooltip.chute.contains", Component.translatable((String)this.item.getDescriptionId()).getString(), this.item.getCount()).style(ChatFormatting.GREEN).forGoggles(tooltip);
        }
        return true;
    }

    public ItemStack getItem() {
        return this.item;
    }
}
