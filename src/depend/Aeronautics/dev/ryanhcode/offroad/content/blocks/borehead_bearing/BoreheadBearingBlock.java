/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock
 *  com.simibubi.create.foundation.block.IBE
 *  dev.simulated_team.simulated.api.CustomStressImpactTooltipProvider
 *  dev.simulated_team.simulated.index.SimBlockMovementChecks
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectList
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 */
package dev.ryanhcode.offroad.content.blocks.borehead_bearing;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.data.OffroadLang;
import dev.ryanhcode.offroad.index.OffroadBlockEntityTypes;
import dev.simulated_team.simulated.api.CustomStressImpactTooltipProvider;
import dev.simulated_team.simulated.index.SimBlockMovementChecks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class BoreheadBearingBlock
extends DirectionalAxisKineticBlock
implements IBE<BoreheadBearingBlockEntity>,
CustomStressImpactTooltipProvider {
    private static final ObjectList<BlockPos> TEMP_POSITIONS = new ObjectArrayList();

    public BoreheadBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.FAIL;
        }
        if (stack.isEmpty()) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                if (be.isRunning()) {
                    be.startDisassemblySlowdown();
                    return;
                }
                be.setAssembleNextTick(true);
            });
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return super.hasShaftTowards(world, pos, state, face);
    }

    public Class<BoreheadBearingBlockEntity> getBlockEntityClass() {
        return BoreheadBearingBlockEntity.class;
    }

    public BlockEntityType<? extends BoreheadBearingBlockEntity> getBlockEntityType() {
        return (BlockEntityType)OffroadBlockEntityTypes.BOREHEAD_BEARING.get();
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockEntity be;
        InteractionResult resultType = super.onWrenched(state, context);
        if (!context.getLevel().isClientSide && resultType.consumesAction() && (be = context.getLevel().getBlockEntity(context.getClickedPos())) instanceof BoreheadBearingBlockEntity && context.getLevel().getBlockState(context.getClickedPos()).getValue((Property)FACING) != state.getValue((Property)FACING)) {
            ((BoreheadBearingBlockEntity)be).disassemble();
        }
        return resultType;
    }

    public LangBuilder getCustomImpactLang() {
        return OffroadLang.translate("tooltip.borehead_bearing_stress", new Object[0]);
    }

    public int getBarLength() {
        return 4;
    }

    public int getFilledBarLength() {
        return 4;
    }

    static {
        SimBlockMovementChecks.registerAdditionalBlocks((blockState, level, blockPos, set) -> {
            TEMP_POSITIONS.clear();
            if (blockState.getBlock() instanceof BoreheadBearingBlock) {
                TEMP_POSITIONS.addFirst((Object)blockPos.relative((Direction)blockState.getValue((Property)FACING)));
            }
            return TEMP_POSITIONS;
        });
    }
}
