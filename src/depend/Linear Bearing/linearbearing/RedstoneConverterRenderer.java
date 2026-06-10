/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  com.simibubi.create.AllPartialModels
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 */
package com.bearing.linearbearing;

import com.bearing.linearbearing.RedstoneConverterBlock;
import com.bearing.linearbearing.RedstoneConverterBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllPartialModels;
import java.util.Optional;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class RedstoneConverterRenderer
implements BlockEntityRenderer<RedstoneConverterBlockEntity> {
    public RedstoneConverterRenderer(BlockEntityRendererProvider.Context context) {
    }

    public void render(RedstoneConverterBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getLevel() == null || !state.hasProperty((Property)RedstoneConverterBlock.FACING)) {
            return;
        }
        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);
        Direction facing = (Direction)state.getValue((Property)RedstoneConverterBlock.FACING);
        Direction backDirection = facing.getOpposite();
        Direction.Axis axis = facing.getAxis();
        BlockPos pos = be.getBlockPos();
        ms.mulPose(backDirection.getRotation());
        ms.mulPose(Axis.XP.rotationDegrees(-90.0f));
        if (be.getLevel() != null) {
            boolean isAxisInverted;
            float speed = be.getSpeed();
            float renderTime = AnimationTickHolder.getRenderTime((LevelAccessor)be.getLevel());
            float baseAngle = speed != 0.0f ? renderTime * speed * 3.0f / 10.0f % 360.0f : 0.0f;
            float fineTuning = 22.5f;
            boolean bl = isAxisInverted = backDirection.getAxisDirection() == Direction.AxisDirection.NEGATIVE;
            if (axis == Direction.Axis.Y) {
                isAxisInverted = !isAxisInverted;
            }
            float angleDirection = isAxisInverted ? -1.0f : 1.0f;
            float finalAngle = (baseAngle + fineTuning) * angleDirection % 360.0f;
            float even_NORTH = 0.0f;
            float odd_NORTH = -22.5f;
            float even_SOUTH = 0.0f;
            float odd_SOUTH = -22.5f;
            float even_EAST = 0.0f;
            float odd_EAST = -22.5f;
            float even_WEST = 0.0f;
            float odd_WEST = -22.5f;
            float even_UP = -45.0f;
            float odd_UP = -22.5f;
            float even_DOWN = -45.0f;
            float odd_DOWN = -22.5f;
            int coordinateSum = 0;
            switch (axis) {
                case X: {
                    coordinateSum = pos.getY() + pos.getZ();
                    break;
                }
                case Y: {
                    coordinateSum = pos.getX() + pos.getZ();
                    break;
                }
                case Z: {
                    coordinateSum = pos.getX() + pos.getY();
                }
            }
            boolean isEvenCell = Math.abs(coordinateSum) % 2 == 0;
            float gridOffset = 0.0f;
            switch (facing) {
                case NORTH: {
                    gridOffset = isEvenCell ? even_NORTH : odd_NORTH;
                    break;
                }
                case SOUTH: {
                    gridOffset = isEvenCell ? even_SOUTH : odd_SOUTH;
                    break;
                }
                case EAST: {
                    gridOffset = isEvenCell ? even_EAST : odd_EAST;
                    break;
                }
                case WEST: {
                    gridOffset = isEvenCell ? even_WEST : odd_WEST;
                    break;
                }
                case UP: {
                    gridOffset = isEvenCell ? even_UP : odd_UP;
                    break;
                }
                case DOWN: {
                    gridOffset = isEvenCell ? even_DOWN : odd_DOWN;
                }
            }
            finalAngle = (finalAngle + gridOffset * angleDirection) % 360.0f;
            ms.mulPose(Axis.ZP.rotationDegrees(finalAngle));
        }
        ms.translate(-0.5, -0.5, -0.5);
        ModelResourceLocation mrl = new ModelResourceLocation(AllPartialModels.SHAFT_HALF.modelLocation(), "standalone");
        BakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(mrl);
        int packedLight = LevelRenderer.getLightColor((BlockAndTintGetter)be.getLevel(), (BlockPos)be.getBlockPos().relative(backDirection));
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull((ResourceLocation)TextureAtlas.LOCATION_BLOCKS));
        BlockState shaftState = ((Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath((String)"create", (String)"shaft"))).defaultBlockState();
        for (Property prop : shaftState.getProperties()) {
            if (!prop.getName().equals("type")) continue;
            shaftState = RedstoneConverterRenderer.setValueHelper(shaftState, prop, "half");
            break;
        }
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(ms.last(), vertexConsumer, shaftState, bakedModel, 1.0f, 1.0f, 1.0f, packedLight, overlay);
        ms.popPose();
    }

    private static <T extends Comparable<T>> BlockState setValueHelper(BlockState state, Property<?> property, String value) {
        Optional optional = property.getValue(value);
        if (optional.isPresent()) {
            return (BlockState)state.setValue(property, (Comparable)optional.get());
        }
        return state;
    }
}
