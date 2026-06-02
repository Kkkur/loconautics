/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 */
package com.simibubi.create.foundation.blockEntity.behaviour.animatedContainer;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.gui.menu.MenuBase;
import java.util.function.Consumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class AnimatedContainerBehaviour<M extends MenuBase<? extends SmartBlockEntity>>
extends BlockEntityBehaviour {
    public static final BehaviourType<AnimatedContainerBehaviour<?>> TYPE = new BehaviourType();
    public int openCount;
    private Class<M> menuClass;
    private Consumer<Boolean> openChanged;

    public AnimatedContainerBehaviour(SmartBlockEntity be, Class<M> menuClass) {
        super(be);
        this.menuClass = menuClass;
        this.openCount = 0;
    }

    public void onOpenChanged(Consumer<Boolean> openChanged) {
        this.openChanged = openChanged;
    }

    @Override
    public void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(compound, registries, clientPacket);
        if (clientPacket) {
            this.openCount = compound.getInt("OpenCount");
        }
    }

    @Override
    public void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(compound, registries, clientPacket);
        if (clientPacket) {
            compound.putInt("OpenCount", this.openCount);
        }
    }

    @Override
    public void lazyTick() {
        this.updateOpenCount();
        super.lazyTick();
    }

    void updateOpenCount() {
        Level level = this.getWorld();
        if (level.isClientSide) {
            return;
        }
        if (this.openCount == 0) {
            return;
        }
        int prevOpenCount = this.openCount;
        this.openCount = 0;
        for (Player playerentity : level.getEntitiesOfClass(Player.class, new AABB(this.getPos()).inflate(8.0))) {
            if (!this.menuClass.isInstance(playerentity.containerMenu) || ((MenuBase)((Object)this.menuClass.cast((Object)playerentity.containerMenu))).contentHolder != this.blockEntity) continue;
            ++this.openCount;
        }
        if (prevOpenCount != this.openCount) {
            if (this.openChanged != null && prevOpenCount == 0 && this.openCount > 0) {
                this.openChanged.accept(true);
            }
            if (this.openChanged != null && prevOpenCount > 0 && this.openCount == 0) {
                this.openChanged.accept(false);
            }
            this.blockEntity.sendData();
        }
    }

    public void startOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        if (this.getWorld().isClientSide) {
            return;
        }
        if (this.openCount < 0) {
            this.openCount = 0;
        }
        ++this.openCount;
        if (this.openCount == 1 && this.openChanged != null) {
            this.openChanged.accept(true);
        }
        this.blockEntity.sendData();
    }

    public void stopOpen(Player player) {
        if (player.isSpectator()) {
            return;
        }
        if (this.getWorld().isClientSide) {
            return;
        }
        --this.openCount;
        if (this.openCount == 0 && this.openChanged != null) {
            this.openChanged.accept(false);
        }
        this.blockEntity.sendData();
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }
}
