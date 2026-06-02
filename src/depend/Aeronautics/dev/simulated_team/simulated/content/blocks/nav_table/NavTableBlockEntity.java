/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  javax.annotation.Nullable
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.world.Clearable
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Quaterniond
 *  org.joml.Quaternionf
 */
package dev.simulated_team.simulated.content.blocks.nav_table;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableInventory;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.util.SimMathUtils;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaterniond;
import org.joml.Quaternionf;

public class NavTableBlockEntity
extends SmartBlockEntity
implements Clearable {
    public SubLevel subLevel;
    public NavTableInventory inventory;
    @Nullable
    public Vec3 currentTarget = null;
    public boolean isPowering;
    private float relativeAngle = 0.0f;
    public final LerpedFloat lerpedAngleDegrees;
    private final Map<Direction, Integer> signalStrengthCache = new EnumMap<Direction, Integer>(Direction.class);
    private int ticks = 0;
    private double distanceToTarget;
    private double lastDistanceToTarget;

    public NavTableBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = new NavTableInventory(this);
        this.lerpedAngleDegrees = LerpedFloat.angular();
    }

    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public void tick() {
        NavigationTarget navigationTarget;
        super.tick();
        if (this.level.isClientSide) {
            this.lerpedAngleDegrees.tickChaser();
        }
        if (this.level == null || this.isVirtual()) {
            return;
        }
        this.subLevel = Sable.HELPER.getContaining((BlockEntity)this);
        if (!this.level.isClientSide) {
            this.updateTarget();
            this.updateCurrentAngle();
            if (this.getTargetPosition(false) != null) {
                double dist = this.getProjectedSelfPos().distanceTo(this.getTargetPosition(true));
                if (dist >= 5000.0) {
                    SimAdvancements.FAR_FROM_HOME.awardToNearby(this.getBlockPos(), this.level, 40, 10.0);
                }
                this.sendData();
            }
        }
        if (this.selectivelyUpdateNeighbors()) {
            this.notifyUpdate();
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
        if ((navigationTarget = this.getNavTableItem()) != null) {
            if (this.ticks > 10) {
                this.ticks = 0;
                this.lastDistanceToTarget = this.distanceToTarget;
                this.distanceToTarget = navigationTarget.distanceToTarget(this);
            }
            ++this.ticks;
        }
    }

    private void updateTarget() {
        NavigationTarget nti = this.getNavTableItem();
        this.currentTarget = nti != null ? nti.getTarget(this, this.getHeldItem()) : null;
        this.notifyUpdate();
    }

    public float getClientTargetAngle(float partialTicks) {
        if (this.level.isClientSide) {
            return -AngleHelper.rad((double)this.lerpedAngleDegrees.getValue(partialTicks));
        }
        return 0.0f;
    }

    public void forceCurrentAngle(float angle) {
        this.lerpedAngleDegrees.chase((double)angle, (double)0.8f, LerpedFloat.Chaser.EXP);
    }

    private void updateCurrentAngle() {
        if (this.level.isClientSide || this.getTargetPosition(false) == null) {
            this.relativeAngle = 0.0f;
            return;
        }
        Vec3 originPos = this.getProjectedSelfPos();
        Vec3 difference = this.getTargetPosition(true).subtract(originPos);
        Vec3 directionToTarget = difference.normalize();
        Quaterniond sublevelRot = this.getSublevelRot();
        Quaternionf rotation = ((Direction)this.getBlockState().getValue((Property)NavTableBlock.FACING)).getRotation();
        directionToTarget = SimMathUtils.rotateQuat(directionToTarget, sublevelRot);
        directionToTarget = SimMathUtils.rotateQuat(directionToTarget, rotation);
        directionToTarget = new Vec3(directionToTarget.x, 0.0, directionToTarget.z);
        this.relativeAngle = (360.0f + AngleHelper.deg((double)((float)Math.atan2(directionToTarget.z, directionToTarget.x)))) % 360.0f;
    }

    public int getRedstoneStrength(Direction direction) {
        if (this.level.isClientSide && this.isVirtual()) {
            Direction facing = (Direction)this.getBlockState().getValue((Property)NavTableBlock.FACING);
            Vec3i normal = facing.getNormal();
            double andleRad = Math.toRadians(this.lerpedAngleDegrees.getValue());
            Vec3 targetPos = new Vec3(Math.cos(andleRad), 0.0, Math.sin(andleRad));
            targetPos = NavigationTarget.getPlaneProjectedPos(targetPos, normal);
            double dot = -targetPos.dot(Vec3.atLowerCornerOf((Vec3i)direction.getNormal()));
            return (int)(Math.asin(dot) / Math.PI * 30.0 + 0.5);
        }
        int power = 0;
        NavigationTarget nti = this.getNavTableItem();
        if (nti != null && this.getTargetPosition(false) != null) {
            power = nti.getRedstoneStrength(this, direction, this.getHeldItem());
        }
        return power;
    }

    public Vec3 getProjectedSelfPos() {
        Vec3 pos = Vec3.atCenterOf((Vec3i)this.worldPosition);
        if (this.subLevel != null) {
            pos = this.subLevel.logicalPose().transformPosition(pos);
        }
        return pos;
    }

    @Nullable
    public Vec3 getTargetPosition(boolean project) {
        if (this.currentTarget == null) {
            return null;
        }
        return project ? Sable.HELPER.projectOutOfSubLevel(this.getLevel(), this.currentTarget) : this.currentTarget;
    }

    public Quaterniond getSublevelRot() {
        Quaterniond rot = new Quaterniond();
        if (this.subLevel != null) {
            rot = this.subLevel.logicalPose().orientation();
        }
        return rot;
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        tag.put("CurrentStack", this.getHeldItem().saveOptional(registries));
        if (this.currentTarget != null) {
            this.writeCurrentTarget(tag);
        }
        tag.putFloat("RelativeAngle", this.relativeAngle);
        super.write(tag, registries, clientPacket);
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        ItemStack stack = ItemStack.parseOptional((HolderLookup.Provider)registries, (CompoundTag)tag.getCompound("CurrentStack"));
        this.inventory.slot.setStack(stack);
        if (tag.contains("CurrentTarget")) {
            this.currentTarget = this.readCurrentTarget(tag);
            this.isPowering = true;
        } else {
            this.isPowering = false;
        }
        this.relativeAngle = tag.getFloat("RelativeAngle");
        if (clientPacket) {
            this.lerpedAngleDegrees.chase((double)this.relativeAngle, (double)0.8f, LerpedFloat.Chaser.EXP);
        }
        super.read(tag, registries, clientPacket);
    }

    private void writeCurrentTarget(CompoundTag tag) {
        ListTag currentTarget = VecHelper.writeNBT((Vec3)this.currentTarget);
        tag.put("CurrentTarget", (Tag)currentTarget);
    }

    private Vec3 readCurrentTarget(CompoundTag tag) {
        ListTag targetList = tag.getList("CurrentTarget", 6);
        return VecHelper.readNBT((ListTag)targetList);
    }

    private boolean selectivelyUpdateNeighbors() {
        if (this.level == null || this.level.isClientSide) {
            return false;
        }
        BlockState state = this.getBlockState();
        boolean notifyUpdate = false;
        for (Direction direction : Iterate.directions) {
            int curStrength;
            int oldStrength;
            if (direction.getAxis() == ((Direction)state.getValue((Property)NavTableBlock.FACING)).getAxis() || (oldStrength = this.signalStrengthCache.computeIfAbsent(direction, d -> -1).intValue()) == (curStrength = this.getRedstoneStrength(direction))) continue;
            this.signalStrengthCache.put(direction, curStrength);
            this.level.updateNeighborsAt(this.worldPosition.relative(direction), state.getBlock());
            notifyUpdate = true;
        }
        return notifyUpdate;
    }

    public NavigationTarget getNavTableItem() {
        return NavigationTarget.ofStack(this.getHeldItem());
    }

    public ItemStack getHeldItem() {
        return this.inventory.getItem(0);
    }

    public ItemStack setHeldItem(ItemStack newStack) {
        ItemStack oldStack = this.getHeldItem();
        this.inventory.setItem(0, newStack);
        return oldStack;
    }

    public void dropHeldItem() {
        ItemEntity itementity = new ItemEntity(this.level, (double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5, this.getHeldItem());
        itementity.setDefaultPickUpDelay();
        this.level.addFreshEntity((Entity)itementity);
        this.inventory.clearContent();
    }

    public double distanceToTarget() {
        return this.distanceToTarget;
    }

    public double lastDistanceToTarget() {
        return this.lastDistanceToTarget;
    }

    public void clearContent() {
        this.inventory.clearContent();
    }

    public float getRelativeAngle() {
        return this.relativeAngle;
    }
}
