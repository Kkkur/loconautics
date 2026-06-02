/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.behaviour.display.DisplayTarget
 *  com.simibubi.create.content.redstone.displayLink.DisplayLinkContext
 *  com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats
 *  com.simibubi.create.foundation.utility.CreateLang
 *  com.tterrag.registrate.util.entry.RegistryEntry
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 */
package dev.simulated_team.simulated.content.blocks.nameplate;

import com.simibubi.create.api.behaviour.display.DisplayTarget;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.utility.CreateLang;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.api.ConditionalDisplayTarget;
import dev.simulated_team.simulated.content.blocks.nameplate.NameplateBlockEntity;
import dev.simulated_team.simulated.data.SimLang;
import java.util.List;
import net.createmod.catnip.theme.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class NameplateBlockTarget
extends ConditionalDisplayTarget {
    public static final RegistryEntry<DisplayTarget, NameplateBlockTarget> NAMEPLATE = Simulated.getRegistrate().displayTarget("nameplate", NameplateBlockTarget::new).register();

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public boolean allowsWriting(DisplayLinkContext context) {
        BlockEntity blockEntity = context.getTargetBlockEntity();
        if (!(blockEntity instanceof NameplateBlockEntity)) return false;
        NameplateBlockEntity nbe = (NameplateBlockEntity)blockEntity;
        if (!nbe.waxed) return false;
        return true;
    }

    @Override
    public Component getErrorMessage(DisplayLinkContext context) {
        return SimLang.translate("nameplate.target.unwaxed", new Object[0]).color(Color.RED).component();
    }

    public void acceptText(int line, List<MutableComponent> text, DisplayLinkContext context) {
        BlockEntity blockEntity = context.getTargetBlockEntity();
        if (blockEntity instanceof NameplateBlockEntity) {
            NameplateBlockEntity nbe = (NameplateBlockEntity)blockEntity;
            if (nbe.waxed) {
                nbe.setName(text.get(0).getString(), true, null);
            }
        }
    }

    public DisplayTargetStats provideStats(DisplayLinkContext context) {
        return new DisplayTargetStats(1, 0, (DisplayTarget)this);
    }

    public Component getLineOptionText(int line) {
        return CreateLang.translateDirect((String)"display_target.single_line", (Object[])new Object[0]);
    }
}
