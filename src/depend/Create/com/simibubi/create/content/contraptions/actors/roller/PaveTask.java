/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.minecraft.core.BlockPos
 */
package com.simibubi.create.content.contraptions.actors.roller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;

public class PaveTask {
    private Couple<Double> horizontalInterval;
    private Map<Couple<Integer>, Float> heightValues = new HashMap<Couple<Integer>, Float>();

    public PaveTask(double h1, double h2) {
        this.horizontalInterval = Couple.create((Object)h1, (Object)h2);
    }

    public Couple<Double> getHorizontalInterval() {
        return this.horizontalInterval;
    }

    public void put(int x, int z, float y) {
        this.heightValues.put((Couple<Integer>)Couple.create((Object)x, (Object)z), Float.valueOf(y));
    }

    public float get(Couple<Integer> coords) {
        return this.heightValues.get(coords).floatValue();
    }

    public Set<Couple<Integer>> keys() {
        return this.heightValues.keySet();
    }

    public void put(BlockPos p) {
        this.put(p.getX(), p.getZ(), p.getY());
    }
}
