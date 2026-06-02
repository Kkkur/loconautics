/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.DirectionProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.absorber;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import dev.simulated_team.simulated.content.blocks.absorber.AbsorberBlockEntity;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AbsorberBlock
extends HorizontalDirectionalBlock
implements IBE<AbsorberBlockEntity>,
IWrenchable {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WET = BooleanProperty.create((String)"wet");
    public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<AbsorberBlock> CODEC = AbsorberBlock.simpleCodec(AbsorberBlock::new);

    public AbsorberBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)POWERED, (Comparable)Boolean.valueOf(false))).setValue((Property)WET, (Comparable)Boolean.valueOf(false)));
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean flag;
        if (!level.isClientSide && (flag = ((Boolean)state.getValue((Property)POWERED)).booleanValue()) != level.hasNeighborSignal(pos)) {
            level.setBlock(pos, (BlockState)state.cycle((Property)POWERED), 2);
        }
    }

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SimBlockShapes.EVAPORATOR;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{HORIZONTAL_FACING, POWERED, WET});
        super.createBlockStateDefinition(builder);
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.is(Items.CARROT) && ((Boolean)state.getValue((Property)POWERED)).booleanValue()) {
            level.playLocalSound(pos, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 0.8f, 0.9f + 0.2f * level.random.nextFloat(), false);
            level.playLocalSound(pos, SimSoundEvents.ABSORBER_EATS.event(), SoundSource.BLOCKS, 0.33f, 0.8f + 0.2f * level.random.nextFloat(), false);
            if (level instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)level;
                Vec3 mouthPos = pos.getCenter().add(Vec3.atLowerCornerOf((Vec3i)((Direction)state.getValue((Property)FACING)).getNormal()).scale(0.5));
                serverLevel.sendParticles((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, stack), mouthPos.x, mouthPos.y, mouthPos.z, 5, 0.0, 0.1, 0.0, 0.01);
            }
            stack.consume(1, (LivingEntity)player);
            return ItemInteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Direction dir = pContext.getHorizontalDirection().getOpposite();
        assert (pContext.getPlayer() != null);
        return (BlockState)this.defaultBlockState().setValue((Property)HORIZONTAL_FACING, (Comparable)(pContext.getPlayer().isShiftKeyDown() ? dir.getOpposite() : dir));
    }

    public Class<AbsorberBlockEntity> getBlockEntityClass() {
        return AbsorberBlockEntity.class;
    }

    public BlockEntityType<? extends AbsorberBlockEntity> getBlockEntityType() {
        return null;
    }
}
