/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.EntityBuilder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 */
package dev.simulated_team.simulated.service;

import com.tterrag.registrate.builders.EntityBuilder;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface SimEntityService {
    public static final SimEntityService INSTANCE = ServiceUtil.load(SimEntityService.class);

    public CompoundTag getCustomData(Entity var1);

    public double getPlayerReach(Player var1);

    public <T extends Entity, P> EntityBuilder<T, P> loaderEntityTransform(EntityBuilder<T, P> var1, SimEntityTypes.EntityLoaderData var2);
}
