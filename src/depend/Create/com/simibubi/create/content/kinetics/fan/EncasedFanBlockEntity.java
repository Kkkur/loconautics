/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.fan;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.fan.AirCurrent;
import com.simibubi.create.content.kinetics.fan.EncasedFanBlock;
import com.simibubi.create.content.kinetics.fan.IAirCurrentSource;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.List;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

@MethodsReturnNonnullByDefault
public class EncasedFanBlockEntity
extends KineticBlockEntity
implements IAirCurrentSource {
    public AirCurrent airCurrent = new AirCurrent(this);
    protected int airCurrentUpdateCooldown;
    protected int entitySearchCooldown;
    protected boolean updateAirFlow = true;

    public EncasedFanBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        this.registerAwardables(behaviours, AllAdvancements.ENCASED_FAN, AllAdvancements.FAN_PROCESSING);
    }

    @Override
    protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            this.airCurrent.rebuild();
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
    }

    @Override
    public AirCurrent getAirCurrent() {
        return this.airCurrent;
    }

    @Override
    @Nullable
    public Level getAirCurrentWorld() {
        return this.level;
    }

    @Override
    public BlockPos getAirCurrentPos() {
        return this.worldPosition;
    }

    @Override
    public Direction getAirflowOriginSide() {
        return (Direction)this.getBlockState().getValue((Property)EncasedFanBlock.FACING);
    }

    @Override
    public Direction getAirFlowDirection() {
        float speed = this.getSpeed();
        if (speed == 0.0f) {
            return null;
        }
        Direction facing = (Direction)this.getBlockState().getValue((Property)BlockStateProperties.FACING);
        return (speed = EncasedFanBlockEntity.convertToDirection(speed, facing)) > 0.0f ? facing : facing.getOpposite();
    }

    @Override
    public void remove() {
        super.remove();
        this.updateChute();
    }

    @Override
    public boolean isSourceRemoved() {
        return this.remove;
    }

    @Override
    public void onSpeedChanged(float prevSpeed) {
        super.onSpeedChanged(prevSpeed);
        this.updateAirFlow = true;
        this.updateChute();
    }

    public void updateChute() {
        Direction direction = (Direction)this.getBlockState().getValue((Property)EncasedFanBlock.FACING);
        if (!direction.getAxis().isVertical()) {
            return;
        }
        BlockEntity poweredChute = this.level.getBlockEntity(this.worldPosition.relative(direction));
        if (!(poweredChute instanceof ChuteBlockEntity)) {
            return;
        }
        ChuteBlockEntity chuteBE = (ChuteBlockEntity)poweredChute;
        if (direction == Direction.DOWN) {
            chuteBE.updatePull();
        } else {
            chuteBE.updatePush(1);
        }
    }

    public void blockInFrontChanged() {
        this.updateAirFlow = true;
    }

    @Override
    public void tick() {
        boolean server;
        super.tick();
        boolean bl = server = !this.level.isClientSide || this.isVirtual();
        if (server && this.airCurrentUpdateCooldown-- <= 0) {
            this.airCurrentUpdateCooldown = (Integer)AllConfigs.server().kinetics.fanBlockCheckRate.get();
            this.updateAirFlow = true;
        }
        if (this.updateAirFlow) {
            this.updateAirFlow = false;
            this.airCurrent.rebuild();
            if (this.airCurrent.maxDistance > 0.0f) {
                this.award(AllAdvancements.ENCASED_FAN);
            }
            this.sendData();
        }
        if (this.getSpeed() == 0.0f) {
            return;
        }
        if (this.entitySearchCooldown-- <= 0) {
            this.entitySearchCooldown = 5;
            this.airCurrent.findEntities();
        }
        this.airCurrent.tick();
    }
}
