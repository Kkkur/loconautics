/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.common.Tags$Items
 */
package com.simibubi.create.content.contraptions.piston;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;

public class MechanicalPistonBlock
extends DirectionalAxisKineticBlock
implements IBE<MechanicalPistonBlockEntity> {
    public static final EnumProperty<PistonState> STATE = EnumProperty.create((String)"state", PistonState.class);
    protected boolean isSticky;

    public static MechanicalPistonBlock normal(BlockBehaviour.Properties properties) {
        return new MechanicalPistonBlock(properties, false);
    }

    public static MechanicalPistonBlock sticky(BlockBehaviour.Properties properties) {
        return new MechanicalPistonBlock(properties, true);
    }

    protected MechanicalPistonBlock(BlockBehaviour.Properties properties, boolean sticky) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.NORTH)).setValue(STATE, (Comparable)((Object)PistonState.RETRACTED)));
        this.isSticky = sticky;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{STATE});
        super.createBlockStateDefinition(builder);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!stack.is(Tags.Items.SLIMEBALLS)) {
            if (stack.isEmpty()) {
                this.withBlockEntityDo((BlockGetter)level, pos, be -> {
                    be.assembleNextTick = true;
                });
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (state.getValue(STATE) != PistonState.RETRACTED) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Direction direction = (Direction)state.getValue((Property)FACING);
        if (hitResult.getDirection() != direction) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (((MechanicalPistonBlock)state.getBlock()).isSticky) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            Vec3 vec = hitResult.getLocation();
            level.addParticle((ParticleOptions)ParticleTypes.ITEM_SLIME, vec.x, vec.y, vec.z, 0.0, 0.0, 0.0);
            return ItemInteractionResult.SUCCESS;
        }
        AllSoundEvents.SLIME_ADDED.playOnServer(level, (Vec3i)pos, 0.5f, 1.0f);
        if (!player.isCreative()) {
            stack.shrink(1);
        }
        level.setBlockAndUpdate(pos, (BlockState)((BlockState)AllBlocks.STICKY_MECHANICAL_PISTON.getDefaultState().setValue((Property)FACING, (Comparable)direction)).setValue((Property)AXIS_ALONG_FIRST_COORDINATE, (Comparable)((Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE))));
        return ItemInteractionResult.SUCCESS;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        if (!fromPos.equals((Object)pos.relative(direction.getOpposite()))) {
            return;
        }
        if (!level.isClientSide && !level.getBlockTicks().willTickThisTick(pos, (Object)this)) {
            level.scheduleTick(pos, (Block)this, 1);
        }
    }

    public void tick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource r) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        BlockState pole = worldIn.getBlockState(pos.relative(direction.getOpposite()));
        if (!AllBlocks.PISTON_EXTENSION_POLE.has(pole)) {
            return;
        }
        if (((Direction)pole.getValue((Property)PistonExtensionPoleBlock.FACING)).getAxis() != direction.getAxis()) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            if (be.lastException == null) {
                return;
            }
            be.lastException = null;
            be.sendData();
        });
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        if (state.getValue(STATE) != PistonState.RETRACTED) {
            return InteractionResult.PASS;
        }
        return super.onWrenched(state, context);
    }

    public BlockState playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
        BlockState block;
        BlockPos currentPos;
        int offset;
        Direction direction = (Direction)state.getValue((Property)FACING);
        BlockPos pistonHead = null;
        BlockPos pistonBase = pos;
        boolean dropBlocks = player == null || !player.isCreative();
        Integer maxPoles = MechanicalPistonBlock.maxAllowedPistonPoles();
        for (offset = 1; offset < maxPoles; ++offset) {
            currentPos = pos.relative(direction, offset);
            block = worldIn.getBlockState(currentPos);
            if (MechanicalPistonBlock.isExtensionPole(block) && direction.getAxis() == ((Direction)block.getValue((Property)BlockStateProperties.FACING)).getAxis()) continue;
            if (!MechanicalPistonBlock.isPistonHead(block) || block.getValue((Property)BlockStateProperties.FACING) != direction) break;
            pistonHead = currentPos;
            break;
        }
        if (pistonHead != null && pistonBase != null) {
            BlockPos.betweenClosedStream((BlockPos)pistonBase, pistonHead).filter(p -> !p.equals((Object)pos)).forEach(p -> worldIn.destroyBlock(p, dropBlocks));
        }
        for (offset = 1; offset < maxPoles && MechanicalPistonBlock.isExtensionPole(block = worldIn.getBlockState(currentPos = pos.relative(direction.getOpposite(), offset))) && direction.getAxis() == ((Direction)block.getValue((Property)BlockStateProperties.FACING)).getAxis(); ++offset) {
            worldIn.destroyBlock(currentPos, dropBlocks);
        }
        return super.playerWillDestroy(worldIn, pos, state, player);
    }

    public static int maxAllowedPistonPoles() {
        return (Integer)AllConfigs.server().kinetics.maxPistonPoles.get();
    }

    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        if (state.getValue(STATE) == PistonState.EXTENDED) {
            return AllShapes.MECHANICAL_PISTON_EXTENDED.get((Direction)state.getValue((Property)FACING));
        }
        if (state.getValue(STATE) == PistonState.MOVING) {
            return AllShapes.MECHANICAL_PISTON.get((Direction)state.getValue((Property)FACING));
        }
        return Shapes.block();
    }

    @Override
    public Class<MechanicalPistonBlockEntity> getBlockEntityClass() {
        return MechanicalPistonBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalPistonBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.MECHANICAL_PISTON.get();
    }

    public static boolean isPiston(BlockState state) {
        return AllBlocks.MECHANICAL_PISTON.has(state) || MechanicalPistonBlock.isStickyPiston(state);
    }

    public static boolean isStickyPiston(BlockState state) {
        return AllBlocks.STICKY_MECHANICAL_PISTON.has(state);
    }

    public static boolean isExtensionPole(BlockState state) {
        return AllBlocks.PISTON_EXTENSION_POLE.has(state);
    }

    public static boolean isPistonHead(BlockState state) {
        return AllBlocks.MECHANICAL_PISTON_HEAD.has(state);
    }

    public static enum PistonState implements StringRepresentable
    {
        RETRACTED,
        MOVING,
        EXTENDED;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
