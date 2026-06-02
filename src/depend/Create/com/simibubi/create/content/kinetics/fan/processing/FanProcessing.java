/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 */
package com.simibubi.create.content.kinetics.fan.processing;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.infrastructure.config.AllConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FanProcessing {
    public static boolean canProcess(ItemEntity entity, FanProcessingType type) {
        CompoundTag compound;
        if (entity.getPersistentData().contains("CreateData") && (compound = entity.getPersistentData().getCompound("CreateData")).contains("Processing")) {
            CompoundTag processing = compound.getCompound("Processing");
            if (AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
                return type.canProcess(entity.getItem(), entity.level());
            }
            if (processing.getInt("Time") >= 0) {
                return true;
            }
            if (processing.getInt("Time") == -1) {
                return false;
            }
        }
        return type.canProcess(entity.getItem(), entity.level());
    }

    public static boolean applyProcessing(ItemEntity entity, FanProcessingType type) {
        if (FanProcessing.decrementProcessingTime(entity, type) != 0) {
            return false;
        }
        List<ItemStack> stacks = type.process(entity.getItem(), entity.level());
        if (stacks == null) {
            return false;
        }
        if (stacks.isEmpty()) {
            entity.discard();
            return false;
        }
        entity.setItem(stacks.remove(0));
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity((Entity)entityIn);
        }
        return true;
    }

    public static TransportedItemStackHandlerBehaviour.TransportedResult applyProcessing(TransportedItemStack transported, Level world, FanProcessingType type) {
        TransportedItemStackHandlerBehaviour.TransportedResult ignore = TransportedItemStackHandlerBehaviour.TransportedResult.doNothing();
        if (transported.processedBy != type) {
            int processingTime;
            transported.processedBy = type;
            int timeModifierForStackSize = (transported.stack.getCount() - 1) / 16 + 1;
            transported.processingTime = processingTime = (Integer)AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize + 1;
            if (!type.canProcess(transported.stack, world)) {
                transported.processingTime = -1;
            }
            return ignore;
        }
        if (transported.processingTime == -1) {
            return ignore;
        }
        if (transported.processingTime-- > 0) {
            return ignore;
        }
        List<ItemStack> stacks = type.process(transported.stack, world);
        if (stacks == null) {
            return ignore;
        }
        ArrayList<TransportedItemStack> transportedStacks = new ArrayList<TransportedItemStack>();
        for (ItemStack additional : stacks) {
            TransportedItemStack newTransported = transported.getSimilar();
            newTransported.stack = additional.copy();
            transportedStacks.add(newTransported);
        }
        return TransportedItemStackHandlerBehaviour.TransportedResult.convertTo(transportedStacks);
    }

    private static int decrementProcessingTime(ItemEntity entity, FanProcessingType type) {
        CompoundTag processing;
        CompoundTag createData;
        CompoundTag nbt = entity.getPersistentData();
        if (!nbt.contains("CreateData")) {
            nbt.put("CreateData", (Tag)new CompoundTag());
        }
        if (!(createData = nbt.getCompound("CreateData")).contains("Processing")) {
            createData.put("Processing", (Tag)new CompoundTag());
        }
        if (!(processing = createData.getCompound("Processing")).contains("Type") || AllFanProcessingTypes.parseLegacy(processing.getString("Type")) != type) {
            ResourceLocation key = CreateBuiltInRegistries.FAN_PROCESSING_TYPE.getKey((Object)type);
            if (key == null) {
                throw new IllegalArgumentException("Could not get id for FanProcessingType " + String.valueOf(type) + "!");
            }
            processing.putString("Type", key.toString());
            int timeModifierForStackSize = (entity.getItem().getCount() - 1) / 16 + 1;
            int processingTime = (Integer)AllConfigs.server().kinetics.fanProcessingTime.get() * timeModifierForStackSize + 1;
            processing.putInt("Time", processingTime);
        }
        int value = processing.getInt("Time") - 1;
        processing.putInt("Time", value);
        return value;
    }
}
