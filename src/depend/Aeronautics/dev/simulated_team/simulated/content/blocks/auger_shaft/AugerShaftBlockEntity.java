/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate$SpeedLevel
 *  com.simibubi.create.content.kinetics.base.KineticBlock
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticEffectHandler
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock
 *  com.simibubi.create.content.redstone.displayLink.source.ItemThroughputDisplaySource
 *  com.simibubi.create.foundation.utility.CreateLang
 *  dev.ryanhcode.sable.util.LevelAccelerator
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.Container
 *  net.minecraft.world.Containers
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticEffectHandler;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlock;
import com.simibubi.create.content.redstone.displayLink.source.ItemThroughputDisplaySource;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.ryanhcode.sable.util.LevelAccelerator;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerActorInventory;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerInventory;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import dev.simulated_team.simulated.content.blocks.auger_shaft.BlockHarvester;
import dev.simulated_team.simulated.content.blocks.auger_shaft.ItemReciever;
import dev.simulated_team.simulated.content.blocks.auger_shaft.auger_groups.AugerDistributor;
import dev.simulated_team.simulated.content.particle.AugerIndicatorParticleData;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.multiloader.inventory.ContainerSlot;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import dev.simulated_team.simulated.service.SimInventoryService;
import dev.simulated_team.simulated.util.Observable;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import java.util.List;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

