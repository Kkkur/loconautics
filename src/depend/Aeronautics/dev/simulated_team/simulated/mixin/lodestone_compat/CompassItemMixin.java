/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.component.DataComponentType
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.CompassItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.context.UseOnContext
 *  net.minecraft.world.level.Level
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.simulated_team.simulated.mixin.lodestone_compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.simulated_team.simulated.content.navigation_targets.lodestone_compass_compatability.LodestoneTrackingMap;
import dev.simulated_team.simulated.index.SimDataComponents;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={CompassItem.class})
public abstract class CompassItemMixin
extends Item {
    public CompassItemMixin(Item.Properties properties) {
        super(properties);
    }

    @Inject(method={"inventoryTick"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;")})
    private void simulated$checkID(ItemStack stack, Level level, Entity entity, int itemSlot, boolean isSelected, CallbackInfo ci) {
        if (!level.isClientSide && stack.has(SimDataComponents.LODESTONE_COMPASS_SUBLEVEL_TRACKER)) {
            UUID trackerID = (UUID)stack.get(SimDataComponents.LODESTONE_COMPASS_SUBLEVEL_TRACKER);
            LodestoneTrackingMap map = LodestoneTrackingMap.getOrLoad(level);
            if (map != null && entity instanceof ServerPlayer) {
                ServerPlayer sp = (ServerPlayer)entity;
                map.sendUpdateForPlayer(trackerID, sp);
            }
        }
    }

    @WrapOperation(method={"useOn"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/ItemStack;set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;")})
    public <T> T simulated$setLodestoneData(ItemStack instance, DataComponentType<? super T> component, T value, Operation<T> original, @Local(argsOnly=true) UseOnContext context) {
        UUID uuid;
        BlockPos pos = context.getClickedPos();
        LodestoneTrackingMap map = LodestoneTrackingMap.getOrLoad(context.getLevel());
        if (map != null && (uuid = map.addOrGetLodestoneTrackingPoint(pos)) != null) {
            instance.set(SimDataComponents.LODESTONE_COMPASS_SUBLEVEL_TRACKER, (Object)uuid);
        }
        return (T)original.call(new Object[]{instance, component, value});
    }
}
