/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.math.VecHelper
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.trains.graph;

import com.simibubi.create.content.trains.graph.DimensionPalette;
import com.simibubi.create.content.trains.graph.EdgeData;
import com.simibubi.create.content.trains.graph.TrackGraph;
import com.simibubi.create.content.trains.graph.TrackNode;
import com.simibubi.create.content.trains.track.BezierConnection;
import com.simibubi.create.content.trains.track.TrackMaterial;
import java.util.Collection;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TrackEdge {
    public TrackNode node1;
    public TrackNode node2;
    BezierConnection turn;
    EdgeData edgeData;
    boolean interDimensional;
    TrackMaterial trackMaterial;

    public TrackEdge(TrackNode node1, TrackNode node2, BezierConnection turn, TrackMaterial trackMaterial) {
        this.interDimensional = !node1.location.dimension.equals(node2.location.dimension);
        this.edgeData = new EdgeData(this);
        this.node1 = node1;
        this.node2 = node2;
        this.turn = turn;
        this.trackMaterial = trackMaterial;
    }

    public TrackMaterial getTrackMaterial() {
        return this.trackMaterial;
    }

    public boolean isTurn() {
        return this.turn != null;
    }

    public boolean isInterDimensional() {
        return this.interDimensional;
    }

    public EdgeData getEdgeData() {
        return this.edgeData;
    }

    public BezierConnection getTurn() {
        return this.turn;
    }

    public Vec3 getDirection(boolean fromFirst) {
        return this.getPosition(null, fromFirst ? 0.25 : 1.0).subtract(this.getPosition(null, fromFirst ? 0.0 : 0.75)).normalize();
    }

    public Vec3 getDirectionAt(double t) {
        double length = this.getLength();
        double step = 0.5 / length;
        Vec3 ahead = this.getPosition(null, Math.min(1.0, (t /= length) + step));
        Vec3 behind = this.getPosition(null, Math.max(0.0, t - step));
        return ahead.subtract(behind).normalize();
    }

    public boolean canTravelTo(TrackEdge other) {
        if (this.isInterDimensional() || other.isInterDimensional()) {
            return true;
        }
        Vec3 newDirection = other.getDirection(true);
        return this.getDirection(false).dot(newDirection) > 0.875;
    }

    public double getLength() {
        return this.isInterDimensional() ? 0.0 : (this.isTurn() ? this.turn.getLength() : this.node1.location.getLocation().distanceTo(this.node2.location.getLocation()));
    }

    public double incrementT(double currentT, double distance) {
        boolean tooFar = Math.abs(distance) > 5.0;
        double length = this.getLength();
        return !tooFar && this.isTurn() ? this.turn.incrementT(currentT, distance) : currentT + (distance /= length == 0.0 ? 1.0 : length);
    }

    public Vec3 getPosition(@Nullable TrackGraph trackGraph, double t) {
        Vec3 positionSmoothed;
        if (this.isTurn()) {
            return this.turn.getPosition(Mth.clamp((double)t, (double)0.0, (double)1.0));
        }
        if (trackGraph != null && (this.node1.location.yOffsetPixels != 0 || this.node2.location.yOffsetPixels != 0) && (positionSmoothed = this.getPositionSmoothed(trackGraph, t)) != null) {
            return positionSmoothed;
        }
        return VecHelper.lerp((float)((float)t), (Vec3)this.node1.location.getLocation(), (Vec3)this.node2.location.getLocation());
    }

    public Vec3 getNormal(@Nullable TrackGraph trackGraph, double t) {
        Vec3 normalSmoothed;
        if (this.isTurn()) {
            return this.turn.getNormal(Mth.clamp((double)t, (double)0.0, (double)1.0));
        }
        if (trackGraph != null && (this.node1.location.yOffsetPixels != 0 || this.node2.location.yOffsetPixels != 0) && (normalSmoothed = this.getNormalSmoothed(trackGraph, t)) != null) {
            return normalSmoothed;
        }
        return this.node1.getNormal();
    }

    @Nullable
    public Vec3 getPositionSmoothed(TrackGraph trackGraph, double t) {
        Vec3 node1Location = null;
        Vec3 node2Location = null;
        for (TrackEdge trackEdge : trackGraph.getConnectionsFrom(this.node1).values()) {
            if (!trackEdge.isTurn()) continue;
            node1Location = trackEdge.getPosition(trackGraph, 0.0);
        }
        for (TrackEdge trackEdge : trackGraph.getConnectionsFrom(this.node2).values()) {
            if (!trackEdge.isTurn()) continue;
            node2Location = trackEdge.getPosition(trackGraph, 0.0);
        }
        if (node1Location == null || node2Location == null) {
            return null;
        }
        return VecHelper.lerp((float)((float)t), (Vec3)node1Location, node2Location);
    }

    @Nullable
    public Vec3 getNormalSmoothed(TrackGraph trackGraph, double t) {
        Vec3 node1Normal = null;
        Vec3 node2Normal = null;
        for (TrackEdge trackEdge : trackGraph.getConnectionsFrom(this.node1).values()) {
            if (!trackEdge.isTurn()) continue;
            node1Normal = trackEdge.getNormal(trackGraph, 0.0);
        }
        for (TrackEdge trackEdge : trackGraph.getConnectionsFrom(this.node2).values()) {
            if (!trackEdge.isTurn()) continue;
            node2Normal = trackEdge.getNormal(trackGraph, 0.0);
        }
        if (node1Normal == null || node2Normal == null) {
            return null;
        }
        return VecHelper.lerp((float)0.5f, (Vec3)node1Normal, node2Normal);
    }

    /*
     * Exception decompiling
     */
    public Collection<double[]> getIntersection(TrackNode node1, TrackNode node2, TrackEdge other, TrackNode other1, TrackNode other2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * java.lang.UnsupportedOperationException
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.NewAnonymousArray.getDimSize(NewAnonymousArray.java:142)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.isNewArrayLambda(LambdaRewriter.java:455)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:409)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:167)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:105)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:87)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:103)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredReturn.rewriteExpressions(StructuredReturn.java:99)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doClass(Driver.java:84)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:78)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public CompoundTag write(DimensionPalette dimensions) {
        CompoundTag baseCompound = this.isTurn() ? this.turn.write(BlockPos.ZERO) : new CompoundTag();
        baseCompound.put("Signals", (Tag)this.edgeData.write(dimensions));
        baseCompound.putString("Material", this.getTrackMaterial().id.toString());
        return baseCompound;
    }

    public static TrackEdge read(TrackNode node1, TrackNode node2, CompoundTag tag, TrackGraph graph, DimensionPalette dimensions) {
        TrackEdge trackEdge = new TrackEdge(node1, node2, tag.contains("Positions") ? new BezierConnection(tag, BlockPos.ZERO) : null, TrackMaterial.deserialize(tag.getString("Material")));
        trackEdge.edgeData = EdgeData.read(tag.getCompound("Signals"), trackEdge, graph, dimensions);
        return trackEdge;
    }
}
