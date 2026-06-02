/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.impl.contraption.BlockMovementChecksImpl
 *  dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.impl.contraption.BlockMovementChecksImpl;
import dev.ryanhcode.offroad.content.blocks.rock_cutting_wheel.RockCuttingWheelBlockEntity;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public class RockCuttingWheelBlock
extends AbstractDirectionalAxisBlock
implements IBE<RockCuttingWheelBlockEntity> {
    public static final MapCodec<RockCuttingWheelBlock> CODEC = RockCuttingWheelBlock.simpleCodec(RockCuttingWheelBlock::new);

    public RockCuttingWheelBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public Class<RockCuttingWheelBlockEntity> getBlockEntityClass() {
        return RockCuttingWheelBlockEntity.class;
    }

    public BlockEntityType<? extends RockCuttingWheelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)OffroadBlockEntityTypes.ROCKCUTTING_WHEEL_BLOCK_ENTITY.get();
    }

    static {
        BlockMovementChecksImpl.registerAttachedCheck((state, world, pos, direction) -> {
            if (state.getBlock() instanceof RockCuttingWheelBlock) {
                if (direction != state.getValue((Property)BlockStateProperties.FACING)) {
                    return BlockMovementChecks.CheckResult.SUCCESS;
                }
                return BlockMovementChecks.CheckResult.FAIL;
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
        BlockMovementChecksImpl.registerNotSupportiveCheck((state, direction) -> {
            if (state.getBlock() instanceof RockCuttingWheelBlock && direction.equals((Object)state.getValue((Property)BlockStateProperties.FACING))) {
                return BlockMovementChecks.CheckResult.FAIL;
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
    }
}
