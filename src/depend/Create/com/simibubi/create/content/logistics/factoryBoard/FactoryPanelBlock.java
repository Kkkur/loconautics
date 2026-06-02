/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.MapCodec
 *  io.netty.buffer.ByteBuf
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.AttachFace
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.BooleanProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.EntityCollisionContext
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.bus.api.Event
 *  net.neoforged.neoforge.common.NeoForge
 *  net.neoforged.neoforge.event.level.BlockEvent$BreakEvent
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllShapes;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.api.schematic.requirement.SpecialBlockItemRequirement;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockItem;
import com.simibubi.create.content.logistics.packagerLink.LogisticallyLinkedBlockItem;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.CreateLang;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class FactoryPanelBlock
extends FaceAttachedHorizontalDirectionalBlock
implements ProperWaterloggedBlock,
IBE<FactoryPanelBlockEntity>,
IWrenchable,
SpecialBlockItemRequirement {
    public static final MapCodec<FactoryPanelBlock> CODEC = FactoryPanelBlock.simpleCodec(FactoryPanelBlock::new);
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public FactoryPanelBlock(BlockBehaviour.Properties p_53182_) {
        super(p_53182_);
        this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue((Property)WATERLOGGED, (Comparable)Boolean.valueOf(false))).setValue((Property)POWERED, (Comparable)Boolean.valueOf(false)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(new Property[]{FACE, FACING, WATERLOGGED, POWERED}));
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return FactoryPanelBlock.canAttachLenient(pLevel, pPos, FactoryPanelBlock.getConnectedDirection((BlockState)pState).getOpposite());
    }

    public static boolean canAttachLenient(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return !pReader.getBlockState(blockpos).getCollisionShape((BlockGetter)pReader, blockpos).isEmpty();
    }

    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState stateForPlacement = super.getStateForPlacement(pContext);
        if (stateForPlacement == null) {
            return null;
        }
        if (stateForPlacement.getValue((Property)FACE) == AttachFace.FLOOR) {
            stateForPlacement = (BlockState)stateForPlacement.setValue((Property)FACING, (Comparable)((Direction)stateForPlacement.getValue((Property)FACING)).getOpposite());
        }
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        FactoryPanelBlockEntity fpbe = (FactoryPanelBlockEntity)this.getBlockEntity((BlockGetter)level, pos);
        Vec3 location = pContext.getClickLocation();
        if (blockState.is((Block)this) && location != null && fpbe != null) {
            if (!level.isClientSide()) {
                PanelSlot targetedSlot = FactoryPanelBlock.getTargetedSlot(pos, blockState, location);
                ItemStack panelItem = FactoryPanelBlockItem.fixCtrlCopiedStack(pContext.getItemInHand());
                UUID networkFromStack = LogisticallyLinkedBlockItem.networkFromStack(panelItem);
                Player pPlayer = pContext.getPlayer();
                if (fpbe.addPanel(targetedSlot, networkFromStack) && pPlayer != null) {
                    pPlayer.displayClientMessage((Component)CreateLang.translateDirect("logistically_linked.connected", new Object[0]), true);
                    if (!pPlayer.isCreative()) {
                        panelItem.shrink(1);
                        if (panelItem.isEmpty()) {
                            pPlayer.setItemInHand(pContext.getHand(), ItemStack.EMPTY);
                        }
                    }
                }
            }
            stateForPlacement = blockState;
        }
        return this.withWater(stateForPlacement, pContext);
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        PanelSlot slot = FactoryPanelBlock.getTargetedSlot(pos, state, context.getClickLocation());
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        return this.onBlockEntityUse((BlockGetter)world, pos, be -> {
            FactoryPanelBehaviour behaviour = be.panels.get((Object)slot);
            if (behaviour == null || !behaviour.isActive()) {
                return InteractionResult.SUCCESS;
            }
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(world, pos, world.getBlockState(pos), player);
            NeoForge.EVENT_BUS.post((Event)event);
            if (event.isCanceled()) {
                return InteractionResult.SUCCESS;
            }
            if (!be.removePanel(slot)) {
                return InteractionResult.SUCCESS;
            }
            if (!player.isCreative()) {
                player.getInventory().placeItemBackInInventory(AllBlocks.FACTORY_GAUGE.asStack());
            }
            IWrenchable.playRemoveSound(world, pos);
            if (be.activePanels() == 0) {
                world.destroyBlock(pos, false);
            }
            return InteractionResult.SUCCESS;
        });
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        if (pPlacer == null) {
            return;
        }
        AdvancementBehaviour.setPlacedBy(pLevel, pPos, pPlacer);
        double range = pPlacer.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        HitResult hitResult = pPlacer.pick(range, 1.0f, false);
        Vec3 location = hitResult.getLocation();
        if (location == null) {
            return;
        }
        PanelSlot initialSlot = FactoryPanelBlock.getTargetedSlot(pPos, pState, location);
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, fpbe -> fpbe.addPanel(initialSlot, LogisticallyLinkedBlockItem.networkFromStack(pStack)));
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!AllBlocks.FACTORY_GAUGE.isIn(stack)) {
            return ItemInteractionResult.SUCCESS;
        }
        Vec3 location = hitResult.getLocation();
        if (location == null) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!FactoryPanelBlockItem.isTuned(stack)) {
            AllSoundEvents.DENY.playOnServer(level, (Vec3i)pos);
            player.displayClientMessage((Component)CreateLang.translate("factory_panel.tune_before_placing", new Object[0]).component(), true);
            return ItemInteractionResult.FAIL;
        }
        PanelSlot newSlot = FactoryPanelBlock.getTargetedSlot(pos, state, location);
        this.withBlockEntityDo((BlockGetter)level, pos, fpbe -> {
            if (!fpbe.addPanel(newSlot, LogisticallyLinkedBlockItem.networkFromStack(FactoryPanelBlockItem.fixCtrlCopiedStack(stack)))) {
                return;
            }
            player.displayClientMessage((Component)CreateLang.translateDirect("logistically_linked.connected", new Object[0]), true);
            level.playSound(null, pos, this.soundType.getPlaceSound(), SoundSource.BLOCKS);
            if (player.isCreative()) {
                return;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
        });
        return ItemInteractionResult.SUCCESS;
    }

    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (this.tryDestroySubPanelFirst(state, level, pos, player)) {
            return false;
        }
        boolean result = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        return result;
    }

    private boolean tryDestroySubPanelFirst(BlockState state, Level level, BlockPos pos, Player player) {
        double range = player.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE) + 1.0;
        HitResult hitResult = player.pick(range, 1.0f, false);
        Vec3 location = hitResult.getLocation();
        PanelSlot destroyedSlot = FactoryPanelBlock.getTargetedSlot(pos, state, location);
        return InteractionResult.SUCCESS == this.onBlockEntityUse((BlockGetter)level, pos, fpbe -> {
            if (fpbe.activePanels() < 2) {
                return InteractionResult.FAIL;
            }
            if (!fpbe.removePanel(destroyedSlot)) {
                return InteractionResult.FAIL;
            }
            if (!player.isCreative()) {
                FactoryPanelBlock.popResource((Level)level, (BlockPos)pos, (ItemStack)AllBlocks.FACTORY_GAUGE.asStack());
            }
            return InteractionResult.SUCCESS;
        });
    }

    public boolean isSignalSource(BlockState pState) {
        return true;
    }

    public int getSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false ? 15 : 0;
    }

    public int getDirectSignal(BlockState pBlockState, BlockGetter pBlockAccess, BlockPos pPos, Direction pSide) {
        return (Boolean)pBlockState.getValue((Property)POWERED) != false && FactoryPanelBlock.getConnectedDirection((BlockState)pBlockState) == pSide ? 15 : 0;
    }

    public boolean canBeReplaced(BlockState pState, BlockPlaceContext pUseContext) {
        if (!AllBlocks.FACTORY_GAUGE.isIn(pUseContext.getItemInHand())) {
            return false;
        }
        Vec3 location = pUseContext.getClickLocation();
        BlockPos pos = pUseContext.getClickedPos();
        PanelSlot slot = FactoryPanelBlock.getTargetedSlot(pos, pState, location);
        FactoryPanelBlockEntity blockEntity = (FactoryPanelBlockEntity)this.getBlockEntity((BlockGetter)pUseContext.getLevel(), pos);
        if (blockEntity == null) {
            return false;
        }
        return !blockEntity.panels.get((Object)slot).isActive();
    }

    public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        EntityCollisionContext ecc;
        if (pContext instanceof EntityCollisionContext && (ecc = (EntityCollisionContext)pContext).getEntity() == null) {
            return this.getShape(pState, pLevel, pPos, pContext);
        }
        return Shapes.empty();
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        FactoryPanelBlockEntity blockEntity = (FactoryPanelBlockEntity)this.getBlockEntity(pLevel, pPos);
        if (blockEntity != null) {
            return blockEntity.getShape();
        }
        return AllShapes.FACTORY_PANEL_FALLBACK.get(FactoryPanelBlock.getConnectedDirection((BlockState)pState));
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        this.updateWater(pLevel, pState, pCurrentPos);
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public FluidState getFluidState(BlockState pState) {
        return this.fluidState(pState);
    }

    public static Direction connectedDirection(BlockState state) {
        return FactoryPanelBlock.getConnectedDirection((BlockState)state);
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        IBE.onRemove(pState, pLevel, pPos, pNewState);
    }

    public static PanelSlot getTargetedSlot(BlockPos pos, BlockState blockState, Vec3 clickLocation) {
        double bestDistance = Double.MAX_VALUE;
        PanelSlot bestSlot = PanelSlot.BOTTOM_LEFT;
        Vec3 localClick = clickLocation.subtract(Vec3.atLowerCornerOf((Vec3i)pos));
        float xRot = 57.295776f * FactoryPanelBlock.getXRot(blockState);
        float yRot = 57.295776f * FactoryPanelBlock.getYRot(blockState);
        for (PanelSlot slot : PanelSlot.values()) {
            Vec3 vec = new Vec3(0.25 + (double)slot.xOffset * 0.5, 0.0, 0.25 + (double)slot.yOffset * 0.5);
            vec = VecHelper.rotateCentered((Vec3)vec, (double)180.0, (Direction.Axis)Direction.Axis.Y);
            vec = VecHelper.rotateCentered((Vec3)vec, (double)(xRot + 90.0f), (Direction.Axis)Direction.Axis.X);
            double diff = (vec = VecHelper.rotateCentered((Vec3)vec, (double)yRot, (Direction.Axis)Direction.Axis.Y)).distanceToSqr(localClick);
            if (diff > bestDistance) continue;
            bestDistance = diff;
            bestSlot = slot;
        }
        return bestSlot;
    }

    @Override
    public Class<FactoryPanelBlockEntity> getBlockEntityClass() {
        return FactoryPanelBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends FactoryPanelBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.FACTORY_PANEL.get();
    }

    public static float getXRot(BlockState state) {
        AttachFace face = state.getOptionalValue((Property)FACE).orElse(AttachFace.FLOOR);
        return face == AttachFace.CEILING ? 1.5707964f : (face == AttachFace.FLOOR ? -1.5707964f : 0.0f);
    }

    public static float getYRot(BlockState state) {
        Direction facing = state.getOptionalValue((Property)FACING).orElse(Direction.SOUTH);
        AttachFace face = state.getOptionalValue((Property)FACE).orElse(AttachFace.FLOOR);
        return (face == AttachFace.CEILING ? (float)Math.PI : 0.0f) + AngleHelper.rad((double)AngleHelper.horizontalAngle((Direction)facing));
    }

    @Override
    public ItemRequirement getRequiredItems(BlockState state, BlockEntity blockEntity) {
        return ItemRequirement.NONE;
    }

    @NotNull
    protected MapCodec<? extends FaceAttachedHorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public static enum PanelSlot implements StringRepresentable
    {
        TOP_LEFT(1, 1),
        TOP_RIGHT(0, 1),
        BOTTOM_LEFT(1, 0),
        BOTTOM_RIGHT(0, 0);

        public static final Codec<PanelSlot> CODEC;
        public static final StreamCodec<ByteBuf, PanelSlot> STREAM_CODEC;
        public final int xOffset;
        public final int yOffset;

        private PanelSlot(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        @NotNull
        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }

        static {
            CODEC = StringRepresentable.fromValues(PanelSlot::values);
            STREAM_CODEC = CatnipStreamCodecBuilders.ofEnum(PanelSlot.class);
        }
    }

    public static enum PanelType {
        NETWORK,
        PACKAGER;

    }

    public static enum PanelState {
        PASSIVE,
        ACTIVE;

    }
}
