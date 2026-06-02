/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.IntAttached
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.data.WorldAttached
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.LevelAccessor
 */
package com.simibubi.create.content.contraptions.actors.trainControls;

import com.simibubi.create.content.contraptions.AbstractContraptionEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.createmod.catnip.data.IntAttached;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class ControlsServerHandler {
    public static WorldAttached<Map<UUID, ControlsContext>> receivedInputs = new WorldAttached($ -> new HashMap());
    static final int TIMEOUT = 30;

    public static void tick(LevelAccessor world) {
        Map map = (Map)receivedInputs.get(world);
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            ControlsContext ctx = (ControlsContext)entry.getValue();
            Collection<ManuallyPressedKey> list = ctx.keys;
            if (ctx.entity.isRemoved()) {
                iterator.remove();
                continue;
            }
            Iterator<ManuallyPressedKey> entryIterator = list.iterator();
            while (entryIterator.hasNext()) {
                ManuallyPressedKey pressedKey = entryIterator.next();
                pressedKey.decrement();
                if (pressedKey.isAlive()) continue;
                entryIterator.remove();
            }
            Player player = world.getPlayerByUUID((UUID)entry.getKey());
            if (player == null) {
                ctx.entity.stopControlling(ctx.controlsLocalPos);
                iterator.remove();
                continue;
            }
            if (!ctx.entity.control(ctx.controlsLocalPos, list.stream().map(Pair::getSecond).toList(), player)) {
                ctx.entity.stopControlling(ctx.controlsLocalPos);
            }
            if (!list.isEmpty()) continue;
            iterator.remove();
        }
    }

    public static void receivePressed(LevelAccessor world, AbstractContraptionEntity entity, BlockPos controlsPos, UUID uniqueID, Collection<Integer> collect, boolean pressed) {
        Map map = (Map)receivedInputs.get(world);
        if (map.containsKey(uniqueID) && ((ControlsContext)map.get((Object)uniqueID)).entity != entity) {
            map.remove(uniqueID);
        }
        ControlsContext ctx = map.computeIfAbsent(uniqueID, $ -> new ControlsContext(entity, controlsPos));
        Collection<ManuallyPressedKey> list = ctx.keys;
        block0: for (Integer activated : collect) {
            for (ManuallyPressedKey entry : list) {
                Integer inputType = (Integer)entry.getSecond();
                if (!inputType.equals(activated)) continue;
                if (!pressed) {
                    entry.setFirst(0);
                    continue block0;
                }
                entry.keepAlive();
                continue block0;
            }
            if (!pressed) continue;
            list.add(new ManuallyPressedKey(activated));
        }
    }

    static class ControlsContext {
        Collection<ManuallyPressedKey> keys;
        AbstractContraptionEntity entity;
        BlockPos controlsLocalPos;

        public ControlsContext(AbstractContraptionEntity entity, BlockPos controlsPos) {
            this.entity = entity;
            this.controlsLocalPos = controlsPos;
            this.keys = new ArrayList<ManuallyPressedKey>();
        }
    }

    static class ManuallyPressedKey
    extends IntAttached<Integer> {
        public ManuallyPressedKey(Integer second) {
            super(Integer.valueOf(30), (Object)second);
        }

        public void keepAlive() {
            this.setFirst(30);
        }

        public boolean isAlive() {
            return (Integer)this.getFirst() > 0;
        }
    }
}
