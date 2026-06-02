/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.MutableComponent
 */
package com.simibubi.create.content.trains.display;

import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.network.chat.MutableComponent;

public static class GlobalTrainDisplayData.TrainDeparturePrediction
implements Comparable<GlobalTrainDisplayData.TrainDeparturePrediction> {
    public Train train;
    public int ticks;
    public MutableComponent scheduleTitle;
    public String destination;

    public GlobalTrainDisplayData.TrainDeparturePrediction(Train train, int ticks, MutableComponent scheduleTitle, String destination) {
        this.scheduleTitle = scheduleTitle;
        this.destination = destination;
        this.train = train;
        this.ticks = ticks;
    }

    private int getCompareTicks() {
        if (this.ticks == -1) {
            return Integer.MAX_VALUE;
        }
        if (this.ticks < 200) {
            return 0;
        }
        return this.ticks;
    }

    @Override
    public int compareTo(GlobalTrainDisplayData.TrainDeparturePrediction o) {
        int compare = Integer.compare(this.getCompareTicks(), o.getCompareTicks());
        if (compare == 0) {
            return this.train.name.getString().compareTo(o.train.name.getString());
        }
        return compare;
    }
}
