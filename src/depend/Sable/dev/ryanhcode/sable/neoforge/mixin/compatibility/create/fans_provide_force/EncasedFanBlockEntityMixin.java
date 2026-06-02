/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.fan.EncasedFanBlock
 *  com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.fans_provide_force;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlockEntity;
import dev.ryanhcode.sable.api.block.propeller.BlockEntityPropeller;
import dev.ryanhcode.sable.api.block.propeller.BlockEntitySubLevelPropellerActor;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={EncasedFanBlockEntity.class})
public class EncasedFanBlockEntityMixin
extends KineticBlockEntity
implements BlockEntitySubLevelPropellerActor,
BlockEntityPropeller {
    @Unique
    private boolean sable$blocked;

    public EncasedFanBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void sable$tick(ServerSubLevel subLevel) {
        BlockPos frontPos = this.getBlockPos().relative((Direction)this.getBlockState().getValue((Property)EncasedFanBlock.FACING));
        this.sable$blocked = !this.level.getBlockState(frontPos).isAir();
    }

    @Override
    public BlockEntityPropeller getPropeller() {
        return this;
    }

    @Override
    public Direction getBlockDirection() {
        return (Direction)this.getBlockState().getValue((Property)EncasedFanBlock.FACING);
    }

    protected float sable$getPropSpeed() {
        float rotationSpeed = EncasedFanBlockEntityMixin.convertToAngular((float)this.getSpeed());
        return (float)this.getBlockDirection().getAxisDirection().getStep() * rotationSpeed * 3.0f;
    }

    @Override
    public double getAirflow() {
        return 0.1f * this.sable$getPropSpeed();
    }

    @Override
    public double getThrust() {
        return 0.3f * this.sable$getPropSpeed();
    }

    @Override
    public boolean isActive() {
        return !this.sable$blocked && Math.abs(this.sable$getPropSpeed()) > 0.01f;
    }
}
