/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.ByteBufferBuilder
 *  com.mojang.blaze3d.vertex.MeshData
 *  com.mojang.blaze3d.vertex.MeshData$SortState
 *  com.mojang.blaze3d.vertex.PoseStack
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.ItemBlockRenderTypes
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.SectionBufferBuilderPack
 *  net.minecraft.client.renderer.block.BlockRenderDispatcher
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
 *  net.minecraft.client.renderer.blockentity.BlockEntityRenderer
 *  net.minecraft.client.renderer.chunk.RenderChunkRegion
 *  net.minecraft.client.renderer.chunk.VisGraph
 *  net.minecraft.client.renderer.chunk.VisibilitySet
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.SectionPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.RenderShape
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Vector3ic
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.ryanhcode.sable.sublevel.render.dispatcher.SubLevelTextureCache;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3ic;
import org.lwjgl.system.NativeResource;

public class SubLevelMeshBuilder {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockRenderDispatcher blockRenderer;
    private final BlockEntityRenderDispatcher blockEntityRenderer;
    private final SubLevelTextureCache textureCache;

    public SubLevelMeshBuilder(BlockRenderDispatcher blockRenderDispatcher, BlockEntityRenderDispatcher blockEntityRenderDispatcher, SubLevelTextureCache textureCache) {
        this.blockRenderer = blockRenderDispatcher;
        this.blockEntityRenderer = blockEntityRenderDispatcher;
        this.textureCache = textureCache;
    }

    public Results compile(Vector3ic origin, SectionPos sectionPos, RenderChunkRegion renderChunkRegion, SectionBufferBuilderPack sectionBufferBuilderPack) {
        Results results = new Results();
        BlockPos min = sectionPos.origin();
        BlockPos max = min.offset(15, 15, 15);
        VisGraph visGraph = new VisGraph();
        PoseStack poseStack = new PoseStack();
        ModelBlockRenderer.enableCaching();
        Reference2ObjectArrayMap faceMeshes = new Reference2ObjectArrayMap(RenderType.chunkBufferLayers().size());
        RandomSource randomSource = RandomSource.create();
        BlockPos.MutableBlockPos offsetPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos rightPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos upPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos forwardPos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos aoOffsetPos = new BlockPos.MutableBlockPos();
        for (BlockPos pos : BlockPos.betweenClosed((BlockPos)min, (BlockPos)max)) {
            BlockEntity blockEntity;
            BlockState blockState = renderChunkRegion.getBlockState(pos);
            if (blockState.isSolidRender((BlockGetter)renderChunkRegion, pos)) {
                visGraph.setOpaque(pos);
            }
            if (blockState.hasBlockEntity() && (blockEntity = renderChunkRegion.getBlockEntity(pos)) != null) {
                this.handleBlockEntity(results, blockEntity);
            }
            if (blockState.getRenderShape() != RenderShape.MODEL) continue;
            RenderType renderType = ItemBlockRenderTypes.getChunkRenderType((BlockState)blockState);
            BakedModel model = this.blockRenderer.getBlockModel(blockState);
            long seed = blockState.getSeed(pos);
            randomSource.setSeed(seed);
            List unculledQuads = model.getQuads(blockState, null, randomSource);
            if (unculledQuads.isEmpty()) {
                BakedQuad[] quads = new BakedQuad[6];
                boolean valid = true;
                for (Direction direction : DIRECTIONS) {
                    randomSource.setSeed(seed);
                    List culledQuads = model.getQuads(blockState, direction, randomSource);
                    if (culledQuads.size() != 1) {
                        valid = false;
                        break;
                    }
                    BakedQuad quad = (BakedQuad)culledQuads.getFirst();
                    if (!SubLevelMeshBuilder.isAxisAligned(quad)) {
                        valid = false;
                        break;
                    }
                    quads[direction.get3DDataValue()] = quad;
                }
                if (valid) {
                    QuadMesh mesh = faceMeshes.computeIfAbsent(renderType, unused -> new QuadMesh());
                    int posX = pos.getX() & 0xF;
                    int posY = pos.getY() & 0xF;
                    int posZ = pos.getZ() & 0xF;
                    for (Direction direction : DIRECTIONS) {
                        offsetPos.setWithOffset((Vec3i)pos, direction);
                        if (!Block.shouldRenderFace((BlockState)blockState, (BlockGetter)renderChunkRegion, (BlockPos)pos, (Direction)direction, (BlockPos)offsetPos)) continue;
                        int packedLight = LevelRenderer.getLightColor((BlockAndTintGetter)renderChunkRegion, (BlockState)blockState, (BlockPos)offsetPos);
                        int blockLight = LightTexture.block((int)packedLight);
                        int skyLight = LightTexture.sky((int)packedLight);
                        int textureId = this.textureCache.getTextureId(quads[direction.get3DDataValue()]);
                        int packedData = posX | posY << 4 | posZ << 8 | skyLight << 12 | blockLight << 16 | textureId << 20;
                        IntList face = mesh.faces[direction.get3DDataValue()];
                        face.add(packedData);
                        face.add(sectionPos.x() - origin.x() | sectionPos.y() - origin.y() << 8 | sectionPos.z() - origin.z() << 16 | SubLevelMeshBuilder.getFaceAO((BlockAndTintGetter)renderChunkRegion, (BlockPos)offsetPos, direction, aoOffsetPos, rightPos, upPos, forwardPos));
                    }
                    continue;
                }
            }
            System.out.printf("Block at %s isn't a cube %n", pos);
        }
        results.renderedQuadLayers.putAll((Map<RenderType, QuadMesh>)faceMeshes);
        ModelBlockRenderer.clearCache();
        results.visibilitySet = visGraph.resolve();
        return results;
    }

