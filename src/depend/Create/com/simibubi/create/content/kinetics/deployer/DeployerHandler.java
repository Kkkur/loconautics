/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.createmod.catnip.levelWrappers.WrappedLevel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.network.protocol.game.ServerboundPlayerActionPacket$Action
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.ServerPlayerGameMode
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.InteractionResultHolder
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.npc.AbstractVillager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodProperties
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.BucketItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.MobBucketItem
 *  net.minecraft.world.item.context.BlockPlaceContext
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.GameType
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.ServerLevelAccessor
 *  net.minecraft.world.level.block.BaseFireBlock
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.DoublePlantBlock
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.DoubleBlockHalf
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.common.CommonHooks
 *  net.neoforged.neoforge.common.extensions.IBaseRailBlockExtension
 *  net.neoforged.neoforge.common.util.TriState
 *  net.neoforged.neoforge.event.EventHooks
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  org.apache.commons.lang3.tuple.Pair
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.deployer;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.mounted.CartAssemblerBlockItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemComponent;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.kinetics.deployer.DeployerFakePlayer;
import com.simibubi.create.content.trains.track.ITrackBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.levelWrappers.WrappedLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.extensions.IBaseRailBlockExtension;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class DeployerHandler {
    private static final Map<BlockPos, List<ItemEntity>> CAPTURED_BLOCK_DROPS = new HashMap<BlockPos, List<ItemEntity>>();
    public static final Map<BlockPos, List<ItemEntity>> CAPTURED_BLOCK_DROPS_VIEW = Collections.unmodifiableMap(CAPTURED_BLOCK_DROPS);

    static boolean shouldActivate(ItemStack held, Level world, BlockPos targetPos, @Nullable Direction facing) {
        if (held.getItem() instanceof BlockItem && world.getBlockState(targetPos).getBlock() == ((BlockItem)held.getItem()).getBlock()) {
            return false;
        }
        Item item = held.getItem();
        if (item instanceof BucketItem) {
            BucketItem bucketItem = (BucketItem)item;
            Fluid fluid = bucketItem.content;
            if (fluid != Fluids.EMPTY && world.getFluidState(targetPos).getType() == fluid) {
                return false;
            }
        }
        return held.isEmpty() || facing != Direction.DOWN || BlockEntityBehaviour.get((BlockGetter)world, targetPos, TransportedItemStackHandlerBehaviour.TYPE) == null;
    }

    static void activate(DeployerFakePlayer player, Vec3 vec, BlockPos clickedPos, Vec3 extensionVector, DeployerBlockEntity.Mode mode) {
        HashMultimap attributeModifiers = HashMultimap.create();
        ItemStack mainHandItem = player.getMainHandItem();
        mainHandItem.getAttributeModifiers().modifiers().forEach(e -> attributeModifiers.put((Object)e.attribute(), (Object)e.modifier()));
        EnchantmentHelper.forEachModifier((ItemStack)mainHandItem, (EquipmentSlot)EquipmentSlot.MAINHAND, (x$0, x$1) -> attributeModifiers.put(x$0, x$1));
        player.getAttributes().addTransientAttributeModifiers((Multimap)attributeModifiers);
        DeployerHandler.activateInner(player, vec, clickedPos, extensionVector, mode);
        player.getAttributes().removeAttributeModifiers((Multimap)attributeModifiers);
    }

    private static void activateInner(DeployerFakePlayer player, Vec3 vec, BlockPos clickedPos, Vec3 extensionVector, DeployerBlockEntity.Mode mode) {
        ItemStack resultStack;
        InteractionResultHolder onItemRightClick;
        InteractionResult onItemUse;
        boolean flag1;
        InteractionResult actionresult;
        ClipContext rayTraceContext;
        BlockHitResult result;
        Vec3 rayOrigin = vec.add(extensionVector.scale(1.515625));
        Vec3 rayTarget = vec.add(extensionVector.scale(2.484375));
        player.setPos(rayOrigin.x, rayOrigin.y, rayOrigin.z);
        BlockPos pos = BlockPos.containing((Position)vec);
        ItemStack stack = player.getMainHandItem();
        Item item = stack.getItem();
        ServerLevel level = player.serverLevel();
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(clickedPos)).stream().filter(e -> !(e instanceof AbstractContraptionEntity)).toList();
        InteractionHand hand = InteractionHand.MAIN_HAND;
        if (!entities.isEmpty()) {
            Entity entity = entities.get(level.random.nextInt(entities.size()));
            ArrayList capturedDrops = new ArrayList();
            boolean success = false;
            entity.captureDrops(capturedDrops);
            if (mode == DeployerBlockEntity.Mode.USE) {
                InteractionResult cancelResult = CommonHooks.onInteractEntity((Player)player, (Entity)entity, (InteractionHand)hand);
                if (cancelResult == InteractionResult.FAIL) {
                    entity.captureDrops(null);
                    return;
                }
                if (cancelResult == null) {
                    LivingEntity livingEntity;
                    if (entity.interact((Player)player, hand).consumesAction()) {
                        AbstractVillager villager;
                        if (entity instanceof AbstractVillager && (villager = (AbstractVillager)entity).getTradingPlayer() instanceof DeployerFakePlayer) {
                            villager.setTradingPlayer(null);
                        }
                        success = true;
                    } else if (entity instanceof LivingEntity && stack.interactLivingEntity((Player)player, livingEntity = (LivingEntity)entity, hand).consumesAction()) {
                        success = true;
                    }
                }
                if (!success && entity instanceof Player) {
                    FoodProperties foodProperties;
                    Player playerEntity = (Player)entity;
                    if (stack.has(DataComponents.FOOD) && (foodProperties = item.getFoodProperties(stack, (LivingEntity)player)) != null && playerEntity.canEat(foodProperties.canAlwaysEat())) {
                        ItemStack copy = stack.copy();
                        player.setItemInHand(hand, stack.finishUsingItem((Level)level, (LivingEntity)playerEntity));
                        player.spawnedItemEffects = copy;
                        success = true;
                    }
                    if (AllTags.AllItemTags.DEPLOYABLE_DRINK.matches(stack)) {
                        player.spawnedItemEffects = stack.copy();
                        player.setItemInHand(hand, stack.finishUsingItem((Level)level, (LivingEntity)playerEntity));
                        success = true;
                    }
                }
            }
            if (mode == DeployerBlockEntity.Mode.PUNCH) {
                player.resetAttackStrengthTicker();
                player.attack(entity);
                success = true;
            }
            entity.captureDrops(null);
            capturedDrops.forEach(e -> player.getInventory().placeItemBackInInventory(e.getItem()));
            if (success) {
                return;
            }
        }
        if ((result = level.clip(rayTraceContext = new ClipContext(rayOrigin, rayTarget, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player))).getBlockPos() != clickedPos) {
            result = new BlockHitResult(result.getLocation(), result.getDirection(), clickedPos, result.isInside());
        }
        BlockState clickedState = level.getBlockState(clickedPos);
        Direction face = result.getDirection();
        if (face == null) {
            face = Direction.getNearest((double)extensionVector.x, (double)extensionVector.y, (double)extensionVector.z).getOpposite();
        }
        if (mode == DeployerBlockEntity.Mode.PUNCH) {
            if (!level.mayInteract((Player)player, clickedPos)) {
                return;
            }
            if (clickedState.getShape((BlockGetter)level, clickedPos).isEmpty()) {
                player.blockBreakingProgress = null;
                return;
            }
            PlayerInteractEvent.LeftClickBlock event = CommonHooks.onLeftClickBlock((Player)player, (BlockPos)clickedPos, (Direction)face, (ServerboundPlayerActionPacket.Action)ServerboundPlayerActionPacket.Action.START_DESTROY_BLOCK);
            if (event.isCanceled()) {
                return;
            }
            if (BlockHelper.extinguishFire((Level)level, (Player)player, clickedPos, face)) {
                return;
            }
            if (event.getUseBlock() != TriState.FALSE) {
                clickedState.attack((Level)level, clickedPos, (Player)player);
            }
            if (stack.isEmpty()) {
                return;
            }
            float progress = clickedState.getDestroyProgress((Player)player, (BlockGetter)level, clickedPos) * 16.0f;
            float before = 0.0f;
            Pair<BlockPos, Float> blockBreakingProgress = player.blockBreakingProgress;
            if (blockBreakingProgress != null) {
                before = ((Float)blockBreakingProgress.getValue()).floatValue();
            }
            progress += before;
            level.playSound(null, clickedPos, clickedState.getSoundType().getHitSound(), SoundSource.NEUTRAL, 0.25f, 1.0f);
            if (progress >= 1.0f) {
                DeployerHandler.tryHarvestBlock((ServerPlayer)player, player.gameMode, clickedPos);
                level.destroyBlockProgress(player.getId(), clickedPos, -1);
                player.blockBreakingProgress = null;
                return;
            }
            if (progress <= 0.0f) {
                player.blockBreakingProgress = null;
                return;
            }
            if ((int)(before * 10.0f) != (int)(progress * 10.0f)) {
                level.destroyBlockProgress(player.getId(), clickedPos, (int)(progress * 10.0f));
            }
            player.blockBreakingProgress = Pair.of((Object)clickedPos, (Object)Float.valueOf(progress));
            return;
        }
        UseOnContext itemusecontext = new UseOnContext((Player)player, hand, result);
        TriState useBlock = TriState.DEFAULT;
        TriState useItem = TriState.DEFAULT;
        if (!clickedState.getShape((BlockGetter)level, clickedPos).isEmpty()) {
            PlayerInteractEvent.RightClickBlock event = CommonHooks.onRightClickBlock((Player)player, (InteractionHand)hand, (BlockPos)clickedPos, (BlockHitResult)result);
            useBlock = event.getUseBlock();
            useItem = event.getUseItem();
        }
        if (useItem != TriState.FALSE && (actionresult = stack.onItemUseFirst(itemusecontext)) != InteractionResult.PASS) {
            return;
        }
        boolean holdingSomething = !player.getMainHandItem().isEmpty();
        boolean bl = flag1 = !player.isShiftKeyDown() || !holdingSomething || stack.doesSneakBypassUse((LevelReader)level, clickedPos, (Player)player);
        if (useBlock != TriState.FALSE && flag1 && DeployerHandler.safeOnUse(clickedState, (Level)level, clickedPos, (Player)player, hand, result).consumesAction()) {
            return;
        }
        if (stack.isEmpty()) {
            return;
        }
        if (useItem == TriState.FALSE) {
            return;
        }
        if (item instanceof CartAssemblerBlockItem && clickedState.canBeReplaced(new BlockPlaceContext(itemusecontext))) {
            return;
        }
        if (item == Items.FLINT_AND_STEEL) {
            Direction newFace = result.getDirection();
            BlockPos newPos = result.getBlockPos();
            if (!BaseFireBlock.canBePlacedAt((Level)level, (BlockPos)clickedPos, (Direction)newFace)) {
                newFace = Direction.UP;
            }
            if (clickedState.isAir()) {
                newPos = newPos.relative(face.getOpposite());
            }
            result = new BlockHitResult(result.getLocation(), newFace, newPos, result.isInside());
            itemusecontext = new UseOnContext((Player)player, hand, result);
        }
        if ((onItemUse = stack.useOn(itemusecontext)).consumesAction()) {
            BlockItem bi;
            if (item instanceof BlockItem && ((bi = (BlockItem)item).getBlock() instanceof IBaseRailBlockExtension || bi.getBlock() instanceof ITrackBlock)) {
                player.placedTracks = true;
            }
            return;
        }
        if (item == Items.ENDER_PEARL) {
            return;
        }
        if (AllTags.AllItemTags.DEPLOYABLE_DRINK.matches(item)) {
            return;
        }
        Object itemUseWorld = level;
        if (item instanceof BucketItem || item instanceof SandPaperItem) {
            itemUseWorld = new ItemUseWorld(level, face, pos);
        }
        if ((onItemRightClick = item.use((Level)itemUseWorld, (Player)player, hand)).getResult().consumesAction() && item instanceof MobBucketItem) {
            MobBucketItem bucketItem = (MobBucketItem)item;
            bucketItem.checkExtraContent((Player)player, (Level)level, stack, clickedPos);
        }
        if ((resultStack = (ItemStack)onItemRightClick.getObject()) != stack || resultStack.getCount() != stack.getCount() || resultStack.getUseDuration((LivingEntity)player) > 0 || resultStack.getDamageValue() != stack.getDamageValue()) {
            player.setItemInHand(hand, (ItemStack)onItemRightClick.getObject());
        }
        if (stack.getItem() instanceof SandPaperItem && stack.has(AllDataComponents.SAND_PAPER_POLISHING)) {
            player.spawnedItemEffects = ((SandPaperItemComponent)stack.get(AllDataComponents.SAND_PAPER_POLISHING)).item();
            AllSoundEvents.SANDING_SHORT.playOnServer((Level)level, (Vec3i)pos, 0.25f, 1.0f);
        }
        if (!player.getUseItem().isEmpty()) {
            player.setItemInHand(hand, stack.finishUsingItem((Level)level, (LivingEntity)player));
        }
        player.stopUsingItem();
    }

    public static boolean tryHarvestBlock(ServerPlayer player, ServerPlayerGameMode interactionManager, BlockPos pos) {
        ServerLevel world = player.serverLevel();
        BlockState blockstate = world.getBlockState(pos);
        GameType gameType = interactionManager.getGameModeForPlayer();
        if (CommonHooks.fireBlockBreak((Level)world, (GameType)gameType, (ServerPlayer)player, (BlockPos)pos, (BlockState)blockstate).isCanceled()) {
            return false;
        }
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (player.blockActionRestricted((Level)world, pos, gameType)) {
            return false;
        }
        ItemStack prevHeldItem = player.getMainHandItem();
        ItemStack heldItem = prevHeldItem.copy();
        boolean canHarvest = blockstate.canHarvestBlock((BlockGetter)world, pos, (Player)player);
        prevHeldItem.mineBlock((Level)world, blockstate, pos, (Player)player);
        if (prevHeldItem.isEmpty() && !heldItem.isEmpty()) {
            EventHooks.onPlayerDestroyItem((Player)player, (ItemStack)heldItem, (InteractionHand)InteractionHand.MAIN_HAND);
        }
        BlockPos posUp = pos.above();
        BlockState stateUp = world.getBlockState(posUp);
        if (blockstate.getBlock() instanceof DoublePlantBlock && blockstate.getValue((Property)DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER && stateUp.getBlock() == blockstate.getBlock() && stateUp.getValue((Property)DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
            world.setBlock(posUp, Blocks.AIR.defaultBlockState(), 35);
        } else if (!blockstate.onDestroyedByPlayer((Level)world, pos, (Player)player, canHarvest, world.getFluidState(pos))) {
            return true;
        }
        blockstate.getBlock().destroy((LevelAccessor)world, pos, blockstate);
        if (!canHarvest) {
            return true;
        }
        Block.getDrops((BlockState)blockstate, (ServerLevel)world, (BlockPos)pos, (BlockEntity)blockEntity, (Entity)player, (ItemStack)prevHeldItem).forEach(item -> player.getInventory().placeItemBackInInventory(item));
        blockstate.spawnAfterBreak(world, pos, prevHeldItem, true);
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static InteractionResult safeOnUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
        ArrayList drops = new ArrayList(4);
        CAPTURED_BLOCK_DROPS.put(pos, drops);
        try {
            InteractionResult result = BlockHelper.invokeUse(state, world, player, hand, ray);
            for (ItemEntity itemEntity : drops) {
                player.getInventory().placeItemBackInInventory(itemEntity.getItem());
            }
            InteractionResult interactionResult = result;
            return interactionResult;
        }
        finally {
            CAPTURED_BLOCK_DROPS.remove(pos);
        }
    }

    private static final class ItemUseWorld
    extends WrappedLevel
    implements ServerLevelAccessor {
        private final Direction face;
        private final BlockPos pos;
        boolean rayMode = false;

        private ItemUseWorld(ServerLevel level, Direction face, BlockPos pos) {
            super((Level)level);
            this.face = face;
            this.pos = pos;
        }

        public ServerLevel getLevel() {
            return (ServerLevel)this.level;
        }

        public BlockHitResult clip(ClipContext context) {
            this.rayMode = true;
            BlockHitResult rayTraceBlocks = super.clip(context);
            this.rayMode = false;
            return rayTraceBlocks;
        }

        public BlockState getBlockState(BlockPos position) {
            if (this.rayMode && (this.pos.relative(this.face.getOpposite(), 3).equals((Object)position) || this.pos.relative(this.face.getOpposite(), 1).equals((Object)position))) {
                return Blocks.BEDROCK.defaultBlockState();
            }
            return this.level.getBlockState(position);
        }
    }
}
