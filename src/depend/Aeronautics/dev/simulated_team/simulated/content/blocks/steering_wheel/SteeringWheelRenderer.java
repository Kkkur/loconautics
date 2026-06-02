/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.simibubi.create.AllPartialModels
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer
 *  com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer
 *  com.simibubi.create.foundation.model.BakedModelHelper
 *  dev.engine_room.flywheel.api.visualization.VisualizationManager
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperBufferFactory
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.catnip.render.SuperByteBufferCache$Compartment
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.LevelAccessor
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionfc
 */
package dev.simulated_team.simulated.content.blocks.steering_wheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelRenderer;
import com.simibubi.create.foundation.model.BakedModelHelper;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.index.SimPartialModels;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.createmod.catnip.theme.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionfc;

public class SteeringWheelRenderer
extends KineticBlockEntityRenderer<SteeringWheelBlockEntity> {
    public static final SuperByteBufferCache.Compartment<ModelKey> STEERING_WHEEL = new SuperByteBufferCache.Compartment();

    public SteeringWheelRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    protected void renderSafe(SteeringWheelBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        if (VisualizationManager.supportsVisualization((LevelAccessor)be.getLevel())) {
            return;
        }
        boolean floor = (Boolean)be.getBlockState().getValue((Property)SteeringWheelBlock.ON_FLOOR);
        Direction facing = (Direction)be.getBlockState().getValue((Property)SteeringWheelBlock.FACING);
        if (be.shouldRenderShaft()) {
            BlockState state = this.getRenderedBlockState((KineticBlockEntity)be);
            RenderType type = this.getRenderType((KineticBlockEntity)be, state);
            SteeringWheelRenderer.renderRotatingBuffer((KineticBlockEntity)be, (SuperByteBuffer)CachedBuffers.partialFacing((PartialModel)AllPartialModels.SHAFT_HALF, (BlockState)be.getBlockState(), (Direction)(floor ? Direction.DOWN : Direction.UP)), (PoseStack)ms, (VertexConsumer)buffer.getBuffer(type), (int)light);
        }
        SuperByteBuffer model = this.getWheelModel(be);
        model.rotateCentered((Quaternionfc)facing.getRotation());
        if (floor) {
            model.translate(0.0, 0.40625, -0.3125);
        } else {
            model.translate(0.0, 0.40625, 0.3125);
        }
        model.rotateCentered(be.getRenderAngle(partialTicks), Direction.UP);
        model.light(light);
        model.color(Color.WHITE);
        model.renderInto(ms, buffer.getBuffer(RenderType.solid()));
    }

    private SuperByteBuffer getWheelModel(SteeringWheelBlockEntity be) {
        ModelKey key = new ModelKey(be.material);
        return SuperByteBufferCache.getInstance().get(STEERING_WHEEL, (Object)key, () -> {
            BakedModel model = SteeringWheelRenderer.generateModel(SimPartialModels.STEERING_WHEEL.get(), be.material);
            return SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState(), new PoseStack());
        });
    }

    public static BakedModel generateModel(BakedModel template, BlockState planksBlockState) {
        Block planksBlock = planksBlockState.getBlock();
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)planksBlock);
        String wood = SteeringWheelRenderer.plankStateToWoodName(planksBlockState);
        if (wood == null) {
            return BakedModelHelper.generateModel((BakedModel)template, sprite -> null);
        }
        Reference2ReferenceOpenHashMap map = new Reference2ReferenceOpenHashMap();
        map.put(WaterWheelRenderer.OAK_PLANKS_TEMPLATE.get(), SteeringWheelRenderer.getSpriteOnSide(planksBlockState, Direction.UP));
        return BakedModelHelper.generateModel((BakedModel)template, ((Map)map)::get);
    }

    @Nullable
    private static String plankStateToWoodName(BlockState planksBlockState) {
        Block planksBlock = planksBlockState.getBlock();
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)planksBlock);
        String path = id.getPath();
        if (path.endsWith("_planks")) {
            return (path.startsWith("archwood") ? "blue_" : "") + path.substring(0, path.length() - 7);
        }
        if (path.contains("wood/planks/")) {
            return path.substring(12);
        }
        return null;
    }

    private static TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model == null) {
            return null;
        }
        RandomSource random = RandomSource.create();
        random.setSeed(42L);
        List quads = model.getQuads(state, side, random);
        if (!quads.isEmpty()) {
            return ((BakedQuad)quads.get(0)).getSprite();
        }
        random.setSeed(42L);
        quads = model.getQuads(state, null, random);
        if (!quads.isEmpty()) {
            for (BakedQuad quad : quads) {
                if (quad.getDirection() != side) continue;
                return quad.getSprite();
            }
        }
        return model.getParticleIcon();
    }

    public record ModelKey(BlockState material) {
    }
}