    private static int getFaceAO(BlockAndTintGetter level, BlockPos pos, Direction direction, BlockPos.MutableBlockPos offset, BlockPos.MutableBlockPos right, BlockPos.MutableBlockPos up, BlockPos.MutableBlockPos forward) {
        if (!Minecraft.useAmbientOcclusion()) {
            return 0;
        }
        switch (direction) {
            case DOWN: {
                right.set(0, 0, -1);
                up.set(1, 0, 0);
                break;
            }
            case UP: {
                right.set(0, 0, 1);
                up.set(1, 0, 0);
                break;
            }
            case NORTH: {
                right.set(-1, 0, 0);
                up.set(0, 1, 0);
                break;
            }
            case SOUTH: {
                right.set(1, 0, 0);
                up.set(0, 1, 0);
                break;
            }
            case WEST: {
                right.set(0, 0, 1);
                up.set(0, 1, 0);
                break;
            }
            case EAST: {
                right.set(0, 0, -1);
                up.set(0, 1, 0);
            }
        }
        offset.setWithOffset((Vec3i)pos, -up.getX(), -up.getY(), -up.getZ());
        boolean downAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset);
        boolean downLeftAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(-right.getX(), -right.getY(), -right.getZ()));
        boolean leftAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(up.getX(), up.getY(), up.getZ()));
        boolean upLeftAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(up.getX(), up.getY(), up.getZ()));
        boolean upAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(right.getX(), right.getY(), right.getZ()));
        boolean upRightAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(right.getX(), right.getY(), right.getZ()));
        boolean rightAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(-up.getX(), -up.getY(), -up.getZ()));
        boolean downRightAO = SubLevelMeshBuilder.isOpaque(level, (BlockPos)offset.move(-up.getX(), -up.getY(), -up.getZ()));
        int ao0 = SubLevelMeshBuilder.vertexAO(downAO, leftAO, downLeftAO);
        int ao1 = SubLevelMeshBuilder.vertexAO(downAO, rightAO, downRightAO);
        int ao2 = SubLevelMeshBuilder.vertexAO(upAO, rightAO, upRightAO);
        int ao3 = SubLevelMeshBuilder.vertexAO(upAO, leftAO, upLeftAO);
        return switch (direction) {
            case Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST -> ao3 << 24 | ao0 << 26 | ao1 << 28 | ao2 << 30;
            default -> ao0 << 24 | ao1 << 26 | ao2 << 28 | ao3 << 30;
        };
    }

    private static int vertexAO(boolean side1, boolean side2, boolean corner) {
        if (side1 && side2) {
            return 3;
        }
        return (side1 ? 1 : 0) + (side2 ? 1 : 0) + (corner ? 1 : 0);
    }

    private static boolean isOpaque(BlockAndTintGetter level, BlockPos pos) {
        return level.getBlockState(pos).isCollisionShapeFullBlock((BlockGetter)level, pos);
    }

    private static boolean isAxisAligned(BakedQuad quad) {
        int[] vertices = quad.getVertices();
        for (int i = 0; i < vertices.length / 8; ++i) {
            float x = Float.intBitsToFloat(vertices[i * 8]);
            float y = Float.intBitsToFloat(vertices[i * 8 + 1]);
            float z = Float.intBitsToFloat(vertices[i * 8 + 2]);
            if (!((double)Math.abs(x - (float)Math.round(x)) > 0.01) && !((double)Math.abs(y - (float)Math.round(y)) > 0.01) && !((double)Math.abs(z - (float)Math.round(z)) > 0.01)) continue;
            return false;
        }
        return true;
    }

    private ByteBufferBuilder getOrBeginQuadLayer(Map<RenderType, ByteBufferBuilder> map, SectionBufferBuilderPack pack, RenderType renderType) {
        ByteBufferBuilder bufferBuilder = map.get(renderType);
        if (bufferBuilder == null) {
            bufferBuilder = pack.buffer(renderType);
            map.put(renderType, bufferBuilder);
        }
        return bufferBuilder;
    }

    private <E extends BlockEntity> void handleBlockEntity(Results results, E blockEntity) {
        BlockEntityRenderer blockEntityRenderer = this.blockEntityRenderer.getRenderer(blockEntity);
        if (blockEntityRenderer != null) {
            if (blockEntityRenderer.shouldRenderOffScreen(blockEntity)) {
                results.globalBlockEntities.add(blockEntity);
            } else {
                results.blockEntities.add(blockEntity);
            }
        }
    }

    public static final class Results
    implements NativeResource {
        public final List<BlockEntity> globalBlockEntities = new ArrayList<BlockEntity>();
        public final List<BlockEntity> blockEntities = new ArrayList<BlockEntity>();
        public final Map<RenderType, QuadMesh> renderedQuadLayers = new Reference2ObjectArrayMap();
        public final Map<RenderType, MeshData> renderedModelLayers = new Reference2ObjectArrayMap();
        public VisibilitySet visibilitySet = new VisibilitySet();
        @Nullable
        public MeshData.SortState transparencyState;

        public void free() {
            this.renderedModelLayers.values().forEach(MeshData::close);
        }
    }

    public static class QuadMesh {
        private final IntList[] faces = new IntArrayList[DIRECTIONS.length];

        public QuadMesh() {
            for (int i = 0; i < this.faces.length; ++i) {
                this.faces[i] = new IntArrayList();
            }
        }

        public IntList[] getFaces() {
            return this.faces;
        }
    }
}
