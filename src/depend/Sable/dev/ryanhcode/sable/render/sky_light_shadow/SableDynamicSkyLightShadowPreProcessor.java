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
 *  io.github.ocelot.glslprocessor.api.node.GlslNode
 *  io.github.ocelot.glslprocessor.api.node.GlslNodeList
 *  io.github.ocelot.glslprocessor.api.node.GlslTree
 *  io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode
 *  io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode$Operand
 *  io.github.ocelot.glslprocessor.api.node.expression.GlslOperationNode
 *  io.github.ocelot.glslprocessor.api.node.expression.GlslOperationNode$Operand
 *  io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode
 *  io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode
 *  io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode
 *  net.minecraft.client.renderer.RenderType
 */
package dev.ryanhcode.sable.render.sky_light_shadow;

import dev.ryanhcode.sable.render.sky_light_shadow.SableSkyLightShadows;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslInjectionPoint;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.node.GlslNode;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.expression.GlslAssignmentNode;
import io.github.ocelot.glslprocessor.api.node.expression.GlslOperationNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.function.GlslInvokeFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslVariableNode;
import java.util.List;
import net.minecraft.client.renderer.RenderType;

public class SableDynamicSkyLightShadowPreProcessor
implements ShaderPreProcessor {
    public static final String SAMPLER_NAME = "SableShadowSampler";
    public static final String SHADOW_VOLUME_SIZE_UNIFORM = "SableShadowVolumeSize";
    public static final String ENABLE_UNIFORM = "SableShadowsEnabled";
    public static final String SHADOW_ORIGIN_UNIFORM = "SableShadowOrigin";

    public void modify(ShaderPreProcessor.Context ctx, GlslTree tree) throws GlslSyntaxException {
        if (!SableSkyLightShadows.isEnabled()) {
            return;
        }
        if (!ctx.isSourceFile()) {
            return;
        }
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
        GlslNodeList mainFunctionBody = ((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody();
        assert (mainFunctionBody != null);
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform sampler2D %s;".formatted(SAMPLER_NAME)));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float %s;".formatted(SHADOW_VOLUME_SIZE_UNIFORM)));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float %s;".formatted(ENABLE_UNIFORM)));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform vec3 %s;".formatted(SHADOW_ORIGIN_UNIFORM)));
        for (int i = 0; i < mainFunctionBody.size(); ++i) {
            GlslVariableNode variableNode;
            GlslInvokeFunctionNode invokeNode;
            GlslNode glslNode;
            GlslOperationNode operationNode;
            GlslNode second;
            GlslAssignmentNode assignmentNode;
            GlslNode node = mainFunctionBody.get(i);
            if (!(node instanceof GlslAssignmentNode) || (assignmentNode = (GlslAssignmentNode)node).getOperand() != GlslAssignmentNode.Operand.EQUAL || !((second = assignmentNode.getSecond()) instanceof GlslOperationNode) || (operationNode = (GlslOperationNode)second).getOperand() != GlslOperationNode.Operand.MULTIPLY || !((glslNode = operationNode.getSecond()) instanceof GlslInvokeFunctionNode) || !((glslNode = (invokeNode = (GlslInvokeFunctionNode)glslNode).getHeader()) instanceof GlslVariableNode) || !(variableNode = (GlslVariableNode)glslNode).getName().equals("minecraft_sample_lightmap")) continue;
            List replacementNodes = GlslParser.parseExpressionList((String)"\n                                    float skyLightScale;\n                                    if (%s > 0.0) {\n                                        float volumeSize = %s;\n                                        vec3 shadowOrigin = %s;\n                                        vec2 shadowUv = ((pos.xz - shadowOrigin.xz) * vec2(1.0, -1.0) + volumeSize) / (volumeSize * 2.0);\n\n                                        float sampleAverage = 0.0;\n                                        int sampleRadius = 3;\n                                        float spacing = 1.0;\n\n                                        for (int i = -sampleRadius; i <= sampleRadius; i++) {\n                                            for (int j = -sampleRadius; j <= sampleRadius; j++) {\n                                                float depthSample = texture(%s, shadowUv + vec2(i, j) * spacing / (volumeSize * 2.0)).r;\n\n                                                // TODO: Pass shadow near plane in\n                                                float depth = 0.5 + depthSample * (volumeSize - 0.5);\n\n                                                float y = shadowOrigin.y - depth;\n\n//                                                pos = Position + ChunkOffset;\n//                                                pos.y = y;\n//                                                gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);\n\n                                                if (y >= pos.y) {\n                                                    float strength = max(min((y - pos.y - 2.0) / 15.0, 1.0), 0.0);\n                                                    float scale = (i + j) / float(sampleRadius);\n                                                    sampleAverage += max(1.0 - scale, 0.0) * 0.6 * strength;\n                                                }\n                                            }\n                                        }\n\n                                        sampleAverage /= float((sampleRadius * 2 + 1) * (sampleRadius * 2 + 1));\n                                        skyLightScale = smoothstep(0.0, 1.0, 1.0 - sampleAverage);\n                                    } else {\n                                        skyLightScale = 1.0;\n                                    }\n\n                                    vec2 sableLightModification = vec2(1.0, skyLightScale);\n\n                                    vertexColor = Color * minecraft_sample_lightmap(Sampler2, ivec2(UV2 * sableLightModification));\n\n".formatted(ENABLE_UNIFORM, SHADOW_VOLUME_SIZE_UNIFORM, SHADOW_ORIGIN_UNIFORM, SAMPLER_NAME));
            mainFunctionBody.set(i, (GlslNode)replacementNodes.get(0));
            for (int j = 1; j < replacementNodes.size(); ++j) {
                mainFunctionBody.add(i + j, (GlslNode)replacementNodes.get(j));
            }
            break;
        }
    }
}
