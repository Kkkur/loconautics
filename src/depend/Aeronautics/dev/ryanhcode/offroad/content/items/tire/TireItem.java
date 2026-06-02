/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.simulated_team.simulated.util.SimColors
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.Style
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 *  org.jetbrains.annotations.NotNull
 */
package dev.ryanhcode.offroad.content.items.tire;

import dev.simulated_team.simulated.util.SimColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class TireItem
extends Item {
    public TireItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(@NotNull UseOnContext context) {
        Player player = context.getPlayer();
        if (player != null && player.level().isClientSide) {
            player.displayClientMessage((Component)Component.translatable((String)"item.offroad.tire.placement_error").setStyle(Style.EMPTY.withColor(SimColors.NUH_UH_RED)), true);
        }
        return super.useOn(context);
    }
}