public class AugerShaftBlockEntity
extends KineticBlockEntity
implements ItemReciever,
Observable,
Clearable {
    private static final float MAX_AUGER_SPEED_TICKS = 8.0f;
    private LevelAccelerator accelerator;
    private AugerDistributor attachedGroup;
    public AugerActorInventory actorInventory;
    public AugerInventory inventory;
    private final LerpedFloat updateTracker = LerpedFloat.linear();
    public int intDirection;
    public Direction flowDirection;
    @ApiStatus.Internal
    public boolean beingWrenched;
    private int itemsMoved;
    private boolean maxSpeed;
    private boolean observed = false;
    private int particleCooldown = 100;

    public AugerShaftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new AugerInventory(this);
        this.actorInventory = new AugerActorInventory(this, 8);
        this.setLazyTickRate(20);
        this.updateTracker.chase(1.0, 0.0, LerpedFloat.Chaser.LINEAR);
        this.effects = new AugerKineticEffectHandler(this);
    }

    public void tick() {
        assert (this.level != null);
        if (this.accelerator == null) {
            this.accelerator = new LevelAccelerator(this.level);
        }
        this.intDirection = Mth.sign((double)this.getSpeed());
        this.flowDirection = Direction.get((Direction.AxisDirection)(this.intDirection == 1 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE), (Direction.Axis)((Direction.Axis)this.getBlockState().getValue((Property)AugerShaftBlock.AXIS)));
        super.tick();
        this.handleUpdateTracking();
        this.accelerator.clearCache();
        if (this.level.isClientSide) {
            --this.particleCooldown;
            if (this.observed && this.particleCooldown < 0) {
                this.particleCooldown = 100;
                this.effects.spawnRotationIndicators();
            }
            this.observed = false;
        }
    }

    private void handleUpdateTracking() {
        assert (this.level != null);
        if (this.getSpeed() == 0.0f) {
            this.resetUpdateTracker();
            return;
        }
        if (this.inventory.isEmpty()) {
            if (!this.level.isClientSide) {
                this.extract();
            }
            if (this.inventory.isEmpty()) {
                this.resetUpdateTracker();
                return;
            }
        }
        this.updateTracker.chase(1.0, (double)this.getItemSpeed(), LerpedFloat.Chaser.LINEAR);
        this.updateTracker.tickChaser();
        if (this.updateTracker.settled()) {
            this.handleItemPassed();
            this.resetUpdateTracker();
        }
    }

    private void extract() {
        ItemStack extracted;
        Object wrapped;
        Direction antiFlowDir;
        BlockPos antiFlowPos;
        assert (this.level != null);
        if (!this.actorInventory.isEmpty()) {
            ContainerSlot largestSlot = null;
            for (ContainerSlot populatedSlot : this.actorInventory.getPopulatedSlots()) {
                if (largestSlot != null && largestSlot.getStack().getCount() >= populatedSlot.getStack().getCount()) continue;
                largestSlot = populatedSlot;
            }
            if (largestSlot != null) {
                ItemStack extracted2 = this.actorInventory.extractSlot(largestSlot.getIndex(), largestSlot.getStack().getCount(), false);
                this.inventory.insertSlot(extracted2, 0, false);
            }
        }
        if (this.inventory.isEmpty() && !(this.accelerator.getBlockState(antiFlowPos = this.worldPosition.relative(antiFlowDir = this.flowDirection.getOpposite())).getBlock() instanceof AugerShaftBlock) && (wrapped = SimInventoryService.INSTANCE.getInventory(this.level.getBlockEntity(antiFlowPos), antiFlowDir)) != null && !(extracted = ((InventoryLoaderWrapper)wrapped).extractAny(16, true, false)).isEmpty()) {
            this.inventory.insertSlot(((InventoryLoaderWrapper)wrapped).extractAny(16, false, false), 0, false);
        }
    }

    private void handleItemPassed() {
        assert (this.level != null && !this.level.isClientSide);
        BlockEntity gatheredBE = this.level.getBlockEntity(this.worldPosition.relative(this.flowDirection));
        if (gatheredBE instanceof AugerShaftBlockEntity) {
            AugerShaftBlockEntity abe = (AugerShaftBlockEntity)gatheredBE;
            int beforeCount = this.inventory.getItem(0).getCount();
            this.inventory.slot.setStack(abe.inventory.insertSlot(this.inventory.slot.getStack(), 0, false));
            int totalMoved = beforeCount - this.inventory.slot.getStack().getCount();
            this.itemsMoved += totalMoved;
            if (totalMoved != 0) {
                this.notifyUpdate();
            }
        }
    }

    private float getItemSpeed() {
        float totalTicks = 8.0f / (Math.abs(this.getSpeed()) / 256.0f);
        this.maxSpeed = totalTicks == 8.0f;
        return 1.0f / totalTicks;
    }

    private void resetUpdateTracker() {
        this.updateTracker.startWithValue(0.0);
    }

    public void lazyTick() {
        super.lazyTick();
        if (!this.level.isClientSide && this.getSpeed() != 0.0f) {
            this.refreshActors();
        }
        if (!this.level.isClientSide) {
            DisplayLinkBlock.sendToGatherers((LevelAccessor)this.level, (BlockPos)this.getBlockPos(), (dlbe, a) -> a.itemReceived(dlbe, this.itemsMoved), ItemThroughputDisplaySource.class);
            this.itemsMoved = 0;
        }
        if (this.level.isClientSide && this.maxSpeed && this.itemsMoved > 0) {
            this.sendObserved(this.getBlockPos());
        }
    }

    private void refreshActors() {
        Direction dir = this.flowDirection.getOpposite();
        Direction.Axis axis = dir.getAxis();
        BlockPos relPos = this.getBlockPos().relative(dir);
        if (this.accelerator.getBlockState(relPos).hasBlockEntity()) {
            BlockEntity be = this.level.getBlockEntity(relPos);
            if (be instanceof BlockHarvester) {
                AugerDistributor distributor;
                BlockHarvester harvester = (BlockHarvester)be;
                if (this.attachedGroup != null) {
                    this.attachedGroup.removeReceiver(this);
                }
                if ((distributor = harvester.simulated$getAssociatedDistributor()) != null) {
                    this.attachedGroup = distributor;
                    this.attachedGroup.addReceiver(this);
                } else {
                    this.attachedGroup = new AugerDistributor();
                    this.attachedGroup.addReceiver(this);
                }
            }
        } else {
            return;
        }
        if (this.attachedGroup != null) {
            this.attachedGroup.gatherAndAssociateHarvesters(SimDirectionUtil.getSurroundingDirections(axis), relPos, this.getLevel(), this.accelerator);
        }
    }

    public void destroy() {
        super.destroy();
        if (!this.level.isClientSide && !this.beingWrenched) {
            Containers.dropContents((Level)this.level, (BlockPos)this.worldPosition, (Container)this.inventory);
            this.inventory.clearContent();
            this.actorInventory.clearAndDropContents(this.level, this.worldPosition);
            this.resetUpdateTracker();
        }
    }

    protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        compound.put("Inventory", (Tag)this.inventory.write(registries));
        compound.put("ActorInventory", (Tag)this.actorInventory.write(registries));
        if (!clientPacket) {
            compound.putFloat("Progress", this.updateTracker.getValue());
        }
    }

    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        this.inventory.read(registries, compound.getCompound("Inventory"));
        this.actorInventory.read(registries, compound.getCompound("ActorInventory"));
        if (!clientPacket) {
            this.updateTracker.setValue((double)compound.getFloat("Progress"));
        }
    }

    public AugerInventory getInventory() {
        return this.inventory;
    }

    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        int count;
        int actorItems;
        boolean added = super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        if (this.getSpeed() > 0.0f) {
            float perTickSpeed = this.getItemSpeed();
            float perSecondSpeed = perTickSpeed * 20.0f;
            SimLang.translate("auger_shaft.item_flow", Math.floor(perSecondSpeed * 100.0f) / 100.0).style(ChatFormatting.YELLOW).forGoggles(tooltip);
            added = true;
        }
        if ((actorItems = this.actorInventory.storedItemCount) > 0) {
            SimLang.translate("auger_shaft.actor_items", actorItems).style(ChatFormatting.GRAY).forGoggles(tooltip);
            added = true;
        }
        if ((count = this.inventory.slot.getStack().getCount()) > 0) {
            CreateLang.translate((String)"tooltip.chute.contains", (Object[])new Object[]{Component.translatable((String)this.inventory.slot.getStack().getDescriptionId()).getString(), count}).style(ChatFormatting.GREEN).forGoggles(tooltip);
            added = true;
        }
        this.observed = true;
        return added;
    }

    @Override
    public ItemStack onRecieveItem(ItemStack item, BlockPos fromPos) {
        if (!this.isSpeedRequirementFulfilled() || this.isOverStressed()) {
            return item;
        }
        ItemInfoWrapper info = ItemInfoWrapper.generateFromStack(item);
        long amountInserted = this.actorInventory.insertGeneral(info, item.getCount(), true);
        if (amountInserted > 0L) {
            this.actorInventory.insertGeneral(info, item.getCount(), false);
            item = item.copy();
            item.shrink((int)amountInserted);
            return item;
        }
        return item;
    }

    @Override
    public boolean removed() {
        return this.isRemoved();
    }

    @Override
    public boolean isActive() {
        return this.getSpeed() != 0.0f;
    }

    public void clearContent() {
        this.inventory.clearContent();
        this.actorInventory.clearContent();
    }

    public class AugerKineticEffectHandler
    extends KineticEffectHandler {
        public AugerKineticEffectHandler(KineticBlockEntity kte) {
            super(kte);
        }

        public void spawnRotationIndicators() {
            AugerShaftBlockEntity auger = AugerShaftBlockEntity.this;
            float speed = auger.getSpeed();
            if (speed == 0.0f) {
                return;
            }
            BlockState state = auger.getBlockState();
            Block block = state.getBlock();
            if (!(block instanceof KineticBlock)) {
                return;
            }
            KineticBlock kb = (KineticBlock)block;
            float radius1 = kb.getParticleInitialRadius();
            float radius2 = kb.getParticleTargetRadius();
            Direction direction = auger.flowDirection;
            BlockPos pos = auger.getBlockPos();
            Level level = auger.getLevel();
            if (direction == null || auger.speed == 0.0f) {
                return;
            }
            if (level == null) {
                return;
            }
            Vec3 vec = VecHelper.getCenterOf((Vec3i)pos);
            IRotate.SpeedLevel speedLevel = IRotate.SpeedLevel.of((float)speed);
            int color = speedLevel.getColor();
            int particleSpeed = speedLevel.getParticleSpeed();
            particleSpeed *= (int)Math.signum(speed);
            for (int i = 0; i < 3; ++i) {
                AugerIndicatorParticleData particleData = new AugerIndicatorParticleData(color, particleSpeed, radius1, radius2, (float)i / 3.0f, 10, direction);
                if (level instanceof ServerLevel) {
                    ServerLevel serverLevel = (ServerLevel)level;
                    serverLevel.sendParticles((ParticleOptions)particleData, vec.x, vec.y, vec.z, 20, 0.0, 0.0, 0.0, 1.0);
                    continue;
                }
                for (int j = 0; j < 20; ++j) {
                    level.addParticle((ParticleOptions)particleData, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
                }
            }
        }
    }
}
