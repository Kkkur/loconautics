/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.caffeinemc.mods.sodium.api.texture.SpriteUtil
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.InventoryMenu
 *  net.neoforged.bus.api.IEventBus
 *  net.neoforged.neoforge.client.event.RenderLevelStageEvent$Stage
 */
package com.simibubi.create.compat.sodium;

import com.simibubi.create.Create;
import java.util.function.Function;
import net.caffeinemc.mods.sodium.api.texture.SpriteUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class SodiumCompat {
    public static final ResourceLocation SAW_TEXTURE = Create.asResource("block/saw_reversed");
    public static final ResourceLocation FACTORY_PANEL_TEXTURE = Create.asResource("block/factory_panel_connections_animated");

    public static void init(IEventBus modEventBus, IEventBus neoEventBus) {
        Minecraft mc = Minecraft.getInstance();
        neoEventBus.addListener(event -> {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
                Function atlas = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
                TextureAtlasSprite sawSprite = (TextureAtlasSprite)atlas.apply(SAW_TEXTURE);
                SpriteUtil.INSTANCE.markSpriteActive(sawSprite);
                TextureAtlasSprite factoryPanelSprite = (TextureAtlasSprite)atlas.apply(FACTORY_PANEL_TEXTURE);
                SpriteUtil.INSTANCE.markSpriteActive(factoryPanelSprite);
            }
        });
    }
}
