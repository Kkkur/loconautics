/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.AllTags
 *  com.simibubi.create.api.contraption.BlockMovementChecks$CheckResult
 *  com.simibubi.create.content.equipment.wrench.IWrenchable
 *  com.simibubi.create.foundation.block.IBE
 *  com.simibubi.create.impl.contraption.BlockMovementChecksImpl
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Holder
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.DirectionalBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.neoforge.common.Tags$Items
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.handle;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.impl.contraption.BlockMovementChecksImpl;
import dev.simulated_team.simulated.content.blocks.handle.HandleBlockEntity;
import dev.simulated_team.simulated.content.blocks.handle.HandleShaper;
import dev.simulated_team.simulated.content.blocks.util.AbstractDirectionalAxisBlock;
import dev.simulated_team.simulated.index.SimBlockEntityTypes;
import dev.simulated_team.simulated.index.SimClickInteractions;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

public class HandleBlock
extends AbstractDirectionalAxisBlock
implements IBE<HandleBlockEntity>,
IWrenchable {
    public static final MapCodec<HandleBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group((App)HandleBlock.propertiesCodec(), (App)DyeColor.CODEC.fieldOf("color").forGetter(HandleBlock::getColor), (App)StringRepresentable.fromValues(Variant::values).fieldOf("variant").forGetter(HandleBlock::getVariant)).apply((Applicative)instance, HandleBlock::new));
    private static final HandleShaper SHAPER = HandleShaper.make();
    @Nullable
    private final DyeColor color;
    private final Variant variant;

    public HandleBlock(BlockBehaviour.Properties properties, @Nullable DyeColor dyeColor, Variant variant) {
        super(properties);
        this.color = dyeColor;
        this.variant = variant;
    }

    public static boolean canInteractWithHandle(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        return mainHandItem.isEmpty() || mainHandItem.is((Holder)AllItems.EXTENDO_GRIP);
    }

    protected ItemInteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (AllItems.WRENCH.isIn(itemStack)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (HandleBlock.canInteractWithHandle(player)) {
            if (level.isClientSide && player.isLocalPlayer()) {
                SimClickInteractions.HANDLE_HANDLER.startHold(level, player, blockPos);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos) {
        Direction facing = ((Direction)state.getValue((Property)FACING)).getOpposite();
        BlockPos neighbourPos = pos.relative(facing);
        BlockState neighbour = worldIn.getBlockState(neighbourPos);
        return !neighbour.getCollisionShape((BlockGetter)worldIn, neighbourPos).isEmpty();
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (worldIn.isClientSide) {
            return;
        }
        Direction blockFacing = (Direction)state.getValue((Property)FACING);
        if (fromPos.equals((Object)pos.relative(blockFacing.getOpposite())) && !this.canSurvive(state, (LevelReader)worldIn, pos)) {
            worldIn.destroyBlock(pos, true);
        }
    }

    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPER.get((Direction)state.getValue((Property)FACING), (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE));
    }

    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof HandleBlockEntity) {
            HandleBlockEntity be = (HandleBlockEntity)blockEntity;
            return be.hasPlayer() ? 15 : 0;
        }
        return 0;
    }

    public BlockEntityType<? extends HandleBlockEntity> getBlockEntityType() {
        return (BlockEntityType)SimBlockEntityTypes.HANDLE.get();
    }

    public Class<HandleBlockEntity> getBlockEntityClass() {
        return HandleBlockEntity.class;
    }

    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public Variant getVariant() {
        return this.variant;
    }

    public static boolean isHorizontal(BlockState state) {
        Direction.Axis axis = ((Direction)state.getValue((Property)FACING)).getAxis();
        return axis != Direction.Axis.Y && (Boolean)state.getValue((Property)AXIS_ALONG_FIRST_COORDINATE) ^ axis == Direction.Axis.X;
    }

    static {
        BlockMovementChecksImpl.registerAttachedCheck((state, world, pos, direction) -> {
            BlockState relativeState = world.getBlockState(pos.relative(direction));
            if (state.getBlock() instanceof HandleBlock && state.getValue((Property)FACING) == direction.getOpposite()) {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            if (relativeState.getBlock() instanceof HandleBlock && relativeState.getValue((Property)FACING) == direction) {
                return BlockMovementChecks.CheckResult.SUCCESS;
            }
            return BlockMovementChecks.CheckResult.PASS;
        });
    }

    public static enum Variant implements StringRepresentable
    {
        IRON(Ingredient.of((TagKey)Tags.Items.NUGGETS_IRON)),
        COPPER(Ingredient.of((TagKey)AllTags.commonItemTag((String)"nuggets/copper"))),
        DYED(null);

        @Nullable
        final Ingredient ingredient;

        private Variant(Ingredient ingredient) {
            this.ingredient = ingredient;
        }

        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Nullable
        public Ingredient getIngredient() {
            return this.ingredient;
        }
    }
}
