/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.color.block.BlockColor
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.GrassColor
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.EntityBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.StairBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityTicker
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  net.neoforged.neoforge.common.world.AuxiliaryLightManager
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.decoration.copycat;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;
import com.simibubi.create.content.decoration.copycat.CopycatModel;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import org.jetbrains.annotations.Nullable;

public abstract class CopycatBlock
extends Block
implements IBE<CopycatBlockEntity>,
IWrenchable {
    public CopycatBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
    }

    @Override
    @Nullable
    public <S extends BlockEntity> BlockEntityTicker<S> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<S> p_153214_) {
        return null;
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        this.onWrenched(state, context);
        return IWrenchable.super.onSneakWrenched(state, context);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        return this.onBlockEntityUse((BlockGetter)context.getLevel(), context.getClickedPos(), ufte -> {
            ItemStack consumedItem = ufte.getConsumedItem();
            if (!ufte.hasCustomMaterial()) {
                return InteractionResult.PASS;
            }
            Player player = context.getPlayer();
            if (!player.isCreative()) {
                player.getInventory().placeItemBackInInventory(consumedItem);
            }
            context.getLevel().levelEvent(2001, context.getClickedPos(), Block.getId((BlockState)ufte.getBlockState()));
            ufte.setMaterial(AllBlocks.COPYCAT_BASE.getDefaultState());
            ufte.setConsumedItem(ItemStack.EMPTY);
            return InteractionResult.SUCCESS;
        });
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        Direction face = hitResult.getDirection();
        BlockState materialIn = this.getAcceptedBlockState(level, pos, stack, face);
        if (materialIn != null) {
            materialIn = this.prepareMaterial(level, pos, state, player, hand, hitResult, materialIn);
        }
        if (materialIn == null) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        BlockState material = materialIn;
        return this.onBlockEntityUseItemOn((BlockGetter)level, pos, ufte -> {
            if (ufte.getMaterial().is(material.getBlock())) {
                if (!ufte.cycleMaterial()) {
                    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                }
                ufte.getLevel().playSound(null, ufte.getBlockPos(), SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.75f, 0.95f);
                return ItemInteractionResult.SUCCESS;
            }
            if (ufte.hasCustomMaterial()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            if (level.isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            ufte.setMaterial(material);
            ufte.setConsumedItem(stack);
            ufte.getLevel().playSound(null, ufte.getBlockPos(), material.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0f, 0.75f);
            if (player.isCreative()) {
                return ItemInteractionResult.SUCCESS;
            }
            stack.shrink(1);
            if (stack.isEmpty()) {
                player.setItemInHand(hand, ItemStack.EMPTY);
            }
            return ItemInteractionResult.SUCCESS;
        });
    }

    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
        if (pPlacer == null) {
            return;
        }
        ItemStack offhandItem = pPlacer.getItemInHand(InteractionHand.OFF_HAND);
        BlockState appliedState = this.getAcceptedBlockState(pLevel, pPos, offhandItem, Direction.orderedByNearest((Entity)pPlacer)[0]);
        if (appliedState == null) {
            return;
        }
        this.withBlockEntityDo((BlockGetter)pLevel, pPos, ufte -> {
            Player player;
            if (ufte.hasCustomMaterial()) {
                return;
            }
            ufte.setMaterial(appliedState);
            ufte.setConsumedItem(offhandItem);
            if (pPlacer instanceof Player && (player = (Player)pPlacer).isCreative()) {
                return;
            }
            offhandItem.shrink(1);
            if (offhandItem.isEmpty()) {
                pPlacer.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
            }
        });
    }

    @Nullable
    public BlockState getAcceptedBlockState(Level pLevel, BlockPos pPos, ItemStack item, Direction face) {
        Item item2 = item.getItem();
        if (!(item2 instanceof BlockItem)) {
            return null;
        }
        BlockItem bi = (BlockItem)item2;
        Block block = bi.getBlock();
        if (block instanceof CopycatBlock) {
            return null;
        }
        BlockState appliedState = block.defaultBlockState();
        boolean hardCodedAllow = this.isAcceptedRegardless(appliedState);
        if (!AllTags.AllBlockTags.COPYCAT_ALLOW.matches(block) && !hardCodedAllow) {
            if (AllTags.AllBlockTags.COPYCAT_DENY.matches(block)) {
                return null;
            }
            if (block instanceof EntityBlock) {
                return null;
            }
            if (block instanceof StairBlock) {
                return null;
            }
            if (pLevel != null) {
                VoxelShape shape = appliedState.getShape((BlockGetter)pLevel, pPos);
                if (shape.isEmpty() || !shape.bounds().equals((Object)Shapes.block().bounds())) {
                    return null;
                }
                VoxelShape collisionShape = appliedState.getCollisionShape((BlockGetter)pLevel, pPos);
                if (collisionShape.isEmpty()) {
                    return null;
                }
            }
        }
        if (face != null) {
            Direction.Axis axis = face.getAxis();
            if (appliedState.hasProperty((Property)BlockStateProperties.FACING)) {
                appliedState = (BlockState)appliedState.setValue((Property)BlockStateProperties.FACING, (Comparable)face);
            }
            if (appliedState.hasProperty((Property)BlockStateProperties.HORIZONTAL_FACING) && axis != Direction.Axis.Y) {
                appliedState = (BlockState)appliedState.setValue((Property)BlockStateProperties.HORIZONTAL_FACING, (Comparable)face);
            }
            if (appliedState.hasProperty((Property)BlockStateProperties.AXIS)) {
                appliedState = (BlockState)appliedState.setValue((Property)BlockStateProperties.AXIS, (Comparable)axis);
            }
            if (appliedState.hasProperty((Property)BlockStateProperties.HORIZONTAL_AXIS) && axis != Direction.Axis.Y) {
                appliedState = (BlockState)appliedState.setValue((Property)BlockStateProperties.HORIZONTAL_AXIS, (Comparable)axis);
            }
        }
        return appliedState;
    }

    public boolean isAcceptedRegardless(BlockState material) {
        return false;
    }

    public BlockState prepareMaterial(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer, InteractionHand pHand, BlockHitResult pHit, BlockState material) {
        return material;
    }

    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.hasBlockEntity() || pState.getBlock() == pNewState.getBlock()) {
            return;
        }
        if (!pIsMoving) {
            this.withBlockEntityDo((BlockGetter)pLevel, pPos, ufte -> Block.popResource((Level)pLevel, (BlockPos)pPos, (ItemStack)ufte.getConsumedItem()));
        }
        pLevel.removeBlockEntity(pPos);
    }

    public BlockState playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
        super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
        if (pPlayer.isCreative()) {
            this.withBlockEntityDo((BlockGetter)pLevel, pPos, ufte -> ufte.setConsumedItem(ItemStack.EMPTY));
        }
        return pState;
    }

    @Override
    public Class<CopycatBlockEntity> getBlockEntityClass() {
        return CopycatBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CopycatBlockEntity> getBlockEntityType() {
        return (BlockEntityType)AllBlockEntityTypes.COPYCAT.get();
    }

    @OnlyIn(value=Dist.CLIENT)
    public BlockState getAppearance(BlockState state, BlockAndTintGetter level, BlockPos pos, Direction side, @Nullable BlockState queryState, @Nullable BlockPos queryPos) {
        if (this.isIgnoredConnectivitySide(level, state, side, pos, queryPos)) {
            return state;
        }
        ModelData modelData = level.getModelData(pos);
        if (modelData == ModelData.EMPTY) {
            return CopycatBlock.getMaterial((BlockGetter)level, pos);
        }
        return CopycatModel.getMaterial(modelData);
    }

    public boolean isIgnoredConnectivitySide(BlockAndTintGetter reader, BlockState state, Direction face, @Nullable BlockPos fromPos, @Nullable BlockPos toPos) {
        return false;
    }

    public abstract boolean canConnectTexturesToward(BlockAndTintGetter var1, BlockPos var2, BlockPos var3, BlockState var4);

    public static BlockState getMaterial(BlockGetter reader, BlockPos targetPos) {
        BlockEntity blockEntity = reader.getBlockEntity(targetPos);
        if (blockEntity instanceof CopycatBlockEntity) {
            CopycatBlockEntity cbe = (CopycatBlockEntity)blockEntity;
            return cbe.getMaterial();
        }
        return Blocks.AIR.defaultBlockState();
    }

    public boolean canFaceBeOccluded(BlockState state, Direction face) {
        return false;
    }

    public boolean shouldFaceAlwaysRender(BlockState state, Direction face) {
        return false;
    }

    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        return CopycatBlock.getMaterial((BlockGetter)level, pos).getSoundType();
    }

    public float getFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        return CopycatBlock.getMaterial((BlockGetter)level, pos).getFriction(level, pos, entity);
    }

    public boolean hasDynamicLightEmission(BlockState state) {
        return true;
    }

    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        AuxiliaryLightManager lightManager = level.getAuxLightManager(pos);
        if (lightManager != null) {
            return lightManager.getLightAt(pos);
        }
        return super.getLightEmission(state, level, pos);
    }

    public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
        return CopycatBlock.getMaterial(level, pos).canHarvestBlock(level, pos, player);
    }

    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
        return CopycatBlock.getMaterial(level, pos).getExplosionResistance(level, pos, explosion);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        BlockState material = CopycatBlock.getMaterial((BlockGetter)level, pos);
        if (AllBlocks.COPYCAT_BASE.has(material) || player != null && player.isShiftKeyDown()) {
            return new ItemStack((ItemLike)this);
        }
        return material.getCloneItemStack(target, level, pos, player);
    }

    public boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
        return CopycatBlock.getMaterial((BlockGetter)level, pos).addLandingEffects(level, pos, state2, entity, numberOfParticles);
    }

    public boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
        return CopycatBlock.getMaterial((BlockGetter)level, pos).addRunningEffects(level, pos, entity);
    }

    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return CopycatBlock.getMaterial((BlockGetter)level, pos).getEnchantPowerBonus(level, pos);
    }

    public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
        return CopycatBlock.getMaterial(level, pos).canEntityDestroy(level, pos, entity);
    }

    public void fallOn(Level pLevel, BlockState pState, BlockPos pPos, Entity pEntity, float p_152430_) {
        BlockState material = CopycatBlock.getMaterial((BlockGetter)pLevel, pPos);
        material.getBlock().fallOn(pLevel, material, pPos, pEntity, p_152430_);
    }

    public float getDestroyProgress(BlockState pState, Player pPlayer, BlockGetter pLevel, BlockPos pPos) {
        return CopycatBlock.getMaterial(pLevel, pPos).getDestroyProgress(pPlayer, pLevel, pPos);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static BlockColor wrappedColor() {
        return new WrappedBlockColor();
    }

    @OnlyIn(value=Dist.CLIENT)
    public static class WrappedBlockColor
    implements BlockColor {
        public int getColor(BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos, int pTintIndex) {
            if (pLevel == null || pPos == null) {
                return GrassColor.get((double)0.5, (double)1.0);
            }
            return Minecraft.getInstance().getBlockColors().getColor(CopycatBlock.getMaterial((BlockGetter)pLevel, pPos), pLevel, pPos, pTintIndex);
        }
    }
}
