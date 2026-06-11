package com.lycoris.loconautics.allsable;

import com.lycoris.loconautics.core.LoconauticsConstants;

import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * Command harness for the all-Sable trains.
 *
 * <ul>
 *   <li><b>{@code /loconautics derail}</b> — look at a Sable cart and derail it: the car is released from the
 *       rail and becomes a free physics sub-level (see {@link SableTrainSpawner#derailLookedAt}). This is the
 *       manual hook for the derailment mechanic; the driver also derails a car automatically when it loses all
 *       its bogeys.</li>
 * </ul>
 *
 * <p>All the older debug commands (railtest / railtest2 / sabletrain) were removed now that assembly is driven
 * from the train station's GUI.
 */
@EventBusSubscriber(modid = LoconauticsConstants.MOD_ID)
public final class RailDebug {

    private RailDebug() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("loconautics")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("derail")
                        .executes(ctx -> SableTrainSpawner.derailLookedAt(ctx.getSource().getPlayerOrException()))));
    }
}
