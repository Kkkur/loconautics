/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.api.visualization.VisualizationContext
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.NbtUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.FallingBlock
 *  net.minecraft.world.level.block.SlabBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.block.state.properties.SlabType
 *  net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate$StructureBlockInfo
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.contraptions.actors.roller;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.actors.roller.PaveTask;
import com.simibubi.create.content.contraptions.actors.roller.RollerActorVisual;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlock;
import com.simibubi.create.content.contraptions.actors.roller.RollerBlockEntity;
import com.simibubi.create.content.contraptions.actors.roller.RollerRenderer;
import com.simibubi.create.content.contraptions.actors.roller.TrackPaverV2;
import com.simibubi.create.content.contraptions.behaviour.MovementContext;
import com.simibubi.create.content.contraptions.pulley.PulleyContraption;
import com.simibubi.create.content.contraptions.render.ActorVisual;
import com.simibubi.create.content.contraptions.render.ContraptionMatrices;
import com.simibubi.create.content.kinetics.base.BlockBreakingMovementBehaviour;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import com.simibubi.create.content.trains.bogey.StandardBogeyBlock;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.CarriageBogey;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.entity.TravellingPoint;
import com.simibubi.create.content.trains.graph.TrackEdge;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.foundation.damageTypes.CreateDamageSources;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.virtualWorld.VirtualRenderWorld;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

