/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.MenuType
 */
package dev.simulated_team.simulated.service;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import dev.simulated_team.simulated.service.ServiceUtil;
import java.util.function.Consumer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

public interface SimMenuService {
    public static final SimMenuService INSTANCE = ServiceUtil.load(SimMenuService.class);

    public <T extends LinkedTypewriterMenuCommon> T getLoaderLinkedTypewriter(MenuType<?> var1, int var2, Inventory var3, RegistryFriendlyByteBuf var4);

    public <T extends LinkedTypewriterMenuCommon> T getLoaderLinkedTypewriter(MenuType<?> var1, int var2, Inventory var3, LinkedTypewriterBlockEntity var4);

    public void openScreen(ServerPlayer var1, MenuProvider var2, Consumer<RegistryFriendlyByteBuf> var3);
}
