/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllSoundEvents
 *  com.simibubi.create.content.equipment.clipboard.ClipboardCloneable
 *  com.simibubi.create.foundation.blockEntity.SmartBlockEntity
 *  com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour
 *  dev.ryanhcode.sable.Sable
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.core.Position
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.entity.BlockEntityType
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.equipment.clipboard.ClipboardCloneable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import dev.ryanhcode.sable.Sable;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlock;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LinkedTypewriterBlockEntity
extends SmartBlockEntity
implements MenuProvider,
ClipboardCloneable {
    private LinkedTypewriterEntries entryMap;
    private final List<Integer> pressedKeys = new ArrayList<Integer>();
    private UUID currentUser;
    private String typedEntry = "";
    public boolean powered;

    public LinkedTypewriterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.entryMap = new LinkedTypewriterEntries();
    }

    public void addBehaviours(List<BlockEntityBehaviour> list) {
    }

    public void tick() {
        super.tick();
        assert (this.level != null);
        this.entryMap.updateNetworks(this.level);
        if (!this.level.isClientSide) {
            Player currentPlayer;
            if ((Boolean)this.getBlockState().getValue((Property)LinkedTypewriterBlock.POWERED) != this.powered) {
                this.level.setBlockAndUpdate(this.getBlockPos(), (BlockState)this.getBlockState().setValue((Property)LinkedTypewriterBlock.POWERED, (Comparable)Boolean.valueOf(this.powered)));
            }
            if (!(this.currentUser == null || (currentPlayer = this.level.getPlayerByUUID(this.currentUser)) != null && LinkedTypewriterBlockEntity.playerInRange(currentPlayer, this.level, this.getBlockPos()))) {
                this.disconnectUser();
            }
        }
    }

    public static boolean playerInRange(Player player, Level world, BlockPos pos) {
        double range = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();
        return Sable.HELPER.distanceSquaredWithSubLevels(world, (Position)player.getEyePosition(), (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) < range * range;
    }

    public LinkedTypewriterEntries getTypewriterEntries() {
        return this.entryMap;
    }

    public boolean checkAndStartUsing(UUID userID) {
        PlayerTypewriterExtension player;
        if (this.currentUser == null && (player = (PlayerTypewriterExtension)this.level.getPlayerByUUID(userID)) != null) {
            LinkedTypewriterBlockEntity nbe;
            BlockEntity blockEntity;
            this.currentUser = userID;
            BlockPos previousTypewriter = player.simulated$getCurrentTypewriter();
            if (previousTypewriter != null && (blockEntity = this.level.getBlockEntity(previousTypewriter)) instanceof LinkedTypewriterBlockEntity && (nbe = (LinkedTypewriterBlockEntity)blockEntity) != this) {
                nbe.disconnectUser();
            }
            this.powered = true;
            player.simulated$setCurrentTypewriter(this.getBlockPos());
            if (this.level.isClientSide) {
                LinkedTypewriterInteractionHandler.associateTypewriter(this);
            } else {
                this.level.playSound(null, this.worldPosition, AllSoundEvents.CONTROLLER_PUT.getMainEvent(), SoundSource.BLOCKS, 1.0f, 0.95f + 0.1f * this.level.getRandom().nextFloat());
            }
            return true;
        }
        return false;
    }

    public boolean checkUser(UUID user) {
        return user.equals(this.currentUser);
    }

    public void disconnectUser() {
        this.powered = false;
        this.currentUser = null;
        if (!this.level.isClientSide) {
            this.pressedKeys.clear();
            this.entryMap.deactivateAll();
            this.setChanged();
            this.sendData();
            this.level.playSound(null, this.worldPosition, SimSoundEvents.LINKED_TYPEWRITER_DING.event(), SoundSource.BLOCKS, 1.0f, 0.95f + 0.1f * this.level.getRandom().nextFloat());
        } else {
            LinkedTypewriterInteractionHandler.associateTypewriter(null);
        }
    }

    public List<Integer> getPressedKeys() {
        return this.pressedKeys;
    }

    public void onKeyInteraction(UUID user, @Nullable LinkedTypewriterEntries.KeyboardEntry toBind, int key, boolean press) {
        if (!this.checkUser(user)) {
            return;
        }
        if (press && toBind != null) {
            this.entryMap.setKey(key, toBind);
            return;
        }
        if (press) {
            this.pressKey(key);
        } else {
            this.releaseKey(key);
        }
    }

    public void pressKey(int key) {
        this.pressedKeys.add(key);
        if (key == 259) {
            if (!this.typedEntry.isEmpty()) {
                this.typedEntry = this.typedEntry.substring(0, this.typedEntry.length() - 1);
            }
        } else {
            this.typedEntry = this.typedEntry + (char)key;
        }
        if (this.typedEntry.length() >= 25) {
            this.typedEntry = this.typedEntry.substring(1);
        }
        this.entryMap.activateKey(key, this);
    }

    public void releaseKey(int key) {
        this.pressedKeys.remove((Object)key);
        this.entryMap.deactivateKey(key);
    }

    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putString("typedEntry", this.typedEntry);
        tag.put("Keys", (Tag)this.entryMap.saveKeys(registries));
        if (this.currentUser != null) {
            tag.putUUID("CurrentUser", this.currentUser);
        }
    }

    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        this.typedEntry = tag.getString("typedEntry");
        this.entryMap = LinkedTypewriterEntries.readKeys(registries, tag.getList("Keys", 10), this.getBlockPos());
        this.currentUser = tag.contains("CurrentUser") ? tag.getUUID("CurrentUser") : null;
    }

    public String getTypedEntry() {
        return this.typedEntry;
    }

    public void invalidate() {
        this.pressedKeys.clear();
        this.entryMap.deactivateAll();
        this.entryMap.updateNetworks(this.level);
        super.invalidate();
    }

    public void destroy() {
        this.pressedKeys.clear();
        this.entryMap.deactivateAll();
        this.entryMap.updateNetworks(this.level);
        super.destroy();
    }

    @Nullable
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return LinkedTypewriterMenuCommon.create(id, inventory, this);
    }

    public Component getDisplayName() {
        return ((LinkedTypewriterBlock)((Object)SimBlocks.LINKED_TYPEWRITER.get())).getName();
    }

    public String getClipboardKey() {
        return "TypewriterKeys";
    }

    public boolean writeToClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Direction side) {
        tag.put("Keys", (Tag)this.entryMap.saveKeys(registries));
        return true;
    }

    public boolean readFromClipboard(// Could not load outer class - annotation placement on inner may be incorrect
     @NotNull HolderLookup.Provider registries, CompoundTag tag, Player player, Direction side, boolean simulate) {
        if (simulate) {
            return true;
        }
        this.entryMap = LinkedTypewriterEntries.readKeys(registries, tag.getList("Keys", 10), this.getBlockPos());
        return true;
    }
}
