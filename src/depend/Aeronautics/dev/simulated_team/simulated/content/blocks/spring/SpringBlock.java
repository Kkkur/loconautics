/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents
 *  com.simibubi.create.Create
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.foundation.block.WrenchableDirectionalBlock
 *  dev.ryanhcode.sable.Sable
 *  dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package dev.simulated_team.simulated.content.blocks.spring;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.WrenchableDirectionalBlock;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.block.BlockSubLevelAssemblyListener;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.simulated_team.simulated.content.blocks.spring.SpringBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimBlockShapes;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.util.SimColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpringBlock
extends WrenchableDirectionalBlock
implements IBE<SpringBlockEntity>,
BlockSubLevelAssemblyListener,
IWrenchable {
    public static final EnumProperty<Size> SIZE = EnumProperty.create((String)"size", Size.class);

    public SpringBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(SIZE, (Comparable)((Object)Size.MEDIUM)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(new Property[]{SIZE}));
    }

    public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return SimItems.SPRING.asStack();
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pMovedByPiston) {
        IBE.onRemove((BlockState)pState, (Level)pLevel, (BlockPos)pPos, (BlockState)pNewState);
    }

    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        return SpringBlock.canAttach(pLevel, pPos, ((Direction)pState.getValue((Property)FACING)).getOpposite());
    }

    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return ((Direction)pState.getValue((Property)FACING)).getOpposite() == pFacing && !pState.canSurvive((LevelReader)pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public static boolean canAttach(LevelReader pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.relative(pDirection);
        return pReader.getBlockState(blockpos).isFaceSturdy((BlockGetter)pReader, blockpos, pDirection.getOpposite());
    }

    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (((Size)((Object)pState.getValue(SIZE))).ordinal()) {
            default -> throw new MatchException(null, null);
            case 0 -> SimBlockShapes.SMALL_SPRING.get((Direction)pState.getValue((Property)FACING));
            case 1 -> SimBlockShapes.SPRING.get((Direction)pState.getValue((Property)FACING));
            case 2 -> SimBlockShapes.LARGE_SPRING.get((Direction)pState.getValue((Property)FACING));
        };
    }

    public static boolean tryAdjustSpring(Level level, BlockPos pos, Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SpringBlockEntity) {
            SpringBlockEntity spring = (SpringBlockEntity)blockEntity;
            String error = spring.tryChangeLengthOrError(level, player.isShiftKeyDown() ? -0.25 : 0.25);
            if (error == null) {
                SpringBlock.sendLengthMessage("new_length", SimColors.SUCCESS_LIME, spring, player);
                return true;
            }
            SpringBlock.sendLengthMessage(error, SimColors.NUH_UH_RED, spring, player);
        }
        return false;
    }

    private static void sendLengthMessage(String suffix, int color, SpringBlockEntity spring, Player player) {
        SimLang.translate("spring." + suffix, String.format("%.2f", spring.desiredLength)).color(color).sendStatus(player);
    }

    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        BlockPos pos;
        Level level = context.getLevel();
        SpringBlockEntity be = (SpringBlockEntity)this.getBlockEntity((BlockGetter)level, pos = context.getClickedPos());
        if (be == null) {
            return InteractionResult.SUCCESS;
        }
        SpringBlockEntity partner = be.getPairedSpring();
        BlockState partnerState = partner.getBlockState();
        BlockPos partnerPos = partner.getBlockPos();
        Size size = (Size)((Object)state.getValue(SIZE));
        Size newSize = size.cycle();
        BlockState newState = (BlockState)state.setValue(SIZE, (Comparable)((Object)newSize));
        BlockState newPartnerState = (BlockState)partnerState.setValue(SIZE, (Comparable)((Object)newSize));
        level.setBlockAndUpdate(pos, newState);
        level.setBlockAndUpdate(partnerPos, newPartnerState);
        AllSoundEvents.WRENCH_ROTATE.playOnServer(level, (Vec3i)pos, 1.0f, Create.RANDOM.nextFloat() + 0.5f);
        return InteractionResult.SUCCESS;
    }

    public Class<SpringBlockEntity> getBlockEntityClass() {
        return SpringBlockEntity.class;
    }

    public BlockEntityType<? extends SpringBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.SPRING.get();
    }

    protected void spawnAfterBreak(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, ItemStack itemStack, boolean bl) {
        super.spawnAfterBreak(blockState, serverLevel, blockPos, itemStack, bl);
    }

    public void beforeMove(ServerLevel originLevel, ServerLevel newLevel, BlockState newState, BlockPos oldPos, BlockPos newPos) {
        BlockEntity blockEntity = newLevel.getBlockEntity(oldPos);
        if (blockEntity instanceof SpringBlockEntity) {
            SpringBlockEntity spring = (SpringBlockEntity)blockEntity;
            spring.assembling = true;
        }
    }

    public void afterMove(ServerLevel oldLevel, ServerLevel newLevel, BlockState state, BlockPos oldPos, BlockPos newPos) {
        SpringBlockEntity spring;
        SpringBlockEntity partner;
        BlockEntity blockEntity = newLevel.getBlockEntity(newPos);
        if (blockEntity instanceof SpringBlockEntity && (partner = (spring = (SpringBlockEntity)blockEntity).getPairedSpring()) != null) {
            SubLevel subLevel = Sable.HELPER.getContaining((Level)newLevel, (Vec3i)newPos);
            partner.setPartnerPos(newPos, subLevel != null ? subLevel.getUniqueId() : null);
        }
    }

    public static enum Size implements StringRepresentable
    {
        SMALL("small"),
        MEDIUM("medium"),
        LARGE("large");

        private static final Size[] VALUES;
        private final String name;

        private Size(String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getSerializedName() {
            return this.name;
        }

        public Size cycle() {
            return VALUES[(this.ordinal() + 1) % VALUES.length];
        }

        static {
            VALUES = Size.values();
        }
    }
}
