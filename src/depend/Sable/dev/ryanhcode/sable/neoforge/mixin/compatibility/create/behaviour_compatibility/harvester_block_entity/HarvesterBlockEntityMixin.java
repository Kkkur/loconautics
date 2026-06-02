/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity
 *  com.simibubi.create.content.contraptions.behaviour.MovementContext
 *  com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity
 *  dev.ryanhcode.sable.companion.math.JOMLConversion
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.behaviour_compatibility.harvester_block_entity;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.foundation.blockEntity.CachedRenderBBBlockEntity;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockEntitySubLevelActor;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterLerpedSpeed;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.create.harvester.HarvesterTicker;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={HarvesterBlockEntity.class})
public abstract class HarvesterBlockEntityMixin
extends CachedRenderBBBlockEntity
implements HarvesterLerpedSpeed,
BlockEntitySubLevelActor {
    @Unique
    private final LerpedFloat sable$lerpedSpeed = LerpedFloat.angular();
    @Unique
    private BlockPos sable$previousPos = BlockPos.ZERO;

    public HarvesterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void sable$clientTick() {
        double velocity = Sable.HELPER.getVelocity(this.getLevel(), JOMLConversion.atCenterOf((Vec3i)this.getBlockPos())).length();
        this.sable$lerpedSpeed.chase((double)this.sable$lerpedSpeed.getValue() + velocity * 5.0, 20.0, LerpedFloat.Chaser.LINEAR);
        this.sable$lerpedSpeed.tickChaser();
    }

    @Override
    public void sable$tick(ServerSubLevel subLevel) {
        ActiveSableCompanion helper = Sable.HELPER;
        Vec3 center = this.getBlockPos().getCenter();
        BlockPos gatheredPos = helper.runIncludingSubLevels(this.level, (Position)center, false, helper.getContaining((BlockEntity)this), (sublevel, pos) -> {
            if (HarvesterTicker.blockEntityBehaviour.isValidCrop(this.level, pos, this.level.getBlockState(pos))) {
                return pos;
            }
            return null;
        });
        if (gatheredPos == null) {
            gatheredPos = BlockPos.containing((Position)helper.projectOutOfSubLevel(this.level, (Position)center));
        }
        if (!this.sable$previousPos.equals((Object)gatheredPos)) {
            this.sable$previousPos = gatheredPos;
            HarvesterTicker.dummyMovementContext.update(this.level, this.getBlockPos(), this.getBlockState(), null);
            HarvesterTicker.blockEntityBehaviour.visitNewPosition((MovementContext)HarvesterTicker.dummyMovementContext, this.sable$previousPos);
        }
    }

    @Override
    public LerpedFloat sable$getLerpedFloat() {
        return this.sable$lerpedSpeed;
    }
}
