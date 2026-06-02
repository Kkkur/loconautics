/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.outliner.Outline
 *  net.createmod.ponder.foundation.PonderScene
 *  net.createmod.ponder.foundation.instruction.TickingInstruction
 *  net.minecraft.util.Mth
 *  net.minecraft.world.phys.Vec3
 */
package dev.simulated_team.simulated.ponder.instructions;

import dev.simulated_team.simulated.ponder.outliners.LerpedLineOutline;
import dev.simulated_team.simulated.ponder.records.PonderLineRecord;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import net.createmod.catnip.outliner.Outline;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class ChasingLineInstruction
extends TickingInstruction {
    final PonderLineRecord startLine;
    final PonderLineRecord endLine;
    final int size;
    final int color;
    final String slot;
    final FloatUnaryOperator smoothing;
    final int lerpTicks;
    LerpedLineOutline lerpedLine;
    Vec3 oldLineStart;
    Vec3 oldLineEnd;

    public ChasingLineInstruction(PonderLineRecord startLine, PonderLineRecord endLine, int size, int color, String slot, int lerpTicks, int holdTicks, FloatUnaryOperator smoothing) {
        super(false, lerpTicks + holdTicks);
        this.startLine = startLine;
        this.endLine = endLine;
        this.size = size;
        this.color = color;
        this.slot = slot;
        this.smoothing = smoothing;
        this.lerpTicks = lerpTicks;
    }

    public ChasingLineInstruction(PonderLineRecord line, int size, int color, String slot, int lerpTicks, int holdTicks, FloatUnaryOperator smoothing) {
        super(false, lerpTicks + holdTicks);
        this.startLine = new PonderLineRecord(line.startPos(), line.startPos());
        this.endLine = line;
        this.size = size;
        this.color = color;
        this.slot = slot;
        this.smoothing = smoothing;
        this.lerpTicks = lerpTicks;
    }

    public ChasingLineInstruction(Vec3 startPos, Vec3 endPos, int size, int color, int lerpTicks, int holdTicks, FloatUnaryOperator smoothing) {
        super(false, lerpTicks + holdTicks);
        this.startLine = new PonderLineRecord(startPos, startPos);
        this.endLine = new PonderLineRecord(startPos, endPos);
        this.size = size;
        this.color = color;
        this.slot = startPos.toString();
        this.smoothing = smoothing;
        this.lerpTicks = lerpTicks;
    }

    public ChasingLineInstruction(Vec3 startPos, Vec3 endPos, int size, int color, String slot, int lerpTicks, int holdTicks, FloatUnaryOperator smoothing) {
        super(false, lerpTicks + holdTicks);
        this.startLine = new PonderLineRecord(startPos, startPos);
        this.endLine = new PonderLineRecord(startPos, endPos);
        this.size = size;
        this.color = color;
        this.slot = slot;
        this.smoothing = smoothing;
        this.lerpTicks = lerpTicks;
    }

    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        this.oldLineStart = this.startLine.startPos();
        this.oldLineEnd = this.startLine.endPos();
        this.lerpedLine = new LerpedLineOutline(this.startLine);
    }

    public void tick(PonderScene scene) {
        super.tick(scene);
        float percentage = Mth.clamp((float)((float)(this.totalTicks - this.remainingTicks) / (float)this.lerpTicks), (float)0.0f, (float)1.0f);
        percentage = this.smoothing.apply(percentage);
        Vec3 currentStartPos = this.endLine.startPos().subtract(this.startLine.startPos()).scale((double)percentage).add(this.startLine.startPos());
        Vec3 currentEndPos = this.endLine.endPos().subtract(this.startLine.endPos()).scale((double)percentage).add(this.startLine.endPos());
        this.lerpedLine.update(this.oldLineStart, this.oldLineEnd, currentStartPos, currentEndPos);
        scene.getOutliner().showOutline((Object)this.slot, (Outline)this.lerpedLine).lineWidth((float)this.size / 16.0f).colored(this.color);
        this.oldLineStart = currentStartPos;
        this.oldLineEnd = currentEndPos;
    }
}
