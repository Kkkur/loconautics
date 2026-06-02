/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.Veil
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$Context
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$IncludeOverloadStrategy
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
 *  io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException
 *  net.minecraft.client.renderer.RenderType
 */
package dev.ryanhcode.sable.render.dynamic_shade;

import dev.ryanhcode.sable.render.dynamic_shade.SableDynamicDirectionalShading;
import foundry.veil.Veil;
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
import io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.RenderType;

public class SableDynamicDirectionalShadingPreProcessor
implements ShaderPreProcessor {
    public void modify(ShaderPreProcessor.Context ctx, GlslTree tree) throws GlslSyntaxException, IOException, LexerException {
        if (!SableDynamicDirectionalShading.isEnabled()) {
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
        ctx.include(tree, Veil.veilPath((String)"light"), ShaderPreProcessor.IncludeOverloadStrategy.SOURCE);
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float SableEnableNormalLighting;"));
        tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform float SableSkyLightScale;"));
        if (tree.field("NormalMat").isEmpty()) {
            tree.getBody().add(GlslInjectionPoint.BEFORE_MAIN, GlslParser.parseExpression((String)"uniform mat3 NormalMat;"));
        }
        GlslNodeList body = ((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody();
        body.add(GlslParser.parseExpression((String)"vertexColor.rgb *= mix(vec3(1.0), vec3(block_brightness(inverse(NormalMat) * (ModelViewMat * vec4(Normal, 0.0)).xyz)), SableEnableNormalLighting);"));
        GlslNodeList mainFunctionBody = ((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody();
        assert (mainFunctionBody != null);
        for (int i = 0; i < mainFunctionBody.size(); ++i) {
            GlslVariableNode variableNode;
            GlslInvokeFunctionNode invokeNode;
            GlslNode glslNode;
            GlslOperationNode operationNode;
            GlslNode second;
            GlslAssignmentNode assignmentNode;
            GlslNode node = mainFunctionBody.get(i);
            if (!(node instanceof GlslAssignmentNode) || (assignmentNode = (GlslAssignmentNode)node).getOperand() != GlslAssignmentNode.Operand.EQUAL || !((second = assignmentNode.getSecond()) instanceof GlslOperationNode) || (operationNode = (GlslOperationNode)second).getOperand() != GlslOperationNode.Operand.MULTIPLY || !((glslNode = operationNode.getSecond()) instanceof GlslInvokeFunctionNode) || !((glslNode = (invokeNode = (GlslInvokeFunctionNode)glslNode).getHeader()) instanceof GlslVariableNode) || !(variableNode = (GlslVariableNode)glslNode).getName().equals("minecraft_sample_lightmap")) continue;
            List replacementNodes = GlslParser.parseExpressionList((String)"vertexColor = Color * minecraft_sample_lightmap(Sampler2, ivec2(UV2 * vec2(1.0, SableSkyLightScale)));");
            mainFunctionBody.set(i, (GlslNode)replacementNodes.getFirst());
            for (int j = 1; j < replacementNodes.size(); ++j) {
                mainFunctionBody.add(i + j, (GlslNode)replacementNodes.get(j));
            }
            break;
        }
    }
}
