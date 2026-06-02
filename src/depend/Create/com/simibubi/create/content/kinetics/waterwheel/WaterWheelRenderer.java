/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap
 *  net.createmod.catnip.registry.RegisteredObjectsHelper
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.StitchedSprite
 *  net.createmod.catnip.render.SuperBufferFactory
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.render.SuperByteBufferCache
 *  net.createmod.catnip.render.SuperByteBufferCache$Compartment
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider$Context
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.core.Direction$AxisDirection
 *  net.minecraft.core.Holder
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.neoforged.neoforge.client.model.data.ModelData
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.kinetics.waterwheel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import com.simibubi.create.content.kinetics.waterwheel.LargeWaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlock;
import com.simibubi.create.content.kinetics.waterwheel.WaterWheelBlockEntity;
import com.simibubi.create.foundation.model.BakedModelHelper;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.StitchedSprite;
import net.createmod.catnip.render.SuperBufferFactory;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.render.SuperByteBufferCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class WaterWheelRenderer<T extends WaterWheelBlockEntity>
extends KineticBlockEntityRenderer<T> {
    public static final SuperByteBufferCache.Compartment<ModelKey> WATER_WHEEL = new SuperByteBufferCache.Compartment();
    public static final StitchedSprite OAK_PLANKS_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace((String)"block/oak_planks"));
    public static final StitchedSprite OAK_LOG_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace((String)"block/oak_log"));
    public static final StitchedSprite OAK_LOG_TOP_TEMPLATE = new StitchedSprite(ResourceLocation.withDefaultNamespace((String)"block/oak_log_top"));
    protected final boolean large;
    private static final String[] LOG_LOCATIONS = new String[]{"x_log", "x_stem", "x_block", "wood/log/x"};

    public WaterWheelRenderer(BlockEntityRendererProvider.Context context, boolean large) {
        super(context);
        this.large = large;
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelRenderer<T> standard(BlockEntityRendererProvider.Context context) {
        return new WaterWheelRenderer<T>(context, false);
    }

    public static <T extends WaterWheelBlockEntity> WaterWheelRenderer<T> large(BlockEntityRendererProvider.Context context) {
        return new WaterWheelRenderer<T>(context, true);
    }

    @Override
    protected SuperByteBuffer getRotatedModel(T be, BlockState state) {
        ModelKey key = new ModelKey(this.large, state, ((WaterWheelBlockEntity)be).material);
        return SuperByteBufferCache.getInstance().get(WATER_WHEEL, (Object)key, () -> {
            BakedModel model = WaterWheelRenderer.generateModel(key);
            BlockState state1 = key.state();
            Direction dir = key.large() ? Direction.fromAxisAndDirection((Direction.Axis)((Direction.Axis)state1.getValue((Property)LargeWaterWheelBlock.AXIS)), (Direction.AxisDirection)Direction.AxisDirection.POSITIVE) : (Direction)state1.getValue((Property)WaterWheelBlock.FACING);
            PoseStack transform = (PoseStack)CachedBuffers.rotateToFaceVertical((Direction)dir).get();
            return SuperBufferFactory.getInstance().createForBlock(model, Blocks.AIR.defaultBlockState(), transform);
        });
    }

    public static BakedModel generateModel(ModelKey key) {
        return WaterWheelRenderer.generateModel(Variant.of(key.large(), key.state()), key.material());
    }

    public static BakedModel generateModel(Variant variant, BlockState material) {
        return WaterWheelRenderer.generateModel(variant.model(), material);
    }

    public static BakedModel generateModel(BakedModel template, BlockState planksBlockState) {
        Block planksBlock = planksBlockState.getBlock();
        ResourceLocation id = RegisteredObjectsHelper.getKeyOrThrow((Block)planksBlock);
        String wood = WaterWheelRenderer.plankStateToWoodName(planksBlockState);
        if (wood == null) {
            return BakedModelHelper.generateModel(template, sprite -> null);
        }
        String namespace = id.getNamespace();
        BlockState logBlockState = WaterWheelRenderer.getLogBlockState(namespace, wood);
        Reference2ReferenceOpenHashMap map = new Reference2ReferenceOpenHashMap();
        map.put(OAK_PLANKS_TEMPLATE.get(), WaterWheelRenderer.getSpriteOnSide(planksBlockState, Direction.UP));
        map.put(OAK_LOG_TEMPLATE.get(), WaterWheelRenderer.getSpriteOnSide(logBlockState, Direction.SOUTH));
        map.put(OAK_LOG_TOP_TEMPLATE.get(), WaterWheelRenderer.getSpriteOnSide(logBlockState, Direction.UP));
        return BakedModelHelper.generateModel(template, ((Map)map)::get);
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

    private static BlockState getLogBlockState(String namespace, String wood) {
        for (String location : LOG_LOCATIONS) {
            Optional<BlockState> state = BuiltInRegistries.BLOCK.getHolder(ResourceKey.create((ResourceKey)Registries.BLOCK, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)namespace, (String)location.replace("x", wood)))).map(Holder::value).map(Block::defaultBlockState);
            if (!state.isPresent()) continue;
            return state.get();
        }
        return Blocks.OAK_LOG.defaultBlockState();
    }

    private static TextureAtlasSprite getSpriteOnSide(BlockState state, Direction side) {
        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
        if (model == null) {
            return null;
        }
        RandomSource random = RandomSource.create();
        random.setSeed(42L);
        List quads = model.getQuads(state, side, random, ModelData.EMPTY, null);
        if (!quads.isEmpty()) {
            return ((BakedQuad)quads.get(0)).getSprite();
        }
        random.setSeed(42L);
        quads = model.getQuads(state, null, random, ModelData.EMPTY, null);
        if (!quads.isEmpty()) {
            for (BakedQuad quad : quads) {
                if (quad.getDirection() != side) continue;
                return quad.getSprite();
            }
        }
        return model.getParticleIcon(ModelData.EMPTY);
    }

    public record ModelKey(boolean large, BlockState state, BlockState material) {
    }

    public static enum Variant {
        SMALL(AllPartialModels.WATER_WHEEL),
        LARGE(AllPartialModels.LARGE_WATER_WHEEL),
        LARGE_EXTENSION(AllPartialModels.LARGE_WATER_WHEEL_EXTENSION);

        private final PartialModel partial;

        private Variant(PartialModel partial) {
            this.partial = partial;
        }

        public BakedModel model() {
            return this.partial.get();
        }

        public static Variant of(boolean large, BlockState blockState) {
            if (large) {
                boolean extension = (Boolean)blockState.getValue((Property)LargeWaterWheelBlock.EXTENSION);
                if (extension) {
                    return LARGE_EXTENSION;
                }
                return LARGE;
            }
            return SMALL;
        }
    }
}
