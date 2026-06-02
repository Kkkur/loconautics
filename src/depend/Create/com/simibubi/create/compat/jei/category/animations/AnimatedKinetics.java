/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  mezz.jei.api.gui.drawable.IDrawable
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.gui.ILightingSettings
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.gui.element.GuiGameElement$GuiRenderBuilder
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.simibubi.create.compat.jei.category.animations;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.foundation.gui.CustomLightingSettings;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import mezz.jei.api.gui.drawable.IDrawable;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.gui.ILightingSettings;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class AnimatedKinetics
implements IDrawable {
    public int offset = 0;
    public static final ILightingSettings DEFAULT_LIGHTING = CustomLightingSettings.builder().firstLightRotation(12.5f, -45.0f).secondLightRotation(-20.0f, -50.0f).build();

    public static GuiGameElement.GuiRenderBuilder defaultBlockElement(BlockState state) {
        return GuiGameElement.of((BlockState)state).lighting(DEFAULT_LIGHTING);
    }

    public static GuiGameElement.GuiRenderBuilder defaultBlockElement(PartialModel partial) {
        return GuiGameElement.of((PartialModel)partial).lighting(DEFAULT_LIGHTING);
    }

    public static float getCurrentAngle() {
        return AnimationTickHolder.getRenderTime() * 4.0f % 360.0f;
    }

    protected BlockState shaft(Direction.Axis axis) {
        return (BlockState)AllBlocks.SHAFT.getDefaultState().setValue((Property)BlockStateProperties.AXIS, (Comparable)axis);
    }

    protected PartialModel cogwheel() {
        return AllPartialModels.SHAFTLESS_COGWHEEL;
    }

    protected GuiGameElement.GuiRenderBuilder blockElement(BlockState state) {
        return AnimatedKinetics.defaultBlockElement(state);
    }

    protected GuiGameElement.GuiRenderBuilder blockElement(PartialModel partial) {
        return AnimatedKinetics.defaultBlockElement(partial);
    }

    public int getWidth() {
        return 50;
    }

    public int getHeight() {
        return 50;
    }
}
