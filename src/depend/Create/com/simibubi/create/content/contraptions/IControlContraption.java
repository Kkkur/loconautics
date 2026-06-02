/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.lang.Lang
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.contraptions;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.gui.AllIcons;
import net.createmod.catnip.lang.Lang;
import net.minecraft.core.BlockPos;

public interface IControlContraption {
    public boolean isAttachedTo(AbstractContraptionEntity var1);

    public void attach(ControlledContraptionEntity var1);

    public void onStall();

    public boolean isValid();

    public BlockPos getBlockPosition();

    public static enum RotationMode implements INamedIconOptions
    {
        ROTATE_PLACE(AllIcons.I_ROTATE_PLACE),
        ROTATE_PLACE_RETURNED(AllIcons.I_ROTATE_PLACE_RETURNED),
        ROTATE_NEVER_PLACE(AllIcons.I_ROTATE_NEVER_PLACE);

        private String translationKey;
        private AllIcons icon;

        private RotationMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.contraptions.movement_mode." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }

    public static enum MovementMode implements INamedIconOptions
    {
        MOVE_PLACE(AllIcons.I_MOVE_PLACE),
        MOVE_PLACE_RETURNED(AllIcons.I_MOVE_PLACE_RETURNED),
        MOVE_NEVER_PLACE(AllIcons.I_MOVE_NEVER_PLACE);

        private String translationKey;
        private AllIcons icon;

        private MovementMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "create.contraptions.movement_mode." + Lang.asId((String)this.name());
        }

        @Override
        public AllIcons getIcon() {
            return this.icon;
        }

        @Override
        public String getTranslationKey() {
            return this.translationKey;
        }
    }
}
