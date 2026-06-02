/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer
 *  com.simibubi.create.foundation.item.render.SimpleCustomRenderer
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 *  org.spongepowered.asm.mixin.Mixin
 */
package dev.simulated_team.simulated.neoforge.mixin.item_renderer;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItem;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value={PhysicsStaffItem.class})
public abstract class PhysicsStaffItemMixin
extends Item {
    public PhysicsStaffItemMixin(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept((IClientItemExtensions)SimpleCustomRenderer.create((Item)this, (CustomRenderedItemModelRenderer)new PhysicsStaffItemRenderer()));
    }
}
