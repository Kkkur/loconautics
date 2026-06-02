/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
 *  com.simibubi.create.foundation.item.render.SimpleCustomRenderer
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.simulated_team.simulated.neoforge.mixin.self_mixins;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItem;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItemRenderer;
import java.util.function.Consumer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={PlungerLauncherItem.class})
public abstract class PlungerLauncherItemMixin
extends Item {
    public PlungerLauncherItemMixin(Item.Properties properties) {
        super(properties);
    }

    public boolean onEntitySwing(ItemStack stack, LivingEntity entity, InteractionHand hand) {
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions)SimpleCustomRenderer.create((Item)this, (CustomRenderedItemModelRenderer)new PlungerLauncherItemRenderer()));
    }
}
