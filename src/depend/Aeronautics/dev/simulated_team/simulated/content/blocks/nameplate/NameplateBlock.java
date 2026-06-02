/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.createmod.catnip.placement.IPlacementHelper
 *  net.createmod.catnip.placement.PlacementHelpers
 *  net.createmod.catnip.placement.PlacementOffset
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.SignApplicator
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.HorizontalDirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.entity.SignBlockEntity
 *  net.minecraft.world.level.block.entity.SignText
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlockEntity;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateScreen;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimBlocks;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import net.createmod.catnip.placement.IPlacementHelper;
import net.createmod.catnip.placement.PlacementHelpers;
import net.createmod.catnip.placement.PlacementOffset;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SignApplicator;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NameplateBlock
extends HorizontalDirectionalBlock
implements IBE<NameplateBlockEntity>,
IWrenchable,
BlockSubLevelAssemblyListener {
    public static final EnumProperty<Position> POSITION = EnumProperty.create((String)"position", Position.class);
    public static final MapCodec<NameplateBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)NameplateBlock.propertiesCodec(), (App)DyeColor.CODEC.fieldOf("DyeColor").forGetter(NameplateBlock::getColor)).apply((Applicative)instance, NameplateBlock::new));
    private static final int placementHelperId = PlacementHelpers.register((IPlacementHelper)new PlacementHelper());
    protected final DyeColor color;

    public NameplateBlock(BlockBehaviour.Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(new Property[]{POSITION});
        pBuilder.add(new Property[]{FACING});
    }

    public static boolean hasBackSupport(Direction facingDir, LevelReader level, BlockPos pos) {
        return !level.getBlockState(pos.relative(facingDir, -1)).isAir();
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        Direction facing = (Direction)pState.getValue((Property)FACING);
        if (NameplateBlock.hasBackSupport(facing, pLevel, pPos)) {
            return true;
        }
        BlockState leftState = pLevel.getBlockState(pPos.relative(facing.getClockWise()));
        if (leftState.getBlock().equals(pState.getBlock()) && leftState.getValue((Property)FACING) == facing) {
            return true;
        }
        BlockState rightState = pLevel.getBlockState(pPos.relative(facing.getCounterClockWise()));
        return rightState.getBlock().equals(pState.getBlock()) && rightState.getValue((Property)FACING) == facing;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        Direction direction = pContext.getClickedFace();
        if (pContext.getClickedFace().getAxis().equals((Object)Direction.Axis.Y)) {
            direction = pContext.getHorizontalDirection().getOpposite();
        }
        BlockPos pos = pContext.getClickedPos();
        Level level = pContext.getLevel();
        Position position = this.getPositionState((LevelAccessor)level, pos, direction);
        return (BlockState)((BlockState)state.setValue(POSITION, (Comparable)((Object)position))).setValue((Property)FACING, (Comparable)direction);
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SimBlockShapes.NAMEPLATE.get((Direction)pState.getValue((Property)FACING));
    }

    public Position getPositionState(LevelAccessor level, BlockPos pos, Direction facing) {
        boolean rightBlock;
        boolean leftBlock;
        NameplateBlock npb;
        boolean right;
        NameplateBlock npb2;
        Position outPos = Position.SINGLE;
        BlockState leftState = level.getBlockState(pos.offset(facing.getClockWise(Direction.Axis.Y).getNormal()));
        BlockState rightState = level.getBlockState(pos.offset(facing.getCounterClockWise(Direction.Axis.Y).getNormal()));
        Block block = leftState.getBlock();
        boolean left = block instanceof NameplateBlock && (npb2 = (NameplateBlock)block).getColor() == this.getColor();
        Block block2 = rightState.getBlock();
        boolean bl = right = block2 instanceof NameplateBlock && (npb = (NameplateBlock)block2).getColor() == this.getColor();
        if (left && (leftBlock = ((Direction)leftState.getValue((Property)FACING)).equals((Object)facing))) {
            outPos = Position.RIGHT;
        }
        if (right && (rightBlock = ((Direction)rightState.getValue((Property)FACING)).equals((Object)facing))) {
            outPos = Position.LEFT;
        }
        if (left && right) {
            boolean rightBlock2 = ((Direction)rightState.getValue((Property)FACING)).equals((Object)facing);
            boolean leftBlock2 = ((Direction)leftState.getValue((Property)FACING)).equals((Object)facing);
            if (leftBlock2 && rightBlock2) {
                outPos = Position.MIDDLE;
            }
        }
        return outPos;
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        Item result;
        if (!player.isShiftKeyDown() && player.mayBuild()) {
            BlockItem bi;
            IPlacementHelper placementHelper = PlacementHelpers.get((int)placementHelperId);
            Item item = itemStack.getItem();
            if (item instanceof BlockItem && blockState.is((bi = (BlockItem)item).getBlock()) && placementHelper.matchesItem(itemStack) && (result = placementHelper.getOffset(player, level, blockState, blockPos, blockHitResult).placeInWorld(level, (BlockItem)itemStack.getItem(), player, interactionHand, blockHitResult)) == ItemInteractionResult.SUCCESS) {
                return ItemInteractionResult.SUCCESS;
            }
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack heldItem = player.getItemInHand(interactionHand);
        result = heldItem.getItem();
        if (result instanceof SignApplicator) {
            SignApplicator signApplicator = (SignApplicator)result;
            MutableBoolean success = new MutableBoolean(false);
            this.withBlockEntityDo((BlockGetter)level, blockPos, nbe -> {
                NameplateBlockEntity controller = nbe.findController();
                if (controller.allowsEditing()) {
                    SignBlockEntity dummySign = new SignBlockEntity(blockPos, Blocks.OAK_SIGN.defaultBlockState());
                    dummySign.setLevel(controller.getLevel());
                    SignText text = dummySign.getFrontText().setMessage(0, (Component)Component.literal((String)controller.getName())).setColor(controller.getTextColor()).setHasGlowingText(controller.glowing);
                    dummySign.setText(text, true);
                    dummySign.setWaxed(controller.waxed);
                    if (signApplicator.canApplyToSign(text, player) && signApplicator.tryApplyToSign(controller.getLevel(), dummySign, true, player)) {
                        text = dummySign.getFrontText();
                        controller.setTextColor(text.getColor(), true);
                        controller.glowing = text.hasGlowingText();
                        controller.waxed = dummySign.isWaxed();
                        controller.updateNameplates(this.getColor(), (Direction)blockState.getValue((Property)FACING));
                        success.setTrue();
                    }
                    nbe.sendData();
                }
            });
            return success.booleanValue() ? ItemInteractionResult.SUCCESS : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            this.withBlockEntityDo((BlockGetter)level, blockPos, nbe -> {
                NameplateBlockEntity controller = nbe.findController();
                if (!controller.waxed) {
                    NameplateScreen.setScreen(nbe);
                } else {
                    level.playSound(player, blockPos, SoundEvents.WAXED_SIGN_INTERACT_FAIL, SoundSource.BLOCKS);
                }
            });
        }
        return ItemInteractionResult.SUCCESS;
    }

    public void neighborChanged(BlockState state, Level level, BlockPos selfPos, Block neighborBlock, BlockPos neighborPos, boolean pMovedByPiston) {
        super.neighborChanged(state, level, selfPos, neighborBlock, neighborPos, pMovedByPiston);
        BlockEntity blockEntity = level.getBlockEntity(selfPos);
        if (blockEntity instanceof NameplateBlockEntity) {
            NameplateBlockEntity nbe = (NameplateBlockEntity)blockEntity;
            if (neighborPos.equals((Object)selfPos.relative(((Direction)state.getValue((Property)FACING)).getClockWise(Direction.Axis.Y)))) {
                nbe.checkAndUpdateController(this.color, (Direction)state.getValue((Property)FACING));
            } else {
                nbe.findController().checkAndUpdateController(this.color, (Direction)state.getValue((Property)FACING));
            }
            if (!NameplateBlockEntity.hasSupport(nbe)) {
                level.destroyBlock(selfPos, true);
            }
        }
    }

    public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pPos, BlockPos pNeighborPos) {
        Position posState = this.getPositionState(pLevel, pPos, (Direction)pState.getValue((Property)FACING));
        return (BlockState)super.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos).setValue(POSITION, (Comparable)((Object)posState));
    }

    public Class<NameplateBlockEntity> getBlockEntityClass() {
        return NameplateBlockEntity.class;
    }

    public BlockEntityType<? extends NameplateBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.NAMEPLATE.get();
    }

    public DyeColor getColor() {
        return this.color;
    }

    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public void afterMove(ServerLevel serverLevel, ServerLevel resultingLevel, BlockState blockState, BlockPos oldPos, BlockPos newPos) {
        SubLevel subLevel = Sable.HELPER.getContaining((Level)resultingLevel, (Vec3i)newPos);
        NameplateBlockEntity nameplate = (NameplateBlockEntity)this.getBlockEntity((BlockGetter)resultingLevel, newPos);
        if (nameplate != null && nameplate.getName() != null && subLevel != null && subLevel.getName() == null) {
            subLevel.setName(nameplate.getName());
        }
    }

    public static enum Position implements StringRepresentable
    {
        SINGLE,
        LEFT,
        RIGHT,
        MIDDLE;


        @NotNull
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    private static class PlacementHelper
    implements IPlacementHelper {
        private PlacementHelper() {
        }

        public Predicate<ItemStack> getItemPredicate() {
            return stack -> {
                for (BlockEntry nameplate : SimBlocks.NAMEPLATES) {
                    if (!nameplate.is(stack.getItem())) continue;
                    return true;
                }
                return false;
            };
        }

        public Predicate<BlockState> getStatePredicate() {
            return state -> {
                for (BlockEntry nameplate : SimBlocks.NAMEPLATES) {
                    if (!nameplate.has(state)) continue;
                    return true;
                }
                return false;
            };
        }

        public PlacementOffset getOffset(Player player, Level world, BlockState state, BlockPos pos, BlockHitResult ray, ItemStack heldItem) {
            BlockItem bi;
            Item item = heldItem.getItem();
            if (item instanceof BlockItem && state.is((bi = (BlockItem)item).getBlock())) {
                return super.getOffset(player, world, state, pos, ray, heldItem);
            }
            return PlacementOffset.fail();
        }

        public PlacementOffset getOffset(Player player, Level level, BlockState blockState, BlockPos blockPos, BlockHitResult blockHitResult) {
            List directions = IPlacementHelper.orderedByDistance((BlockPos)blockPos, (Vec3)blockHitResult.getLocation(), dir -> {
                if (dir.getAxis() != ((Direction)blockState.getValue((Property)HorizontalDirectionalBlock.FACING)).getClockWise().getAxis()) {
                    return false;
                }
                BlockPos relPos = blockPos.relative(dir);
                return level.getBlockState(relPos).canBeReplaced() && blockState.canSurvive((LevelReader)level, relPos);
            });
            if (directions.isEmpty()) {
                return PlacementOffset.fail();
            }
            return PlacementOffset.success((Vec3i)blockPos.relative((Direction)directions.getFirst()), s -> (BlockState)s.setValue((Property)HorizontalDirectionalBlock.FACING, (Comparable)((Direction)blockState.getValue((Property)HorizontalDirectionalBlock.FACING))));
        }
    }
}
