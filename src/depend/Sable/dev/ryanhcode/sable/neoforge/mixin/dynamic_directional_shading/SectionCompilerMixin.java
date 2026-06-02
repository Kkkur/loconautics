/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexSorting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.SectionBufferBuilderPack
 *  net.minecraft.client.renderer.block.ModelBlockRenderer
 *  net.minecraft.client.renderer.chunk.RenderChunkRegion
 *  net.minecraft.client.renderer.chunk.SectionCompiler
 *  net.minecraft.client.renderer.chunk.SectionCompiler$Results
 *  net.minecraft.core.SectionPos
 *  net.neoforged.neoforge.client.event.AddSectionGeometryEvent$AdditionalSectionRenderer
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.ryanhcode.sable.neoforge.mixin.dynamic_directional_shading;

import com.mojang.blaze3d.vertex.VertexSorting;
import dev.ryanhcode.sable.api.sublevel.ClientSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.mixinterface.dynamic_directional_shading.ModelBlockRendererCacheExtension;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.SectionPos;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SectionCompiler.class})
public class SectionCompilerMixin {
    @Inject(method={"compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"}, at={@At(value="HEAD")})
    private void sable$preCompile(SectionPos sectionPos, RenderChunkRegion region, VertexSorting sorting, SectionBufferBuilderPack pack, List<AddSectionGeometryEvent.AdditionalSectionRenderer> list, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        ClientLevel level = Minecraft.getInstance().level;
        ClientSubLevelContainer container = SubLevelContainer.getContainer(level);
        LevelPlot plot = container.getPlot(sectionPos.chunk());
        ((ModelBlockRendererCacheExtension)ModelBlockRenderer.CACHE.get()).sable$setOnSubLevel(plot != null);
    }

    @Inject(method={"compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;"}, at={@At(value="TAIL")})
    private void sable$postCompile(SectionPos arg, RenderChunkRegion arg2, VertexSorting arg3, SectionBufferBuilderPack arg4, List<AddSectionGeometryEvent.AdditionalSectionRenderer> additionalRenderers, CallbackInfoReturnable<SectionCompiler.Results> cir) {
        ((ModelBlockRendererCacheExtension)ModelBlockRenderer.CACHE.get()).sable$setOnSubLevel(false);
    }
}
