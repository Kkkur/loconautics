/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  net.createmod.catnip.data.Iterate
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.commands.Commands
 *  net.minecraft.commands.arguments.EntityArgument
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.vehicle.AbstractMinecart
 */
package com.simibubi.create.infrastructure.command;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.simibubi.create.AllAttachmentTypes;
import com.simibubi.create.content.contraptions.minecart.CouplingHandler;
import com.simibubi.create.content.contraptions.minecart.capability.CapabilityMinecartController;
import com.simibubi.create.content.contraptions.minecart.capability.MinecartController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import net.createmod.catnip.data.Iterate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

public class CouplingCommand {
    public static final SimpleCommandExceptionType ONLY_MINECARTS_ALLOWED = new SimpleCommandExceptionType((Message)Component.literal((String)"Only Minecarts can be coupled"));
    public static final SimpleCommandExceptionType SAME_DIMENSION = new SimpleCommandExceptionType((Message)Component.literal((String)"Minecarts have to be in the same Dimension"));
    public static final DynamicCommandExceptionType TWO_CARTS = new DynamicCommandExceptionType(a -> Component.literal((String)("Your selector targeted " + String.valueOf(a) + " entities. You can only couple 2 Minecarts at a time.")));

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"coupling").requires(cs -> cs.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal((String)"add").then(Commands.argument((String)"cart1", (ArgumentType)EntityArgument.entity()).then(Commands.argument((String)"cart2", (ArgumentType)EntityArgument.entity()).executes(ctx -> {
            Entity cart1 = EntityArgument.getEntity((CommandContext)ctx, (String)"cart1");
            if (!(cart1 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            Entity cart2 = EntityArgument.getEntity((CommandContext)ctx, (String)"cart2");
            if (!(cart2 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            if (!cart1.getCommandSenderWorld().equals(cart2.getCommandSenderWorld())) {
                throw SAME_DIMENSION.create();
            }
            Entity source = ((CommandSourceStack)ctx.getSource()).getEntity();
            CouplingHandler.tryToCoupleCarts(source instanceof Player ? (Player)source : null, cart1.getCommandSenderWorld(), cart1.getId(), cart2.getId());
            return 1;
        })))).then(Commands.argument((String)"carts", (ArgumentType)EntityArgument.entities()).executes(ctx -> {
            Collection entities = EntityArgument.getEntities((CommandContext)ctx, (String)"carts");
            if (entities.size() != 2) {
                throw TWO_CARTS.create((Object)entities.size());
            }
            ArrayList eList = Lists.newArrayList((Iterable)entities);
            Entity cart1 = (Entity)eList.get(0);
            if (!(cart1 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            Entity cart2 = (Entity)eList.get(1);
            if (!(cart2 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            if (!cart1.getCommandSenderWorld().equals(cart2.getCommandSenderWorld())) {
                throw SAME_DIMENSION.create();
            }
            Entity source = ((CommandSourceStack)ctx.getSource()).getEntity();
            CouplingHandler.tryToCoupleCarts(source instanceof Player ? (Player)source : null, cart1.getCommandSenderWorld(), cart1.getId(), cart2.getId());
            return 1;
        })))).then(Commands.literal((String)"remove").then(Commands.argument((String)"cart1", (ArgumentType)EntityArgument.entity()).then(Commands.argument((String)"cart2", (ArgumentType)EntityArgument.entity()).executes(ctx -> {
            Entity cart1 = EntityArgument.getEntity((CommandContext)ctx, (String)"cart1");
            if (!(cart1 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            Entity cart2 = EntityArgument.getEntity((CommandContext)ctx, (String)"cart2");
            if (!(cart2 instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            MinecartController cart1Capability = (MinecartController)cart1.getData(AllAttachmentTypes.MINECART_CONTROLLER);
            if (cart1Capability == MinecartController.EMPTY) {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Minecart has no Couplings Attached"), true);
                return 0;
            }
            int cart1Couplings = (cart1Capability.isConnectedToCoupling() ? 1 : 0) + (cart1Capability.isLeadingCoupling() ? 1 : 0);
            if (cart1Couplings == 0) {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Minecart has no Couplings Attached"), true);
                return 0;
            }
            for (boolean bool : Iterate.trueAndFalse) {
                UUID coupledCart = cart1Capability.getCoupledCart(bool);
                if (coupledCart == null || coupledCart != cart2.getUUID()) continue;
                MinecartController cart2Controller = CapabilityMinecartController.getIfPresent(cart1.getCommandSenderWorld(), coupledCart);
                if (cart2Controller == null) {
                    return 0;
                }
                cart1Capability.removeConnection(bool);
                cart2Controller.removeConnection(!bool);
                return 1;
            }
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"The specified Carts are not coupled"), true);
            return 0;
        }))))).then(Commands.literal((String)"removeAll").then(Commands.argument((String)"cart", (ArgumentType)EntityArgument.entity()).executes(ctx -> {
            Entity cart = EntityArgument.getEntity((CommandContext)ctx, (String)"cart");
            if (!(cart instanceof AbstractMinecart)) {
                throw ONLY_MINECARTS_ALLOWED.create();
            }
            MinecartController capability = (MinecartController)cart.getData(AllAttachmentTypes.MINECART_CONTROLLER);
            if (capability == MinecartController.EMPTY) {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Minecart has no Couplings Attached"), true);
                return 0;
            }
            int couplings = (capability.isConnectedToCoupling() ? 1 : 0) + (capability.isLeadingCoupling() ? 1 : 0);
            if (couplings == 0) {
                ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)"Minecart has no Couplings Attached"), true);
                return 0;
            }
            capability.decouple();
            ((CommandSourceStack)ctx.getSource()).sendSuccess(() -> Component.literal((String)("Removed " + couplings + " couplings from the Minecart")), true);
            return couplings;
        })));
    }
}
