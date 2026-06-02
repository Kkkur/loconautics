/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.util.Tuple
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.content.blocks.redstone.directional_receiver;

import dev.simulated_team.simulated.content.blocks.redstone.AbstractLinkedReceiverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class DirectionalLinkedReceiverBlockEntity
extends AbstractLinkedReceiverBlockEntity {
    private double angleToClosestLink;

    public DirectionalLinkedReceiverBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public Tuple<Integer, Double> getSignalFromLink(Vec3 relativePosition, int transmittedStrength) {
        Direction dir = (Direction)this.getBlockState().getValue((Property)DirectionalBlock.FACING);
        Vec3 normal = new Vec3((double)dir.getStepX(), (double)dir.getStepY(), (double)dir.getStepZ());
        double length = relativePosition.length();
        if (length > 256.0) {
            return new Tuple((Object)0, (Object)0.0);
        }
        double dot = relativePosition.dot(normal) / length;
        if (dot < 0.0) {
            return new Tuple((Object)0, (Object)0.0);
        }
        double angle = Math.asin(dot);
        this.angleToClosestLink = Math.acos(dot);
        return new Tuple((Object)((int)Math.min(9.23098669932993 * angle + 1.0, (double)transmittedStrength)), (Object)Math.toDegrees(angle));
    }

    public double getAngleToClosestLink() {
        return this.angleToClosestLink;
    }
}
