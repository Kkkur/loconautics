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
 */
package dev.ryanhcode.sable.render.water_occlusion;

import dev.ryanhcode.sable.render.water_occlusion.WaterOcclusionRenderer;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslInjectionPoint;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;

public class SableWaterOcclusionPreProcessor
implements ShaderPreProcessor {
    public static final String CLOSE_SAMPLER_NAME = "SableCloseSampler";
    public static final String FAR_SAMPLER_NAME = "SableFarSampler";
    public static final String ENABLE_UNIFORM = "SableWaterOcclusionEnabled";

    public void modify(ShaderPreProcessor.Context ctx, GlslTree tree) throws GlslSyntaxException {
        if (!WaterOcclusionRenderer.isEnabled()) {
            return;
        }
        if (!ctx.isSourceFile()) {
            return;
        }
        if (!(ctx instanceof ShaderPreProcessor.MinecraftContext)) {
            return;
        }
        ShaderPreProcessor.MinecraftContext minecraftContext = (ShaderPreProcessor.MinecraftContext)ctx;
        if (!ctx.isFragment() || !minecraftContext.shaderInstance().equals("rendertype_translucent")) {
            return;
        }
        GlslNodeList mainFunctionBody = ((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody();
        assert (mainFunctionBody != null);
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform vec2 %s;".formatted("ScreenSize")));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform sampler2D %s;".formatted(CLOSE_SAMPLER_NAME)));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform sampler2D %s;".formatted(FAR_SAMPLER_NAME)));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float %s;".formatted(ENABLE_UNIFORM)));
        mainFunctionBody.add(1, GlslParser.parseExpression((String)"if(%s > 0.0) {\n    float closeDepth = texture(%s, gl_FragCoord.xy / ScreenSize).r;\n    float farDepth = texture(%s, gl_FragCoord.xy / ScreenSize).r;\n    float waterDepth = gl_FragCoord.z;\n    if (waterDepth > closeDepth && waterDepth < farDepth) { discard; }\n}\n".formatted(ENABLE_UNIFORM, CLOSE_SAMPLER_NAME, FAR_SAMPLER_NAME).trim()));
    }
}
