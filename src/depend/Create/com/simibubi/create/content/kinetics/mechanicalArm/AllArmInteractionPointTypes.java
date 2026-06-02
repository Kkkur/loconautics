/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Registry
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.Containers
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.WorldlyContainer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.CampfireCookingRecipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.CampfireBlock
 *  net.minecraft.world.level.block.ComposterBlock
 *  net.minecraft.world.level.block.JukeboxBlock
 *  net.minecraft.world.level.block.RespawnAnchorBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.CampfireBlockEntity
 *  net.minecraft.world.level.block.entity.JukeboxBlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.items.IItemHandler
 *  net.neoforged.neoforge.items.ItemHandlerHelper
 *  net.neoforged.neoforge.items.wrapper.SidedInvWrapper
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.mechanicalArm;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.Create;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlock;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerBlock;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import com.simibubi.create.content.kinetics.saw.SawBlock;
import com.simibubi.create.content.logistics.chute.AbstractChuteBlock;
import com.simibubi.create.content.logistics.funnel.AbstractFunnelBlock;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlock;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BeltTunnelBlock;
import com.simibubi.create.content.processing.basin.BasinBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.InvManipulationBehaviour;
import java.util.Optional;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class AllArmInteractionPointTypes {
    private static <T extends ArmInteractionPointType> void register(String name, T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, (ResourceLocation)Create.asResource(name), type);
    }

    @ApiStatus.Internal
    public static void init() {
    }

    static {
        AllArmInteractionPointTypes.register("basin", new BasinType());
        AllArmInteractionPointTypes.register("belt", new BeltType());
        AllArmInteractionPointTypes.register("blaze_burner", new BlazeBurnerType());
        AllArmInteractionPointTypes.register("chute", new ChuteType());
        AllArmInteractionPointTypes.register("crafter", new CrafterType());
        AllArmInteractionPointTypes.register("crushing_wheels", new CrushingWheelsType());
        AllArmInteractionPointTypes.register("deployer", new DeployerType());
        AllArmInteractionPointTypes.register("depot", new DepotType());
        AllArmInteractionPointTypes.register("funnel", new FunnelType());
        AllArmInteractionPointTypes.register("millstone", new MillstoneType());
        AllArmInteractionPointTypes.register("packager", new PackagerType());
        AllArmInteractionPointTypes.register("saw", new SawType());
        AllArmInteractionPointTypes.register("campfire", new CampfireType());
        AllArmInteractionPointTypes.register("composter", new ComposterType());
        AllArmInteractionPointTypes.register("jukebox", new JukeboxType());
        AllArmInteractionPointTypes.register("respawn_anchor", new RespawnAnchorType());
    }

    public static class BasinType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return BasinBlock.isBasin((LevelReader)level, pos);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class BeltType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.BELT.has(state) && !(level.getBlockState(pos.above()).getBlock() instanceof BeltTunnelBlock) && BeltBlock.canTransportObjects(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BeltPoint(this, level, pos, state);
        }
    }

    public static class BlazeBurnerType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.BLAZE_BURNER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new BlazeBurnerPoint(this, level, pos, state);
        }
    }

    public static class ChuteType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AbstractChuteBlock.isChute(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new TopFaceArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CrafterType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.MECHANICAL_CRAFTER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CrafterPoint(this, level, pos, state);
        }
    }

    public static class CrushingWheelsType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.CRUSHING_WHEEL_CONTROLLER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CrushingWheelPoint(this, level, pos, state);
        }
    }

    public static class DeployerType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.DEPLOYER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new DeployerPoint(this, level, pos, state);
        }
    }

    public static class DepotType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.DEPOT.has(state) || AllBlocks.WEIGHTED_EJECTOR.has(state) || AllBlocks.TRACK_STATION.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new DepotPoint(this, level, pos, state);
        }
    }

    public static class FunnelType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return !(!(state.getBlock() instanceof AbstractFunnelBlock) || state.hasProperty((Property)FunnelBlock.EXTRACTING) && (Boolean)state.getValue((Property)FunnelBlock.EXTRACTING) != false || state.hasProperty(BeltFunnelBlock.SHAPE) && state.getValue(BeltFunnelBlock.SHAPE) == BeltFunnelBlock.Shape.PUSHING);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new FunnelPoint(this, level, pos, state);
        }
    }

    public static class MillstoneType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.MILLSTONE.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class PackagerType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.PACKAGER.has(state) || AllBlocks.REPACKAGER.has(state);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class SawType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return AllBlocks.MECHANICAL_SAW.has(state) && state.getValue((Property)SawBlock.FACING) == Direction.UP && ((KineticBlockEntity)level.getBlockEntity(pos)).getSpeed() != 0.0f;
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new DepotPoint(this, level, pos, state);
        }
    }

    public static class CampfireType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof CampfireBlock;
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CampfirePoint(this, level, pos, state);
        }
    }

    public static class ComposterType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.is(Blocks.COMPOSTER);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ComposterPoint(this, level, pos, state);
        }
    }

    public static class JukeboxType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.is(Blocks.JUKEBOX);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new JukeboxPoint(this, level, pos, state);
        }
    }

    public static class RespawnAnchorType
    extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.is(Blocks.RESPAWN_ANCHOR);
        }

        @Override
        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new RespawnAnchorPoint(this, level, pos, state);
        }
    }

    public static class CrushingWheelPoint
    extends DepositOnlyArmInteractionPoint {
        public CrushingWheelPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 1.0, 0.5);
        }
    }

    public static class RespawnAnchorPoint
    extends DepositOnlyArmInteractionPoint {
        public RespawnAnchorPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 1.0, 0.5);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            if (!stack.is(Items.GLOWSTONE)) {
                return stack;
            }
            if (this.cachedState.getOptionalValue((Property)RespawnAnchorBlock.CHARGE).orElse(4) == 4) {
                return stack;
            }
            if (!simulate) {
                RespawnAnchorBlock.charge(null, (Level)this.level, (BlockPos)this.pos, (BlockState)this.cachedState);
            }
            ItemStack remainder = stack.copy();
            remainder.shrink(1);
            return remainder;
        }
    }

    public static class JukeboxPoint
    extends TopFaceArmInteractionPoint {
        public JukeboxPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public int getSlotCount(ArmBlockEntity armBlockEntity) {
            return 1;
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            if (stack.get(DataComponents.JUKEBOX_PLAYABLE) == null) {
                return stack;
            }
            if (this.cachedState.getOptionalValue((Property)JukeboxBlock.HAS_RECORD).orElse(true).booleanValue()) {
                return stack;
            }
            BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
            if (!(blockEntity instanceof JukeboxBlockEntity)) {
                return stack;
            }
            JukeboxBlockEntity jukeboxBE = (JukeboxBlockEntity)blockEntity;
            if (!jukeboxBE.getTheItem().isEmpty()) {
                return stack;
            }
            ItemStack remainder = stack.copy();
            ItemStack toInsert = remainder.split(1);
            if (!simulate) {
                jukeboxBE.setTheItem(toInsert);
            }
            return remainder;
        }

        @Override
        public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
            if (!this.cachedState.getOptionalValue((Property)JukeboxBlock.HAS_RECORD).orElse(false).booleanValue()) {
                return ItemStack.EMPTY;
            }
            BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
            if (!(blockEntity instanceof JukeboxBlockEntity)) {
                return ItemStack.EMPTY;
            }
            JukeboxBlockEntity jukeboxBE = (JukeboxBlockEntity)blockEntity;
            if (!simulate) {
                return jukeboxBE.removeItem(slot, amount);
            }
            return jukeboxBE.getTheItem();
        }
    }

    public static class ComposterPoint
    extends ArmInteractionPoint {
        public ComposterPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 0.8125, 0.5);
        }

        @Override
        public void updateCachedState() {
            BlockState oldState = this.cachedState;
            super.updateCachedState();
            if (this.cachedHandler != null && oldState != this.cachedState) {
                this.level.invalidateCapabilities(this.cachedHandler.pos());
            }
        }

        @Override
        @Nullable
        protected IItemHandler getHandler(ArmBlockEntity armBlockEntity) {
            return null;
        }

        protected WorldlyContainer getContainer() {
            ComposterBlock composterBlock = (ComposterBlock)Blocks.COMPOSTER;
            return composterBlock.getContainer(this.cachedState, (LevelAccessor)this.level, this.pos);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            SidedInvWrapper handler = new SidedInvWrapper(this.getContainer(), Direction.UP);
            return ItemHandlerHelper.insertItem((IItemHandler)handler, (ItemStack)stack, (boolean)simulate);
        }

        @Override
        public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
            SidedInvWrapper handler = new SidedInvWrapper(this.getContainer(), Direction.DOWN);
            return handler.extractItem(slot, amount, simulate);
        }

        @Override
        public int getSlotCount(ArmBlockEntity armBlockEntity) {
            return 2;
        }
    }

    public static class CampfirePoint
    extends DepositOnlyArmInteractionPoint {
        public CampfirePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.pos);
            if (!(blockEntity instanceof CampfireBlockEntity)) {
                return stack;
            }
            CampfireBlockEntity campfireBE = (CampfireBlockEntity)blockEntity;
            Optional recipe = campfireBE.getCookableRecipe(stack);
            if (recipe.isEmpty()) {
                return stack;
            }
            if (simulate) {
                boolean hasSpace = false;
                for (ItemStack campfireStack : campfireBE.getItems()) {
                    if (!campfireStack.isEmpty()) continue;
                    hasSpace = true;
                    break;
                }
                if (!hasSpace) {
                    return stack;
                }
                ItemStack remainder = stack.copy();
                remainder.shrink(1);
                return remainder;
            }
            ItemStack remainder = stack.copy();
            campfireBE.placeFood(null, remainder, ((CampfireCookingRecipe)((RecipeHolder)recipe.get()).value()).getCookingTime());
            return remainder;
        }
    }

    public static class FunnelPoint
    extends DepositOnlyArmInteractionPoint {
        public FunnelPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            Direction funnelFacing = FunnelBlock.getFunnelFacing(this.cachedState);
            Vec3i normal = funnelFacing != null ? funnelFacing.getNormal() : Vec3i.ZERO;
            return VecHelper.getCenterOf((Vec3i)this.pos).add(Vec3.atLowerCornerOf((Vec3i)normal).scale((double)-0.15f));
        }

        @Override
        protected Direction getInteractionDirection() {
            Direction funnelFacing = FunnelBlock.getFunnelFacing(this.cachedState);
            return funnelFacing != null ? funnelFacing.getOpposite() : Direction.UP;
        }

        @Override
        public void updateCachedState() {
            BlockState oldState = this.cachedState;
            super.updateCachedState();
            if (oldState != this.cachedState) {
                this.cachedAngles = null;
            }
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            BlockEntity blockEntity;
            FilteringBehaviour filtering = BlockEntityBehaviour.get((BlockGetter)this.level, this.pos, FilteringBehaviour.TYPE);
            InvManipulationBehaviour inserter = BlockEntityBehaviour.get((BlockGetter)this.level, this.pos, InvManipulationBehaviour.TYPE);
            if (this.cachedState.getOptionalValue((Property)BlockStateProperties.POWERED).orElse(false).booleanValue()) {
                return stack;
            }
            if (inserter == null) {
                return stack;
            }
            if (filtering != null && !filtering.test(stack)) {
                return stack;
            }
            if (simulate) {
                inserter.simulate();
            }
            ItemStack insert = inserter.insert(stack);
            if (!simulate && insert.getCount() != stack.getCount() && (blockEntity = this.level.getBlockEntity(this.pos)) instanceof FunnelBlockEntity) {
                FunnelBlockEntity funnelBlockEntity = (FunnelBlockEntity)blockEntity;
                funnelBlockEntity.onTransfer(stack);
                if (funnelBlockEntity.hasFlap()) {
                    funnelBlockEntity.flap(true);
                }
            }
            return insert;
        }
    }

    public static class DepotPoint
    extends ArmInteractionPoint {
        public DepotPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 0.875, 0.5);
        }
    }

    public static class DeployerPoint
    extends ArmInteractionPoint {
        public DeployerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Direction getInteractionDirection() {
            return this.cachedState.getOptionalValue((Property)DeployerBlock.FACING).orElse(Direction.UP).getOpposite();
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return super.getInteractionPositionVector().add(Vec3.atLowerCornerOf((Vec3i)this.getInteractionDirection().getNormal()).scale((double)0.65f));
        }

        @Override
        public void updateCachedState() {
            BlockState oldState = this.cachedState;
            super.updateCachedState();
            if (oldState != this.cachedState) {
                this.cachedAngles = null;
            }
        }
    }

    public static class CrafterPoint
    extends ArmInteractionPoint {
        public CrafterPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Direction getInteractionDirection() {
            return this.cachedState.getOptionalValue(MechanicalCrafterBlock.HORIZONTAL_FACING).orElse(Direction.SOUTH).getOpposite();
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return super.getInteractionPositionVector().add(Vec3.atLowerCornerOf((Vec3i)this.getInteractionDirection().getNormal()).scale(0.5));
        }

        @Override
        public void updateCachedState() {
            BlockState oldState = this.cachedState;
            super.updateCachedState();
            if (oldState != this.cachedState) {
                this.cachedAngles = null;
            }
        }

        @Override
        public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
            BlockEntity be = this.level.getBlockEntity(this.pos);
            if (!(be instanceof MechanicalCrafterBlockEntity)) {
                return ItemStack.EMPTY;
            }
            MechanicalCrafterBlockEntity crafter = (MechanicalCrafterBlockEntity)be;
            MechanicalCrafterBlockEntity.Inventory inventory = crafter.getInventory();
            inventory.allowExtraction();
            ItemStack extract = super.extract(armBlockEntity, slot, amount, simulate);
            inventory.forbidExtraction();
            return extract;
        }
    }

    public static class BlazeBurnerPoint
    extends DepositOnlyArmInteractionPoint {
        public BlazeBurnerPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack insert(ArmBlockEntity armBlockEntity, ItemStack stack, boolean simulate) {
            ItemStack input = stack.copy();
            InteractionResultHolder<ItemStack> res = BlazeBurnerBlock.tryInsert(this.cachedState, this.level, this.pos, input, false, false, simulate);
            ItemStack remainder = (ItemStack)res.getObject();
            if (input.isEmpty()) {
                return remainder;
            }
            if (!simulate) {
                Containers.dropItemStack((Level)this.level, (double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), (ItemStack)remainder);
            }
            return input;
        }
    }

    public static class BeltPoint
    extends DepotPoint {
        public BeltPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public void keepAlive() {
            super.keepAlive();
            BeltBlockEntity beltBE = BeltHelper.getSegmentBE((LevelAccessor)this.level, this.pos);
            if (beltBE == null) {
                return;
            }
            TransportedItemStackHandlerBehaviour transport = beltBE.getBehaviour(TransportedItemStackHandlerBehaviour.TYPE);
            if (transport == null) {
                return;
            }
            MutableBoolean found = new MutableBoolean(false);
            transport.handleProcessingOnAllItems(tis -> {
                if (found.isTrue()) {
                    return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
                }
                tis.lockedExternally = true;
                found.setTrue();
                return TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
            });
        }
    }

    public static class TopFaceArmInteractionPoint
    extends ArmInteractionPoint {
        public TopFaceArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        protected Vec3 getInteractionPositionVector() {
            return Vec3.atLowerCornerOf((Vec3i)this.pos).add(0.5, 1.0, 0.5);
        }
    }

    public static class DepositOnlyArmInteractionPoint
    extends ArmInteractionPoint {
        public DepositOnlyArmInteractionPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public void cycleMode() {
        }

        @Override
        public ItemStack extract(ArmBlockEntity armBlockEntity, int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotCount(ArmBlockEntity armBlockEntity) {
            return 0;
        }
    }
}
