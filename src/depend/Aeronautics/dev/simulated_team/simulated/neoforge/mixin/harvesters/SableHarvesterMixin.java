/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 */
package dev.simulated_team.simulated.neoforge.mixin.harvesters;

import com.simibubi.create.content.contraptions.actors.harvester.HarvesterBlockEntity;
import dev.simulated_team.simulated.content.blocks.auger_shaft.BlockHarvester;
import dev.simulated_team.simulated.content.blocks.auger_shaft.auger_groups.AugerDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value={HarvesterBlockEntity.class})
public class SableHarvesterMixin
implements BlockHarvester {
    @Unique
    private AugerDistributor simulated$distributor;

    @Override
    public AugerDistributor simulated$getAssociatedDistributor() {
        return this.simulated$distributor;
    }

    @Override
    public void simulated$setDistributor(AugerDistributor distributor) {
        this.simulated$distributor = distributor;
    }
}
