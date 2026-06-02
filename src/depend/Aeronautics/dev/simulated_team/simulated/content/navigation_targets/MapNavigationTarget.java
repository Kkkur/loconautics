/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.component.DataComponents
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.component.MapDecorations
 *  net.minecraft.world.item.component.MapDecorations$Entry
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.saveddata.maps.MapBanner
 *  net.minecraft.world.level.saveddata.maps.MapId
 *  net.minecraft.world.level.saveddata.maps.MapItemSavedData
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.navigation_targets;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import dev.simulated_team.simulated.index.SimTags;
import java.util.Collection;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MapNavigationTarget
implements NavigationTarget {
    @Override
    @Nullable
    public Vec3 getTarget(NavTableBlockEntity navBE, ItemStack self) {
        Level level = navBE.getLevel();
        Vec3 pos = navBE.getProjectedSelfPos();
        return MapNavigationTarget.getNearestDecorationPos(level, pos, self);
    }

    @Override
    public float getMaxRange() {
        return 0.0f;
    }

    private static Vec3 getNearestDecorationPos(Level level, Vec3 pos, ItemStack stack) {
        MapDecorations decorations = (MapDecorations)stack.getComponents().get(DataComponents.MAP_DECORATIONS);
        MapId mapId = (MapId)stack.getComponents().get(DataComponents.MAP_ID);
        if (decorations != null && mapId != null) {
            double closestDist = Double.POSITIVE_INFINITY;
            Vec3 closestPos = null;
            for (MapDecorations.Entry decoration : decorations.decorations().values()) {
                double dist;
                if (!decoration.type().is(SimTags.Misc.NAV_TABLE_FINDABLE) || !((dist = pos.distanceToSqr(decoration.x(), pos.y(), decoration.z())) < closestDist)) continue;
                closestPos = new Vec3(decoration.x(), pos.y(), decoration.z());
                closestDist = dist;
            }
            MapItemSavedData mapData = level.getMapData(mapId);
            if (mapData != null) {
                Collection banners = mapData.getBanners();
                for (MapBanner banner : banners) {
                    Vec3 bannerPos = banner.pos().getCenter();
                    double dist = pos.distanceToSqr(bannerPos.x(), pos.y(), bannerPos.z());
                    if (!(dist < closestDist)) continue;
                    closestPos = bannerPos;
                    closestDist = dist;
                }
            }
            return closestPos;
        }
        return null;
    }
}
