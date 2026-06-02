/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.simibubi.create.content.redstone.link.controller.LinkedControllerItem
 *  com.tterrag.registrate.util.entry.BlockEntry
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 */
package dev.simulated_team.simulated.mixin.linked_controller_binding;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value={LinkedControllerItem.class})
public abstract class LinkedControllerItemMixin {
    @WrapOperation(method={"onItemUseFirst"}, at={@At(value="INVOKE", target="Lcom/tterrag/registrate/util/entry/BlockEntry;has(Lnet/minecraft/world/level/block/state/BlockState;)Z", ordinal=1)})
    private <T extends Block> boolean simulated$onItemUseFirst(BlockEntry<T> instance, BlockState state, Operation<Boolean> original) {
        return (Boolean)original.call(new Object[]{instance, state}) != false || SimBlocks.MODULATING_LINKED_RECEIVER.has(state) || SimBlocks.DIRECTIONAL_LINKED_RECEIVER.has(state);
    }
}
