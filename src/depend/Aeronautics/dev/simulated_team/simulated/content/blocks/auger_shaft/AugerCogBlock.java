/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.simpleRelays.ICogWheel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.simulated_team.simulated.content.blocks.auger_shaft;

import com.simibubi.create.content.kinetics.simpleRelays.ICogWheel;
import dev.simulated_team.simulated.content.blocks.auger_shaft.AugerShaftBlock;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class AugerCogBlock
extends AugerShaftBlock
implements ICogWheel {
    public AugerCogBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public boolean isSmallCog() {
        return true;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return this.transformAuger(state, SimBlocks.AUGER_SHAFT.getDefaultState(), context, level);
    }

    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return super.canSurvive(state, level, pos);
    }
}
