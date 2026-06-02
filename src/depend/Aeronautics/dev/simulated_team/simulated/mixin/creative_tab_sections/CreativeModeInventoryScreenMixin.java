/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.llamalad7.mixinextras.sugar.Local
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.simulated_team.simulated.mixin.creative_tab_sections;

import com.llamalad7.mixinextras.sugar.Local;
import dev.simulated_team.simulated.client.sections.SimulatedSection;
import dev.simulated_team.simulated.index.SimResourceManagers;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.registrate.simulated_tab.SimulatedCreativeTab;
import dev.simulated_team.simulated.service.SimTabService;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={CreativeModeInventoryScreen.class})
public class CreativeModeInventoryScreenMixin {
    @Shadow
    private static CreativeModeTab selectedTab;

    @Inject(method={"render"}, at={@At(value="TAIL")})
    private void simulated$render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (selectedTab == SimTabService.INSTANCE.getCreativeTab()) {
            SimulatedCreativeTab.renderBanners((CreativeModeInventoryScreen)this, guiGraphics, mouseX, mouseY);
        }
    }

    @Inject(method={"getTooltipFromContainerItem"}, at={@At(value="INVOKE", target="Lnet/minecraft/world/item/CreativeModeTabs;tabs()Ljava/util/List;")})
    private void simulated$getTooltipFromContainerItem(ItemStack stack, CallbackInfoReturnable<List<Component>> cir, @Local(ordinal=1) List<Component> list1, @Local int i) {
        SimulatedSection section;
        ResourceLocation key = BuiltInRegistries.ITEM.getKey((Object)stack.getItem());
        ResourceLocation id = SimulatedRegistrate.ITEM_TO_SECTION.get(key);
        if (id != null && (section = SimResourceManagers.SIMULATED_SECTION.get(id)) != null) {
            list1.add(i, (Component)section.title().text().copy().withStyle(ChatFormatting.BLUE));
        }
    }
}