public class RollerMovementBehaviour
extends BlockBreakingMovementBehaviour {
    RollerTravellingPoint rollerScout = new RollerTravellingPoint(this);

    @Override
    public boolean isActive(MovementContext context) {
        return super.isActive(context) && !(context.contraption instanceof PulleyContraption) && VecHelper.isVecPointingTowards((Vec3)context.relativeMotion, (Direction)((Direction)context.state.getValue((Property)RollerBlock.FACING)));
    }

    @Override
    public boolean disableBlockEntityRendering() {
        return true;
    }

    @Override
    @Nullable
    public ActorVisual createVisual(VisualizationContext visualizationContext, VirtualRenderWorld simulationWorld, MovementContext movementContext) {
        return new RollerActorVisual(visualizationContext, simulationWorld, movementContext);
    }

    @Override
    public void renderInContraption(MovementContext context, VirtualRenderWorld renderWorld, ContraptionMatrices matrices, MultiBufferSource buffers) {
        if (!VisualizationManager.supportsVisualization((LevelAccessor)context.world)) {
            RollerRenderer.renderInContraption(context, renderWorld, matrices, buffers);
        }
    }

    @Override
    public Vec3 getActiveAreaOffset(MovementContext context) {
        return Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)RollerBlock.FACING)).getNormal()).scale(0.45).subtract(0.0, 2.0, 0.0);
    }

    @Override
    protected float getBlockBreakingSpeed(MovementContext context) {
        return Mth.clamp((float)(super.getBlockBreakingSpeed(context) * 1.5f), (float)0.0078125f, (float)16.0f);
    }

    @Override
    public boolean canBreak(Level world, BlockPos breakingPos, BlockState state) {
        for (Direction side : Iterate.directions) {
            if (!world.getBlockState(breakingPos.relative(side)).is(BlockTags.PORTALS)) continue;
            return false;
        }
        return super.canBreak(world, breakingPos, state) && !state.getCollisionShape((BlockGetter)world, breakingPos).isEmpty() && !AllTags.AllBlockTags.TRACKS.matches(state);
    }

    @Override
    protected DamageSource getDamageSource(Level level) {
        return CreateDamageSources.roller(level);
    }

    @Override
    public void visitNewPosition(MovementContext context, BlockPos pos) {
        Level world = context.world;
        BlockState stateVisited = world.getBlockState(pos);
        if (!stateVisited.isRedstoneConductor((BlockGetter)world, pos)) {
            this.damageEntities(context, pos, world);
        }
        if (world.isClientSide) {
            return;
        }
        List<BlockPos> positionsToBreak = this.getPositionsToBreak(context, pos);
        if (positionsToBreak.isEmpty()) {
            this.triggerPaver(context, pos);
            return;
        }
        BlockPos argMax = null;
        double max = -1.0;
        for (BlockPos toBreak : positionsToBreak) {
            float hardness = context.world.getBlockState(toBreak).getDestroySpeed((BlockGetter)world, toBreak);
            if ((double)hardness < max) continue;
            max = hardness;
            argMax = toBreak;
        }
        if (argMax == null) {
            this.triggerPaver(context, pos);
            return;
        }
        context.data.put("ReferencePos", NbtUtils.writeBlockPos((BlockPos)pos));
        context.data.put("BreakingPos", NbtUtils.writeBlockPos(argMax));
        context.stall = true;
    }

    @Override
    protected void onBlockBroken(MovementContext context, BlockPos pos, BlockState brokenState) {
        super.onBlockBroken(context, pos, brokenState);
        if (!context.data.contains("ReferencePos")) {
            return;
        }
        BlockPos referencePos = NBTHelper.readBlockPos((CompoundTag)context.data, (String)"ReferencePos");
        for (BlockPos otherPos : this.getPositionsToBreak(context, referencePos)) {
            if (otherPos.equals((Object)pos)) continue;
            this.destroyBlock(context, otherPos);
        }
        this.triggerPaver(context, referencePos);
        context.data.remove("ReferencePos");
    }

    @Override
    protected void destroyBlock(MovementContext context, BlockPos breakingPos) {
        BlockState blockState = context.world.getBlockState(breakingPos);
        boolean noHarvest = blockState.is(BlockTags.NEEDS_IRON_TOOL) || blockState.is(BlockTags.NEEDS_STONE_TOOL) || blockState.is(BlockTags.NEEDS_DIAMOND_TOOL);
        BlockHelper.destroyBlock(context.world, breakingPos, 1.0f, stack -> {
            if (noHarvest || context.world.random.nextBoolean()) {
                return;
            }
            this.collectOrDropItem(context, (ItemStack)stack);
        });
        super.destroyBlock(context, breakingPos);
    }

    protected List<BlockPos> getPositionsToBreak(MovementContext context, BlockPos visitedPos) {
        PaveTask profileForTracks;
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        RollerBlockEntity.RollingMode mode = this.getMode(context);
        if (mode != RollerBlockEntity.RollingMode.TUNNEL_PAVE) {
            return positions;
        }
        int startingY = 1;
        if (!this.getStateToPaveWith(context).isAir()) {
            FilterItemStack filter = context.getFilterFromBE();
            if (!ItemHelper.extract((IItemHandler)context.contraption.getStorage().getAllItems(), stack -> filter.test(context.world, (ItemStack)stack), 1, true).isEmpty()) {
                startingY = 0;
            }
        }
        if ((profileForTracks = this.createHeightProfileForTracks(context)) != null) {
            for (Couple<Integer> coords : profileForTracks.keys()) {
                boolean shouldPlaceSlab;
                float height = profileForTracks.get(coords);
                BlockPos targetPosition = BlockPos.containing((double)((Integer)coords.getFirst()).intValue(), (double)height, (double)((Integer)coords.getSecond()).intValue());
                boolean bl = shouldPlaceSlab = (double)height > Math.floor(height) + 0.45;
                if (startingY == 1 && shouldPlaceSlab && context.world.getBlockState(targetPosition.above()).getOptionalValue((Property)SlabBlock.TYPE).orElse(SlabType.DOUBLE) == SlabType.BOTTOM) {
                    startingY = 2;
                }
                for (int i = startingY; i <= (shouldPlaceSlab ? 3 : 2); ++i) {
                    if (!this.testBreakerTarget(context, targetPosition.above(i), i)) continue;
                    positions.add(targetPosition.above(i));
                }
            }
            return positions;
        }
        for (int i = startingY; i <= 2; ++i) {
            if (!this.testBreakerTarget(context, visitedPos.above(i), i)) continue;
            positions.add(visitedPos.above(i));
        }
        return positions;
    }

    protected boolean testBreakerTarget(MovementContext context, BlockPos target, int columnY) {
        BlockState stateToPaveWith = this.getStateToPaveWith(context);
        BlockState stateToPaveWithAsSlab = this.getStateToPaveWithAsSlab(context);
        BlockState stateAbove = context.world.getBlockState(target);
        if (columnY == 0 && stateAbove.is(stateToPaveWith.getBlock())) {
            return false;
        }
        if (stateToPaveWithAsSlab != null && columnY == 1 && stateAbove.is(stateToPaveWithAsSlab.getBlock())) {
            return false;
        }
        return this.canBreak(context.world, target, stateAbove);
    }

    @Nullable
    protected PaveTask createHeightProfileForTracks(MovementContext context) {
        if (context.contraption == null) {
            return null;
        }
        AbstractContraptionEntity abstractContraptionEntity = context.contraption.entity;
        if (!(abstractContraptionEntity instanceof CarriageContraptionEntity)) {
            return null;
        }
        CarriageContraptionEntity cce = (CarriageContraptionEntity)abstractContraptionEntity;
        Carriage carriage = cce.getCarriage();
        if (carriage == null) {
            return null;
        }
        Train train = carriage.train;
        if (train == null || train.graph == null) {
            return null;
        }
        CarriageBogey mainBogey = (CarriageBogey)carriage.bogeys.getFirst();
        TravellingPoint point = mainBogey.trailing();
        this.rollerScout.node1 = point.node1;
        this.rollerScout.node2 = point.node2;
        this.rollerScout.edge = point.edge;
        this.rollerScout.position = point.position;
        Direction.Axis axis = Direction.Axis.X;
        StructureTemplate.StructureBlockInfo info = context.contraption.getBlocks().get(BlockPos.ZERO);
        if (info != null && info.state().hasProperty((Property)StandardBogeyBlock.AXIS)) {
            axis = (Direction.Axis)info.state().getValue((Property)StandardBogeyBlock.AXIS);
        }
        Direction orientation = cce.getInitialOrientation();
        Direction rollerFacing = (Direction)context.state.getValue((Property)RollerBlock.FACING);
        int step = orientation.getAxisDirection().getStep();
        double widthWiseOffset = axis.choose(-context.localPos.getZ(), 0, -context.localPos.getX()) * step;
        double lengthWiseOffset = axis.choose(-context.localPos.getX(), 0, context.localPos.getZ()) * step - 1;
        if (rollerFacing == orientation.getClockWise()) {
            lengthWiseOffset += 1.0;
        }
        double distanceToTravel = 2.0;
        PaveTask heightProfile = new PaveTask(widthWiseOffset, widthWiseOffset);
        TravellingPoint.ITrackSelector steering = this.rollerScout.steer(TravellingPoint.SteerDirection.NONE, new Vec3(0.0, 1.0, 0.0));
        this.rollerScout.traversalCallback = (edge, coords) -> {};
        this.rollerScout.travel(train.graph, lengthWiseOffset + 1.0, steering);
        this.rollerScout.traversalCallback = (edge, coords) -> {
            if (edge == null) {
                return;
            }
            if (edge.isInterDimensional()) {
                return;
            }
            if (edge.node1.getLocation().dimension != context.world.dimension()) {
                return;
            }
            TrackPaverV2.pave(heightProfile, train.graph, edge, (Double)coords.getFirst(), (Double)coords.getSecond());
        };
        this.rollerScout.travel(train.graph, distanceToTravel, steering);
        for (Couple<Integer> entry : heightProfile.keys()) {
            heightProfile.put((Integer)entry.getFirst(), (Integer)entry.getSecond(), (float)context.localPos.getY() + heightProfile.get(entry));
        }
        return heightProfile;
    }

    protected void triggerPaver(MovementContext context, BlockPos pos) {
        BlockState stateToPaveWith = this.getStateToPaveWith(context);
        BlockState stateToPaveWithAsSlab = this.getStateToPaveWithAsSlab(context);
        RollerBlockEntity.RollingMode mode = this.getMode(context);
        if (mode != RollerBlockEntity.RollingMode.TUNNEL_PAVE && stateToPaveWith.isAir()) {
            return;
        }
        Vec3 directionVec = Vec3.atLowerCornerOf((Vec3i)((Direction)context.state.getValue((Property)RollerBlock.FACING)).getClockWise().getNormal());
        directionVec = (Vec3)context.rotation.apply(directionVec);
        PaveResult paveResult = PaveResult.PASS;
        int yOffset = 0;
        ArrayList<Pair> paveSet = new ArrayList<Pair>();
        PaveTask profileForTracks = this.createHeightProfileForTracks(context);
        if (profileForTracks == null) {
            paveSet.add(Pair.of((Object)pos, (Object)false));
        } else {
            for (Couple<Integer> coords : profileForTracks.keys()) {
                float height = profileForTracks.get(coords);
                boolean shouldPlaceSlab = (double)height > Math.floor(height) + 0.45;
                BlockPos targetPosition = BlockPos.containing((double)((Integer)coords.getFirst()).intValue(), (double)height, (double)((Integer)coords.getSecond()).intValue());
                paveSet.add(Pair.of((Object)targetPosition, (Object)shouldPlaceSlab));
            }
        }
        if (paveSet.isEmpty()) {
            return;
        }
        while (paveResult == PaveResult.PASS) {
            if (yOffset > (Integer)AllConfigs.server().kinetics.rollerFillDepth.get()) {
                paveResult = PaveResult.FAIL;
                break;
            }
            HashSet<Pair> currentLayer = new HashSet<Pair>();
            if (mode == RollerBlockEntity.RollingMode.WIDE_FILL) {
                for (Pair anchor : paveSet) {
                    int radius = (yOffset + 1) / 2;
                    for (int i = -radius; i <= radius; ++i) {
                        for (int j = -radius; j <= radius; ++j) {
                            if (BlockPos.ZERO.distManhattan((Vec3i)new BlockPos(i, 0, j)) > radius) continue;
                            currentLayer.add(Pair.of((Object)((BlockPos)anchor.getFirst()).offset(i, -yOffset, j), (Object)((Boolean)anchor.getSecond())));
                        }
                    }
                }
            } else {
                for (Pair anchor : paveSet) {
                    currentLayer.add(Pair.of((Object)((BlockPos)anchor.getFirst()).below(yOffset), (Object)((Boolean)anchor.getSecond())));
                }
            }
            boolean completelyBlocked = true;
            boolean anyBlockPlaced = false;
            for (Pair currentPos : currentLayer) {
                if (stateToPaveWithAsSlab != null && yOffset == 0 && ((Boolean)currentPos.getSecond()).booleanValue()) {
                    this.tryFill(context, ((BlockPos)currentPos.getFirst()).above(), stateToPaveWithAsSlab);
                }
                if ((paveResult = this.tryFill(context, (BlockPos)currentPos.getFirst(), stateToPaveWith)) != PaveResult.FAIL) {
                    completelyBlocked = false;
                }
                if (paveResult != PaveResult.SUCCESS) continue;
                anyBlockPlaced = true;
            }
            if (anyBlockPlaced) {
                paveResult = PaveResult.SUCCESS;
            } else if (!completelyBlocked || yOffset == 0) {
                paveResult = PaveResult.PASS;
            }
            if (paveResult == PaveResult.SUCCESS && stateToPaveWith.getBlock() instanceof FallingBlock) {
                paveResult = PaveResult.PASS;
            }
            if (paveResult != PaveResult.PASS || mode == RollerBlockEntity.RollingMode.TUNNEL_PAVE) break;
            ++yOffset;
        }
        if (paveResult == PaveResult.SUCCESS) {
            context.data.putInt("WaitingTicks", 2);
            context.data.put("LastPos", NbtUtils.writeBlockPos((BlockPos)pos));
            context.stall = true;
        }
    }

    public static BlockState getStateToPaveWith(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof BlockItem) {
            BlockItem bi = (BlockItem)item;
            BlockState defaultBlockState = bi.getBlock().defaultBlockState();
            if (defaultBlockState.hasProperty((Property)SlabBlock.TYPE)) {
                defaultBlockState = (BlockState)defaultBlockState.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.DOUBLE);
            }
            return defaultBlockState;
        }
        return Blocks.AIR.defaultBlockState();
    }

    protected BlockState getStateToPaveWith(MovementContext context) {
        return RollerMovementBehaviour.getStateToPaveWith(ItemStack.parseOptional((HolderLookup.Provider)context.world.registryAccess(), (CompoundTag)context.blockEntityData.getCompound("Filter")));
    }

    protected BlockState getStateToPaveWithAsSlab(MovementContext context) {
        BlockState stateToPaveWith = this.getStateToPaveWith(context);
        if (stateToPaveWith.hasProperty((Property)SlabBlock.TYPE)) {
            return (BlockState)stateToPaveWith.setValue((Property)SlabBlock.TYPE, (Comparable)SlabType.BOTTOM);
        }
        Block block = stateToPaveWith.getBlock();
        if (block == null) {
            return null;
        }
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey((Object)block);
        String namespace = rl.getNamespace();
        String blockName = rl.getPath();
        int nameLength = blockName.length();
        ArrayList<CallSite> possibleSlabLocations = new ArrayList<CallSite>();
        possibleSlabLocations.add((CallSite)((Object)(blockName + "_slab")));
        if (blockName.endsWith("s") && nameLength > 1) {
            possibleSlabLocations.add((CallSite)((Object)(blockName.substring(0, nameLength - 1) + "_slab")));
        }
        if (blockName.endsWith("planks") && nameLength > 7) {
            possibleSlabLocations.add((CallSite)((Object)(blockName.substring(0, nameLength - 7) + "_slab")));
        }
        for (String string : possibleSlabLocations) {
            Optional result = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath((String)namespace, (String)string));
            if (result.isEmpty()) continue;
            return ((Block)result.get()).defaultBlockState();
        }
        return null;
    }

    protected RollerBlockEntity.RollingMode getMode(MovementContext context) {
        return RollerBlockEntity.RollingMode.values()[context.blockEntityData.getInt("ScrollValue")];
    }

    protected PaveResult tryFill(MovementContext context, BlockPos targetPos, BlockState toPlace) {
        Level level = context.world;
        if (!level.isLoaded(targetPos)) {
            return PaveResult.FAIL;
        }
        BlockState existing = level.getBlockState(targetPos);
        if (existing.is(toPlace.getBlock())) {
            return PaveResult.PASS;
        }
        if (!(existing.is(BlockTags.LEAVES) || existing.canBeReplaced() || existing.getCollisionShape((BlockGetter)level, targetPos).isEmpty() && !existing.is(BlockTags.PORTALS))) {
            return PaveResult.FAIL;
        }
        FilterItemStack filter = context.getFilterFromBE();
        ItemStack held = ItemHelper.extract((IItemHandler)context.contraption.getStorage().getAllItems(), stack -> filter.test(context.world, (ItemStack)stack), 1, false);
        if (held.isEmpty()) {
            return PaveResult.FAIL;
        }
        level.setBlockAndUpdate(targetPos, toPlace);
        return PaveResult.SUCCESS;
    }

    private final class RollerTravellingPoint
    extends TravellingPoint {
        public BiConsumer<TrackEdge, Couple<Double>> traversalCallback;

        private RollerTravellingPoint(RollerMovementBehaviour rollerMovementBehaviour) {
        }

        @Override
        protected Double edgeTraversedFrom(TrackGraph graph, boolean forward, TravellingPoint.IEdgePointListener edgePointListener, TravellingPoint.ITurnListener turnListener, double prevPos, double totalDistance) {
            double from = forward ? prevPos : this.position;
            double to = forward ? this.position : prevPos;
            this.traversalCallback.accept(this.edge, (Couple<Double>)Couple.create((Object)from, (Object)to));
            return super.edgeTraversedFrom(graph, forward, edgePointListener, turnListener, prevPos, totalDistance);
        }
    }

    private static enum PaveResult {
        FAIL,
        PASS,
        SUCCESS;

    }
}
