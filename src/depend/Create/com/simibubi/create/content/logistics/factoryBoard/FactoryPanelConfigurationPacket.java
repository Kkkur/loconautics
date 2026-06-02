/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs
 *  net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders
 *  net.createmod.catnip.net.base.BasePacketPayload$PacketTypeProvider
 *  net.minecraft.network.RegistryFriendlyByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  org.jetbrains.annotations.Nullable
 */
package com.simibubi.create.content.logistics.factoryBoard;

import com.simibubi.create.AllPackets;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBlockEntity;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelConnection;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelPosition;
import com.simibubi.create.foundation.networking.BlockEntityConfigurationPacket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.createmod.catnip.codecs.stream.CatnipLargerStreamCodecs;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.createmod.catnip.net.base.BasePacketPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import org.jetbrains.annotations.Nullable;

public class FactoryPanelConfigurationPacket
extends BlockEntityConfigurationPacket<FactoryPanelBlockEntity> {
    public static final StreamCodec<RegistryFriendlyByteBuf, FactoryPanelConfigurationPacket> STREAM_CODEC = CatnipLargerStreamCodecs.composite(FactoryPanelPosition.STREAM_CODEC, packet -> packet.position, (StreamCodec)ByteBufCodecs.STRING_UTF8, packet -> packet.address, (StreamCodec)ByteBufCodecs.map(HashMap::new, FactoryPanelPosition.STREAM_CODEC, (StreamCodec)ByteBufCodecs.INT), packet -> packet.inputAmounts, (StreamCodec)ItemStack.OPTIONAL_LIST_STREAM_CODEC, packet -> packet.craftingArrangement, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.outputAmount, (StreamCodec)ByteBufCodecs.VAR_INT, packet -> packet.promiseClearingInterval, (StreamCodec)CatnipStreamCodecBuilders.nullable(FactoryPanelPosition.STREAM_CODEC), packet -> packet.removeConnection, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.clearPromises, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.reset, (StreamCodec)ByteBufCodecs.BOOL, packet -> packet.redstoneReset, FactoryPanelConfigurationPacket::new);
    private final FactoryPanelPosition position;
    private final String address;
    private final Map<FactoryPanelPosition, Integer> inputAmounts;
    private final List<ItemStack> craftingArrangement;
    private final int outputAmount;
    private final int promiseClearingInterval;
    private final FactoryPanelPosition removeConnection;
    private final boolean clearPromises;
    private final boolean reset;
    private final boolean redstoneReset;

    public FactoryPanelConfigurationPacket(FactoryPanelPosition position, String address, Map<FactoryPanelPosition, Integer> inputAmounts, List<ItemStack> craftingArrangement, int outputAmount, int promiseClearingInterval, @Nullable FactoryPanelPosition removeConnection, boolean clearPromises, boolean reset, boolean sendRedstoneReset) {
        super(position.pos());
        this.position = position;
        this.address = address;
        this.inputAmounts = inputAmounts;
        this.craftingArrangement = craftingArrangement;
        this.outputAmount = outputAmount;
        this.promiseClearingInterval = promiseClearingInterval;
        this.removeConnection = removeConnection;
        this.clearPromises = clearPromises;
        this.reset = reset;
        this.redstoneReset = sendRedstoneReset;
    }

    public BasePacketPayload.PacketTypeProvider getTypeProvider() {
        return AllPackets.CONFIGURE_FACTORY_PANEL;
    }

    @Override
    protected void applySettings(ServerPlayer player, FactoryPanelBlockEntity be) {
        FactoryPanelBehaviour behaviour = be.panels.get((Object)this.position.slot());
        if (behaviour == null) {
            return;
        }
        behaviour.recipeAddress = this.reset ? "" : this.address;
        behaviour.recipeOutput = this.reset ? 1 : this.outputAmount;
        behaviour.promiseClearingInterval = this.reset ? -1 : this.promiseClearingInterval;
        List<Object> list = behaviour.activeCraftingArrangement = this.reset ? List.of() : this.craftingArrangement;
        if (this.reset) {
            behaviour.forceClearPromises = true;
            behaviour.disconnectAll();
            behaviour.setFilter(ItemStack.EMPTY);
            behaviour.count = 0;
            be.redraw = true;
            be.notifyUpdate();
            return;
        }
        if (this.redstoneReset) {
            behaviour.disconnectAllLinks();
            be.notifyUpdate();
            return;
        }
        for (Map.Entry<FactoryPanelPosition, Integer> entry : this.inputAmounts.entrySet()) {
            FactoryPanelPosition key = entry.getKey();
            FactoryPanelConnection connection = behaviour.targetedBy.get(key);
            if (connection == null) continue;
            connection.amount = entry.getValue();
        }
        if (this.removeConnection != null) {
            behaviour.targetedBy.remove(this.removeConnection);
            FactoryPanelBehaviour source = FactoryPanelBehaviour.at((BlockAndTintGetter)be.getLevel(), this.removeConnection);
            if (source != null) {
                source.targeting.remove(behaviour.getPanelPosition());
                source.blockEntity.sendData();
            }
        }
        if (this.clearPromises) {
            behaviour.forceClearPromises = true;
        }
        be.notifyUpdate();
    }
}
