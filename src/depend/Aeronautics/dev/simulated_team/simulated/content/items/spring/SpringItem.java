/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.context.UseOnContext
 */
package dev.simulated_team.simulated.content.items.spring;

import dev.simulated_team.simulated.index.SimClickInteractions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class SpringItem
extends Item {
    public SpringItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer().isLocalPlayer() && SimClickInteractions.SPRING_INTERACTION.tryStartPlacement(context)) {
            return InteractionResult.SUCCESS;
        }
        return super.useOn(context);
    }
}
