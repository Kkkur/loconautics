/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.bearing.BearingBlock
 *  com.simibubi.create.foundation.block.IBE
 *  dev.simulated_team.simulated.api.CustomStressImpactTooltipProvider
 *  net.createmod.catnip.lang.LangBuilder
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing;

import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import dev.eriksonn.aeronautics.content.blocks.propeller.bearing.propeller_bearing.PropellerBearingBlockEntity;
import dev.eriksonn.aeronautics.data.AeroLang;
import dev.eriksonn.aeronautics.index.AeroBlockEntityTypes;
import dev.eriksonn.aeronautics.index.AeroBlockShapes;
import dev.simulated_team.simulated.api.CustomStressImpactTooltipProvider;
import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PropellerBearingBlock
extends BearingBlock
implements IBE<PropellerBearingBlockEntity>,
CustomStressImpactTooltipProvider {
    public PropellerBearingBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public LangBuilder getCustomImpactLang() {
        return AeroLang.translate("propeller.sails", new Object[0]);
    }

    public int getBarLength() {
        return 3;
    }

    public int getFilledBarLength() {
        return 3;
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
            this.withBlockEntityDo((BlockGetter)level, pos, te -> {
                if (te.isRunning()) {
                    te.startDisassemblySlowdown();
                    return;
                }
                te.setAssembleNextTick(true);
            });
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext ctx) {
        return AeroBlockShapes.PROPELLER_BEARING.get((Direction)pState.getValue((Property)FACING));
    }

    public Class<PropellerBearingBlockEntity> getBlockEntityClass() {
        return PropellerBearingBlockEntity.class;
    }

    public BlockEntityType<? extends PropellerBearingBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AeroBlockEntityTypes.PROPELLER_BEARING.get();
    }
}
