/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Pair
 *  net.minecraft.world.phys.AABB
 */
package dev.simulated_team.simulated.content.entities.honey_glue;

import dev.simulated_team.simulated.service.SimConfigService;
import net.createmod.catnip.data.Pair;
import net.minecraft.world.phys.AABB;

public class HoneyGlueMaxSizing {
    public static Pair<Boolean, String> checkBounds(AABB bb) {
        if (HoneyGlueMaxSizing.checkBBMin(bb)) {
            return Pair.of((Object)false, (Object)"Contracted area is too small");
        }
        if (HoneyGlueMaxSizing.checkBBMax(bb)) {
            return Pair.of((Object)false, (Object)"Expanded area is too large");
        }
        return Pair.of((Object)true, (Object)"");
    }

    public static boolean checkBBMin(AABB bb) {
        return bb.getXsize() < 1.0 || bb.getYsize() < 1.0 || bb.getZsize() < 1.0;
    }

    public static boolean checkBBMax(AABB bb) {
        int max = (Integer)SimConfigService.INSTANCE.server().assembly.honeyGlueRange.get();
        return bb.getXsize() > (double)max || bb.getYsize() > (double)max || bb.getZsize() > (double)max;
    }
}
