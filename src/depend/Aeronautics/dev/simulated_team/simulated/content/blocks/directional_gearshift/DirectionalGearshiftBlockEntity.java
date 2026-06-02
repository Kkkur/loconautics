/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.simulated_team.simulated.content.blocks.directional_gearshift;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import dev.simulated_team.simulated.content.blocks.directional_gearshift.DirectionalGearshiftBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DirectionalGearshiftBlockEntity
extends SplitShaftBlockEntity {
    public DirectionalGearshiftBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getRotationSpeedModifier(Direction face) {
        if (this.hasSource()) {
            if (face == this.getSourceFacing()) {
                return 1.0f;
            }
            boolean leftPowered = (Boolean)this.getBlockState().getValue((Property)DirectionalGearshiftBlock.LEFT_POWERED);
            boolean rightPowered = (Boolean)this.getBlockState().getValue((Property)DirectionalGearshiftBlock.RIGHT_POWERED);
            if (rightPowered && leftPowered) {
                return 0.0f;
            }
            if (leftPowered) {
                return 1.0f;
            }
            if (rightPowered) {
                return -1.0f;
            }
        }
        return 0.0f;
    }
}
