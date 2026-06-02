/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.network.chat.Component
 */
package dev.simulated_team.simulated.util.extra_kinetics;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import net.minecraft.network.chat.Component;

public static interface ExtraKinetics.ExtraKineticsBlockEntity {
    public KineticBlockEntity getParentBlockEntity();

    default public Component getKey() {
        return SimLang.translate("extra_kinetics.default", new Object[0]).component();
    }
}
