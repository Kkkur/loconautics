/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.ryanhcode.sable.api.sublevel.SubLevelObserver
 *  dev.ryanhcode.sable.sublevel.SubLevel
 *  dev.ryanhcode.sable.sublevel.plot.LevelPlot
 *  dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 */
package dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map;

import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.Balloon;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public static class BalloonMap.BalloonSubLevelObserver
implements SubLevelObserver {
    private final Level level;

    public BalloonMap.BalloonSubLevelObserver(Level level) {
        this.level = level;
    }

    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        if (reason == SubLevelRemovalReason.REMOVED) {
            LevelPlot plot = subLevel.getPlot();
            BalloonMap map = (BalloonMap)MAP.get((LevelAccessor)this.level);
            Iterator<Balloon> iter = map.getBalloons().iterator();
            while (iter.hasNext()) {
                BlockPos controllerPos;
                Balloon balloon = iter.next();
                if (balloon.isAssembling() || !plot.contains((double)(controllerPos = balloon.getControllerPos()).getX(), (double)controllerPos.getZ())) continue;
                balloon.onRemoved();
                iter.remove();
            }
        }
    }
}
