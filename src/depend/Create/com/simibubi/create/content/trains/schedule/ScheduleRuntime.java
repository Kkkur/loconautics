/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.Mth
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.trains.schedule;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.trains.display.GlobalTrainDisplayData;
import com.simibubi.create.content.trains.entity.Carriage;
import com.simibubi.create.content.trains.entity.Train;
import com.simibubi.create.content.trains.graph.DiscoveredPath;
import com.simibubi.create.content.trains.schedule.Schedule;
import com.simibubi.create.content.trains.schedule.ScheduleEntry;
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition;
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay;
import com.simibubi.create.content.trains.schedule.destination.ChangeTitleInstruction;
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction;
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction;
import com.simibubi.create.content.trains.station.GlobalStation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ScheduleRuntime {
    private static final int TBD = -1;
    private static final int INVALID = -2;
    public Train train;
    public Schedule schedule;
    public boolean isAutoSchedule;
    public boolean paused;
    public boolean completed;
    public int currentEntry;
    public State state;
    public List<Integer> conditionProgress;
    public List<CompoundTag> conditionContext;
    public String currentTitle;
    public int ticksInTransit;
    public List<Integer> predictionTicks;
    public boolean displayLinkUpdateRequested;
    private static final int INTERVAL = 40;
    private int cooldown;

    public ScheduleRuntime(Train train) {
        this.train = train;
        this.reset();
    }

    public void startCooldown() {
        this.cooldown = 40;
    }

    public void destinationReached() {
        if (this.state != State.IN_TRANSIT) {
            return;
        }
        this.state = State.POST_TRANSIT;
        this.conditionProgress.clear();
        this.conditionContext.clear();
        this.displayLinkUpdateRequested = true;
        for (Carriage carriage : this.train.carriages) {
            carriage.storage.resetIdleCargoTracker();
        }
        if (this.ticksInTransit > 0) {
            int current = this.predictionTicks.get(this.currentEntry);
            if (current > 0) {
                this.ticksInTransit = (current + this.ticksInTransit) / 2;
            }
            this.predictionTicks.set(this.currentEntry, this.ticksInTransit);
        }
        if (this.currentEntry >= this.schedule.entries.size()) {
            return;
        }
        List<List<ScheduleWaitCondition>> conditions = this.schedule.entries.get((int)this.currentEntry).conditions;
        for (int i = 0; i < conditions.size(); ++i) {
            this.conditionProgress.add(0);
            this.conditionContext.add(new CompoundTag());
        }
    }

    public void transitInterrupted() {
        if (this.schedule == null || this.state != State.IN_TRANSIT) {
            return;
        }
        this.state = State.PRE_TRANSIT;
        this.cooldown = 0;
    }

    public void tick(Level level) {
        if (this.schedule == null) {
            return;
        }
        if (this.paused) {
            return;
        }
        if (this.train.derailed) {
            return;
        }
        if (this.train.navigation.destination != null) {
            ++this.ticksInTransit;
            return;
        }
        if (this.checkEndOfScheduleReached()) {
            return;
        }
        if (this.cooldown-- > 0) {
            return;
        }
        if (this.state == State.IN_TRANSIT) {
            return;
        }
        if (this.state == State.POST_TRANSIT) {
            this.tickConditions(level);
            return;
        }
        DiscoveredPath nextPath = this.startCurrentInstruction(level);
        if (nextPath == null) {
            return;
        }
        this.train.status.successfulNavigation();
        if (nextPath.destination == this.train.getCurrentStation()) {
            this.state = State.IN_TRANSIT;
            this.destinationReached();
            return;
        }
        if (this.train.navigation.startNavigation(nextPath) != -1.0) {
            this.state = State.IN_TRANSIT;
            this.ticksInTransit = 0;
        }
    }

    private boolean checkEndOfScheduleReached() {
        if (this.currentEntry < this.schedule.entries.size()) {
            return false;
        }
        this.currentEntry = 0;
        if (!this.schedule.cyclic) {
            this.paused = true;
            this.completed = true;
        }
        return true;
    }

    public void tickConditions(Level level) {
        ScheduleEntry entry = this.schedule.entries.get(this.currentEntry);
        List<List<ScheduleWaitCondition>> conditions = entry.conditions;
        if (!entry.instruction.supportsConditions()) {
            this.state = State.PRE_TRANSIT;
            ++this.currentEntry;
            return;
        }
        for (int i = 0; i < conditions.size(); ++i) {
            List<ScheduleWaitCondition> list = conditions.get(i);
            int progress = this.conditionProgress.get(i);
            if (progress >= list.size()) {
                this.state = State.PRE_TRANSIT;
                ++this.currentEntry;
                return;
            }
            CompoundTag tag = this.conditionContext.get(i);
            ScheduleWaitCondition condition = list.get(progress);
            int prevVersion = tag.getInt("StatusVersion");
            if (condition.tickCompletion(level, this.train, tag)) {
                this.conditionContext.set(i, new CompoundTag());
                this.conditionProgress.set(i, progress + 1);
                this.displayLinkUpdateRequested |= i == 0;
            }
            this.displayLinkUpdateRequested |= i == 0 && prevVersion != tag.getInt("StatusVersion");
        }
        for (Carriage carriage : this.train.carriages) {
            carriage.storage.tickIdleCargoTracker();
        }
    }

    public DiscoveredPath startCurrentInstruction(Level level) {
        if (this.checkEndOfScheduleReached()) {
            return null;
        }
        ScheduleEntry entry = this.schedule.entries.get(this.currentEntry);
        ScheduleInstruction instruction = entry.instruction;
        return instruction.start(this, level);
    }

    public void setSchedule(Schedule schedule, boolean auto) {
        this.reset();
        this.schedule = schedule;
        this.currentEntry = Mth.clamp((int)schedule.savedProgress, (int)0, (int)(schedule.entries.size() - 1));
        this.paused = false;
        this.isAutoSchedule = auto;
        this.train.status.newSchedule();
        this.predictionTicks = new ArrayList<Integer>();
        schedule.entries.forEach($ -> this.predictionTicks.add(-1));
        this.displayLinkUpdateRequested = true;
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void discardSchedule() {
        this.train.navigation.cancelNavigation();
        this.reset();
    }

    private void reset() {
        this.paused = true;
        this.completed = false;
        this.isAutoSchedule = false;
        this.currentEntry = 0;
        this.currentTitle = "";
        this.schedule = null;
        this.state = State.PRE_TRANSIT;
        this.conditionProgress = new ArrayList<Integer>();
        this.conditionContext = new ArrayList<CompoundTag>();
        this.predictionTicks = new ArrayList<Integer>();
    }

    public Collection<GlobalTrainDisplayData.TrainDeparturePrediction> submitPredictions() {
        int index;
        ArrayList<GlobalTrainDisplayData.TrainDeparturePrediction> predictions = new ArrayList<GlobalTrainDisplayData.TrainDeparturePrediction>();
        int entryCount = this.schedule.entries.size();
        int accumulatedTime = 0;
        int current = this.currentEntry;
        if (this.state == State.POST_TRANSIT || current >= entryCount) {
            int departureTime;
            GlobalStation currentStation = this.train.getCurrentStation();
            if (currentStation != null) {
                predictions.add(this.createPrediction(current, currentStation.name, this.currentTitle, 0));
            }
            accumulatedTime = (departureTime = this.estimateStayDuration(current)) == -2 ? -2 : (accumulatedTime += departureTime);
        } else {
            GlobalStation destination = this.train.navigation.destination;
            if (destination != null) {
                float predictedTime;
                double speed = Math.min(this.train.throttle * (double)this.train.maxSpeed(), (double)((this.train.maxSpeed() + this.train.maxTurnSpeed()) / 2.0f));
                int timeRemaining = (int)(this.train.navigation.distanceToDestination / speed) * 2;
                if (this.predictionTicks.size() > current && this.train.navigation.distanceStartedAt != 0.0 && (predictedTime = (float)this.predictionTicks.get(current).intValue()) > 0.0f) {
                    predictedTime = (float)((double)predictedTime * Mth.clamp((double)(this.train.navigation.distanceToDestination / this.train.navigation.distanceStartedAt), (double)0.0, (double)1.0));
                    timeRemaining = (timeRemaining + (int)predictedTime) / 2;
                }
                predictions.add(this.createPrediction(current, destination.name, this.currentTitle, accumulatedTime += timeRemaining));
                int departureTime = this.estimateStayDuration(current);
                accumulatedTime = departureTime != -2 ? (accumulatedTime += departureTime) : -2;
            } else {
                this.predictForEntry(current, this.currentTitle, accumulatedTime, predictions);
            }
        }
        String currentTitle = this.currentTitle;
        for (int i = 1; i < entryCount && ((index = (i + current) % entryCount) != 0 || this.schedule.cyclic); ++i) {
            ScheduleInstruction scheduleInstruction = this.schedule.entries.get((int)index).instruction;
            if (scheduleInstruction instanceof ChangeTitleInstruction) {
                ChangeTitleInstruction title = (ChangeTitleInstruction)scheduleInstruction;
                currentTitle = title.getScheduleTitle();
                continue;
            }
            accumulatedTime = this.predictForEntry(index, currentTitle, accumulatedTime, predictions);
        }
        predictions.removeIf(Objects::isNull);
        return predictions;
    }

    private int predictForEntry(int index, String currentTitle, int accumulatedTime, Collection<GlobalTrainDisplayData.TrainDeparturePrediction> predictions) {
        ScheduleEntry entry = this.schedule.entries.get(index);
        ScheduleInstruction scheduleInstruction = entry.instruction;
        if (!(scheduleInstruction instanceof DestinationInstruction)) {
            return accumulatedTime;
        }
        DestinationInstruction filter = (DestinationInstruction)scheduleInstruction;
        if (this.predictionTicks.size() <= this.currentEntry) {
            return accumulatedTime;
        }
        int departureTime = this.estimateStayDuration(index);
        if (accumulatedTime < 0) {
            predictions.add(this.createPrediction(index, filter.getFilter(), currentTitle, accumulatedTime));
            return Math.min(accumulatedTime, departureTime);
        }
        int predictedTime = this.predictionTicks.get(index);
        accumulatedTime += predictedTime;
        if (predictedTime == -1) {
            accumulatedTime = -1;
        }
        predictions.add(this.createPrediction(index, filter.getFilter(), currentTitle, accumulatedTime));
        if (accumulatedTime != -1) {
            accumulatedTime += departureTime;
        }
        if (departureTime == -2) {
            accumulatedTime = -2;
        }
        return accumulatedTime;
    }

    private int estimateStayDuration(int index) {
        if (index >= this.schedule.entries.size()) {
            if (!this.schedule.cyclic) {
                return -2;
            }
            index = 0;
        }
        ScheduleEntry scheduleEntry = this.schedule.entries.get(index);
        block0: for (List<ScheduleWaitCondition> list : scheduleEntry.conditions) {
            int total = 0;
            for (ScheduleWaitCondition condition : list) {
                if (!(condition instanceof ScheduledDelay)) continue block0;
                ScheduledDelay wait = (ScheduledDelay)condition;
                total += wait.totalWaitTicks();
            }
            return total;
        }
        return -2;
    }

    private GlobalTrainDisplayData.TrainDeparturePrediction createPrediction(int index, String destination, String currentTitle, int time) {
        String text;
        if (time == -2) {
            return null;
        }
        int size = this.schedule.entries.size();
        if (index >= size) {
            if (!this.schedule.cyclic) {
                return new GlobalTrainDisplayData.TrainDeparturePrediction(this.train, time, CommonComponents.space(), destination);
            }
            index %= size;
        }
        if ((text = currentTitle).isBlank()) {
            for (int i = 1; i < size; ++i) {
                int j = (index + i) % size;
                ScheduleEntry scheduleEntry = this.schedule.entries.get(j);
                ScheduleInstruction scheduleInstruction = scheduleEntry.instruction;
                if (!(scheduleInstruction instanceof DestinationInstruction)) continue;
                DestinationInstruction instruction = (DestinationInstruction)scheduleInstruction;
                text = instruction.getFilter().replaceAll("\\*", "").trim();
                break;
            }
        }
        return new GlobalTrainDisplayData.TrainDeparturePrediction(this.train, time, Component.literal((String)text), destination);
    }

    public CompoundTag write(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("CurrentEntry", this.currentEntry);
        tag.putBoolean("AutoSchedule", this.isAutoSchedule);
        tag.putBoolean("Paused", this.paused);
        tag.putBoolean("Completed", this.completed);
        if (this.schedule != null) {
            tag.put("Schedule", (Tag)this.schedule.write(registries));
        }
        NBTHelper.writeEnum((CompoundTag)tag, (String)"State", (Enum)this.state);
        tag.putIntArray("ConditionProgress", this.conditionProgress);
        tag.put("ConditionContext", (Tag)NBTHelper.writeCompoundList(this.conditionContext, CompoundTag::copy));
        tag.putIntArray("TransitTimes", this.predictionTicks);
        return tag;
    }

    public void read(HolderLookup.Provider registries, CompoundTag tag) {
        this.reset();
        this.paused = tag.getBoolean("Paused");
        this.completed = tag.getBoolean("Completed");
        this.isAutoSchedule = tag.getBoolean("AutoSchedule");
        this.currentEntry = Math.max(0, tag.getInt("CurrentEntry"));
        if (tag.contains("Schedule")) {
            this.schedule = Schedule.fromTag(registries, tag.getCompound("Schedule"));
        }
        this.state = (State)NBTHelper.readEnum((CompoundTag)tag, (String)"State", State.class);
        for (int i : tag.getIntArray("ConditionProgress")) {
            this.conditionProgress.add(i);
        }
        NBTHelper.iterateCompoundList((ListTag)tag.getList("ConditionContext", 10), this.conditionContext::add);
        int[] readTransits = tag.getIntArray("TransitTimes");
        if (this.schedule != null) {
            this.schedule.entries.forEach($ -> this.predictionTicks.add(-1));
            if (readTransits.length == this.schedule.entries.size()) {
                for (int i = 0; i < readTransits.length; ++i) {
                    this.predictionTicks.set(i, readTransits[i]);
                }
            }
        }
    }

    public ItemStack returnSchedule(HolderLookup.Provider registries) {
        if (this.schedule == null) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = AllItems.SCHEDULE.asStack();
        this.schedule.savedProgress = this.currentEntry;
        stack.set(AllDataComponents.TRAIN_SCHEDULE, (Object)this.schedule.write(registries));
        stack = this.isAutoSchedule ? ItemStack.EMPTY : stack;
        this.discardSchedule();
        return stack;
    }

    public void setSchedulePresentClientside(boolean present) {
        this.schedule = present ? new Schedule() : null;
    }

    public MutableComponent getWaitingStatus(Level level) {
        List<List<ScheduleWaitCondition>> conditions = this.schedule.entries.get((int)this.currentEntry).conditions;
        if (conditions.isEmpty() || this.conditionProgress.isEmpty() || this.conditionContext.isEmpty()) {
            return Component.empty();
        }
        List<ScheduleWaitCondition> list = conditions.get(0);
        int progress = this.conditionProgress.get(0);
        if (progress >= list.size()) {
            return Component.empty();
        }
        CompoundTag tag = this.conditionContext.get(0);
        ScheduleWaitCondition condition = list.get(progress);
        return condition.getWaitingStatus(level, this.train, tag);
    }

    public static enum State {
        PRE_TRANSIT,
        IN_TRANSIT,
        POST_TRANSIT;

    }
}
