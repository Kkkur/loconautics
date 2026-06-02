/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$Context
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$MinecraftContext
 *  io.github.ocelot.glslprocessor.api.GlslInjectionPoint
 *  io.github.ocelot.glslprocessor.api.GlslParser
 *  io.github.ocelot.glslprocessor.api.GlslSyntaxException
 *  io.github.ocelot.glslprocessor.api.node.GlslNodeList
 *  io.github.ocelot.glslprocessor.api.node.GlslTree
 *  io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode
 *  net.minecraft.client.renderer.RenderType
 */
package dev.simulated_team.simulated.content.end_sea;

import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslInjectionPoint;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import java.util.List;
import net.minecraft.client.renderer.RenderType;

public class EndSeaFadeTransformer
implements ShaderPreProcessor {
    public void modify(ShaderPreProcessor.Context ctx, GlslTree tree) throws GlslSyntaxException {
        if (ctx instanceof ShaderPreProcessor.MinecraftContext) {
            ShaderPreProcessor.MinecraftContext minecraftContext = (ShaderPreProcessor.MinecraftContext)ctx;
            List renderTypes = RenderType.chunkBufferLayers();
            boolean anyMatches = false;
            for (RenderType renderType : renderTypes) {
                if (!ctx.isVertex() || !minecraftContext.shaderInstance().equals("rendertype_%s".formatted(renderType.name))) continue;
                anyMatches = true;
            }
            if (!anyMatches) {
                return;
            }
        } else {
            return;
        }
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float EndSeaCameraY;"));
        if (tree.field("NormalMat").isEmpty()) {
            tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform mat3 NormalMat;"));
        }
        GlslNodeList body = ((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody();
        body.add(GlslParser.parseExpression((String)"    if (EndSeaCameraY != 0.0) {\n        vertexColor.rgb = mix(vertexColor.rgb, vec3(0.086, 0.078, 0.109) * 2.0, clamp((-(inverse(NormalMat) * (ModelViewMat * vec4(pos, 0.0)).rgb).y - EndSeaCameraY) / 30.0, 0.0, 1.0));\n    }\n"));
    }
}
