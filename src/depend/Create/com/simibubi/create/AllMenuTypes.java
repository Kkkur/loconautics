/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.tterrag.registrate.builders.MenuBuilder$ForgeMenuFactory
 *  com.tterrag.registrate.builders.MenuBuilder$ScreenFactory
 *  com.tterrag.registrate.util.entry.MenuEntry
 *  com.tterrag.registrate.util.nullness.NonNullSupplier
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package com.simibubi.create;

import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.blueprint.BlueprintMenu;
import com.simibubi.create.content.equipment.blueprint.BlueprintScreen;
import com.simibubi.create.content.equipment.toolbox.ToolboxMenu;
import com.simibubi.create.content.equipment.toolbox.ToolboxScreen;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemMenu;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelSetItemScreen;
import com.simibubi.create.content.logistics.filter.AttributeFilterMenu;
import com.simibubi.create.content.logistics.filter.AttributeFilterScreen;
import com.simibubi.create.content.logistics.filter.FilterMenu;
import com.simibubi.create.content.logistics.filter.FilterScreen;
import com.simibubi.create.content.logistics.filter.PackageFilterMenu;
import com.simibubi.create.content.logistics.filter.PackageFilterScreen;
import com.simibubi.create.content.logistics.packagePort.PackagePortMenu;
import com.simibubi.create.content.logistics.packagePort.PackagePortScreen;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterMenu;
import com.simibubi.create.content.logistics.redstoneRequester.RedstoneRequesterScreen;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryScreen;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestScreen;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerMenu;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerScreen;
import com.simibubi.create.content.schematics.cannon.SchematicannonMenu;
import com.simibubi.create.content.schematics.cannon.SchematicannonScreen;
import com.simibubi.create.content.schematics.table.SchematicTableMenu;
import com.simibubi.create.content.schematics.table.SchematicTableScreen;
import com.simibubi.create.content.trains.schedule.ScheduleMenu;
import com.simibubi.create.content.trains.schedule.ScheduleScreen;
import com.tterrag.registrate.builders.MenuBuilder;
import com.tterrag.registrate.util.entry.MenuEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class AllMenuTypes {
    public static final MenuEntry<SchematicTableMenu> SCHEMATIC_TABLE = AllMenuTypes.register("schematic_table", SchematicTableMenu::new, () -> SchematicTableScreen::new);
    public static final MenuEntry<SchematicannonMenu> SCHEMATICANNON = AllMenuTypes.register("schematicannon", SchematicannonMenu::new, () -> SchematicannonScreen::new);
    public static final MenuEntry<FilterMenu> FILTER = AllMenuTypes.register("filter", FilterMenu::new, () -> FilterScreen::new);
    public static final MenuEntry<AttributeFilterMenu> ATTRIBUTE_FILTER = AllMenuTypes.register("attribute_filter", AttributeFilterMenu::new, () -> AttributeFilterScreen::new);
    public static final MenuEntry<PackageFilterMenu> PACKAGE_FILTER = AllMenuTypes.register("package_filter", PackageFilterMenu::new, () -> PackageFilterScreen::new);
    public static final MenuEntry<BlueprintMenu> CRAFTING_BLUEPRINT = AllMenuTypes.register("crafting_blueprint", BlueprintMenu::new, () -> BlueprintScreen::new);
    public static final MenuEntry<LinkedControllerMenu> LINKED_CONTROLLER = AllMenuTypes.register("linked_controller", LinkedControllerMenu::new, () -> LinkedControllerScreen::new);
    public static final MenuEntry<ToolboxMenu> TOOLBOX = AllMenuTypes.register("toolbox", ToolboxMenu::new, () -> ToolboxScreen::new);
    public static final MenuEntry<ScheduleMenu> SCHEDULE = AllMenuTypes.register("schedule", ScheduleMenu::new, () -> ScheduleScreen::new);
    public static final MenuEntry<StockKeeperCategoryMenu> STOCK_KEEPER_CATEGORY = AllMenuTypes.register("stock_keeper_category", StockKeeperCategoryMenu::new, () -> StockKeeperCategoryScreen::new);
    public static final MenuEntry<StockKeeperRequestMenu> STOCK_KEEPER_REQUEST = AllMenuTypes.register("stock_keeper_request", StockKeeperRequestMenu::new, () -> StockKeeperRequestScreen::new);
    public static final MenuEntry<PackagePortMenu> PACKAGE_PORT = AllMenuTypes.register("package_port", PackagePortMenu::new, () -> PackagePortScreen::new);
    public static final MenuEntry<RedstoneRequesterMenu> REDSTONE_REQUESTER = AllMenuTypes.register("redstone_requester", RedstoneRequesterMenu::new, () -> RedstoneRequesterScreen::new);
    public static final MenuEntry<FactoryPanelSetItemMenu> FACTORY_PANEL_SET_ITEM = AllMenuTypes.register("factory_panel_set_item", FactoryPanelSetItemMenu::new, () -> FactoryPanelSetItemScreen::new);

    private static <C extends AbstractContainerMenu, S extends Screen> MenuEntry<C> register(String name, MenuBuilder.ForgeMenuFactory<C> factory, NonNullSupplier<MenuBuilder.ScreenFactory<C, S>> screenFactory) {
        return Create.registrate().menu(name, factory, screenFactory).register();
    }

    public static void register() {
    }
}
