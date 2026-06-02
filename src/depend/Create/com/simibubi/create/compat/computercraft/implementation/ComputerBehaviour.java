/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dan200.computercraft.api.detail.VanillaDetailRegistries
 *  dan200.computercraft.api.peripheral.IPeripheral
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 */
package com.simibubi.create.compat.computercraft.implementation;

import com.simibubi.create.compat.computercraft.AbstractComputerBehaviour;
import com.simibubi.create.compat.computercraft.events.ComputerEvent;
import com.simibubi.create.compat.computercraft.implementation.luaObjects.PackageLuaObject;
import com.simibubi.create.compat.computercraft.implementation.peripherals.CreativeMotorPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.DisplayLinkPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.FrogportPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.NixieTubePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.PackagerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.PostboxPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.RedstoneRequesterPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.RepackagerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SequencedGearshiftPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SignalPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedControllerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SpeedGaugePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StationPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StickerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StockTickerPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.StressGaugePeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.SyncedPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.TableClothShopPeripheral;
import com.simibubi.create.compat.computercraft.implementation.peripherals.TrackObserverPeripheral;
import com.simibubi.create.content.contraptions.chassis.StickerBlockEntity;
import com.simibubi.create.content.kinetics.gauge.SpeedGaugeBlockEntity;
import com.simibubi.create.content.kinetics.gauge.StressGaugeBlockEntity;
import com.simibubi.create.content.kinetics.motor.CreativeMotorBlockEntity;
import com.simibubi.create.content.kinetics.speedController.SpeedControllerBlockEntity;
import com.simibubi.create.content.kinetics.transmission.sequencer.SequencedGearshiftBlockEntity;
import com.simibubi.create.content.logistics.box.PackageItem;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import com.simibubi.create.content.logistics.packagePort.postbox.PostboxBlockEntity;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.packager.repackager.RepackagerBlockEntity;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tableCloth.TableClothBlockEntity;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkBlockEntity;
import com.simibubi.create.content.redstone.nixieTube.NixieTubeBlockEntity;
import com.simibubi.create.content.trains.observer.TrackObserverBlockEntity;
import com.simibubi.create.content.trains.signal.SignalBlockEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import dan200.computercraft.api.detail.VanillaDetailRegistries;
import dan200.computercraft.api.peripheral.IPeripheral;
import java.util.function.Supplier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ComputerBehaviour
extends AbstractComputerBehaviour {
    SyncedPeripheral<?> peripheral;
    Supplier<SyncedPeripheral<?>> peripheralSupplier;
    SmartBlockEntity be;

    public ComputerBehaviour(SmartBlockEntity be) {
        super(be);
        this.peripheralSupplier = ComputerBehaviour.getPeripheralFor(be);
        this.be = be;
    }

    public static Supplier<SyncedPeripheral<?>> getPeripheralFor(SmartBlockEntity be) {
        if (be instanceof SpeedControllerBlockEntity) {
            SpeedControllerBlockEntity scbe = (SpeedControllerBlockEntity)be;
            return () -> new SpeedControllerPeripheral(scbe, scbe.targetSpeed);
        }
        if (be instanceof CreativeMotorBlockEntity) {
            CreativeMotorBlockEntity cmbe = (CreativeMotorBlockEntity)be;
            return () -> new CreativeMotorPeripheral(cmbe, cmbe.generatedSpeed);
        }
        if (be instanceof DisplayLinkBlockEntity) {
            DisplayLinkBlockEntity dlbe = (DisplayLinkBlockEntity)be;
            return () -> new DisplayLinkPeripheral(dlbe);
        }
        if (be instanceof FrogportBlockEntity) {
            FrogportBlockEntity fpbe = (FrogportBlockEntity)be;
            return () -> new FrogportPeripheral(fpbe);
        }
        if (be instanceof PostboxBlockEntity) {
            PostboxBlockEntity pbbe = (PostboxBlockEntity)be;
            return () -> new PostboxPeripheral(pbbe);
        }
        if (be instanceof NixieTubeBlockEntity) {
            NixieTubeBlockEntity ntbe = (NixieTubeBlockEntity)be;
            return () -> new NixieTubePeripheral(ntbe);
        }
        if (be instanceof SequencedGearshiftBlockEntity) {
            SequencedGearshiftBlockEntity sgbe = (SequencedGearshiftBlockEntity)be;
            return () -> new SequencedGearshiftPeripheral(sgbe);
        }
        if (be instanceof SignalBlockEntity) {
            SignalBlockEntity sbe = (SignalBlockEntity)be;
            return () -> new SignalPeripheral(sbe);
        }
        if (be instanceof SpeedGaugeBlockEntity) {
            SpeedGaugeBlockEntity sgbe = (SpeedGaugeBlockEntity)be;
            return () -> new SpeedGaugePeripheral(sgbe);
        }
        if (be instanceof StressGaugeBlockEntity) {
            StressGaugeBlockEntity sgbe = (StressGaugeBlockEntity)be;
            return () -> new StressGaugePeripheral(sgbe);
        }
        if (be instanceof StockTickerBlockEntity) {
            StockTickerBlockEntity sgbe = (StockTickerBlockEntity)be;
            return () -> new StockTickerPeripheral(sgbe);
        }
        if (be instanceof RepackagerBlockEntity) {
            RepackagerBlockEntity rpbe = (RepackagerBlockEntity)be;
            return () -> new RepackagerPeripheral(rpbe);
        }
        if (be instanceof PackagerBlockEntity) {
            PackagerBlockEntity pgbe = (PackagerBlockEntity)be;
            return () -> new PackagerPeripheral(pgbe);
        }
        if (be instanceof RedstoneRequesterBlockEntity) {
            RedstoneRequesterBlockEntity rrbe = (RedstoneRequesterBlockEntity)be;
            return () -> new RedstoneRequesterPeripheral(rrbe);
        }
        if (be instanceof StationBlockEntity) {
            StationBlockEntity sbe = (StationBlockEntity)be;
            return () -> new StationPeripheral(sbe);
        }
        if (be instanceof TableClothBlockEntity) {
            TableClothBlockEntity tcbe = (TableClothBlockEntity)be;
            return () -> new TableClothShopPeripheral(tcbe);
        }
        if (be instanceof StickerBlockEntity) {
            StickerBlockEntity sbe = (StickerBlockEntity)be;
            return () -> new StickerPeripheral(sbe);
        }
        if (be instanceof StationBlockEntity) {
            StationBlockEntity sbe = (StationBlockEntity)be;
            return () -> new StationPeripheral(sbe);
        }
        if (be instanceof TrackObserverBlockEntity) {
            TrackObserverBlockEntity tobe = (TrackObserverBlockEntity)be;
            return () -> new TrackObserverPeripheral(tobe);
        }
        throw new IllegalArgumentException("No peripheral available for " + String.valueOf(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey((Object)be.getType())));
    }

    public static void registerItemDetailProviders() {
        VanillaDetailRegistries.ITEM_STACK.addProvider((out, stack) -> {
            if (PackageItem.isPackage(stack)) {
                PackageLuaObject packageLuaObject = new PackageLuaObject(null, (ItemStack)stack);
                out.put("package", packageLuaObject);
            }
        });
    }

    @Override
    public IPeripheral getPeripheralCapability() {
        if (this.peripheral == null) {
            this.peripheral = this.peripheralSupplier.get();
        }
        return this.peripheral;
    }

    @Override
    public void removePeripheral() {
        if (this.peripheral != null) {
            this.getWorld().invalidateCapabilities(this.be.getBlockPos());
        }
    }

    @Override
    public void prepareComputerEvent(@NotNull ComputerEvent event) {
        if (this.peripheral != null) {
            this.peripheral.prepareComputerEvent(event);
        }
    }
}
