/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.MapCodec
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.Difficulty
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.kinetics.crusher;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelBlockEntity;
import com.simibubi.create.content.kinetics.crusher.CrushingWheelControllerBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

public class CrushingWheelControllerBlock
extends DirectionalBlock
implements IBE<CrushingWheelControllerBlockEntity> {
    public static final BooleanProperty VALID = BooleanProperty.create((String)"valid");
    public static final MapCodec<CrushingWheelControllerBlock> CODEC = CrushingWheelControllerBlock.simpleCodec(CrushingWheelControllerBlock::new);

    public CrushingWheelControllerBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    public boolean canBeReplaced(BlockState state, BlockPlaceContext useContext) {
        return false;
    }

    public boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity) {
        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{VALID});
        builder.add(new Property[]{FACING});
        super.createBlockStateDefinition(builder);
    }

    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn) {
        if (!((Boolean)state.getValue((Property)VALID)).booleanValue()) {
            return;
        }
        Direction facing = (Direction)state.getValue((Property)FACING);
        Direction.Axis axis = facing.getAxis();
        this.checkEntityForProcessing(worldIn, pos, entityIn);
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> {
            if (be.processingEntity == entityIn) {
                entityIn.makeStuckInBlock(state, new Vec3(axis == Direction.Axis.X ? (double)0.05f : 0.25, axis == Direction.Axis.Y ? (double)0.05f : 0.25, axis == Direction.Axis.Z ? (double)0.05f : 0.25));
            }
        });
    }

    public void checkEntityForProcessing(Level worldIn, BlockPos pos, Entity entityIn) {
        CrushingWheelControllerBlockEntity be = (CrushingWheelControllerBlockEntity)this.getBlockEntity((BlockGetter)worldIn, pos);
        if (be == null) {
            return;
        }
        if (be.crushingspeed == 0.0f) {
            return;
        }
        CompoundTag data = entityIn.getPersistentData();
        if (data.contains("BypassCrushingWheel") && pos.equals((Object)NBTHelper.readBlockPos((CompoundTag)data, (String)"BypassCrushingWheel"))) {
            return;
        }
        if (be.isOccupied()) {
            return;
        }
        boolean isPlayer = entityIn instanceof Player;
        if (isPlayer && ((Player)entityIn).isCreative()) {
            return;
        }
        if (isPlayer && entityIn.level().getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        be.startCrushing(entityIn);
    }

    public void updateEntityAfterFallOn(BlockGetter worldIn, Entity entityIn) {
        super.updateEntityAfterFallOn(worldIn, entityIn);
    }

    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (!((Boolean)stateIn.getValue((Property)VALID)).booleanValue()) {
            return;
        }
        if (rand.nextInt(1) != 0) {
            return;
        }
        double d0 = (float)pos.getX() + rand.nextFloat();
        double d1 = (float)pos.getY() + rand.nextFloat();
        double d2 = (float)pos.getZ() + rand.nextFloat();
        worldIn.addParticle((ParticleOptions)ParticleTypes.CRIT, d0, d1, d2, 0.0, 0.0, 0.0);
    }

    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        this.updateSpeed(stateIn, worldIn, currentPos);
        return stateIn;
    }

    public void updateSpeed(BlockState state, LevelAccessor world, BlockPos pos) {
        this.withBlockEntityDo((BlockGetter)world, pos, be -> {
            if (!((Boolean)state.getValue((Property)VALID)).booleanValue()) {
                if (be.crushingspeed != 0.0f) {
                    be.crushingspeed = 0.0f;
                    be.sendData();
                }
                return;
            }
            for (Direction d : Iterate.directions) {
                BlockEntity adjBE;
                BlockState neighbour = world.getBlockState(pos.relative(d));
                if (!AllBlocks.CRUSHING_WHEEL.has(neighbour) || neighbour.getValue((Property)BlockStateProperties.AXIS) == d.getAxis() || !((adjBE = world.getBlockEntity(pos.relative(d))) instanceof CrushingWheelBlockEntity)) continue;
                CrushingWheelBlockEntity cwbe = (CrushingWheelBlockEntity)adjBE;
                be.crushingspeed = Math.abs(cwbe.getSpeed() / 50.0f);
                be.sendData();
                cwbe.award(AllAdvancements.CRUSHING_WHEEL);
                if (!(Math.abs(cwbe.getSpeed()) > (float)((Integer)AllConfigs.server().kinetics.maxRotationSpeed.get() - 1))) break;
                cwbe.award(AllAdvancements.CRUSHER_MAXED);
                break;
            }
        });
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape standardShape = AllShapes.CRUSHING_WHEEL_CONTROLLER_COLLISION.get((Direction)state.getValue((Property)FACING));
        if (!((Boolean)state.getValue((Property)VALID)).booleanValue()) {
            return standardShape;
        }
        if (!(context instanceof EntityCollisionContext)) {
            return standardShape;
        }
        Entity entity = ((EntityCollisionContext)context).getEntity();
        if (entity == null) {
            return standardShape;
        }
        CompoundTag data = entity.getPersistentData();
        if (pos.equals((Object)NBTHelper.readBlockPos((CompoundTag)data, (String)"BypassCrushingWheel")) && state.getValue((Property)FACING) != Direction.UP) {
            return Shapes.empty();
        }
        CrushingWheelControllerBlockEntity be = (CrushingWheelControllerBlockEntity)this.getBlockEntity(worldIn, pos);
        if (be != null && be.processingEntity == entity) {
            return Shapes.empty();
        }
        return standardShape;
    }

    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.hasBlockEntity() || state.getBlock() == newState.getBlock()) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)worldIn, pos, be -> ItemHelper.dropContents(worldIn, pos, (IItemHandler)be.inventory));
        worldIn.removeBlockEntity(pos);
    }

    @Override
    public Class<CrushingWheelControllerBlockEntity> getBlockEntityClass() {
        return CrushingWheelControllerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CrushingWheelControllerBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.CRUSHING_WHEEL_CONTROLLER.get();
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    @NotNull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }
}
