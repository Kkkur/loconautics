/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package dev.ryanhcode.sable.mixinterface.clip_overwrite;

import dev.ryanhcode.sable.sublevel.SubLevel;
import java.util.function.Predicate;
import org.jetbrains.annotations.Nullable;

public interface ClipContextExtension {
    @Nullable
    public SubLevel sable$getIgnoredSubLevel();

    @Nullable
    public Predicate<SubLevel> sable$getSubLevelIgnoring();

    public void sable$setIgnoredSubLevel(@Nullable SubLevel var1);

    public void sable$setSubLevelIgnoring(@Nullable Predicate<SubLevel> var1);

    public void sable$setIgnoreMainLevel(boolean var1);

    public boolean sable$isIgnoreMainLevel();

    public void sable$setDoNotProject(boolean var1);

    public boolean sable$doNotProject();
}
