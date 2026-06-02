/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeHolder
 *  net.minecraft.world.item.crafting.RecipeType
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.content.kinetics.deployer;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipeParams;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ManualApplicationRecipe
extends ItemApplicationRecipe {
    @SubscribeEvent
    public static void manualApplicationRecipesApplyInWorld(PlayerInteractEvent.RightClickBlock event) {
        boolean keepHeld;
        Level level = event.getLevel();
        ItemStack heldItem = event.getItemStack();
        BlockPos pos = event.getPos();
        BlockState blockState = level.getBlockState(pos);
        if (heldItem.isEmpty()) {
            return;
        }
        if (blockState.isAir()) {
            return;
        }
        if (event.isCanceled()) {
            return;
        }
        RecipeType type = AllRecipeTypes.ITEM_APPLICATION.getType();
        Optional<RecipeHolder> foundRecipe = level.getRecipeManager().getAllRecipesFor(type).stream().filter(r -> {
            ManualApplicationRecipe mar = (ManualApplicationRecipe)r.value();
            return mar.testBlock(blockState) && ((Ingredient)mar.ingredients.get(1)).test(heldItem);
        }).findFirst();
        if (foundRecipe.isEmpty()) {
            return;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
        if (level.isClientSide()) {
            return;
        }
        level.playSound(null, pos, SoundEvents.COPPER_BREAK, SoundSource.PLAYERS, 1.0f, 1.45f);
        ManualApplicationRecipe recipe = (ManualApplicationRecipe)foundRecipe.get().value();
        level.destroyBlock(pos, false);
        BlockState transformedBlock = recipe.transformBlock(blockState, level.random);
        level.setBlock(pos, transformedBlock, 3);
        recipe.rollResults(level.random).forEach(stack -> Block.popResource((Level)level, (BlockPos)pos, (ItemStack)stack));
        boolean creative = event.getEntity() != null && event.getEntity().isCreative();
        boolean unbreakable = heldItem.has(DataComponents.UNBREAKABLE);
        boolean bl = keepHeld = recipe.shouldKeepHeldItem() || creative;
        if (!unbreakable && !keepHeld) {
            if (heldItem.getMaxDamage() > 0) {
                heldItem.hurtAndBreak(1, (LivingEntity)event.getEntity(), EquipmentSlot.MAINHAND);
            } else {
                Player player = event.getEntity();
                InteractionHand hand = event.getHand();
                ItemStack leftover = heldItem.getCraftingRemainingItem();
                heldItem.shrink(1);
                if (heldItem.isEmpty()) {
                    player.setItemInHand(hand, leftover);
                } else if (!player.getInventory().add(leftover)) {
                    player.drop(leftover, false);
                }
            }
        }
        ManualApplicationRecipe.awardAdvancements(event.getEntity(), transformedBlock);
    }

    private static void awardAdvancements(Player player, BlockState placed) {
        CreateAdvancement advancement = null;
        if (AllBlocks.ANDESITE_CASING.has(placed)) {
            advancement = AllAdvancements.ANDESITE_CASING;
        } else if (AllBlocks.BRASS_CASING.has(placed)) {
            advancement = AllAdvancements.BRASS_CASING;
        } else if (AllBlocks.COPPER_CASING.has(placed)) {
            advancement = AllAdvancements.COPPER_CASING;
        } else if (AllBlocks.RAILWAY_CASING.has(placed)) {
            advancement = AllAdvancements.TRAIN_CASING;
        } else {
            return;
        }
        advancement.awardTo(player);
    }

    public ManualApplicationRecipe(ItemApplicationRecipeParams params) {
        super(AllRecipeTypes.ITEM_APPLICATION, params);
    }

    public static RecipeHolder<DeployerApplicationRecipe> asDeploying(RecipeHolder<?> recipe) {
        ManualApplicationRecipe mar = (ManualApplicationRecipe)recipe.value();
        ResourceLocation id = AllRecipeTypes.CAN_BE_AUTOMATED.test(recipe) ? recipe.id().withSuffix("_using_deployer") : recipe.id();
        ItemApplicationRecipe.Builder builder = (ItemApplicationRecipe.Builder)((ItemApplicationRecipe.Builder)new ItemApplicationRecipe.Builder<DeployerApplicationRecipe>(DeployerApplicationRecipe::new, id).require((Ingredient)mar.ingredients.get(0))).require((Ingredient)mar.ingredients.get(1));
        for (ProcessingOutput output : mar.results) {
            builder.output(output);
        }
        if (mar.shouldKeepHeldItem()) {
            builder.toolNotConsumed();
        }
        return new RecipeHolder(id, (Recipe)((DeployerApplicationRecipe)builder.build()));
    }

    public boolean testBlock(BlockState in) {
        return ((Ingredient)this.ingredients.get(0)).test(new ItemStack((ItemLike)in.getBlock().asItem()));
    }

    public BlockState transformBlock(BlockState in, RandomSource randomSource) {
        ProcessingOutput mainOutput = (ProcessingOutput)this.results.get(0);
        ItemStack output = mainOutput.rollOutput(randomSource);
        Item item = output.getItem();
        if (item instanceof BlockItem) {
            BlockItem bi = (BlockItem)item;
            return BlockHelper.copyProperties(in, bi.getBlock().defaultBlockState());
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public List<ItemStack> rollResults(RandomSource randomSource) {
        return this.rollResults(this.getRollableResultsExceptBlock(), randomSource);
    }

    public List<ProcessingOutput> getRollableResultsExceptBlock() {
        ProcessingOutput mainOutput = (ProcessingOutput)this.results.get(0);
        if (mainOutput.getStack().getItem() instanceof BlockItem) {
            return this.results.subList(1, this.results.size());
        }
        return this.results;
    }
}
