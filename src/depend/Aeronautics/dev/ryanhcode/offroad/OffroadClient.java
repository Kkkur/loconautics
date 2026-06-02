/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.ponder.api.registration.PonderPlugin
 *  net.createmod.ponder.foundation.PonderIndex
 */
package dev.ryanhcode.offroad;

import dev.ryanhcode.offroad.content.ponder.OffroadPonderPlugin;
import dev.ryanhcode.offroad.index.OffroadPartialModels;
import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;

public class OffroadClient {
    public static void init() {
        PonderIndex.addPlugin((PonderPlugin)new OffroadPonderPlugin());
        OffroadPartialModels.init();
    }
}
