/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.platform.CatnipServices
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
 *  top.theillusivec4.curios.api.CuriosCapability
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 *  top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler
 */
package com.simibubi.create.compat.curios;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.compat.curios.CuriosRenderers;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public class Curios {
    private static Optional<Map<String, ICurioStacksHandler>> resolveCuriosMap(LivingEntity entity) {
        return Optional.ofNullable((ICuriosItemHandler)entity.getCapability(CuriosCapability.INVENTORY)).map(ICuriosItemHandler::getCurios);
    }

    public static void init(IEventBus modEventBus) {
        modEventBus.addListener(Curios::onClientSetup);
        GogglesItem.addIsWearingPredicate(player -> Curios.resolveCuriosMap((LivingEntity)player).map(curiosMap -> {
            for (ICurioStacksHandler stacksHandler : curiosMap.values()) {
                int slots = stacksHandler.getSlots();
                for (int slot = 0; slot < slots; ++slot) {
                    if (!AllItems.GOGGLES.isIn(stacksHandler.getStacks().getStackInSlot(slot))) continue;
                    return true;
                }
            }
            return false;
        }).orElse(false));
        BacktankUtil.addBacktankSupplier(entity -> Curios.resolveCuriosMap(entity).map(curiosMap -> {
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            for (ICurioStacksHandler stacksHandler : curiosMap.values()) {
                int slots = stacksHandler.getSlots();
                for (int slot = 0; slot < slots; ++slot) {
                    ItemStack itemStack = stacksHandler.getStacks().getStackInSlot(slot);
                    if (!AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.matches(itemStack)) continue;
                    stacks.add(itemStack);
                }
            }
            return stacks;
        }).orElse(Collections.emptyList()));
        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> modEventBus.addListener(CuriosRenderers::onLayerRegister));
    }

    private static void onClientSetup(FMLClientSetupEvent event) {
        CuriosRenderers.register();
    }
}
