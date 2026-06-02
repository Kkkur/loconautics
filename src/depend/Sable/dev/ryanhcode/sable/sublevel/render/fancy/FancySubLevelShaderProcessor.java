/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$Context
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$IncludeOverloadStrategy
 *  foundry.veil.api.client.render.shader.processor.ShaderPreProcessor$VeilContext
 *  io.github.ocelot.glslprocessor.api.GlslParser
 *  io.github.ocelot.glslprocessor.api.GlslSyntaxException
 *  io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier$StorageType
 *  io.github.ocelot.glslprocessor.api.node.GlslNodeList
 *  io.github.ocelot.glslprocessor.api.node.GlslTree
 *  io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode
 *  io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode
 *  io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException
 *  net.minecraft.resources.ResourceLocation
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import dev.ryanhcode.sable.Sable;
import foundry.veil.api.client.render.shader.processor.ShaderPreProcessor;
import io.github.ocelot.glslprocessor.api.GlslParser;
import io.github.ocelot.glslprocessor.api.GlslSyntaxException;
import io.github.ocelot.glslprocessor.api.grammar.GlslTypeQualifier;
import io.github.ocelot.glslprocessor.api.node.GlslNodeList;
import io.github.ocelot.glslprocessor.api.node.GlslTree;
import io.github.ocelot.glslprocessor.api.node.function.GlslFunctionNode;
import io.github.ocelot.glslprocessor.api.node.variable.GlslNewFieldNode;
import io.github.ocelot.glslprocessor.lib.anarres.cpp.LexerException;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.resources.ResourceLocation;

public class FancySubLevelShaderProcessor
implements ShaderPreProcessor {
    public static final String BUFFER_SIZE = "SABLE_TEXTURE_CACHE_SIZE";

    public void modify(ShaderPreProcessor.Context ctx, GlslTree tree) throws IOException, GlslSyntaxException, LexerException {
        ShaderPreProcessor.VeilContext veilContext;
        if (!(ctx instanceof ShaderPreProcessor.VeilContext) || !(veilContext = (ShaderPreProcessor.VeilContext)ctx).isDynamic()) {
            return;
        }
        ResourceLocation name = Objects.requireNonNull(veilContext.name(), "name");
        if (!name.getNamespace().equals("sable") || !name.getPath().startsWith("dynamic_sublevel/")) {
            return;
        }
        if (ctx.isVertex()) {
            veilContext.addDefinitionDependency(BUFFER_SIZE);
            tree.getBody().removeIf(next -> {
                GlslNewFieldNode field;
                return next instanceof GlslNewFieldNode && (field = (GlslNewFieldNode)next).getType().getQualifiers().contains(GlslTypeQualifier.StorageType.IN);
            });
            ctx.include(tree, Sable.sablePath("fancy_sublevel_vertex"), ShaderPreProcessor.IncludeOverloadStrategy.FAIL);
            GlslNodeList body = Objects.requireNonNull(((GlslFunctionNode)tree.mainFunction().orElseThrow()).getBody());
            body.add(0, GlslParser.parseExpression((String)"_sable_unpack()"));
        }
    }
}
