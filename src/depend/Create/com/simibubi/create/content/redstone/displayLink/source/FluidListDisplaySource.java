/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.IntAttached
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.material.Fluid
 *  net.neoforged.neoforge.fluids.FluidStack
 *  net.neoforged.neoforge.fluids.capability.IFluidHandler
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package com.simibubi.create.content.redstone.displayLink.source;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.ValueListDisplaySource;
import com.simibubi.create.content.redstone.smartObserver.SmartObserverBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayBlockEntity;
import com.simibubi.create.content.trains.display.FlapDisplayLayout;
import com.simibubi.create.content.trains.display.FlapDisplaySection;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.inventory.TankManipulationBehaviour;
import com.simibubi.create.foundation.utility.FluidFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.IntAttached;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.mutable.MutableInt;

public class FluidListDisplaySource
extends ValueListDisplaySource {
    @Override
    protected Stream<IntAttached<MutableComponent>> provideEntries(DisplayLinkContext context, int maxRows) {
        BlockEntity sourceBE = context.getSourceBlockEntity();
        if (!(sourceBE instanceof SmartObserverBlockEntity)) {
            return Stream.empty();
        }
        SmartObserverBlockEntity cobe = (SmartObserverBlockEntity)sourceBE;
        TankManipulationBehaviour tankManipulationBehaviour = cobe.getBehaviour(TankManipulationBehaviour.OBSERVE);
        FilteringBehaviour filteringBehaviour = cobe.getBehaviour(FilteringBehaviour.TYPE);
        IFluidHandler handler = (IFluidHandler)tankManipulationBehaviour.getInventory();
        if (handler == null) {
            return Stream.empty();
        }
        HashMap<Fluid, Integer> fluids = new HashMap<Fluid, Integer>();
        HashMap<Fluid, FluidStack> fluidNames = new HashMap<Fluid, FluidStack>();
        for (int i = 0; i < handler.getTanks(); ++i) {
            FluidStack stack = handler.getFluidInTank(i);
            if (stack.isEmpty() || !filteringBehaviour.test(stack)) continue;
            fluids.merge(stack.getFluid(), stack.getAmount(), Integer::sum);
            fluidNames.putIfAbsent(stack.getFluid(), stack);
        }
        return fluids.entrySet().stream().sorted(Comparator.comparingInt(value -> (Integer)value.getValue()).reversed()).limit(maxRows).map(entry -> IntAttached.with((int)((Integer)entry.getValue()), (Object)((FluidStack)fluidNames.get(entry.getKey())).getHoverName().copy()));
    }

    @Override
    protected List<MutableComponent> createComponentsFromEntry(DisplayLinkContext context, IntAttached<MutableComponent> entry) {
        int amount = (Integer)entry.getFirst();
        MutableComponent name = ((MutableComponent)entry.getSecond()).append((Component)WHITESPACE);
        Couple<MutableComponent> formatted = FluidFormatter.asComponents(amount, this.shortenNumbers(context));
        return List.of((MutableComponent)formatted.getFirst(), (MutableComponent)formatted.getSecond(), name);
    }

    @Override
    public void loadFlapDisplayLayout(DisplayLinkContext context, FlapDisplayBlockEntity flapDisplay, FlapDisplayLayout layout) {
        Integer max = ((MutableInt)context.flapDisplayContext).getValue();
        boolean shorten = this.shortenNumbers(context);
        int length = FluidFormatter.asString(max.intValue(), shorten).length();
        String layoutKey = "FluidList_" + length;
        if (layout.isLayout(layoutKey)) {
            return;
        }
        int maxCharCount = flapDisplay.getMaxCharCount(1);
        int numberLength = Math.min(maxCharCount, Math.max(3, length - 2));
        int nameLength = Math.max(maxCharCount - numberLength - 2, 0);
        FlapDisplaySection value = new FlapDisplaySection(7.0f * (float)numberLength, "number", false, false).rightAligned();
        FlapDisplaySection unit = new FlapDisplaySection(14.0f, "fluid_units", true, true);
        FlapDisplaySection name = new FlapDisplaySection(7.0f * (float)nameLength, "alphabet", false, false);
        layout.configure(layoutKey, List.of(value, unit, name));
    }

    @Override
    protected String getTranslationKey() {
        return "list_fluids";
    }

    @Override
    protected boolean valueFirst() {
        return false;
    }
}
