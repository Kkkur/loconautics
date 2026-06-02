/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.tags.ItemTags
 *  net.minecraft.util.RandomSource
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.ItemInteractionResult
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.ShovelItem
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.StateDefinition$Builder
 *  net.minecraft.world.level.block.state.properties.EnumProperty
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.pathfinder.PathComputationType
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.shapes.CollisionContext
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.common.ItemAbility
 */
package com.simibubi.create.content.processing.burner;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.ItemAbility;

public class LitBlazeBurnerBlock
extends Block
implements IWrenchable {
    public static final ItemAbility EXTINGUISH_FLAME_ACTION = ItemAbility.get((String)Create.asResource("extinguish_flame").toString());
    public static final EnumProperty<FlameType> FLAME_TYPE = EnumProperty.create((String)"flame_type", FlameType.class);

    public LitBlazeBurnerBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FLAME_TYPE, (Comparable)((Object)FlameType.REGULAR)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(new Property[]{FLAME_TYPE});
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() instanceof ShovelItem || stack.getItem().canPerformAction(stack, EXTINGUISH_FLAME_ACTION)) {
            level.playSound(player, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5f, 2.0f);
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            stack.hurtAndBreak(1, (LivingEntity)player, EquipmentSlot.MAINHAND);
            level.setBlockAndUpdate(pos, AllBlocks.BLAZE_BURNER.getDefaultState());
            return ItemInteractionResult.SUCCESS;
        }
        if (state.getValue(FLAME_TYPE) == FlameType.REGULAR && stack.is(ItemTags.SOUL_FIRE_BASE_BLOCKS)) {
            level.playSound(player, pos, SoundEvents.SOUL_SAND_PLACE, SoundSource.BLOCKS, 1.0f, level.random.nextFloat() * 0.4f + 0.8f);
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            }
            level.setBlockAndUpdate(pos, (BlockState)this.defaultBlockState().setValue(FLAME_TYPE, (Comparable)((Object)FlameType.SOUL)));
            return ItemInteractionResult.SUCCESS;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return ((BlazeBurnerBlock)AllBlocks.BLAZE_BURNER.get()).getShape(state, reader, pos, context);
    }

    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
        return AllItems.EMPTY_BLAZE_BURNER.asStack();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        world.addAlwaysVisibleParticle((ParticleOptions)ParticleTypes.LARGE_SMOKE, true, (double)pos.getX() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), (double)pos.getY() + random.nextDouble() + random.nextDouble(), (double)pos.getZ() + 0.5 + random.nextDouble() / 3.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (random.nextInt(10) == 0) {
            world.playLocalSound((double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f), SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.25f + random.nextFloat() * 0.25f, random.nextFloat() * 0.7f + 0.6f, false);
        }
        if (state.getValue(FLAME_TYPE) == FlameType.SOUL) {
            if (random.nextInt(8) == 0) {
                world.addParticle((ParticleOptions)ParticleTypes.SOUL, (double)((float)pos.getX() + 0.5f) + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), (double)((float)pos.getY() + 0.3f) + random.nextDouble() / 2.0, (double)((float)pos.getZ() + 0.5f) + random.nextDouble() / 4.0 * (double)(random.nextBoolean() ? 1 : -1), 0.0, random.nextDouble() * 0.04 + 0.04, 0.0);
            }
            return;
        }
        if (random.nextInt(5) == 0) {
            for (int i = 0; i < random.nextInt(1) + 1; ++i) {
                world.addParticle((ParticleOptions)ParticleTypes.LAVA, (double)((float)pos.getX() + 0.5f), (double)((float)pos.getY() + 0.5f), (double)((float)pos.getZ() + 0.5f), (double)(random.nextFloat() / 2.0f), 5.0E-5, (double)(random.nextFloat() / 2.0f));
            }
        }
    }

    public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level p_180641_2_, BlockPos p_180641_3_) {
        return state.getValue(FLAME_TYPE) == FlameType.REGULAR ? 1 : 2;
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context) {
        return ((BlazeBurnerBlock)AllBlocks.BLAZE_BURNER.get()).getCollisionShape(state, reader, pos, context);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public static int getLight(BlockState state) {
        if (state.getValue(FLAME_TYPE) == FlameType.SOUL) {
            return 9;
        }
        return 12;
    }

    public static enum FlameType implements StringRepresentable
    {
        REGULAR,
        SOUL;


        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
