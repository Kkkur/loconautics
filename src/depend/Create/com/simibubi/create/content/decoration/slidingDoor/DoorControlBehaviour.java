/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.nbt.NBTHelper
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 */
package com.simibubi.create.content.decoration.slidingDoor;

import com.simibubi.create.content.decoration.slidingDoor.DoorControl;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public class DoorControlBehaviour
extends BlockEntityBehaviour {
    public static final BehaviourType<DoorControlBehaviour> TYPE = new BehaviourType();
    public DoorControl mode = DoorControl.ALL;

    public DoorControlBehaviour(SmartBlockEntity be) {
        super(be);
    }

    public void set(DoorControl mode) {
        if (this.mode == mode) {
            return;
        }
        this.mode = mode;
        this.blockEntity.notifyUpdate();
    }

    @Override
    public void write(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        NBTHelper.writeEnum((CompoundTag)nbt, (String)"DoorControl", (Enum)this.mode);
        super.write(nbt, registries, clientPacket);
    }

    @Override
    public void read(CompoundTag nbt, HolderLookup.Provider registries, boolean clientPacket) {
        this.mode = (DoorControl)NBTHelper.readEnum((CompoundTag)nbt, (String)"DoorControl", DoorControl.class);
        super.read(nbt, registries, clientPacket);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
