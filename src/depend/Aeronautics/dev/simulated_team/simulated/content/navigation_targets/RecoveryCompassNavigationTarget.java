/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.GlobalPos
 *  net.minecraft.core.component.DataComponentMap
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.navigation_targets;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.index.SimDataComponents;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RecoveryCompassNavigationTarget
implements NavigationTarget {
    @Override
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        String lastPlayer = (String)self.getComponents().get(SimDataComponents.COMPASS_PLACER_UUID);
        if (lastPlayer != null) {
            UUID uuid = UUID.fromString(lastPlayer);
            Player player = navBE.getLevel().getPlayerByUUID(uuid);
            if (player == null) {
                return null;
            }
            if (player.getLastDeathLocation().isEmpty()) {
                return null;
            }
            ResourceKey dimension = navBE.getLevel().dimension();
            GlobalPos globalPos = (GlobalPos)player.getLastDeathLocation().get();
            if (!globalPos.dimension().equals(dimension)) {
                return null;
            }
            return globalPos.pos().getCenter();
        }
        return null;
    }

    @Override
    public void onInsert(ItemStack itemStack, NavTableBlockEntity be, Player player) {
        itemStack.applyComponents(DataComponentMap.builder().set(SimDataComponents.COMPASS_PLACER_UUID, (Object)player.getUUID().toString()).build());
    }
}
