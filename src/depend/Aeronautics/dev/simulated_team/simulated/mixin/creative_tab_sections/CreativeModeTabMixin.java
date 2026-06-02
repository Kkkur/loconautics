/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
 *  com.llamalad7.mixinextras.injector.wrapoperation.Operation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 */
package dev.simulated_team.simulated.mixin.creative_tab_sections;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab;
import dev.simulated_team.simulated.service.SimTabService;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value={CreativeModeTab.class})
public class CreativeModeTabMixin {
    @Shadow
    private Collection<ItemStack> displayItems;
    @Shadow
    private Set<ItemStack> displayItemsSearchTab;

    @WrapMethod(method={"buildContents"})
    private void simulated$buildContents(CreativeModeTab.ItemDisplayParameters parameters, Operation<Void> original) {
        CreativeModeTab self = (CreativeModeTab)this;
        if (self == SimTabService.INSTANCE.getCreativeTab()) {
            LinkedList<ItemStack> displayItems = new LinkedList<ItemStack>();
            LinkedHashSet<ItemStack> searchItems = new LinkedHashSet<ItemStack>();
            SimulatedCreativeTab.processItems(displayItems::add, searchItems::add);
            this.displayItems = displayItems;
            this.displayItemsSearchTab = searchItems;
            return;
        }
        original.call(new Object[]{parameters});
    }
}
