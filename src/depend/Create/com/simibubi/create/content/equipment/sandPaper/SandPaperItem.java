/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.createmod.catnip.data.TriState
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ItemParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.UseAnim
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.common.ItemAbilities
 *  net.neoforged.neoforge.common.ItemAbility
 */
package com.simibubi.create.content.equipment.sandPaper;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemRenderer;
import com.simibubi.create.content.equipment.sandPaper.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.item.CustomUseEffectsItem;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.mixin.accessor.LivingEntityAccessor;
import java.util.function.Consumer;
import javax.annotation.ParametersAreNonnullByDefault;
import net.createmod.catnip.data.TriState;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SandPaperItem
extends Item
implements CustomUseEffectsItem {
    public SandPaperItem(Item.Properties properties) {
        super(properties.durability(8));
    }

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        InteractionResultHolder FAIL = new InteractionResultHolder(InteractionResult.FAIL, (Object)itemstack);
        if (itemstack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
            playerIn.startUsingItem(handIn);
            return new InteractionResultHolder(InteractionResult.PASS, (Object)itemstack);
        }
        InteractionHand otherHand = handIn == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemInOtherHand = playerIn.getItemInHand(otherHand);
        if (SandPaperPolishingRecipe.canPolish(worldIn, itemInOtherHand)) {
            ItemStack item = itemInOtherHand.copy();
            ItemStack toPolish = item.split(1);
            playerIn.startUsingItem(handIn);
            itemstack.set(AllDataComponents.SAND_PAPER_POLISHING, (Object)new SandPaperItemComponent(toPolish));
            playerIn.setItemInHand(otherHand, item);
            return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)itemstack);
        }
        BlockHitResult raytraceresult = SandPaperItem.getPlayerPOVHitResult((Level)worldIn, (Player)playerIn, (ClipContext.Fluid)ClipContext.Fluid.NONE);
        Vec3 hitVec = raytraceresult.getLocation();
        AABB bb = new AABB(hitVec, hitVec).inflate(1.0);
        ItemEntity pickUp = null;
        for (ItemEntity itemEntity : worldIn.getEntitiesOfClass(ItemEntity.class, bb)) {
            ItemStack stack;
            if (!itemEntity.isAlive() || itemEntity.position().distanceTo(playerIn.position()) > 3.0 || !SandPaperPolishingRecipe.canPolish(worldIn, stack = itemEntity.getItem())) continue;
            pickUp = itemEntity;
            break;
        }
        if (pickUp == null) {
            return FAIL;
        }
        ItemStack item = pickUp.getItem().copy();
        ItemStack toPolish = item.split(1);
        playerIn.startUsingItem(handIn);
        if (!worldIn.isClientSide) {
            itemstack.set(AllDataComponents.SAND_PAPER_POLISHING, (Object)new SandPaperItemComponent(toPolish));
            if (item.isEmpty()) {
                pickUp.discard();
            } else {
                pickUp.setItem(item);
            }
        }
        return new InteractionResultHolder(InteractionResult.SUCCESS, (Object)itemstack);
    }

    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
        if (!(entityLiving instanceof Player)) {
            return stack;
        }
        Player player = (Player)entityLiving;
        if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
            ItemStack toPolish = ((SandPaperItemComponent)stack.get(AllDataComponents.SAND_PAPER_POLISHING)).item();
            ItemStack polished = SandPaperPolishingRecipe.applyPolish(level, entityLiving.position(), toPolish, stack);
            if (level.isClientSide) {
                SandPaperItem.spawnParticles(entityLiving.getEyePosition(1.0f).add(entityLiving.getLookAngle().scale(0.5)), toPolish, level);
                return stack;
            }
            Inventory playerInv = player.getInventory();
            if (!polished.isEmpty()) {
                playerInv.placeItemBackInInventory(polished);
            }
            if (toPolish.hasCraftingRemainingItem()) {
                playerInv.placeItemBackInInventory(toPolish.getCraftingRemainingItem());
            }
            stack.remove(AllDataComponents.SAND_PAPER_POLISHING);
            stack.hurtAndBreak(1, entityLiving, LivingEntity.getSlotForHand((InteractionHand)entityLiving.getUsedItemHand()));
        }
        return stack;
    }

    public static void spawnParticles(Vec3 location, ItemStack polishedStack, Level world) {
        for (int i = 0; i < 20; ++i) {
            Vec3 motion = VecHelper.offsetRandomly((Vec3)Vec3.ZERO, (RandomSource)world.random, (float)0.125f);
            world.addParticle((ParticleOptions)new ItemParticleOption(ParticleTypes.ITEM, polishedStack), location.x, location.y, location.z, motion.x, motion.y, motion.z);
        }
    }

    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player)) {
            return;
        }
        Player player = (Player)entityLiving;
        if (stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
            ItemStack toPolish = ((SandPaperItemComponent)stack.get(AllDataComponents.SAND_PAPER_POLISHING)).item();
            player.getInventory().placeItemBackInInventory(toPolish);
            stack.remove(AllDataComponents.SAND_PAPER_POLISHING);
        }
    }

    public InteractionResult useOn(UseOnContext context) {
        BlockPos pos;
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockState state = level.getBlockState(pos = context.getClickedPos());
        BlockState newState = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
        if (newState != null) {
            AllSoundEvents.SANDING_LONG.play(level, player, (Vec3i)pos, 1.0f, 1.0f + (level.random.nextFloat() * 0.5f - 1.0f) / 5.0f);
            level.levelEvent(player, 3005, pos, 0);
        } else {
            newState = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
            if (newState != null) {
                AllSoundEvents.SANDING_LONG.play(level, player, (Vec3i)pos, 1.0f, 1.0f + (level.random.nextFloat() * 0.5f - 1.0f) / 5.0f);
                level.levelEvent(player, 3004, pos, 0);
            }
        }
        if (newState != null) {
            level.setBlockAndUpdate(pos, newState);
            if (player != null) {
                stack.hurtAndBreak(1, (LivingEntity)player, LivingEntity.getSlotForHand((InteractionHand)player.getUsedItemHand()));
            }
            return InteractionResult.sidedSuccess((boolean)level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return itemAbility == ItemAbilities.AXE_SCRAPE || itemAbility == ItemAbilities.AXE_WAX_OFF;
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        ItemStack polishing;
        if (stack.has(AllDataComponents.SAND_PAPER_POLISHING) && !(polishing = ((SandPaperItemComponent)stack.get(AllDataComponents.SAND_PAPER_POLISHING)).item()).isEmpty()) {
            ((LivingEntityAccessor)entity).create$callSpawnItemParticles(polishing, 1);
        }
        if ((entity.getTicksUsingItem() - 6) % 7 == 0) {
            entity.playSound(entity.getEatingSound(stack), 0.9f + 0.2f * random.nextFloat(), random.nextFloat() * 0.2f + 0.9f);
        }
        return true;
    }

    public SoundEvent getEatingSound() {
        return AllSoundEvents.SANDING_SHORT.getMainEvent();
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    public int getEnchantmentValue() {
        return 1;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new SandPaperItemRenderer()));
    }
}
