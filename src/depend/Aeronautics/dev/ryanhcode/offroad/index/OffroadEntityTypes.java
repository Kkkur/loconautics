/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer
 *  com.simibubi.create.content.contraptions.render.ContraptionVisual
 *  com.tterrag.registrate.builders.EntityBuilder
 *  com.tterrag.registrate.util.entry.EntityEntry
 *  dev.simulated_team.simulated.registrate.SimulatedRegistrate
 *  net.minecraft.world.entity.MobCategory
 */
package dev.ryanhcode.offroad.index;

import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.simibubi.create.content.contraptions.render.ContraptionVisual;
import com.tterrag.registrate.builders.EntityBuilder;
import com.tterrag.registrate.util.entry.EntityEntry;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.content.entities.BoreheadContraptionEntity;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.world.entity.MobCategory;

public class OffroadEntityTypes {
    private static final SimulatedRegistrate REGISTRATE = Offroad.getRegistrate();
    public static final EntityEntry<BoreheadContraptionEntity> BOREHEAD_CONTRAPTION_ENTITY = ((EntityBuilder)REGISTRATE.entity("borehead_contraption_entity", BoreheadContraptionEntity::new, MobCategory.MISC).visual(() -> ContraptionVisual::new).renderer(() -> ContraptionEntityRenderer::new).transform(builder -> builder.properties(b -> b.clientTrackingRange(20).updateInterval(40).sized(1.0f, 1.0f).eyeHeight(0.0f).fireImmune()))).register();

    public static void init() {
    }
}
