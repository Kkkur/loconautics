/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.kinetics.base.IRotate
 *  com.simibubi.create.content.kinetics.base.KineticBlockEntity
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.util.extra_kinetics;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface ExtraKinetics {
    @Nullable
    public KineticBlockEntity getExtraKinetics();

    public boolean shouldConnectExtraKinetics();

    default public String getExtraKineticsSaveName() {
        return "DEFAULT";
    }

    public static interface ExtraKineticsBlockEntity {
        public KineticBlockEntity getParentBlockEntity();

        default public Component getKey() {
            return SimLang.translate("extra_kinetics.default", new Object[0]).component();
        }
    }

    @FunctionalInterface
    public static interface ExtraKineticsBlock {
        public IRotate getExtraKineticsRotationConfiguration();
    }
}
