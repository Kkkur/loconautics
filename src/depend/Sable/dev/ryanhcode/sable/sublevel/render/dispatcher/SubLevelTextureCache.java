/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  foundry.veil.api.client.render.shader.block.DynamicShaderBlock
 *  foundry.veil.api.client.render.shader.block.ShaderBlock
 *  foundry.veil.api.client.render.shader.block.ShaderBlock$BufferBinding
 *  it.unimi.dsi.fastutil.objects.Object2IntArrayMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.dispatcher;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.shader.block.DynamicShaderBlock;
import foundry.veil.api.client.render.shader.block.ShaderBlock;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Objects;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.lwjgl.system.NativeResource;

public class SubLevelTextureCache
implements NativeResource {
    private static final int SPRITE_SIZE = 32;
    private static final int DEFAULT_SPRITE_COUNT = 32;
    private final Object2IntMap<PackedTexture> textures = Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap());
    private final Object2IntMap<PackedTexture> newTextures = new Object2IntArrayMap();
    private DynamicShaderBlock<PackedTexture[]> textureBlock = null;

    public SubLevelTextureCache() {
        VeilRenderSystem.renderer().getShaderDefinitions().set("SABLE_TEXTURE_CACHE_SIZE", Integer.toString(32));
    }

    public int getTextureId(BakedQuad quad) {
        int[] vertices = quad.getVertices();
        float u0 = Float.intBitsToFloat(vertices[4]);
        float v0 = Float.intBitsToFloat(vertices[5]);
        float u1 = Float.intBitsToFloat(vertices[12]);
        float v1 = Float.intBitsToFloat(vertices[13]);
        float u2 = Float.intBitsToFloat(vertices[20]);
        float v2 = Float.intBitsToFloat(vertices[21]);
        float u3 = Float.intBitsToFloat(vertices[28]);
        float v3 = Float.intBitsToFloat(vertices[29]);
        return this.textures.computeIfAbsent((Object)new PackedTexture(u0, v0, u1, v1, u2, v2, u3, v3), texture -> {
            int textureId = this.textures.size();
            this.newTextures.put((Object)((PackedTexture)texture), textureId);
            return textureId;
        });
    }

    public void flush() {
        int expectedSize;
        if (this.newTextures.isEmpty()) {
            return;
        }
        if (this.textureBlock == null) {
            this.textureBlock = ShaderBlock.dynamic((ShaderBlock.BufferBinding)ShaderBlock.BufferBinding.UNIFORM, (int)1024, (packedTextures, byteBuffer) -> {
                for (PackedTexture texture : packedTextures) {
                    if (texture == null) break;
                    byteBuffer.putFloat(texture.u0);
                    byteBuffer.putFloat(texture.u1);
                    byteBuffer.putFloat(texture.u2);
                    byteBuffer.putFloat(texture.u3);
                    byteBuffer.putFloat(texture.v0);
                    byteBuffer.putFloat(texture.v1);
                    byteBuffer.putFloat(texture.v2);
                    byteBuffer.putFloat(texture.v3);
                }
            });
            this.textureBlock.set((Object)new PackedTexture[32]);
        }
        if ((expectedSize = this.textures.size() + this.newTextures.size()) * 32 > this.textureBlock.getSize()) {
            int newSize = (int)((double)expectedSize * 1.5);
            this.textureBlock.setSize(newSize * 32);
            PackedTexture[] packedTextures2 = Objects.requireNonNull((PackedTexture[])this.textureBlock.getValue());
            this.textureBlock.set((Object)Arrays.copyOf(packedTextures2, newSize));
            VeilRenderSystem.renderer().getShaderDefinitions().set("SABLE_TEXTURE_CACHE_SIZE", Long.toString(newSize));
        }
        PackedTexture[] packedTextures3 = Objects.requireNonNull((PackedTexture[])this.textureBlock.getValue());
        for (Object2IntMap.Entry entry : this.newTextures.object2IntEntrySet()) {
            packedTextures3[entry.getIntValue()] = (PackedTexture)entry.getKey();
        }
        this.newTextures.clear();
        this.textureBlock.set((Object)packedTextures3);
    }

    public void bind() {
        this.flush();
        if (this.textureBlock != null) {
            VeilRenderSystem.bind((CharSequence)"SableSprites", this.textureBlock);
        }
    }

    public void free() {
        if (this.textureBlock != null) {
            VeilRenderSystem.unbind(this.textureBlock);
            this.textureBlock.free();
            this.textureBlock = null;
        }
    }

    private record PackedTexture(float u0, float v0, float u1, float v1, float u2, float v2, float u3, float v3) {
    }
}
