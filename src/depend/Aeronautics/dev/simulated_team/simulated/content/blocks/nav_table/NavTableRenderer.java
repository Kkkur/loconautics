/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.math.Axis
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  dev.engine_room.flywheel.lib.transform.PoseTransformStack
 *  dev.engine_room.flywheel.lib.transform.TransformStack
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.ItemRenderer
 *  net.minecraft.core.Direction
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 */
package dev.simulated_team.simulated.content.blocks.nav_table;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlock;
import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.RenderableNavigationTarget;
import dev.simulated_team.simulated.index.SimPartialModels;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.util.SimColors;
import dev.simulated_team.simulated.util.SimDirectionUtil;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class NavTableRenderer
extends SmartBlockEntityRenderer<NavTableBlockEntity> {
    public NavTableRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(NavTableBlockEntity navBE, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe((SmartBlockEntity)navBE, partialTicks, ms, buffer, light, overlay);
        ItemStack heldItem = navBE.getHeldItem();
        BlockState navState = navBE.getBlockState();
        Direction facing = (Direction)navState.getValue((Property)NavTableBlock.FACING);
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).pushPose().center()).rotate((Quaternionfc)facing.getRotation());
        float arrowAngle = (float)((double)navBE.getClientTargetAngle(partialTicks) - 1.5707963267948966);
        if (!VisualizationManager.supportsVisualization((LevelAccessor)navBE.getLevel())) {
            ms.pushPose();
            ms.translate(0.0, -0.5, 0.0);
            Vector3f logicalDirectionF = new Vector3f();
            for (Direction direction : SimDirectionUtil.Y_AXIS_PLANE) {
                facing.getRotation().transform((float)direction.getStepX(), (float)direction.getStepY(), (float)direction.getStepZ(), logicalDirectionF);
                Direction logicalDirection = Direction.getNearest((float)logicalDirectionF.x, (float)logicalDirectionF.y, (float)logicalDirectionF.z);
                ms.pushPose();
                SuperByteBuffer indicator = CachedBuffers.partial((PartialModel)SimPartialModels.NAV_TABLE_INDICATOR, (BlockState)navState);
                indicator.rotateToFace(direction);
                indicator.translate(0.0, 0.0, 0.5);
                float signalStrength = navBE.isPowering ? (float)Math.max(navBE.getRedstoneStrength(logicalDirection), 0) / 15.0f : 0.0f;
                int color = SimColors.redstone(signalStrength);
                indicator.light(light).color(color).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
                ms.popPose();
            }
            ms.popPose();
            ms.pushPose();
            ms.translate(0.0, 0.3, 0.0);
            SuperByteBuffer pointer = CachedBuffers.partial((PartialModel)SimPartialModels.NAV_TABLE_POINTER, (BlockState)navState);
            pointer.rotateY(arrowAngle);
            pointer.light(light).renderInto(ms, buffer.getBuffer(RenderType.cutout()));
            ms.popPose();
        }
        ms.pushPose();
        ms.translate(0.0, 0.3, 0.0);
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        boolean blockItem = itemRenderer.getModel(heldItem, null, null, 0).isGui3d();
        ((PoseTransformStack)TransformStack.of((PoseStack)ms).translate(0.0f, blockItem ? 0.25f : 0.15f, 0.0f).rotate((float)Math.toRadians(90.0), Direction.WEST)).scale(blockItem ? 0.5f : 0.375f);
        Item item = heldItem.getItem();
        if (item instanceof RenderableNavigationTarget) {
            RenderableNavigationTarget rnti = (RenderableNavigationTarget)item;
            rnti.renderInNavTable(heldItem, navBE, navState, partialTicks, ms, buffer, light, overlay);
        } else {
            if (heldItem.is(SimTags.Items.ROTATE_WITH_NAV_ARROW)) {
                ms.mulPose(Axis.ZP.rotation(arrowAngle));
            }
            itemRenderer.renderStatic(heldItem, ItemDisplayContext.FIXED, light, overlay, ms, buffer, navBE.getLevel(), 0);
        }
        ms.popPose();
        ms.popPose();
    }
}
