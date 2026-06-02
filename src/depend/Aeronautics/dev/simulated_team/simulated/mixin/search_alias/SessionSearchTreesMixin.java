/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  net.minecraft.client.multiplayer.SessionSearchTrees
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.search_alias;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.simulated_team.simulated.client.SearchAlias;
import java.util.List;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={SessionSearchTrees.class})
public class SessionSearchTreesMixin {
    @WrapOperation(method={"*"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;")})
    private static List<Component> simulated$getTooltipLines(ItemStack instance, Item.TooltipContext i, Player list, TooltipFlag mutablecomponent, Operation<List<Component>> original) {
        List tooltipLines = (List)original.call(new Object[]{instance, i, list, mutablecomponent});
        tooltipLines.addAll(SearchAlias.getAliases(instance).stream().map(Component::literal).toList());
        return tooltipLines;
    }
}
