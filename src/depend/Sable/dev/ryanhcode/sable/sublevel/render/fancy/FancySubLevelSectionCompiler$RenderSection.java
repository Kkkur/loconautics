/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  foundry.veil.api.client.render.VeilRenderSystem
 *  net.minecraft.core.SectionPos
 *  org.joml.Vector3ic
 *  org.lwjgl.system.NativeResource
 */
package dev.ryanhcode.sable.sublevel.render.fancy;

import dev.ryanhcode.sable.sublevel.render.fancy.FancySubLevelSectionCompiler;
import foundry.veil.api.client.render.VeilRenderSystem;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.core.SectionPos;
import org.joml.Vector3ic;
import org.lwjgl.system.NativeResource;

public static class FancySubLevelSectionCompiler.RenderSection
implements NativeResource {
    private final SectionPos pos;
    private final Vector3ic origin;
    private final AtomicReference<FancySubLevelSectionCompiler.CompiledSection> compiledSection;
    private boolean dirty;
    private boolean dirtyFromPlayer;

    public FancySubLevelSectionCompiler.RenderSection(SectionPos pos, Vector3ic origin) {
        this.pos = pos;
        this.origin = origin;
        this.compiledSection = new AtomicReference<FancySubLevelSectionCompiler.CompiledSection>(FancySubLevelSectionCompiler.CompiledSection.UNCOMPILED);
        this.dirty = true;
        this.dirtyFromPlayer = false;
    }

    public void setCompiledSection(FancySubLevelSectionCompiler.CompiledSection compiledSection) {
        FancySubLevelSectionCompiler.CompiledSection oldSection = this.compiledSection.getAndSet(compiledSection);
        if (oldSection != null) {
            VeilRenderSystem.renderThreadExecutor().execute(oldSection::free);
        }
    }

    public void setDirty(boolean playerChanged) {
        this.dirty = true;
        this.dirtyFromPlayer |= playerChanged;
    }

    public void setNotDirty() {
        this.dirty = false;
        this.dirtyFromPlayer = false;
    }

    public SectionPos getPos() {
        return this.pos;
    }

    public Vector3ic getOrigin() {
        return this.origin;
    }

    public FancySubLevelSectionCompiler.CompiledSection getCompiledSection() {
        return this.compiledSection.get();
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public boolean isDirtyFromPlayer() {
        return this.dirtyFromPlayer;
    }

    public void free() {
        this.compiledSection.getAndSet(FancySubLevelSectionCompiler.CompiledSection.UNCOMPILED).free();
    }
}
