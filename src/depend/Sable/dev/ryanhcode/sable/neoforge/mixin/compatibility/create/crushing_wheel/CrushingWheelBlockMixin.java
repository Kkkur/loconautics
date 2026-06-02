/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock
 *  com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock
 *  com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Overwrite
 */
package dev.ryanhcode.sable.neoforge.mixin.compatibility.create.crushing_wheel;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlock;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.sublevel.SubLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value={CrushingWheelBlock.class})
public abstract class CrushingWheelBlockMixin
extends RotatedPillarKineticBlock
implements IBE<CrushingWheelBlockEntity> {
    public CrushingWheelBlockMixin(BlockBehaviour.Properties arg) {
        super(arg);
    }

    @Overwrite
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entityIn) {
        SubLevel subLevel = Sable.HELPER.getContaining(level, (Vec3i)pos);
        Vec3 entityPos = entityIn.position();
        if (subLevel != null) {
            entityPos = subLevel.logicalPose().transformPositionInverse(entityPos);
        }
        if (entityPos.y() < (double)((float)pos.getY() + 1.25f) || !entityIn.onGround()) {
            return;
        }
        float speed = this.getBlockEntityOptional((BlockGetter)level, pos).map(KineticBlockEntity::getSpeed).orElse(Float.valueOf(0.0f)).floatValue();
        double x = 0.0;
        double z = 0.0;
        double entityX = entityPos.x();
        double entityZ = entityPos.z();
        if (state.getValue((Property)AXIS) == Direction.Axis.X) {
            z = speed / 20.0f;
            x += ((double)((float)pos.getX() + 0.5f) - entityX) * (double)0.1f;
        }
        if (state.getValue((Property)AXIS) == Direction.Axis.Z) {
            x = speed / -20.0f;
            z += ((double)((float)pos.getZ() + 0.5f) - entityZ) * (double)0.1f;
        }
        Vec3 impulse = new Vec3(x, 0.0, z);
        if (subLevel != null) {
            impulse = subLevel.logicalPose().transformNormal(impulse);
        }
        entityIn.setDeltaMovement(entityIn.getDeltaMovement().add(impulse));
    }
}
