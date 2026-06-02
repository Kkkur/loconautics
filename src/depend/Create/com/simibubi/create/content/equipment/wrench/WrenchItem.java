/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  net.neoforged.neoforge.event.entity.player.AttackEntityEvent
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.content.equipment.wrench;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.equipment.wrench.WrenchItemRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import org.jetbrains.annotations.NotNull;

public class WrenchItem
extends Item {
    public WrenchItem(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new WrenchItemRenderer()));
    }

    @NotNull
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !player.mayBuild()) {
            return super.useOn(context);
        }
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Block block = state.getBlock();
        if (!(block instanceof IWrenchable)) {
            if (player.isShiftKeyDown() && this.canWrenchPickup(state)) {
                return this.onItemUseOnOther(context);
            }
            return super.useOn(context);
        }
        IWrenchable actor = (IWrenchable)block;
        if (player.isShiftKeyDown()) {
            return actor.onSneakWrenched(state, context);
        }
        return actor.onWrenched(state, context);
    }

    private boolean canWrenchPickup(BlockState state) {
        return AllTags.AllBlockTags.WRENCH_PICKUP.matches(state);
    }

    private InteractionResult onItemUseOnOther(UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (!(world instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        if (player != null && !player.isCreative()) {
            Block.getDrops((BlockState)state, (ServerLevel)((ServerLevel)world), (BlockPos)pos, (BlockEntity)world.getBlockEntity(pos), (Entity)player, (ItemStack)context.getItemInHand()).forEach(itemStack -> player.getInventory().placeItemBackInInventory(itemStack));
        }
        state.spawnAfterBreak((ServerLevel)world, pos, ItemStack.EMPTY, true);
        world.destroyBlock(pos, false);
        AllSoundEvents.WRENCH_REMOVE.playOnServer(world, (Vec3i)pos, 1.0f, Create.RANDOM.nextFloat() * 0.5f + 0.5f);
        return InteractionResult.SUCCESS;
    }

    public static void wrenchInstaKillsMinecarts(AttackEntityEvent event) {
        Entity target = event.getTarget();
        if (!(target instanceof AbstractMinecart)) {
            return;
        }
        AbstractMinecart minecart = (AbstractMinecart)target;
        Player player = event.getEntity();
        ItemStack heldItem = player.getMainHandItem();
        if (!AllItems.WRENCH.isIn(heldItem)) {
            return;
        }
        if (player.isCreative()) {
            return;
        }
        minecart.hurt(minecart.damageSources().playerAttack(player), 100.0f);
    }
}
