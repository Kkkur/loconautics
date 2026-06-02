/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.coordinates.BlockPosArgument
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.item.DyeColor
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.infrastructure.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.actors.seat.SeatBlock;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlock;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class DebugHatsCommand {
    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"debugHats").requires(cs -> cs.hasPermission(4))).then(Commands.argument((String)"pos", (ArgumentType)BlockPosArgument.blockPos()).executes(ctx -> {
            BlockPos origin = BlockPosArgument.getLoadedBlockPos((CommandContext)ctx, (String)"pos");
            BlockPos.MutableBlockPos pos = origin.mutable();
            for (EntityType entityType : BuiltInRegistries.ENTITY_TYPE) {
                ServerLevel level;
                Entity entity = entityType.create((Level)(level = ((CommandSourceStack)ctx.getSource()).getLevel()));
                if (!(entity instanceof LivingEntity)) continue;
                level.setBlockAndUpdate((BlockPos)pos, AllBlocks.SEATS.get(DyeColor.RED).getDefaultState());
                level.setBlockAndUpdate(pos.east(), (BlockState)AllBlocks.STOCK_TICKER.getDefaultState().setValue((Property)StockTickerBlock.FACING, (Comparable)Direction.EAST));
                entity.moveTo(pos.getCenter());
                if (entity instanceof Mob) {
                    Mob mob = (Mob)entity;
                    mob.setNoAi(true);
                }
                entity.setInvulnerable(true);
                entity.setSilent(true);
                level.tryAddFreshEntityWithPassengers(entity);
                SeatBlock.sitDown((Level)level, (BlockPos)pos, entity);
                pos.move(0, 0, 2);
            }
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Placed entities"), true);
            return 1;
        }));
    }
}
