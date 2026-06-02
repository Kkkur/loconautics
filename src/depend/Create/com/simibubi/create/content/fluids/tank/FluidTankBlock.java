/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.capabilities.Capabilities$FluidHandler
 *  net.neoforged.neoforge.common.util.DeferredSoundType
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 */
package com.simibubi.create.content.fluids.tank;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.fluids.tank.CreativeFluidTankBlockEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.fluids.transfer.GenericItemEmptying;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.blockEntity.ComparatorUtil;
import com.simibubi.create.foundation.fluid.FluidHelper;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public class FluidTankBlock
extends Block
implements IWrenchable,
IBE<FluidTankBlockEntity> {
    public static final BooleanProperty TOP = BooleanProperty.create((String)"top");
    public static final BooleanProperty BOTTOM = BooleanProperty.create((String)"bottom");
    public static final EnumProperty<Shape> SHAPE = EnumProperty.create((String)"shape", Shape.class);
    private boolean creative;
    static final VoxelShape CAMPFIRE_SMOKE_CLIP = Block.box((double)0.0, (double)4.0, (double)0.0, (double)16.0, (double)16.0, (double)16.0);
    public static final SoundType SILENCED_METAL = new DeferredSoundType(0.1f, 1.5f, () -> SoundEvents.METAL_BREAK, () -> SoundEvents.METAL_STEP, () -> SoundEvents.METAL_PLACE, () -> SoundEvents.METAL_HIT, () -> SoundEvents.METAL_FALL);

    public static FluidTankBlock regular(BlockBehaviour.Properties p_i48440_1_) {
        return new FluidTankBlock(p_i48440_1_, false);
    }

    public static FluidTankBlock creative(BlockBehaviour.Properties p_i48440_1_) {
        return new FluidTankBlock(p_i48440_1_, true);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
    }

    protected FluidTankBlock(BlockBehaviour.Properties p_i48440_1_, boolean creative) {
        super(p_i48440_1_);
        this.creative = creative;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue((Property)TOP, (Comparable)Boolean.valueOf(true))).setValue((Property)BOTTOM, (Comparable)Boolean.valueOf(true))).setValue(SHAPE, (Comparable)((Object)Shape.WINDOW)));
    }

    public static boolean isTank(BlockState state) {
        return state.getBlock() instanceof FluidTankBlock;
    }

    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean moved) {
        if (oldState.getBlock() == state.getBlock()) {
            return;
        }
        if (moved) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)world, pos, FluidTankBlockEntity::updateConnectivity);
        BlockState newState = world.getBlockState(pos);
        if (state != newState && newState.getBlock() == this) {
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), oldState, newState, 11, 512);
        }
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(new Property[]{TOP, BOTTOM, SHAPE});
    }

    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        FluidTankBlockEntity tankAt = (FluidTankBlockEntity)ConnectivityHandler.partAt(this.getBlockEntityType(), world, pos);
        if (tankAt == null || !tankAt.hasLevel()) {
            return 0;
        }
        FluidTankBlockEntity controllerBE = tankAt.getControllerBE();
        if (controllerBE == null || !controllerBE.window) {
            return 0;
        }
        return tankAt.luminosity;
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        this.withBlockEntityDo((BlockGetter)context.getLevel(), context.getClickedPos(), FluidTankBlockEntity::toggleWindows);
        return InteractionResult.SUCCESS;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pContext == CollisionContext.empty()) {
            return CAMPFIRE_SMOKE_CLIP;
        }
        return pState.getShape(pLevel, pPos);
    }

    public VoxelShape getBlockSupportShape(BlockState pState, BlockGetter pReader, BlockPos pPos) {
        return Shapes.block();
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        if (pDirection == Direction.DOWN && pNeighborState.getBlock() != this) {
            this.withBlockEntityDo((BlockGetter)pLevel, pCurrentPos, FluidTankBlockEntity::updateBoilerTemperature);
        }
        return pState;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        FluidTankBlockEntity controllerBE;
        Fluid fluid;
        boolean onClient = level.isClientSide;
        if (stack.isEmpty()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!player.isCreative() && !this.creative) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        FluidHelper.FluidExchange exchange = null;
        FluidTankBlockEntity be = (FluidTankBlockEntity)ConnectivityHandler.partAt(this.getBlockEntityType(), (BlockGetter)level, pos);
        if (be == null) {
            return ItemInteractionResult.FAIL;
        }
        IFluidHandler tankCapability = (IFluidHandler)level.getCapability(Capabilities.FluidHandler.BLOCK, be.getBlockPos(), null);
        if (tankCapability == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        FluidStack prevFluidInTank = tankCapability.getFluidInTank(0).copy();
        if (FluidHelper.tryEmptyItemIntoBE(level, player, hand, stack, be)) {
            exchange = FluidHelper.FluidExchange.ITEM_TO_TANK;
        } else if (FluidHelper.tryFillItemFromBE(level, player, hand, stack, be)) {
            exchange = FluidHelper.FluidExchange.TANK_TO_ITEM;
        }
        if (exchange == null) {
            if (GenericItemEmptying.canItemBeEmptied(level, stack) || GenericItemFilling.canItemBeFilled(level, stack)) {
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        SoundEvent soundevent = null;
        BlockState fluidState = null;
        FluidStack fluidInTank = tankCapability.getFluidInTank(0);
        if (exchange == FluidHelper.FluidExchange.ITEM_TO_TANK) {
            FluidStack fluidInItem;
            if (this.creative && !onClient && !(fluidInItem = (FluidStack)GenericItemEmptying.emptyItem(level, stack, true).getFirst()).isEmpty() && tankCapability instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank) {
                ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank)tankCapability).setContainedFluid(fluidInItem);
            }
            fluid = fluidInTank.getFluid();
            fluidState = fluid.defaultFluidState().createLegacyBlock();
            soundevent = FluidHelper.getEmptySound(fluidInTank);
        }
        if (exchange == FluidHelper.FluidExchange.TANK_TO_ITEM) {
            if (this.creative && !onClient && tankCapability instanceof CreativeFluidTankBlockEntity.CreativeSmartFluidTank) {
                ((CreativeFluidTankBlockEntity.CreativeSmartFluidTank)tankCapability).setContainedFluid(FluidStack.EMPTY);
            }
            fluid = prevFluidInTank.getFluid();
            fluidState = fluid.defaultFluidState().createLegacyBlock();
            soundevent = FluidHelper.getFillSound(prevFluidInTank);
        }
        if (soundevent != null && !onClient) {
            float pitch = Mth.clamp((float)(1.0f - 1.0f * (float)fluidInTank.getAmount() / (float)(FluidTankBlockEntity.getCapacityMultiplier() * 16)), (float)0.0f, (float)1.0f);
            pitch /= 1.5f;
            pitch += 0.5f;
            level.playSound(null, pos, soundevent, SoundSource.BLOCKS, 0.5f, pitch += (level.random.nextFloat() - 0.5f) / 4.0f);
        }
        if (!FluidStack.isSameFluidSameComponents((FluidStack)fluidInTank, (FluidStack)prevFluidInTank) && be instanceof FluidTankBlockEntity && (controllerBE = be.getControllerBE()) != null) {
            if (fluidState != null && onClient) {
                BlockParticleOption blockParticleData = new BlockParticleOption(ParticleTypes.BLOCK, fluidState);
                float fluidLevel = (float)fluidInTank.getAmount() / (float)tankCapability.getTankCapacity(0);
                boolean reversed = fluidInTank.getFluid().getFluidType().isLighterThanAir();
                if (reversed) {
                    fluidLevel = 1.0f - fluidLevel;
                }
                Vec3 vec = hitResult.getLocation();
                vec = new Vec3(vec.x, (double)((float)controllerBE.getBlockPos().getY() + fluidLevel * ((float)controllerBE.height - 0.5f) + 0.25f), vec.z);
                Vec3 motion = player.position().subtract(vec).scale((double)0.05f);
                vec = vec.add(motion);
                level.addParticle((ParticleOptions)blockParticleData, vec.x, vec.y, vec.z, motion.x, motion.y, motion.z);
                return ItemInteractionResult.SUCCESS;
            }
            controllerBE.sendDataImmediately();
            controllerBE.setChanged();
        }
        return ItemInteractionResult.SUCCESS;
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.hasBlockEntity() && (state.getBlock() != newState.getBlock() || !newState.hasBlockEntity())) {
            BlockEntity be = world.getBlockEntity(pos);
            if (!(be instanceof FluidTankBlockEntity)) {
                return;
            }
            FluidTankBlockEntity tankBE = (FluidTankBlockEntity)be;
            world.removeBlockEntity(pos);
            ConnectivityHandler.splitMulti(tankBE);
        }
    }

    @Override
    public Class<FluidTankBlockEntity> getBlockEntityClass() {
        return FluidTankBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FluidTankBlockEntity> getBlockEntityType() {
        return this.creative ? (BlockEntityType)AllBlockEntityTypes.CREATIVE_FLUID_TANK.get() : (BlockEntityType)AllBlockEntityTypes.FLUID_TANK.get();
    }

    public BlockState mirror(BlockState state, Mirror mirror) {
        if (mirror == Mirror.NONE) {
            return state;
        }
        boolean x = mirror == Mirror.FRONT_BACK;
        switch (((Shape)((Object)state.getValue(SHAPE))).ordinal()) {
            case 4: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(x ? Shape.WINDOW_NW : Shape.WINDOW_SE)));
            }
            case 2: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(x ? Shape.WINDOW_NE : Shape.WINDOW_SW)));
            }
            case 5: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(x ? Shape.WINDOW_SW : Shape.WINDOW_NE)));
            }
            case 3: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)(x ? Shape.WINDOW_SE : Shape.WINDOW_NW)));
            }
        }
        return state;
    }

    public BlockState rotate(BlockState state, Rotation rotation) {
        for (int i = 0; i < rotation.ordinal(); ++i) {
            state = this.rotateOnce(state);
        }
        return state;
    }

    private BlockState rotateOnce(BlockState state) {
        switch (((Shape)((Object)state.getValue(SHAPE))).ordinal()) {
            case 4: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.WINDOW_SE));
            }
            case 2: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.WINDOW_NE));
            }
            case 5: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.WINDOW_SW));
            }
            case 3: {
                return (BlockState)state.setValue(SHAPE, (Comparable)((Object)Shape.WINDOW_NW));
            }
        }
        return state;
    }

    public SoundType getSoundType(BlockState state, LevelReader world, BlockPos pos, Entity entity) {
        SoundType soundType = super.getSoundType(state, world, pos, entity);
        if (entity != null && entity.getPersistentData().contains("SilenceTankSound")) {
            return SILENCED_METAL;
        }
        return soundType;
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState blockState, Level worldIn, BlockPos pos) {
        return this.getBlockEntityOptional((BlockGetter)worldIn, pos).map(FluidTankBlockEntity::getControllerBE).map(be -> ComparatorUtil.fractionToRedstoneLevel(be.getFillState())).orElse(0);
    }

    public static void updateBoilerState(BlockState pState, Level pLevel, BlockPos tankPos) {
        BlockState tankState = pLevel.getBlockState(tankPos);
        Block block = tankState.getBlock();
        if (!(block instanceof FluidTankBlock)) {
            return;
        }
        FluidTankBlock tank = (FluidTankBlock)block;
        FluidTankBlockEntity tankBE = (FluidTankBlockEntity)tank.getBlockEntity((BlockGetter)pLevel, tankPos);
        if (tankBE == null) {
            return;
        }
        FluidTankBlockEntity controllerBE = tankBE.getControllerBE();
        if (controllerBE == null) {
            return;
        }
        controllerBE.updateBoilerState();
    }

    public static enum Shape implements StringRepresentable
    {
        PLAIN,
        WINDOW,
        WINDOW_NW,
        WINDOW_SW,
        WINDOW_NE,
        WINDOW_SE;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
