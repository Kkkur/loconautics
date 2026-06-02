/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.gui.ScreenOpener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.neoforged.api.distmarker.Dist
 *  net.neoforged.api.distmarker.OnlyIn
 *  net.neoforged.neoforge.client.extensions.common.IClientItemExtensions
 */
package com.simibubi.create.content.equipment.zapper.terrainzapper;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.content.equipment.zapper.PlacementPatterns;
import com.simibubi.create.content.equipment.zapper.ZapperItem;
import com.simibubi.create.content.equipment.zapper.terrainzapper.Brush;
import com.simibubi.create.content.equipment.zapper.terrainzapper.PlacementOptions;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainBrushes;
import com.simibubi.create.content.equipment.zapper.terrainzapper.TerrainTools;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperItemRenderer;
import com.simibubi.create.content.equipment.zapper.terrainzapper.WorldshaperScreen;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;
import com.simibubi.create.foundation.utility.CreateLang;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.gui.ScreenOpener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class WorldshaperItem
extends ZapperItem {
    public WorldshaperItem(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(SimpleCustomRenderer.create(this, new WorldshaperItemRenderer()));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    protected void openHandgunGUI(ItemStack item, InteractionHand hand) {
        ScreenOpener.open((Screen)new WorldshaperScreen(item, hand));
    }

    @Override
    protected int getZappingRange(ItemStack stack) {
        return 128;
    }

    @Override
    protected int getCooldownDelay(ItemStack item) {
        return 2;
    }

    @Override
    public Component validateUsage(ItemStack item) {
        if (!item.has(AllDataComponents.SHAPER_BRUSH_PARAMS)) {
            return CreateLang.translateDirect("terrainzapper.shiftRightClickToSet", new Object[0]);
        }
        return super.validateUsage(item);
    }

    @Override
    protected boolean canActivateWithoutSelectedBlock(ItemStack stack) {
        TerrainTools tool = (TerrainTools)((Object)stack.getOrDefault(AllDataComponents.SHAPER_TOOL, (Object)TerrainTools.Fill));
        return !tool.requiresSelectedBlock();
    }

    @Override
    protected boolean activate(Level level, Player player, ItemStack stack, BlockState stateToUse, BlockHitResult raytrace, CompoundTag data) {
        BlockPos targetPos = raytrace.getBlockPos();
        ArrayList<BlockPos> affectedPositions = new ArrayList<BlockPos>();
        Brush brush = ((TerrainBrushes)((Object)stack.getOrDefault(AllDataComponents.SHAPER_BRUSH, (Object)TerrainBrushes.Cuboid))).get();
        BlockPos params = (BlockPos)stack.get(AllDataComponents.SHAPER_BRUSH_PARAMS);
        PlacementOptions option = (PlacementOptions)((Object)stack.getOrDefault(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, (Object)PlacementOptions.Merged));
        TerrainTools tool = (TerrainTools)((Object)stack.getOrDefault(AllDataComponents.SHAPER_TOOL, (Object)TerrainTools.Fill));
        brush.set(params.getX(), params.getY(), params.getZ());
        targetPos = targetPos.offset((Vec3i)brush.getOffset(player.getLookAngle(), raytrace.getDirection(), option));
        brush.addToGlobalPositions((LevelAccessor)level, targetPos, raytrace.getDirection(), affectedPositions, tool);
        PlacementPatterns.applyPattern(affectedPositions, stack, level.random);
        brush.redirectTool(tool).run(level, affectedPositions, raytrace.getDirection(), stateToUse, data, player);
        return true;
    }

    public static void configureSettings(ItemStack stack, PlacementPatterns pattern, TerrainBrushes brush, int brushParamX, int brushParamY, int brushParamZ, TerrainTools tool, PlacementOptions placement) {
        stack.set(AllDataComponents.PLACEMENT_PATTERN, (Object)pattern);
        stack.set(AllDataComponents.SHAPER_BRUSH, (Object)brush);
        stack.set(AllDataComponents.SHAPER_BRUSH_PARAMS, (Object)new BlockPos(brushParamX, brushParamY, brushParamZ));
        stack.set(AllDataComponents.SHAPER_TOOL, (Object)tool);
        stack.set(AllDataComponents.SHAPER_PLACEMENT_OPTIONS, (Object)placement);
    }
}
