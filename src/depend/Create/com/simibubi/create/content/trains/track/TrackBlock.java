/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.Affine
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.BlockFace
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.levelgen.structure.BoundingBox
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.level.material.PushReaction
 *  net.minecraft.world.level.pathfinder.PathType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.minecraft.world.ticks.LevelTickAccess
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.track;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.train.PortalTrackProvider;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.decoration.girder.GirderBlock;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.trains.CubeParticleData;
import com.simibubi.create.content.trains.graph.TrackNodeLocation;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.BezierTrackPointLocation;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.content.trains.track.TrackBlockEntity;
import com.simibubi.create.content.trains.track.TrackMaterial;
import com.simibubi.create.content.trains.track.TrackPropagator;
import com.simibubi.create.content.trains.track.TrackRenderer;
import com.simibubi.create.content.trains.track.TrackShape;
import com.simibubi.create.content.trains.track.TrackTargetingBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.IHaveBigOutline;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.block.render.MultiPosDestructionHandler;
import com.simibubi.create.foundation.block.render.ReducedDestroyEffects;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.Affine;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.BlockFace;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.LevelTickAccess;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class TrackBlock
extends Block
implements IBE<TrackBlockEntity>,
IWrenchable,
ITrackBlock,
SpecialBlockItemRequirement,
ProperWaterloggedBlock,
IHaveBigOutline {
    public static final EnumProperty<TrackShape> SHAPE = EnumProperty.create((String)"shape", TrackShape.class);
    public static final BooleanProperty HAS_BE = BooleanProperty.create((String)"turn");
    protected final TrackMaterial material;

    public TrackBlock(BlockBehaviour.Properties p_49795_, TrackMaterial material) {
        super(p_49795_);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(SHAPE, (Comparable)((Object)TrackShape.ZO))).setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(false))).setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false)));
        this.material = material;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_49915_) {
        super.createBlockStateDefinition(p_49915_.add(new Property[]{SHAPE, HAS_BE, WATERLOGGED}));
    }

    @Nullable
    public PathType getBlockPathType(BlockState state, BlockGetter level, BlockPos pos, @Nullable Mob mob) {
        return PathType.RAIL;
    }

    public FluidState getFluidState(BlockState state) {
        return this.fluidState(state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState stateForPlacement = this.withWater(super.getStateForPlacement(ctx), ctx);
        if (ctx.getPlayer() == null) {
            return stateForPlacement;
        }
        Vec3 lookAngle = ctx.getPlayer().getLookAngle();
        if (Mth.equal((double)(lookAngle = lookAngle.multiply(1.0, 0.0, 1.0)).length(), (double)0.0)) {
            lookAngle = VecHelper.rotate((Vec3)new Vec3(0.0, 0.0, 1.0), (double)(-ctx.getPlayer().getYRot()), (Direction.Axis)Direction.Axis.Y);
        }
        lookAngle = lookAngle.normalize();
        TrackShape best = TrackShape.ZO;
        double bestValue = 3.4028234663852886E38;
        for (TrackShape shape : TrackShape.values()) {
            Vec3 axis;
            double distance;
            if (shape.isJunction() || shape.isPortal() || (distance = Math.min((axis = shape.getAxes().get(0)).distanceToSqr(lookAngle), axis.normalize().scale(-1.0).distanceToSqr(lookAngle))) > bestValue) continue;
            bestValue = distance;
            best = shape;
        }
        Level level = ctx.getLevel();
        Vec3 bestAxis = best.getAxes().get(0);
        if (bestAxis.lengthSqr() == 1.0) {
            for (boolean neg : Iterate.trueAndFalse) {
                BlockPos offset = ctx.getClickedPos().offset((Vec3i)BlockPos.containing((Position)bestAxis.scale(neg ? -1.0 : 1.0)));
                if (!level.getBlockState(offset).isFaceSturdy((BlockGetter)level, offset, Direction.UP) || level.getBlockState(offset.above()).isFaceSturdy((BlockGetter)level, offset, Direction.DOWN)) continue;
                if (best == TrackShape.XO) {
                    TrackShape trackShape = best = neg ? TrackShape.AW : TrackShape.AE;
                }
                if (best != TrackShape.ZO) continue;
                best = neg ? TrackShape.AN : TrackShape.AS;
            }
        }
        return (BlockState)stateForPlacement.setValue(SHAPE, (Comparable)((Object)best));
    }

    public PushReaction getPistonPushReaction(BlockState pState) {
        return PushReaction.BLOCK;
    }

    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        if (pLevel.isClientSide()) {
            return pState;
        }
        if (!pPlayer.isCreative()) {
            return pState;
        }
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, be -> {
            be.cancelDrops = true;
            be.removeInboundConnections(true);
        });
        return pState;
    }

    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        if (pOldState.getBlock() == this && pState.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true)) == pOldState.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true))) {
            return;
        }
        if (pLevel.isClientSide) {
            return;
        }
        LevelTickAccess blockTicks = pLevel.getBlockTicks();
        if (!blockTicks.hasScheduledTick(pPos, (Object)this)) {
            pLevel.scheduleTick(pPos, (Block)this, 1);
        }
        this.updateGirders(pState, pLevel, pPos, (LevelTickAccess<Block>)blockTicks);
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, TrackBlockEntity::validateConnections);
    }

    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource p_60465_) {
        TrackPropagator.onRailAdded((LevelAccessor)level, pos, state);
        this.withBlockEntityDo((BlockGetter)level, pos, tbe -> tbe.tilt.undoSmoothing());
        if (!((TrackShape)((Object)state.getValue(SHAPE))).isPortal()) {
            this.connectToPortal(level, pos, state);
        }
    }

    protected void connectToPortal(ServerLevel level, BlockPos pos, BlockState state) {
        Direction.Axis portalTest;
        TrackShape shape = (TrackShape)((Object)state.getValue(SHAPE));
        Object object = shape == TrackShape.XO ? Direction.Axis.X : (portalTest = shape == TrackShape.ZO ? Direction.Axis.Z : null);
        if (portalTest == null) {
            return;
        }
        boolean pop = false;
        String fail = null;
        BlockPos failPos = null;
        for (Direction d : Iterate.directionsInAxis((Direction.Axis)portalTest)) {
            BlockFace otherTrack;
            BlockPos otherTrackPos;
            BlockPos portalPos = pos.relative(d);
            BlockState portalState = level.getBlockState(portalPos);
            if (!PortalTrackProvider.isSupportedPortal(portalState)) continue;
            pop = true;
            PortalTrackProvider.Exit otherSide = PortalTrackProvider.getOtherSide(level, new BlockFace(pos, d));
            if (otherSide == null) {
                fail = "missing";
                continue;
            }
            ServerLevel otherLevel = otherSide.level();
            BlockState existing = otherLevel.getBlockState(otherTrackPos = (otherTrack = otherSide.face()).getPos());
            if (!existing.canBeReplaced()) {
                fail = "blocked";
                failPos = otherTrackPos;
                continue;
            }
            level.setBlock(pos, (BlockState)((BlockState)state.setValue(SHAPE, (Comparable)((Object)TrackShape.asPortal(d)))).setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true)), 3);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TrackBlockEntity) {
                TrackBlockEntity tbe = (TrackBlockEntity)be;
                tbe.bind((ResourceKey<Level>)otherLevel.dimension(), otherTrackPos);
            }
            BlockState otherState = ProperWaterloggedBlock.withWater((LevelAccessor)otherLevel, (BlockState)((BlockState)state.setValue(SHAPE, (Comparable)((Object)TrackShape.asPortal(otherTrack.getFace())))).setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true)), otherTrackPos);
            otherLevel.setBlock(otherTrackPos, otherState, 3);
            BlockEntity otherBE = otherLevel.getBlockEntity(otherTrackPos);
            if (otherBE instanceof TrackBlockEntity) {
                TrackBlockEntity tbe = (TrackBlockEntity)otherBE;
                tbe.bind((ResourceKey<Level>)level.dimension(), pos);
            }
            pop = false;
        }
        if (!pop) {
            return;
        }
        level.destroyBlock(pos, true);
        if (fail == null) {
            return;
        }
        Player player = level.getNearestPlayer((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), 10.0, (Predicate)Predicates.alwaysTrue());
        if (player == null) {
            return;
        }
        player.displayClientMessage((Component)Component.literal((String)"<!> ").append((Component)CreateLang.translateDirect("portal_track.failed", new Object[0])).withStyle(ChatFormatting.GOLD), false);
        MutableComponent component = failPos != null ? CreateLang.translateDirect("portal_track." + fail, failPos.getX(), failPos.getY(), failPos.getZ()) : CreateLang.translateDirect("portal_track." + fail, new Object[0]);
        player.displayClientMessage((Component)Component.literal((String)" - ").withStyle(ChatFormatting.GRAY).append((Component)component.withStyle(st -> st.withColor(16765876))), false);
    }

    public BlockState updateShape(BlockState state, Direction pDirection, BlockState pNeighborState, LevelAccessor level, BlockPos pCurrentPos, BlockPos pNeighborPos) {
        this.updateWater(level, state, pCurrentPos);
        TrackShape shape = (TrackShape)((Object)state.getValue(SHAPE));
        if (!shape.isPortal()) {
            return state;
        }
        for (Direction d : Iterate.horizontalDirections) {
            BlockPos portalPos;
            BlockState portalState;
            if (TrackShape.asPortal(d) != state.getValue(SHAPE) || pDirection != d || PortalTrackProvider.isSupportedPortal(portalState = level.getBlockState(portalPos = pCurrentPos.relative(d)))) continue;
            return Blocks.AIR.defaultBlockState();
        }
        return state;
    }

    @Override
    public int getYOffsetAt(BlockGetter world, BlockPos pos, BlockState state, Vec3 end) {
        return this.getBlockEntityOptional(world, pos).map(tbe -> tbe.tilt.getYOffsetForAxisEnd(end)).orElse(0);
    }

    @Override
    public Collection<TrackNodeLocation.DiscoveredLocation> getConnected(BlockGetter worldIn, BlockPos pos, BlockState state, boolean linear, TrackNodeLocation connectedTo) {
        Collection<TrackNodeLocation.DiscoveredLocation> list;
        BlockGetter world;
        if (connectedTo != null && worldIn instanceof ServerLevel) {
            ServerLevel sl = (ServerLevel)worldIn;
            v0 = sl.getServer().getLevel(connectedTo.dimension);
        } else {
            v0 = world = worldIn;
        }
        if (this.getTrackAxes(world, pos, state).size() > 1) {
            Vec3 center = Vec3.atBottomCenterOf((Vec3i)pos).add(0.0, this.getElevationAtCenter(world, pos, state), 0.0);
            TrackShape shape = (TrackShape)((Object)state.getValue(SHAPE));
            list = new ArrayList<TrackNodeLocation.DiscoveredLocation>();
            for (Vec3 axis2 : this.getTrackAxes(world, pos, state)) {
                for (boolean fromCenter : Iterate.trueAndFalse) {
                    ITrackBlock.addToListIfConnected(connectedTo, list, (d, b) -> axis2.scale(b != false ? 0.0 : (fromCenter ? -d.doubleValue() : d)).add(center), b -> shape.getNormal(), b -> {
                        ResourceKey resourceKey;
                        if (world instanceof Level) {
                            Level l = (Level)world;
                            resourceKey = l.dimension();
                        } else {
                            resourceKey = Level.OVERWORLD;
                        }
                        return resourceKey;
                    }, v -> 0, axis2, null, (b, v) -> ITrackBlock.getMaterialSimple(world, v));
                }
            }
        } else {
            list = ITrackBlock.super.getConnected(world, pos, state, linear, connectedTo);
        }
        if (!((Boolean)state.getValue((Property)HAS_BE)).booleanValue()) {
            return list;
        }
        if (linear) {
            return list;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TrackBlockEntity)) {
            return list;
        }
        TrackBlockEntity trackBE = (TrackBlockEntity)blockEntity;
        Map<BlockPos, BezierConnection> connections = trackBE.getConnections();
        connections.forEach((connectedPos, bc) -> ITrackBlock.addToListIfConnected(connectedTo, list, (d, b) -> d == 1.0 ? Vec3.atLowerCornerOf((Vec3i)((Vec3i)bc.bePositions.get(b.booleanValue()))) : (Vec3)bc.starts.get(b.booleanValue()), arg_0 -> bc.normals.get(arg_0), b -> {
            ResourceKey resourceKey;
            if (world instanceof Level) {
                Level l = (Level)world;
                resourceKey = l.dimension();
            } else {
                resourceKey = Level.OVERWORLD;
            }
            return resourceKey;
        }, bc::yOffsetAt, null, bc, (b, v) -> ITrackBlock.getMaterialSimple(world, v, bc.getMaterial())));
        if (trackBE.boundLocation == null || !(world instanceof ServerLevel)) {
            return list;
        }
        ServerLevel level = (ServerLevel)world;
        ResourceKey otherDim = (ResourceKey)trackBE.boundLocation.getFirst();
        ServerLevel otherLevel = level.getServer().getLevel(otherDim);
        if (otherLevel == null) {
            return list;
        }
        BlockPos boundPos = (BlockPos)trackBE.boundLocation.getSecond();
        BlockState boundState = otherLevel.getBlockState(boundPos);
        if (!AllTags.AllBlockTags.TRACKS.matches(boundState)) {
            return list;
        }
        Vec3 center = Vec3.atBottomCenterOf((Vec3i)pos).add(0.0, this.getElevationAtCenter(world, pos, state), 0.0);
        Vec3 boundCenter = Vec3.atBottomCenterOf((Vec3i)boundPos).add(0.0, this.getElevationAtCenter((BlockGetter)otherLevel, boundPos, boundState), 0.0);
        TrackShape shape = (TrackShape)((Object)state.getValue(SHAPE));
        TrackShape boundShape = (TrackShape)((Object)boundState.getValue(SHAPE));
        Vec3 boundAxis = this.getTrackAxes((BlockGetter)otherLevel, boundPos, boundState).get(0);
        this.getTrackAxes(world, pos, state).forEach(axis -> ITrackBlock.addToListIfConnected(connectedTo, list, (d, b) -> (b != false ? axis : boundAxis).scale(d.doubleValue()).add(b != false ? center : boundCenter), b -> (b != false ? shape : boundShape).getNormal(), b -> b != false ? level.dimension() : otherLevel.dimension(), v -> 0, axis, null, (b, v) -> ITrackBlock.getMaterialSimple((BlockGetter)(b != false ? level : otherLevel), v)));
        return list;
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRand) {
        if (!((TrackShape)((Object)pState.getValue(SHAPE))).isPortal()) {
            return;
        }
        Vec3 v = Vec3.atLowerCornerOf((Vec3i)pPos).subtract(0.125, 0.0, 0.125);
        CubeParticleData data = new CubeParticleData(1.0f, pRand.nextFloat(), 1.0f, 0.0125f + 0.0625f * pRand.nextFloat(), 30, false);
        pLevel.addParticle((ParticleOptions)data, v.x + (double)(pRand.nextFloat() * 1.5f), v.y + 0.25, v.z + (double)(pRand.nextFloat() * 1.5f), 0.0, 0.04, 0.0);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean removeBE = false;
        if (!(!((Boolean)pState.getValue((Property)HAS_BE)).booleanValue() || pState.is(pNewState.getBlock()) && ((Boolean)pNewState.getValue((Property)HAS_BE)).booleanValue())) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof TrackBlockEntity) {
                TrackBlockEntity tbe = (TrackBlockEntity)blockEntity;
                if (!pLevel.isClientSide) {
                    tbe.cancelDrops = tbe.cancelDrops | pNewState.getBlock() == this;
                    tbe.removeInboundConnections(true);
                }
            }
            removeBE = true;
        }
        if (pNewState.getBlock() != this || pState.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true)) != pNewState.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(true))) {
            TrackPropagator.onRailRemoved((LevelAccessor)pLevel, pPos, pState);
        }
        if (removeBE) {
            pLevel.removeBlockEntity(pPos);
        }
        if (!pLevel.isClientSide) {
            this.updateGirders(pState, pLevel, pPos, (LevelTickAccess<Block>)pLevel.getBlockTicks());
        }
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        for (Map.Entry entry : ((Map)StationBlockEntity.assemblyAreas.get((LevelAccessor)level)).entrySet()) {
            StationBlockEntity station;
            BlockEntity blockEntity;
            if (!((BoundingBox)entry.getValue()).isInside((Vec3i)pos) || !((blockEntity = level.getBlockEntity((BlockPos)entry.getKey())) instanceof StationBlockEntity) || !(station = (StationBlockEntity)blockEntity).trackClicked(player, hand, this, state, pos)) continue;
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private void updateGirders(BlockState pState, Level pLevel, BlockPos pPos, LevelTickAccess<Block> blockTicks) {
        for (Vec3 vec3 : this.getTrackAxes((BlockGetter)pLevel, pPos, pState)) {
            if (vec3.length() > 1.0 || vec3.y != 0.0) continue;
            for (int side : Iterate.positiveAndNegative) {
                GirderBlock girderBlock;
                BlockPos girderPos = pPos.below().offset((Vec3i)BlockPos.containing((double)(vec3.z * (double)side), (double)0.0, (double)(vec3.x * (double)side)));
                BlockState girderState = pLevel.getBlockState(girderPos);
                Block block = girderState.getBlock();
                if (!(block instanceof GirderBlock) || blockTicks.hasScheduledTick(girderPos, (Object)(girderBlock = (GirderBlock)block))) continue;
                pLevel.scheduleTick(girderPos, (Block)girderBlock, 1);
            }
        }
    }

    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return reader.getBlockState(pos.below()).getBlock() != this;
    }

    public VoxelShape getShape(BlockState state, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return this.getFullShape(state);
    }

    public VoxelShape getInteractionShape(BlockState state, BlockGetter pLevel, BlockPos pPos) {
        return this.getFullShape(state);
    }

    private VoxelShape getFullShape(BlockState state) {
        switch ((TrackShape)((Object)state.getValue(SHAPE))) {
            case AE: {
                return AllShapes.TRACK_ASC.get(Direction.EAST);
            }
            case AW: {
                return AllShapes.TRACK_ASC.get(Direction.WEST);
            }
            case AN: {
                return AllShapes.TRACK_ASC.get(Direction.NORTH);
            }
            case AS: {
                return AllShapes.TRACK_ASC.get(Direction.SOUTH);
            }
            case CR_D: {
                return AllShapes.TRACK_CROSS_DIAG;
            }
            case CR_NDX: {
                return AllShapes.TRACK_CROSS_ORTHO_DIAG.get(Direction.SOUTH);
            }
            case CR_NDZ: {
                return AllShapes.TRACK_CROSS_DIAG_ORTHO.get(Direction.SOUTH);
            }
            case CR_O: {
                return AllShapes.TRACK_CROSS;
            }
            case CR_PDX: {
                return AllShapes.TRACK_CROSS_DIAG_ORTHO.get(Direction.EAST);
            }
            case CR_PDZ: {
                return AllShapes.TRACK_CROSS_ORTHO_DIAG.get(Direction.EAST);
            }
            case ND: {
                return AllShapes.TRACK_DIAG.get(Direction.SOUTH);
            }
            case PD: {
                return AllShapes.TRACK_DIAG.get(Direction.EAST);
            }
            case XO: {
                return AllShapes.TRACK_ORTHO.get(Direction.EAST);
            }
            case ZO: {
                return AllShapes.TRACK_ORTHO.get(Direction.SOUTH);
            }
            case TE: {
                return AllShapes.TRACK_ORTHO_LONG.get(Direction.EAST);
            }
            case TW: {
                return AllShapes.TRACK_ORTHO_LONG.get(Direction.WEST);
            }
            case TS: {
                return AllShapes.TRACK_ORTHO_LONG.get(Direction.SOUTH);
            }
            case TN: {
                return AllShapes.TRACK_ORTHO_LONG.get(Direction.NORTH);
            }
        }
        return AllShapes.TRACK_FALLBACK;
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch ((TrackShape)((Object)pState.getValue(SHAPE))) {
            case AE: 
            case AW: 
            case AN: 
            case AS: {
                return Shapes.empty();
            }
        }
        return AllShapes.TRACK_COLLISION;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState state) {
        if (!((Boolean)state.getValue((Property)HAS_BE)).booleanValue()) {
            return null;
        }
        return AllBlockEntityTypes.TRACK.create(p_153215_, state);
    }

    @Override
    public Class<TrackBlockEntity> getBlockEntityClass() {
        return TrackBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends TrackBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.TRACK.get();
    }

    @Override
    public Vec3 getUpNormal(BlockGetter world, BlockPos pos, BlockState state) {
        return ((TrackShape)((Object)state.getValue(SHAPE))).getNormal();
    }

    @Override
    public List<Vec3> getTrackAxes(BlockGetter world, BlockPos pos, BlockState state) {
        return ((TrackShape)((Object)state.getValue(SHAPE))).getAxes();
    }

    @Override
    public Vec3 getCurveStart(BlockGetter world, BlockPos pos, BlockState state, Vec3 axis) {
        boolean vertical = axis.y != 0.0;
        return VecHelper.getCenterOf((Vec3i)pos).add(0.0, (double)(vertical ? 0.0f : -0.5f), 0.0).add(axis.scale(0.5));
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        BlockEntity blockEntity;
        Player player = context.getPlayer();
        Level level = context.getLevel();
        if (!level.isClientSide && !player.isCreative() && ((Boolean)state.getValue((Property)HAS_BE)).booleanValue() && (blockEntity = level.getBlockEntity(context.getClickedPos())) instanceof TrackBlockEntity) {
            TrackBlockEntity trackBE = (TrackBlockEntity)blockEntity;
            trackBE.cancelDrops = true;
            trackBE.connections.values().forEach(bc -> bc.addItemsToPlayer(player));
        }
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public BlockState overlay(BlockGetter world, BlockPos pos, BlockState existing, BlockState placed) {
        if (placed.getBlock() != this) {
            return existing;
        }
        TrackShape existingShape = (TrackShape)((Object)existing.getValue(SHAPE));
        TrackShape placedShape = (TrackShape)((Object)placed.getValue(SHAPE));
        TrackShape combinedShape = null;
        for (boolean flip : Iterate.trueAndFalse) {
            TrackShape s2;
            TrackShape s1 = flip ? existingShape : placedShape;
            TrackShape trackShape = s2 = flip ? placedShape : existingShape;
            if (s1 == TrackShape.XO && s2 == TrackShape.ZO) {
                combinedShape = TrackShape.CR_O;
            }
            if (s1 == TrackShape.PD && s2 == TrackShape.ND) {
                combinedShape = TrackShape.CR_D;
            }
            if (s1 == TrackShape.XO && s2 == TrackShape.PD) {
                combinedShape = TrackShape.CR_PDX;
            }
            if (s1 == TrackShape.ZO && s2 == TrackShape.PD) {
                combinedShape = TrackShape.CR_PDZ;
            }
            if (s1 == TrackShape.XO && s2 == TrackShape.ND) {
                combinedShape = TrackShape.CR_NDX;
            }
            if (s1 != TrackShape.ZO || s2 != TrackShape.ND) continue;
            combinedShape = TrackShape.CR_NDZ;
        }
        if (combinedShape != null) {
            existing = (BlockState)existing.setValue(SHAPE, combinedShape);
        }
        return existing;
    }

    public BlockState rotate(BlockState state, Rotation pRotation) {
        return (BlockState)state.setValue(SHAPE, (Comparable)((Object)((TrackShape)((Object)state.getValue(SHAPE))).rotate(pRotation)));
    }

    public BlockState mirror(BlockState state, Mirror pMirror) {
        return (BlockState)state.setValue(SHAPE, (Comparable)((Object)((TrackShape)((Object)state.getValue(SHAPE))).mirror(pMirror)));
    }

    @Override
    public BlockState getBogeyAnchor(BlockGetter world, BlockPos pos, BlockState state) {
        return (BlockState)AllBlocks.SMALL_BOGEY.getDefaultState().setValue((Property)BlockStateProperties.HORIZONTAL_AXIS, (Comparable)(state.getValue(SHAPE) == TrackShape.XO ? Direction.Axis.X : Direction.Axis.Z));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public PartialModel prepareAssemblyOverlay(BlockGetter world, BlockPos pos, BlockState state, Direction direction, PoseStack ms) {
        TransformStack.of((PoseStack)ms).rotateCentered(AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)direction)), Direction.UP);
        return AllPartialModels.TRACK_ASSEMBLING_OVERLAY;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public <Self extends Affine<Self>> PartialModel prepareTrackOverlay(Affine<Self> affine, BlockGetter world, BlockPos pos, BlockState state, BezierTrackPointLocation bezierPoint, Direction.AxisDirection direction, TrackTargetingBehaviour.RenderedTrackOverlayType type) {
        TrackBlockEntity trackTE;
        BlockEntity length2;
        BlockEntity blockEntity;
        Vec3 axis = null;
        Vec3 diff = null;
        Vec3 normal = null;
        Vec3 offset = null;
        if (bezierPoint != null && (blockEntity = world.getBlockEntity(pos)) instanceof TrackBlockEntity) {
            TrackBlockEntity trackBE = (TrackBlockEntity)blockEntity;
            BezierConnection bc = trackBE.connections.get(bezierPoint.curveTarget());
            if (bc != null) {
                double length2 = Mth.floor((double)(bc.getLength() * 2.0));
                int seg = bezierPoint.segment() + 1;
                double t = (double)seg / length2;
                double tpre = (double)(seg - 1) / length2;
                double tpost = (double)(seg + 1) / length2;
                offset = bc.getPosition(t);
                normal = bc.getNormal(t);
                diff = bc.getPosition(tpost).subtract(bc.getPosition(tpre)).normalize();
                affine.translate(offset.subtract(Vec3.atBottomCenterOf((Vec3i)pos)));
                affine.translate(0.0f, -0.25f, 0.0f);
            } else {
                return null;
            }
        }
        if (normal == null) {
            axis = ((TrackShape)((Object)state.getValue(SHAPE))).getAxes().get(0);
            diff = axis.scale((double)direction.getStep()).normalize();
            normal = this.getUpNormal(world, pos, state);
        }
        Vec3 angles = TrackRenderer.getModelAngles(normal, diff);
        ((Affine)((Affine)((Affine)affine.center()).rotateY((float)angles.y)).rotateX((float)angles.x)).uncenter();
        if (axis != null) {
            affine.translate(0.0f, axis.y != 0.0 ? 0.4375f : 0.0f, axis.y != 0.0 ? (float)direction.getStep() * 2.5f / 16.0f : 0.0f);
        } else {
            affine.translate(0.0f, 0.25f, 0.0f);
            if (direction == Direction.AxisDirection.NEGATIVE) {
                affine.rotateCentered((float)Math.PI, Direction.UP);
            }
        }
        if (bezierPoint == null && (length2 = world.getBlockEntity(pos)) instanceof TrackBlockEntity && (trackTE = (TrackBlockEntity)length2).isTilted()) {
            double yOffset = 0.0;
            for (BezierConnection bc : trackTE.connections.values()) {
                yOffset += ((Vec3)bc.starts.getFirst()).y - (double)pos.getY();
            }
            ((Affine)((Affine)((Affine)affine.center()).rotateXDegrees((float)((double)(-direction.getStep()) * trackTE.tilt.smoothingAngle.get()))).uncenter()).translate(0.0, yOffset / 2.0, 0.0);
        }
        return switch (type) {
            default -> throw new MatchException(null, null);
            case TrackTargetingBehaviour.RenderedTrackOverlayType.DUAL_SIGNAL -> AllPartialModels.TRACK_SIGNAL_DUAL_OVERLAY;
            case TrackTargetingBehaviour.RenderedTrackOverlayType.OBSERVER -> AllPartialModels.TRACK_OBSERVER_OVERLAY;
            case TrackTargetingBehaviour.RenderedTrackOverlayType.SIGNAL -> AllPartialModels.TRACK_SIGNAL_OVERLAY;
            case TrackTargetingBehaviour.RenderedTrackOverlayType.STATION -> AllPartialModels.TRACK_STATION_OVERLAY;
        };
    }

    @Override
    public boolean trackEquals(BlockState state1, BlockState state2) {
        return state1.getBlock() == this && state2.getBlock() == this && state1.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(false)) == state2.setValue((Property)HAS_BE, (Comparable)Boolean.valueOf(false));
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity be) {
        int sameTypeTrackAmount = 1;
        Object2IntArrayMap otherTrackAmounts = new Object2IntArrayMap();
        int girderAmount = 0;
        if (be instanceof TrackBlockEntity) {
            TrackBlockEntity track = (TrackBlockEntity)be;
            for (BezierConnection bezierConnection : track.getConnections().values()) {
                if (!bezierConnection.isPrimary()) continue;
                TrackMaterial material = bezierConnection.getMaterial();
                if (material == this.getMaterial()) {
                    sameTypeTrackAmount += bezierConnection.getTrackItemCost();
                } else {
                    otherTrackAmounts.put((Object)material, otherTrackAmounts.getOrDefault((Object)material, 0) + 1);
                }
                girderAmount += bezierConnection.getGirderItemCost();
            }
        }
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        while (sameTypeTrackAmount > 0) {
            stacks.add(new ItemStack((ItemLike)state.getBlock(), Math.min(sameTypeTrackAmount, 64)));
            sameTypeTrackAmount -= 64;
        }
        for (TrackMaterial material : otherTrackAmounts.keySet()) {
            for (int amt = otherTrackAmounts.getOrDefault((Object)material, 0); amt > 0; amt -= 64) {
                stacks.add(material.asStack(Math.min(amt, 64)));
            }
        }
        while (girderAmount > 0) {
            stacks.add(AllBlocks.METAL_GIRDER.asStack(Math.min(girderAmount, 64)));
            girderAmount -= 64;
        }
        return new ItemRequirement(ItemRequirement.ItemUseType.CONSUME, stacks);
    }

    @Override
    public TrackMaterial getMaterial() {
        return this.material;
    }

    public static class RenderProperties
    extends ReducedDestroyEffects
    implements MultiPosDestructionHandler {
        @Override
        @Nullable
        public Set<BlockPos> getExtraPositions(ClientLevel level, BlockPos pos, BlockState blockState, int progress) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TrackBlockEntity) {
                TrackBlockEntity track = (TrackBlockEntity)blockEntity;
                return new HashSet<BlockPos>(track.connections.keySet());
            }
            return null;
        }
    }
}
