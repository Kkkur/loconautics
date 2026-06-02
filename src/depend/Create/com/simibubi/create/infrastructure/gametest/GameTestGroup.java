/*
 * Decompiled with CFR 0.152.
 */
package com.simibubi.create.infrastructure.gametest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface GameTestGroup {
    public String path();

    public String namespace() default "create";
}
