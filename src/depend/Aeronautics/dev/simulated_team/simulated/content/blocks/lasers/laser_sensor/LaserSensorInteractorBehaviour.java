/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType
 *  dev.ryanhcode.sable.Sable
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.lasers.laser_sensor;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.content.blocks.lasers.LaserBehaviour;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlock;
import dev.simulated_team.simulated.content.blocks.lasers.laser_sensor.LaserSensorBlockEntity;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class LaserSensorInteractorBehaviour
extends LaserBehaviour {
    public static final BehaviourType<LaserSensorInteractorBehaviour> TYPE = new BehaviourType();
    private LaserSensorBlockEntity previousSensor = null;
    public Supplier<Integer> directPower;
    public final Predicate<LaserSensorBlockEntity> filter;

    public LaserSensorInteractorBehaviour(SmartBlockEntity be, Supplier<Couple<Vec3>> positions, Supplier<Float> range, Supplier<Integer> directPower, Predicate<LaserSensorBlockEntity> filter) {
        super(be, positions, range);
        this.directPower = directPower;
        this.filter = filter;
    }

    @Override
    public void tick() {
        Level level = this.blockEntity.getLevel();
        if (level == null) {
            return;
        }
        super.tick();
        if (!this.checkAndUpdateSensor(this.getBlockHitResult(), this.getEntityHitResult())) {
            this.resetPrevData();
        }
    }

    private boolean checkAndUpdateSensor(@Nullable BlockHitResult bhr, @Nullable EntityHitResult ehr) {
        if (bhr == null || bhr.getType() == HitResult.Type.MISS) {
            return false;
        }
        if (this.getClosestHitResult() instanceof EntityHitResult) {
            return false;
        }
        BlockEntity be = this.getWorld().getBlockEntity(bhr.getBlockPos());
        if (be instanceof LaserSensorBlockEntity) {
            LaserSensorBlockEntity lbe = (LaserSensorBlockEntity)be;
            if (this.getProperFacing(be.getBlockState()) != bhr.getDirection() || !this.filter.test(lbe)) {
                return false;
            }
            this.updateHitSensor(lbe, bhr);
        }
        return true;
    }

    private Direction getProperFacing(BlockState sensor) {
        Direction normal = (Direction)sensor.getValue((Property)LaserSensorBlock.FACING);
        AttachFace target = (AttachFace)sensor.getValue((Property)LaserSensorBlock.TARGET);
        if (target.getSerializedName().equals("ceiling")) {
            normal = Direction.UP;
        }
        if (target.getSerializedName().equals("floor")) {
            normal = Direction.DOWN;
        }
        return normal;
    }

    private void updateHitSensor(LaserSensorBlockEntity sensorBE, BlockHitResult context) {
        if (sensorBE != this.previousSensor) {
            this.resetPrevData();
        }
        float distance = (float)Math.sqrt(Sable.HELPER.distanceSquaredWithSubLevels(sensorBE.getLevel(), (Position)this.getLaserPositions().get().get(true), (Position)context.getLocation()));
        sensorBE.updateFromPointer(distance, this.directPower.get());
        this.previousSensor = sensorBE;
    }

    private void resetPrevData() {
        this.previousSensor = null;
    }

    @Override
    public BehaviourType<?> getType() {
        return super.getType();
    }
}
