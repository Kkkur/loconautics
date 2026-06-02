/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.bus.api.SubscribeEvent
 *  net.neoforged.fml.common.EventBusSubscriber
 *  net.neoforged.neoforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 */
package com.simibubi.create.foundation.block;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.utility.BlockHelper;
import java.util.HashSet;
import java.util.Set;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class ItemUseOverrides {
    private static final Set<ResourceLocation> OVERRIDES = new HashSet<ResourceLocation>();

    public static void addBlock(Block block) {
        OVERRIDES.add(RegisteredObjectsHelper.getKeyOrThrow((Block)block));
    }

    @SubscribeEvent
    public static void onBlockActivated(PlayerInteractEvent.RightClickBlock event) {
        if (AllItems.WRENCH.isIn(event.getItemStack())) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        Direction face = event.getFace();
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        BlockState state = level.getBlockState(pos);
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)state.getBlock());
        if (!OVERRIDES.contains(id)) {
            return;
        }
        BlockHitResult blockTrace = new BlockHitResult(VecHelper.getCenterOf((Vec3i)pos), face, pos, true);
        InteractionResult result = BlockHelper.invokeUse(state, level, player, hand, blockTrace);
        if (!result.consumesAction()) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(result);
    }
}
