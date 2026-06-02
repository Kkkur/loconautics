/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 */
package com.simibubi.create.content.logistics.packagerLink;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.logistics.BigItemStack;
import java.util.Comparator;

public class RequestPromise {
    public static final Codec<RequestPromise> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("ticks_existed").forGetter(i -> i.ticksExisted), (App)BigItemStack.CODEC.fieldOf("promised_stack").forGetter(i -> i.promisedStack)).apply((Applicative)instance, RequestPromise::new));
    public int ticksExisted = 0;
    public BigItemStack promisedStack;

    public RequestPromise(BigItemStack promisedStack) {
        this.promisedStack = promisedStack;
    }

    public RequestPromise(int ticksExisted, BigItemStack promisedStack) {
        this.ticksExisted = ticksExisted;
        this.promisedStack = promisedStack;
    }

    public void tick() {
        ++this.ticksExisted;
    }

    public static Comparator<? super RequestPromise> ageComparator() {
        return (i1, i2) -> Integer.compare(i2.ticksExisted, i1.ticksExisted);
    }
}
