/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.outliner.Outliner
 *  net.createmod.catnip.render.BindableTexture
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;
import net.createmod.catnip.outliner.Outliner;
import net.createmod.catnip.render.BindableTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class WorldshaperRenderHandler {
    private static Supplier<Collection<BlockPos>> renderedPositions;

    public static void tick() {
        WorldshaperRenderHandler.gatherSelectedBlocks();
        if (renderedPositions == null) {
            return;
        }
        Outliner.getInstance().showCluster((Object)"terrainZapper", (Iterable)renderedPositions.get()).colored(0xBFBFBF).disableLineNormals().lineWidth(0.03125f).withFaceTexture((BindableTexture)AllSpecialTextures.CHECKERED);
    }

    protected static void gatherSelectedBlocks() {
        LocalPlayer player = Minecraft.getInstance().player;
        ItemStack heldMain = player.getMainHandItem();
        ItemStack heldOff = player.getOffhandItem();
        boolean zapperInMain = AllItems.WORLDSHAPER.isIn(heldMain);
        boolean zapperInOff = AllItems.WORLDSHAPER.isIn(heldOff);
        if (!(!zapperInMain || heldMain.has(AllDataComponents.SHAPER_SWAP) && zapperInOff)) {
            WorldshaperRenderHandler.createBrushOutline(player, heldMain);
            return;
        }
        if (zapperInOff) {
            WorldshaperRenderHandler.createBrushOutline(player, heldOff);
            return;
        }
        renderedPositions = null;
    }

    public static void createBrushOutline(LocalPlayer player, ItemStack zapper) {
        if (!zapper.has(AllDataComponents.SHAPER_BRUSH_PARAMS)) {
            renderedPositions = null;
            return;
        }
        Brush brush = ((TerrainBrushes)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_BRUSH, (Object)TerrainBrushes.Cuboid))).get();
        PlacementOptions placement = (PlacementOptions)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, (Object)PlacementOptions.Merged));
        TerrainTools tool = (TerrainTools)((Object)zapper.getOrDefault(AllDataComponents.SHAPER_TOOL, (Object)TerrainTools.Fill));
        BlockPos params = (BlockPos)zapper.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
        brush.set(params.getX(), params.getY(), params.getZ());
        Vec3 start = player.position().add(0.0, (double)player.getEyeHeight(), 0.0);
        Vec3 range = player.getLookAngle().scale(128.0);
        BlockHitResult raytrace = player.level().clip(new ClipContext(start, start.add(range), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
        if (raytrace == null || raytrace.getType() == HitResult.Type.MISS) {
            renderedPositions = null;
            return;
        }
        BlockPos pos = raytrace.getBlockPos().offset((Vec3i)brush.getOffset(player.getLookAngle(), raytrace.getDirection(), placement));
        renderedPositions = () -> brush.addToGlobalPositions((LevelAccessor)player.level(), pos, raytrace.getDirection(), new ArrayList<BlockPos>(), tool);
    }
}
