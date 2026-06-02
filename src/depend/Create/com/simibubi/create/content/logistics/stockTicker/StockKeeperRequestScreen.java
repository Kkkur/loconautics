/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Lighting
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.PoseStack
 *  com.mojang.blaze3d.vertex.VertexConsumer
 *  com.mojang.math.Axis
 *  dev.engine_room.flywheel.lib.model.baked.PartialModel
 *  mezz.jei.api.runtime.IIngredientFilter
 *  net.createmod.catnip.animation.AnimationTickHolder
 *  net.createmod.catnip.animation.LerpedFloat
 *  net.createmod.catnip.animation.LerpedFloat$Chaser
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.data.Couple
 *  net.createmod.catnip.data.Iterate
 *  net.createmod.catnip.data.Pair
 *  net.createmod.catnip.gui.TextureSheetSegment
 *  net.createmod.catnip.gui.UIRenderHelper
 *  net.createmod.catnip.gui.element.GuiGameElement
 *  net.createmod.catnip.lang.Lang
 *  net.createmod.catnip.math.AngleHelper
 *  net.createmod.catnip.platform.CatnipServices
 *  net.createmod.catnip.render.CachedBuffers
 *  net.createmod.catnip.render.SuperByteBuffer
 *  net.createmod.catnip.theme.Color
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.gui.screens.inventory.InventoryScreen
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.Rect2i
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.util.FormattedCharSequence
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringRepresentable
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item$TooltipContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  net.minecraft.world.item.crafting.CraftingRecipe
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.entity.BlockEntity
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  org.jetbrains.annotations.Nullable
 *  org.lwjgl.glfw.GLFW
 */
package com.simibubi.create.content.logistics.stockTicker;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.compat.Mods;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.content.contraptions.actors.seat.SeatEntity;
import com.simibubi.create.content.equipment.clipboard.ClipboardEntry;
import com.simibubi.create.content.logistics.AddressEditBox;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelScreen;
import com.simibubi.create.content.logistics.packager.InventorySummary;
import com.simibubi.create.content.logistics.stockTicker.CraftableBigItemStack;
import com.simibubi.create.content.logistics.stockTicker.PackageOrder;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderRequestPacket;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperCategoryHidingPacket;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperLockPacket;
import com.simibubi.create.content.logistics.stockTicker.StockKeeperRequestMenu;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerRenderer;
import com.simibubi.create.content.trains.station.NoShadowFontWrapper;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.menu.AbstractSimiContainerScreen;
import com.simibubi.create.foundation.gui.widget.ScrollInput;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import mezz.jei.api.runtime.IIngredientFilter;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.createmod.catnip.config.ConfigBase;
import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.gui.TextureSheetSegment;
import net.createmod.catnip.gui.UIRenderHelper;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.createmod.catnip.theme.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public class StockKeeperRequestScreen
extends AbstractSimiContainerScreen<StockKeeperRequestMenu> {
    private static final AllGuiTextures NUMBERS = AllGuiTextures.NUMBERS;
    private static final AllGuiTextures HEADER = AllGuiTextures.STOCK_KEEPER_REQUEST_HEADER;
    private static final AllGuiTextures BODY = AllGuiTextures.STOCK_KEEPER_REQUEST_BODY;
    private static final AllGuiTextures FOOTER = AllGuiTextures.STOCK_KEEPER_REQUEST_FOOTER;
    StockTickerBlockEntity blockEntity;
    public LerpedFloat itemScroll = LerpedFloat.linear().startWithValue(0.0);
    final int rows = 9;
    final int cols = 9;
    final int rowHeight = 20;
    final int colWidth = 20;
    final Couple<Integer> noneHovered = Couple.create((Object)-1, (Object)-1);
    int itemsX;
    int itemsY;
    int orderY;
    int lockX;
    int besideSearchButtonY;
    int windowWidth;
    int windowHeight;
    int jeiSyncX;
    String previousJEISearchText = "";
    public EditBox searchBox;
    public AddressEditBox addressBox;
    int emptyTicks = 0;
    int successTicks = 0;
    public List<List<BigItemStack>> currentItemSource;
    public List<List<BigItemStack>> displayedItems = new ArrayList<List<BigItemStack>>();
    public List<CategoryEntry> categories = new ArrayList<CategoryEntry>();
    public List<BigItemStack> itemsToOrder = new ArrayList<BigItemStack>();
    public List<CraftableBigItemStack> recipesToOrder = new ArrayList<CraftableBigItemStack>();
    WeakReference<LivingEntity> stockKeeper = new WeakReference<Object>(null);
    WeakReference<BlazeBurnerBlockEntity> blaze = new WeakReference<Object>(null);
    boolean encodeRequester;
    ItemStack itemToProgram;
    List<List<ClipboardEntry>> clipboardItem;
    private final boolean isAdmin;
    private boolean isLocked;
    private boolean scrollHandleActive;
    private boolean ignoreTextInput;
    public boolean refreshSearchNextTick;
    public boolean moveToTopNextTick;
    private List<Rect2i> extraAreas;
    private final Set<Integer> hiddenCategories;
    private InventorySummary forcedEntries;
    private boolean canRequestCraftingPackage;

    public StockKeeperRequestScreen(StockKeeperRequestMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        this.isAdmin = ((StockKeeperRequestMenu)this.menu).isAdmin;
        this.isLocked = ((StockKeeperRequestMenu)this.menu).isLocked;
        this.refreshSearchNextTick = false;
        this.moveToTopNextTick = false;
        this.extraAreas = Collections.emptyList();
        this.forcedEntries = new InventorySummary();
        this.canRequestCraftingPackage = false;
        this.blockEntity = (StockTickerBlockEntity)container.contentHolder;
        this.blockEntity.lastClientsideStockSnapshot = null;
        this.blockEntity.ticksSinceLastUpdate = 15;
        ((StockKeeperRequestMenu)this.menu).screenReference = this;
        this.hiddenCategories = new HashSet<Integer>(this.blockEntity.hiddenCategoriesByPlayer.getOrDefault(((StockKeeperRequestMenu)this.menu).player.getUUID(), List.of()));
        this.itemToProgram = ((StockKeeperRequestMenu)this.menu).player.getMainHandItem();
        boolean bl = this.encodeRequester = AllTags.AllItemTags.TABLE_CLOTHS.matches(this.itemToProgram) || AllBlocks.REDSTONE_REQUESTER.isIn(this.itemToProgram);
        if (AllBlocks.CLIPBOARD.isIn(this.itemToProgram)) {
            this.clipboardItem = ClipboardEntry.readAll(this.itemToProgram);
            boolean anyItems = false;
            block0: for (List<ClipboardEntry> list : this.clipboardItem) {
                for (ClipboardEntry entry : list) {
                    if (entry.icon.isEmpty()) continue;
                    anyItems = true;
                    continue block0;
                }
            }
            if (!anyItems) {
                this.clipboardItem = null;
            }
        }
        for (int yOffset : Iterate.zeroAndOne) {
            for (Direction side : Iterate.horizontalDirections) {
                BlockEntity blockEntity;
                BlockPos seatPos = this.blockEntity.getBlockPos().below(yOffset).relative(side);
                for (SeatEntity seatEntity : this.blockEntity.getLevel().getEntitiesOfClass(SeatEntity.class, new AABB(seatPos))) {
                    Object e;
                    if (seatEntity.getPassengers().isEmpty() || !((e = seatEntity.getPassengers().get(0)) instanceof LivingEntity)) continue;
                    LivingEntity keeper = (LivingEntity)e;
                    this.stockKeeper = new WeakReference<LivingEntity>(keeper);
                }
                if (yOffset != 0 || !((blockEntity = this.blockEntity.getLevel().getBlockEntity(seatPos)) instanceof BlazeBurnerBlockEntity)) continue;
                BlazeBurnerBlockEntity bbbe = (BlazeBurnerBlockEntity)blockEntity;
                this.blaze = new WeakReference<BlazeBurnerBlockEntity>(bbbe);
                return;
            }
        }
    }

    @Override
    protected void init() {
        int appropriateHeight = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 10;
        appropriateHeight -= Mth.positiveModulo((int)(appropriateHeight - HEADER.getHeight() - FOOTER.getHeight()), (int)BODY.getHeight());
        appropriateHeight = Math.min(appropriateHeight, HEADER.getHeight() + FOOTER.getHeight() + BODY.getHeight() * 17);
        this.windowWidth = 226;
        this.windowHeight = appropriateHeight;
        this.setWindowSize(226, this.windowHeight);
        super.init();
        this.clearWidgets();
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        this.itemsX = x + (this.windowWidth - 180) / 2 + 1;
        this.itemsY = y + 33;
        this.orderY = y + this.windowHeight - 72;
        this.jeiSyncX = x + 25;
        this.lockX = x + 186;
        this.besideSearchButtonY = y + 18;
        MutableComponent searchLabel = CreateLang.translateDirect("gui.stock_keeper.search_items", new Object[0]);
        this.searchBox = new EditBox((Font)new NoShadowFontWrapper(this.font), x + 71, y + 22, 100, 9, (Component)searchLabel);
        this.searchBox.setMaxLength(50);
        this.searchBox.setBordered(false);
        this.searchBox.setTextColor(4861233);
        this.addWidget((GuiEventListener)this.searchBox);
        this.refreshSearchNextTick = true;
        this.moveToTopNextTick = true;
        this.syncJEI(true);
        boolean initial = this.addressBox == null;
        String previouslyUsedAddress = initial ? this.blockEntity.previouslyUsedAddress : this.addressBox.getValue();
        this.addressBox = new AddressEditBox((Screen)this, new NoShadowFontWrapper(this.font), x + 27, y + this.windowHeight - 36, 92, 10, true);
        this.addressBox.setTextColor(7424576);
        this.addressBox.setValue(previouslyUsedAddress);
        this.addRenderableWidget((GuiEventListener)this.addressBox);
        this.extraAreas = new ArrayList<Rect2i>();
        int leftHeight = 40;
        int rightHeight = 50;
        LivingEntity keeper = (LivingEntity)this.stockKeeper.get();
        if (keeper != null && keeper.isAlive()) {
            leftHeight = (int)(Math.max(0.0, keeper.getBoundingBox().getYsize()) * 50.0);
        }
        this.extraAreas.add(new Rect2i(0, y + this.windowHeight - 15 - leftHeight, x, this.height));
        if (this.encodeRequester) {
            this.extraAreas.add(new Rect2i(x + this.windowWidth, y + this.windowHeight - 15 - rightHeight, rightHeight + 10, rightHeight));
        }
        if (initial) {
            this.playUiSound(SoundEvents.WOOD_HIT, 0.5f, 1.5f);
            this.playUiSound(SoundEvents.BOOK_PAGE_TURN, 1.0f, 1.0f);
            this.syncJEI(false);
        }
    }

    private void refreshSearchResults(boolean scrollBackUp) {
        this.displayedItems = Collections.emptyList();
        if (scrollBackUp) {
            this.itemScroll.startWithValue(0.0);
        }
        if (this.currentItemSource == null) {
            this.clampScrollBar();
            return;
        }
        if (this.isSchematicListMode()) {
            this.clampScrollBar();
            this.requestSchematicList();
            return;
        }
        this.categories = new ArrayList<CategoryEntry>();
        for (int i = 0; i < this.blockEntity.categories.size(); ++i) {
            ItemStack stack = this.blockEntity.categories.get(i);
            CategoryEntry entry = new CategoryEntry(i, stack.isEmpty() ? "" : stack.getHoverName().getString(), 0);
            entry.hidden = this.hiddenCategories.contains(i);
            this.categories.add(entry);
        }
        CategoryEntry unsorted = new CategoryEntry(-1, CreateLang.translate("gui.stock_keeper.unsorted_category", new Object[0]).string(), 0);
        unsorted.hidden = this.hiddenCategories.contains(-1);
        this.categories.add(unsorted);
        String valueWithPrefix = this.searchBox.getValue();
        boolean anyItemsInCategory = false;
        if (valueWithPrefix.isBlank()) {
            this.displayedItems = new ArrayList<List<BigItemStack>>(this.currentItemSource);
            int categoryY = 0;
            for (int categoryIndex = 0; categoryIndex < this.currentItemSource.size(); ++categoryIndex) {
                this.categories.get((int)categoryIndex).y = categoryY;
                List<BigItemStack> displayedItemsInCategory = this.displayedItems.get(categoryIndex);
                if (displayedItemsInCategory.isEmpty()) continue;
                if (categoryIndex < this.currentItemSource.size() - 1) {
                    anyItemsInCategory = true;
                }
                categoryY += 20;
                if (this.categories.get((int)categoryIndex).hidden) continue;
                categoryY = (int)((double)categoryY + Math.ceil((float)displayedItemsInCategory.size() / 9.0f) * 20.0);
            }
            if (!anyItemsInCategory) {
                this.categories.clear();
            }
            this.clampScrollBar();
            this.updateCraftableAmounts();
            return;
        }
        boolean modSearch = false;
        boolean tagSearch = false;
        modSearch = valueWithPrefix.startsWith("@");
        if (modSearch || (tagSearch = valueWithPrefix.startsWith("#"))) {
            valueWithPrefix = valueWithPrefix.substring(1);
        }
        String value = valueWithPrefix.toLowerCase(Locale.ROOT);
        this.displayedItems = new ArrayList<List<BigItemStack>>();
        this.currentItemSource.forEach($ -> this.displayedItems.add(new ArrayList()));
        int categoryY = 0;
        for (int categoryIndex = 0; categoryIndex < this.displayedItems.size(); ++categoryIndex) {
            List<BigItemStack> category = this.currentItemSource.get(categoryIndex);
            this.categories.get((int)categoryIndex).y = categoryY;
            if (this.displayedItems.size() <= categoryIndex) break;
            List<BigItemStack> displayedItemsInCategory = this.displayedItems.get(categoryIndex);
            for (BigItemStack entry : category) {
                ItemStack stack = entry.stack;
                if (modSearch) {
                    if (!BuiltInRegistries.ITEM.getKey((Object)stack.getItem()).getNamespace().contains(value)) continue;
                    displayedItemsInCategory.add(entry);
                    continue;
                }
                if (tagSearch) {
                    if (!stack.getTags().anyMatch(key -> key.location().toString().contains(value))) continue;
                    displayedItemsInCategory.add(entry);
                    continue;
                }
                if (!stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(value) && !BuiltInRegistries.ITEM.getKey((Object)stack.getItem()).getPath().contains(value)) continue;
                displayedItemsInCategory.add(entry);
            }
            if (displayedItemsInCategory.isEmpty()) continue;
            if (categoryIndex < this.currentItemSource.size() - 1) {
                anyItemsInCategory = true;
            }
            categoryY += 20;
            if (this.categories.get((int)categoryIndex).hidden) continue;
            categoryY = (int)((double)categoryY + Math.ceil((float)displayedItemsInCategory.size() / 9.0f) * 20.0);
        }
        if (!anyItemsInCategory) {
            this.categories.clear();
        }
        this.clampScrollBar();
        this.updateCraftableAmounts();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.addressBox.tick();
        if (!this.forcedEntries.isEmpty()) {
            InventorySummary summary = this.blockEntity.getLastClientsideStockSnapshotAsSummary();
            for (BigItemStack bigItemStack : this.forcedEntries.getStacks()) {
                int limitedAmount = -bigItemStack.count - 1;
                int actualAmount = summary.getCountOf(bigItemStack.stack);
                if (actualAmount > limitedAmount) continue;
                this.forcedEntries.erase(bigItemStack.stack);
            }
        }
        boolean allEmpty = true;
        for (List list : this.displayedItems) {
            allEmpty &= list.isEmpty();
        }
        this.emptyTicks = allEmpty ? ++this.emptyTicks : 0;
        this.successTicks = this.successTicks > 0 && this.itemsToOrder.isEmpty() ? ++this.successTicks : 0;
        List<List<BigItemStack>> clientStockSnapshot = this.blockEntity.getClientStockSnapshot();
        if (clientStockSnapshot != this.currentItemSource) {
            this.currentItemSource = clientStockSnapshot;
            this.refreshSearchResults(false);
            this.revalidateOrders();
        }
        if (this.shouldSyncFromJEI()) {
            this.refreshSearchNextTick = true;
            this.moveToTopNextTick = true;
            this.syncJEI(true);
        }
        if (this.refreshSearchNextTick) {
            this.refreshSearchNextTick = false;
            this.refreshSearchResults(this.moveToTopNextTick);
        }
        this.itemScroll.tickChaser();
        if (Math.abs(this.itemScroll.getValue() - this.itemScroll.getChaseTarget()) < 0.0625f) {
            this.itemScroll.setValue((double)this.itemScroll.getChaseTarget());
        }
        if (this.blockEntity.ticksSinceLastUpdate > 15) {
            this.blockEntity.refreshClientStockSnapshot();
        }
        LivingEntity livingEntity = (LivingEntity)this.stockKeeper.get();
        BlazeBurnerBlockEntity blazeKeeper = (BlazeBurnerBlockEntity)this.blaze.get();
        if (!(livingEntity != null && livingEntity.isAlive() || blazeKeeper != null && !blazeKeeper.isRemoved())) {
            ((StockKeeperRequestMenu)this.menu).player.closeContainer();
        }
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack ms = guiGraphics.pose();
        ms.pushPose();
        ms.translate(0.0f, 0.0f, -300.0f);
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        ms.popPose();
    }

    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        boolean justSent;
        BlazeBurnerBlockEntity keeperBE;
        int entityY;
        int entityX;
        if (this != this.minecraft.screen) {
            return;
        }
        PoseStack ms = graphics.pose();
        float currentScroll = this.itemScroll.getValue(partialTicks);
        Couple<Integer> hoveredSlot = this.getHoveredSlot(mouseX, mouseY);
        int x = this.getGuiLeft();
        int y = this.getGuiTop();
        HEADER.render(graphics, x - 15, y);
        y += HEADER.getHeight();
        for (int i = 0; i < (this.windowHeight - HEADER.getHeight() - FOOTER.getHeight()) / BODY.getHeight(); ++i) {
            BODY.render(graphics, x - 15, y);
            y += BODY.getHeight();
        }
        FOOTER.render(graphics, x - 15, y);
        y = this.getGuiTop();
        if (this.addressBox.getValue().isBlank() && !this.addressBox.isFocused()) {
            graphics.drawString(Minecraft.getInstance().font, (Component)CreateLang.translate("gui.stock_keeper.package_address", new Object[0]).style(ChatFormatting.ITALIC).component(), this.addressBox.getX(), this.addressBox.getY(), -3294040, false);
        }
        int entitySizeOffset = 0;
        LivingEntity keeper = (LivingEntity)this.stockKeeper.get();
        if (keeper != null && keeper.isAlive()) {
            ms.pushPose();
            ms.translate(0.0f, 0.0f, 50.0f);
            entitySizeOffset = (int)(Math.max(0.0, keeper.getBoundingBox().getXsize() - 1.0) * 50.0);
            int entitySizeOffsetY = (int)(Math.max(0.0, keeper.getBoundingBox().getYsize() - 1.0) * 25.0);
            entityX = x - 35 - entitySizeOffset;
            entityY = y + this.windowHeight - 47 - entitySizeOffsetY;
            InventoryScreen.renderEntityInInventoryFollowsMouse((GuiGraphics)graphics, (int)(entityX - 100), (int)(entityY - 100), (int)(entityX + 100), (int)(entityY + 100), (int)50, (float)0.0f, (float)mouseX, (float)Mth.clamp((int)mouseY, (int)(entityY - 50), (int)(entityY + 10)), (LivingEntity)keeper);
            ms.popPose();
        }
        if ((keeperBE = (BlazeBurnerBlockEntity)this.blaze.get()) != null && !keeperBE.isRemoved()) {
            ms.pushPose();
            entityX = x - 35;
            entityY = y + this.windowHeight - 43;
            ms.translate((float)entityX, (float)entityY, 0.0f);
            ms.mulPose(Axis.XP.rotationDegrees(-22.5f));
            ms.mulPose(Axis.YP.rotationDegrees(-45.0f));
            ms.scale(48.0f, -48.0f, 48.0f);
            float animation = keeperBE.headAnimation.getValue(AnimationTickHolder.getPartialTicks()) * 0.175f;
            float horizontalAngle = AngleHelper.rad((double)270.0);
            BlazeBurnerBlock.HeatLevel heatLevel = keeperBE.getHeatLevelForRender();
            boolean canDrawFlame = heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING);
            boolean drawGoggles = keeperBE.goggles;
            PartialModel drawHat = AllPartialModels.LOGISTICS_HAT;
            int hashCode = keeperBE.hashCode();
            Lighting.setupForEntityInInventory();
            VertexConsumer cutout = graphics.bufferSource().getBuffer(RenderType.cutoutMipped());
            ((SuperByteBuffer)CachedBuffers.partial((PartialModel)AllPartialModels.BLAZE_CAGE, (BlockState)keeperBE.getBlockState()).rotateCentered(horizontalAngle + (float)Math.PI, Direction.UP)).light(0xF000F0).renderInto(ms, cutout);
            BlazeBurnerRenderer.renderShared(ms, null, (MultiBufferSource)graphics.bufferSource(), (Level)this.minecraft.level, keeperBE.getBlockState(), heatLevel, animation, horizontalAngle, canDrawFlame, drawGoggles, drawHat, hashCode);
            Lighting.setupFor3DItems();
            ms.popPose();
        }
        if (this.encodeRequester) {
            ms.pushPose();
            ms.translate((float)(x + this.windowWidth + 5), (float)(y + this.windowHeight - 70), 0.0f);
            ms.scale(3.5f, 3.5f, 3.5f);
            GuiGameElement.of((ItemStack)this.itemToProgram).render(graphics);
            ms.popPose();
        }
        for (int index = 0; index < 9 && this.itemsToOrder.size() > index; ++index) {
            BigItemStack entry = this.itemsToOrder.get(index);
            boolean isStackHovered = index == (Integer)hoveredSlot.getSecond() && (Integer)hoveredSlot.getFirst() == -1;
            ms.pushPose();
            ms.translate((float)(this.itemsX + index * 20), (float)this.orderY, 0.0f);
            this.renderItemEntry(graphics, 1.0f, entry, isStackHovered, true);
            ms.popPose();
        }
        if (this.itemsToOrder.size() > 9) {
            graphics.drawString(this.font, (Component)Component.literal((String)("[+" + (this.itemsToOrder.size() - 9) + "]")), x + this.windowWidth - 40, this.orderY + 21, 16316652);
        }
        boolean bl = justSent = this.itemsToOrder.isEmpty() && this.successTicks > 0;
        if (this.isConfirmHovered(mouseX, mouseY) && !justSent) {
            AllGuiTextures.STOCK_KEEPER_REQUEST_SEND_HOVER.render(graphics, x + this.windowWidth - 81, y + this.windowHeight - 41);
        }
        MutableComponent headerTitle = CreateLang.translate("gui.stock_keeper.title", new Object[0]).component();
        graphics.drawString(this.font, (Component)headerTitle, x + this.windowWidth / 2 - this.font.width((FormattedText)headerTitle) / 2, y + 4, 7424576, false);
        MutableComponent component = CreateLang.translate(this.encodeRequester ? "gui.stock_keeper.configure" : "gui.stock_keeper.send", new Object[0]).component();
        if (justSent) {
            float alpha = Mth.clamp((float)(((float)this.successTicks + partialTicks - 5.0f) / 5.0f), (float)0.0f, (float)1.0f);
            ms.pushPose();
            ms.translate(alpha * alpha * 50.0f, 0.0f, 0.0f);
            if (this.successTicks < 10) {
                graphics.drawString(this.font, (Component)component, x + this.windowWidth - 42 - this.font.width((FormattedText)component) / 2, y + this.windowHeight - 35, new Color(0x252525).setAlpha(1.0f - alpha * alpha).getRGB(), false);
            }
            ms.popPose();
        } else {
            graphics.drawString(this.font, (Component)component, x + this.windowWidth - 42 - this.font.width((FormattedText)component) / 2, y + this.windowHeight - 35, 0x252525, false);
        }
        if (justSent) {
            MutableComponent msg = CreateLang.translateDirect("gui.stock_keeper.request_sent", new Object[0]);
            float alpha = Mth.clamp((float)(((float)this.successTicks + partialTicks - 10.0f) / 5.0f), (float)0.0f, (float)1.0f);
            int msgX = x + this.windowWidth / 2 - (this.font.width((FormattedText)msg) + 10) / 2;
            int msgY = this.orderY + 5;
            if (alpha > 0.0f) {
                int c3 = new Color(9198923).setAlpha(alpha).getRGB();
                int w = this.font.width((FormattedText)msg) + 14;
                AllGuiTextures.STOCK_KEEPER_REQUEST_BANNER_L.render(graphics, msgX - 8, msgY - 4);
                UIRenderHelper.drawStretched((GuiGraphics)graphics, (int)msgX, (int)(msgY - 4), (int)w, (int)16, (int)0, (TextureSheetSegment)AllGuiTextures.STOCK_KEEPER_REQUEST_BANNER_M);
                AllGuiTextures.STOCK_KEEPER_REQUEST_BANNER_R.render(graphics, msgX + this.font.width((FormattedText)msg) + 10, msgY - 4);
                graphics.drawString(this.font, (Component)msg, msgX + 5, msgY, c3, false);
            }
        }
        int itemWindowX = x + 21;
        int itemWindowX2 = itemWindowX + 184;
        int itemWindowY = y + 17;
        int itemWindowY2 = y + this.windowHeight - 80;
        graphics.enableScissor(itemWindowX - 5, itemWindowY, itemWindowX2 + 10, itemWindowY2);
        ms.pushPose();
        ms.translate(0.0f, -currentScroll * 20.0f, 0.0f);
        for (int sliceY = -2; sliceY < this.getMaxScroll() * 20 + this.windowHeight - 72; sliceY += AllGuiTextures.STOCK_KEEPER_REQUEST_BG.getHeight()) {
            if ((float)sliceY - currentScroll * 20.0f < -20.0f || (float)sliceY - currentScroll * 20.0f > (float)(this.windowHeight - 72)) continue;
            AllGuiTextures.STOCK_KEEPER_REQUEST_BG.render(graphics, x + 22, y + sliceY + 18);
        }
        AllGuiTextures.STOCK_KEEPER_REQUEST_SEARCH.render(graphics, x + 42, this.searchBox.getY() - 5);
        this.searchBox.render(graphics, mouseX, mouseY, partialTicks);
        if (this.searchBox.getValue().isBlank() && !this.searchBox.isFocused()) {
            graphics.drawString(this.font, this.searchBox.getMessage(), x + this.windowWidth / 2 - this.font.width((FormattedText)this.searchBox.getMessage()) / 2, this.searchBox.getY(), -11915983, false);
        }
        boolean allEmpty = true;
        for (List<BigItemStack> list : this.displayedItems) {
            allEmpty &= list.isEmpty();
        }
        if (allEmpty) {
            Component msg = this.getTroubleshootingMessage();
            float alpha = Mth.clamp((float)(((float)this.emptyTicks - 10.0f) / 5.0f), (float)0.0f, (float)1.0f);
            if (alpha > 0.0f) {
                List split = this.font.split((FormattedText)msg, 160);
                for (int i = 0; i < split.size(); ++i) {
                    FormattedCharSequence sequence = (FormattedCharSequence)split.get(i);
                    int lineWidth = this.font.width(sequence);
                    int n = x + this.windowWidth / 2 - lineWidth / 2 + 1;
                    Objects.requireNonNull(this.font);
                    graphics.drawString(this.font, sequence, n, this.itemsY + 20 + 1 + i * (9 + 1), new Color(4861233).setAlpha(alpha).getRGB(), false);
                    int n2 = x + this.windowWidth / 2 - lineWidth / 2;
                    Objects.requireNonNull(this.font);
                    graphics.drawString(this.font, sequence, n2, this.itemsY + 20 + i * (9 + 1), new Color(16316652).setAlpha(alpha).getRGB(), false);
                }
            }
        }
        block5: for (int categoryIndex = 0; categoryIndex < this.displayedItems.size(); ++categoryIndex) {
            int categoryY;
            List<BigItemStack> category = this.displayedItems.get(categoryIndex);
            CategoryEntry categoryEntry = this.categories.isEmpty() ? null : this.categories.get(categoryIndex);
            int n = categoryY = this.categories.isEmpty() ? 0 : categoryEntry.y;
            if (category.isEmpty()) continue;
            if (!this.categories.isEmpty()) {
                (categoryEntry.hidden ? AllGuiTextures.STOCK_KEEPER_CATEGORY_HIDDEN : AllGuiTextures.STOCK_KEEPER_CATEGORY_SHOWN).render(graphics, this.itemsX, this.itemsY + categoryY + 6);
                graphics.drawString(this.font, categoryEntry.name, this.itemsX + 10, this.itemsY + categoryY + 8, 4861233, false);
                graphics.drawString(this.font, categoryEntry.name, this.itemsX + 9, this.itemsY + categoryY + 7, 16316652, false);
                if (categoryEntry.hidden) continue;
            }
            for (int index = 0; index < category.size(); ++index) {
                int pY = this.itemsY + categoryY + (this.categories.isEmpty() ? 4 : 20) + index / 9 * 20;
                float cullY = (float)pY - currentScroll * 20.0f;
                if (cullY < (float)y) continue;
                if (cullY > (float)(y + this.windowHeight - 72)) continue block5;
                boolean isStackHovered = index == (Integer)hoveredSlot.getSecond() && categoryIndex == (Integer)hoveredSlot.getFirst();
                BigItemStack entry = category.get(index);
                ms.pushPose();
                ms.translate((float)(this.itemsX + index % 9 * 20), (float)pY, 0.0f);
                this.renderItemEntry(graphics, 1.0f, entry, isStackHovered, false);
                ms.popPose();
            }
        }
        if (Mods.JEI.isLoaded()) {
            ((SearchSyncMode)((Object)AllConfigs.client().syncRecipeViewerSearch.get())).buttonTexture.render(graphics, this.jeiSyncX, this.besideSearchButtonY);
        }
        if (this.isAdmin) {
            (this.isLocked ? AllGuiTextures.STOCK_KEEPER_REQUEST_LOCKED : AllGuiTextures.STOCK_KEEPER_REQUEST_UNLOCKED).render(graphics, this.lockX, this.besideSearchButtonY);
        }
        ms.popPose();
        graphics.disableScissor();
        int windowH = this.windowHeight - 92;
        int totalH = this.getMaxScroll() * 20 + windowH;
        int barSize = Math.max(5, Mth.floor((float)((float)windowH / (float)totalH * (float)(windowH - 2))));
        if (barSize < windowH - 2) {
            int barX = this.itemsX + 180;
            int barY = y + 15;
            ms.pushPose();
            ms.translate(0.0f, currentScroll * 20.0f / (float)totalH * (float)(windowH - 2), 0.0f);
            AllGuiTextures pad = AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_PAD;
            graphics.blit(pad.location, barX, barY, pad.getWidth(), barSize, (float)pad.getStartX(), (float)pad.getStartY(), pad.getWidth(), pad.getHeight(), 256, 256);
            AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_TOP.render(graphics, barX, barY);
            if (barSize > 16) {
                AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_MID.render(graphics, barX, barY + barSize / 2 - 4);
            }
            AllGuiTextures.STOCK_KEEPER_REQUEST_SCROLL_BOT.render(graphics, barX, barY + barSize - 5);
            ms.popPose();
        }
        if (this.recipesToOrder.size() > 0) {
            int jeiX = x + (this.windowWidth - 20 * this.recipesToOrder.size()) / 2 + 1;
            int jeiY = this.orderY - 31;
            ms.pushPose();
            ms.translate((float)jeiX, (float)jeiY, 200.0f);
            int xoffset = -3;
            AllGuiTextures.STOCK_KEEPER_REQUEST_BLUEPRINT_LEFT.render(graphics, xoffset, -3);
            xoffset += 10;
            for (int i = 0; i <= (this.recipesToOrder.size() - 1) * 5; ++i) {
                AllGuiTextures.STOCK_KEEPER_REQUEST_BLUEPRINT_MIDDLE.render(graphics, xoffset, -3);
                xoffset += 4;
            }
            AllGuiTextures.STOCK_KEEPER_REQUEST_BLUEPRINT_RIGHT.render(graphics, xoffset, -3);
            for (int index = 0; index < this.recipesToOrder.size(); ++index) {
                CraftableBigItemStack craftableBigItemStack = this.recipesToOrder.get(index);
                boolean isStackHovered = index == (Integer)hoveredSlot.getSecond() && -2 == (Integer)hoveredSlot.getFirst();
                ms.pushPose();
                ms.translate((float)(index * 20), 0.0f, 0.0f);
                this.renderItemEntry(graphics, 1.0f, craftableBigItemStack, isStackHovered, true);
                ms.popPose();
            }
            ms.popPose();
        }
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.renderForeground(graphics, mouseX, mouseY, partialTicks);
        float currentScroll = this.itemScroll.getValue(partialTicks);
        Couple<Integer> hoveredSlot = this.getHoveredSlot(mouseX, mouseY);
        if (hoveredSlot != this.noneHovered) {
            BigItemStack entry;
            boolean orderHovered;
            int slot = (Integer)hoveredSlot.getSecond();
            boolean recipeHovered = (Integer)hoveredSlot.getFirst() == -2;
            boolean bl = orderHovered = (Integer)hoveredSlot.getFirst() == -1;
            BigItemStack bigItemStack = recipeHovered ? (BigItemStack)this.recipesToOrder.get(slot) : (entry = orderHovered ? this.itemsToOrder.get(slot) : this.displayedItems.get((Integer)hoveredSlot.getFirst()).get(slot));
            if (recipeHovered) {
                ArrayList<MutableComponent> lines = new ArrayList<MutableComponent>(entry.stack.getTooltipLines(Item.TooltipContext.of((Level)this.minecraft.level), (Player)this.minecraft.player, (TooltipFlag)TooltipFlag.NORMAL));
                if (lines.size() > 0) {
                    lines.set(0, CreateLang.translateDirect("gui.stock_keeper.craft", ((Component)lines.get(0)).copy()));
                }
                graphics.renderComponentTooltip(this.font, lines, mouseX, mouseY);
            } else {
                graphics.renderTooltip(this.font, entry.stack, mouseX, mouseY);
            }
        }
        if (currentScroll < 1.0f && mouseY > this.besideSearchButtonY && mouseY <= this.besideSearchButtonY + 15) {
            if (Mods.JEI.isLoaded() && mouseX > this.jeiSyncX && mouseX <= this.jeiSyncX + 15) {
                SearchSyncMode mode = (SearchSyncMode)((Object)AllConfigs.client().syncRecipeViewerSearch.get());
                String langKey = "gui.stock_keeper.jei_sync." + mode.getSerializedName();
                graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate(langKey, new Object[0]).component(), CreateLang.translate(langKey + ".description", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.stock_keeper.click_to_cycle", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mouseX, mouseY);
            }
            if (this.isAdmin && mouseX > this.lockX && mouseX <= this.lockX + 15) {
                graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate(this.isLocked ? "gui.stock_keeper.network_locked" : "gui.stock_keeper.network_open", new Object[0]).component(), CreateLang.translate("gui.stock_keeper.network_lock_tip", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.stock_keeper.network_lock_tip_1", new Object[0]).style(ChatFormatting.GRAY).component(), CreateLang.translate("gui.stock_keeper.network_lock_tip_2", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mouseX, mouseY);
            }
        }
        if (this.addressBox.getValue().isBlank() && !this.addressBox.isFocused() && this.addressBox.isHovered()) {
            graphics.renderComponentTooltip(this.font, List.of(CreateLang.translate("gui.factory_panel.restocker_address", new Object[0]).color(ScrollInput.HEADER_RGB).component(), CreateLang.translate("gui.schedule.lmb_edit", new Object[0]).style(ChatFormatting.DARK_GRAY).style(ChatFormatting.ITALIC).component()), mouseX, mouseY);
        }
    }

    private void renderItemEntry(GuiGraphics graphics, float scale, BigItemStack entry, boolean isStackHovered, boolean isRenderingOrders) {
        int customCount = entry.count;
        ItemStack stackWithCount = entry.stack.copyWithCount(customCount);
        if (!isRenderingOrders) {
            BigItemStack order = this.getOrderForItem(stackWithCount);
            if (entry.count < 1000000000) {
                int forcedCount = this.forcedEntries.getCountOf(stackWithCount);
                if (forcedCount != 0) {
                    customCount = Math.min(customCount, -forcedCount - 1);
                }
                if (order != null) {
                    customCount -= order.count;
                }
                customCount = Math.max(0, customCount);
            }
            AllGuiTextures.STOCK_KEEPER_REQUEST_SLOT.render(graphics, 0, 0);
        }
        boolean craftable = entry instanceof CraftableBigItemStack;
        PoseStack ms = graphics.pose();
        ms.pushPose();
        float scaleFromHover = 1.0f;
        if (isStackHovered) {
            scaleFromHover += 0.075f;
        }
        ms.translate(1.0, 1.0, 0.0);
        ms.translate(9.0, 9.0, 0.0);
        ms.scale(scale, scale, scale);
        ms.scale(scaleFromHover, scaleFromHover, scaleFromHover);
        ms.translate(-9.0, -9.0, 0.0);
        if (customCount != 0 || craftable) {
            GuiGameElement.of((ItemStack)stackWithCount).render(graphics);
        }
        ms.popPose();
        ms.pushPose();
        ms.translate(0.0f, 0.0f, 190.0f);
        if (customCount != 0 || craftable) {
            graphics.renderItemDecorations(this.font, stackWithCount, 1, 1, "");
        }
        ms.translate(0.0f, 0.0f, 10.0f);
        if (customCount > 1 || craftable) {
            this.drawItemCount(graphics, entry.count, customCount);
        }
        ms.popPose();
    }

    /*
     * Enabled aggressive block sorting
     */
    private void drawItemCount(GuiGraphics graphics, int count, int customCount) {
        Object text;
        count = customCount;
        Object object = count >= 1000000 ? count / 1000000 + "m" : (count >= 10000 ? count / 1000 + "k" : (count >= 1000 ? (float)(count * 10 / 1000) / 10.0f + "k" : (text = count >= 100 ? "" + count : " " + count)));
        if (count >= 1000000000) {
            text = "+";
        }
        if (((String)text).isBlank()) {
            return;
        }
        int x = (int)Math.floor((double)(-((String)text).length()) * 2.5);
        char[] cArray = ((String)text).toCharArray();
        int n = cArray.length;
        int n2 = 0;
        while (true) {
            block11: {
                if (n2 >= n) {
                    return;
                }
                char c = cArray[n2];
                int index = c - 48;
                int xOffset = index * 6;
                int spriteWidth = NUMBERS.getWidth();
                switch (c) {
                    case ' ': {
                        x += 4;
                        break block11;
                    }
                    case '.': {
                        spriteWidth = 3;
                        xOffset = 60;
                        break;
                    }
                    case 'k': {
                        xOffset = 64;
                        break;
                    }
                    case 'm': {
                        spriteWidth = 7;
                        xOffset = 70;
                        break;
                    }
                    case '+': {
                        spriteWidth = 9;
                        xOffset = 84;
                    }
                }
                RenderSystem.enableBlend();
                graphics.blit(StockKeeperRequestScreen.NUMBERS.location, 14 + x, 10, 0, (float)(NUMBERS.getStartX() + xOffset), (float)NUMBERS.getStartY(), spriteWidth, NUMBERS.getHeight(), 256, 256);
                x += spriteWidth - 1;
            }
            ++n2;
        }
    }

    @Nullable
    private BigItemStack getOrderForItem(ItemStack stack) {
        for (BigItemStack entry : this.itemsToOrder) {
            if (!ItemStack.isSameItemSameComponents((ItemStack)stack, (ItemStack)entry.stack)) continue;
            return entry;
        }
        return null;
    }

    private void revalidateOrders() {
        HashSet<BigItemStack> invalid = new HashSet<BigItemStack>(this.itemsToOrder);
        InventorySummary summary = this.blockEntity.lastClientsideStockSnapshotAsSummary;
        if (this.currentItemSource == null || summary == null) {
            this.itemsToOrder.removeAll(invalid);
            return;
        }
        for (BigItemStack entry : this.itemsToOrder) {
            entry.count = Math.min(summary.getCountOf(entry.stack), entry.count);
            if (entry.count <= 0) continue;
            invalid.remove(entry);
        }
        this.itemsToOrder.removeAll(invalid);
    }

    private Couple<Integer> getHoveredSlot(int x, int y) {
        if (++x < this.itemsX || x >= this.itemsX + 180 || this.isSchematicListMode()) {
            return this.noneHovered;
        }
        if (y >= this.orderY && y < this.orderY + 20) {
            int col = (x - this.itemsX) / 20;
            if (this.itemsToOrder.size() <= col || col < 0) {
                return this.noneHovered;
            }
            return Couple.create((Object)-1, (Object)col);
        }
        if (y >= this.orderY - 31 && y < this.orderY - 31 + 20) {
            int jeiX = this.getGuiLeft() + (this.windowWidth - 20 * this.recipesToOrder.size()) / 2 + 1;
            int col = Mth.floorDiv((int)(x - jeiX), (int)20);
            if (this.recipesToOrder.size() > col && col >= 0) {
                return Couple.create((Object)-2, (Object)col);
            }
        }
        if (y < this.getGuiTop() + 16 || y > this.getGuiTop() + this.windowHeight - 80) {
            return this.noneHovered;
        }
        if (!this.itemScroll.settled()) {
            return this.noneHovered;
        }
        int localY = y - this.itemsY;
        for (int categoryIndex = 0; categoryIndex < this.displayedItems.size(); ++categoryIndex) {
            int col;
            CategoryEntry entry;
            CategoryEntry categoryEntry = entry = this.categories.isEmpty() ? new CategoryEntry(0, "", 0) : this.categories.get(categoryIndex);
            if (entry.hidden) continue;
            int row = Mth.floor((float)((float)(localY - (this.categories.isEmpty() ? 4 : 20) - entry.y) / 20.0f + this.itemScroll.getChaseTarget()));
            int slot = row * 9 + (col = (x - this.itemsX) / 20);
            if (slot < 0) {
                return this.noneHovered;
            }
            if (this.displayedItems.get(categoryIndex).size() <= slot) continue;
            return Couple.create((Object)categoryIndex, (Object)slot);
        }
        return this.noneHovered;
    }

    public Optional<Pair<ItemStack, Rect2i>> getHoveredIngredient(int mouseX, int mouseY) {
        Couple<Integer> hoveredSlot = this.getHoveredSlot(mouseX, mouseY);
        if (hoveredSlot != this.noneHovered) {
            BigItemStack entry;
            int y;
            int x;
            boolean orderHovered;
            int index = (Integer)hoveredSlot.getSecond();
            boolean recipeHovered = (Integer)hoveredSlot.getFirst() == -2;
            boolean bl = orderHovered = (Integer)hoveredSlot.getFirst() == -1;
            if (recipeHovered) {
                int jeiX = this.getGuiLeft() + (this.windowWidth - 20 * this.recipesToOrder.size()) / 2 + 1;
                int jeiY = this.orderY - 31;
                x = jeiX + index * 20;
                y = jeiY;
                entry = this.recipesToOrder.get(index);
            } else if (orderHovered) {
                x = this.itemsX + index * 20;
                y = this.orderY;
                entry = this.itemsToOrder.get(index);
            } else {
                int categoryIndex = (Integer)hoveredSlot.getFirst();
                int categoryY = this.categories.isEmpty() ? 0 : this.categories.get((int)categoryIndex).y;
                x = this.itemsX + index % 9 * 20;
                y = this.itemsY + categoryY + (this.categories.isEmpty() ? 4 : 20) + index / 9 * 20;
                entry = this.displayedItems.get(categoryIndex).get(index);
            }
            Rect2i bounds = new Rect2i(x, y, x + 18, y + 18);
            return Optional.of(Pair.of((Object)entry.stack.copy(), (Object)bounds));
        }
        return Optional.empty();
    }

    private boolean isConfirmHovered(int mouseX, int mouseY) {
        int confirmX = this.getGuiLeft() + 143;
        int confirmY = this.getGuiTop() + this.windowHeight - 39;
        int confirmW = 78;
        int confirmH = 18;
        if (mouseX < confirmX || mouseX >= confirmX + confirmW) {
            return false;
        }
        return mouseY >= confirmY && mouseY < confirmY + confirmH;
    }

    private Component getTroubleshootingMessage() {
        if (this.currentItemSource == null) {
            return CreateLang.translate("gui.stock_keeper.checking_stocks", new Object[0]).component();
        }
        if (this.blockEntity.activeLinks == 0) {
            return CreateLang.translate("gui.stock_keeper.no_packagers_linked", new Object[0]).component();
        }
        if (this.currentItemSource.isEmpty()) {
            return CreateLang.translate("gui.stock_keeper.inventories_empty", new Object[0]).component();
        }
        if (this.isSchematicListMode()) {
            return CreateLang.translate(this.itemsToOrder.isEmpty() ? "gui.stock_keeper.schematic_list.no_results" : "gui.stock_keeper.schematic_list.requesting", new Object[0]).component();
        }
        return CreateLang.translate("gui.stock_keeper.no_search_results", new Object[0]).component();
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int transfer;
        boolean recipeClicked;
        boolean rmb;
        boolean lmb = pButton == 0;
        boolean bl = rmb = pButton == 1;
        if (rmb && this.searchBox.isMouseOver(pMouseX, pMouseY)) {
            this.searchBox.setValue("");
            this.refreshSearchNextTick = true;
            this.moveToTopNextTick = true;
            this.searchBox.setFocused(true);
            this.syncJEI(false);
            return true;
        }
        if (this.addressBox.isFocused()) {
            boolean result = this.addressBox.mouseClicked(pMouseX, pMouseY, pButton);
            if (this.addressBox.isHovered() || result) {
                return result;
            }
            this.addressBox.setFocused(false);
        }
        if (this.searchBox.isFocused()) {
            if (this.searchBox.isHovered()) {
                return this.searchBox.mouseClicked(pMouseX, pMouseY, pButton);
            }
            this.searchBox.setFocused(false);
        }
        int barX = this.itemsX + 180 - 1;
        if (this.getMaxScroll() > 0 && lmb && pMouseX > (double)barX && pMouseX <= (double)(barX + 8) && pMouseY > (double)(this.getGuiTop() + 15) && pMouseY < (double)(this.getGuiTop() + this.windowHeight - 82)) {
            this.scrollHandleActive = true;
            if (this.minecraft.isWindowActive()) {
                GLFW.glfwSetInputMode((long)this.minecraft.getWindow().getWindow(), (int)208897, (int)212994);
            }
            return true;
        }
        Couple<Integer> hoveredSlot = this.getHoveredSlot((int)pMouseX, (int)pMouseY);
        if (this.itemScroll.getChaseTarget() == 0.0f && lmb && pMouseY > (double)this.besideSearchButtonY && pMouseY <= (double)(this.besideSearchButtonY + 15)) {
            if (pMouseX > (double)this.jeiSyncX && pMouseX <= (double)(this.jeiSyncX + 15)) {
                SearchSyncMode.cycleConfig();
                this.refreshSearchNextTick = true;
                this.moveToTopNextTick = true;
                this.syncJEI(false);
                this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                return true;
            }
            if (this.isAdmin && pMouseX > (double)this.lockX && pMouseX <= (double)(this.lockX + 15)) {
                this.isLocked = !this.isLocked;
                CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new StockKeeperLockPacket(this.blockEntity.getBlockPos(), this.isLocked));
                this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
                return true;
            }
        }
        if (lmb && this.isConfirmHovered((int)pMouseX, (int)pMouseY)) {
            this.sendIt();
            this.playUiSound((SoundEvent)SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.0f);
            return true;
        }
        int localY = (int)(pMouseY - (double)this.itemsY);
        if (this.itemScroll.settled() && lmb && !this.categories.isEmpty() && pMouseX >= (double)this.itemsX && pMouseX < (double)(this.itemsX + 180) && pMouseY >= (double)(this.getGuiTop() + 16) && pMouseY <= (double)(this.getGuiTop() + this.windowHeight - 80)) {
            for (int categoryIndex = 0; categoryIndex < this.displayedItems.size(); ++categoryIndex) {
                int indexOf;
                CategoryEntry entry = this.categories.get(categoryIndex);
                if (Mth.floor((float)((float)(localY - entry.y) / 20.0f + this.itemScroll.getChaseTarget())) != 0 || this.displayedItems.get(categoryIndex).isEmpty() || (indexOf = entry.targetBECategory) >= this.blockEntity.categories.size()) continue;
                if (!entry.hidden) {
                    this.hiddenCategories.add(indexOf);
                    this.playUiSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0f, 1.5f);
                } else {
                    this.hiddenCategories.remove(indexOf);
                    this.playUiSound(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 1.0f, 0.675f);
                }
                this.refreshSearchNextTick = true;
                this.moveToTopNextTick = false;
                return true;
            }
        }
        if (hoveredSlot == this.noneHovered || !lmb && !rmb) {
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }
        boolean orderClicked = (Integer)hoveredSlot.getFirst() == -1;
        boolean bl2 = recipeClicked = (Integer)hoveredSlot.getFirst() == -2;
        BigItemStack entry = recipeClicked ? (BigItemStack)this.recipesToOrder.get((Integer)hoveredSlot.getSecond()) : (orderClicked ? this.itemsToOrder.get((Integer)hoveredSlot.getSecond()) : this.displayedItems.get((Integer)hoveredSlot.getFirst()).get((Integer)hoveredSlot.getSecond()));
        ItemStack itemStack = entry.stack;
        int n = StockKeeperRequestScreen.hasShiftDown() ? itemStack.getMaxStackSize() : (transfer = StockKeeperRequestScreen.hasControlDown() ? 10 : 1);
        if (recipeClicked && entry instanceof CraftableBigItemStack) {
            CraftableBigItemStack cbis = (CraftableBigItemStack)entry;
            if (rmb && cbis.count == 0) {
                this.recipesToOrder.remove(cbis);
                return true;
            }
            this.requestCraftable(cbis, rmb ? -transfer : transfer);
            return true;
        }
        BigItemStack existingOrder = this.getOrderForItem(entry.stack);
        if (existingOrder == null) {
            if (this.itemsToOrder.size() >= 9 || rmb) {
                return true;
            }
            existingOrder = new BigItemStack(itemStack.copyWithCount(1), 0);
            this.itemsToOrder.add(existingOrder);
            this.playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.2f);
            this.playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 0.8f);
        }
        int current = existingOrder.count;
        if (rmb || orderClicked) {
            existingOrder.count = current - transfer;
            if (existingOrder.count <= 0) {
                this.itemsToOrder.remove(existingOrder);
                this.playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.8f);
                this.playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 1.8f);
            }
            return true;
        }
        existingOrder.count = current + Math.min(transfer, entry.count - current);
        return true;
    }

    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0 && this.scrollHandleActive) {
            this.scrollHandleActive = false;
            if (this.minecraft.isWindowActive()) {
                GLFW.glfwSetInputMode((long)this.minecraft.getWindow().getWindow(), (int)208897, (int)212993);
            }
        }
        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        BigItemStack existingOrder;
        boolean recipeClicked;
        boolean noHover;
        if (this.addressBox.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return true;
        }
        Couple<Integer> hoveredSlot = this.getHoveredSlot((int)mouseX, (int)mouseY);
        boolean bl = noHover = hoveredSlot == this.noneHovered;
        if (noHover || (Integer)hoveredSlot.getFirst() >= 0 && !StockKeeperRequestScreen.hasShiftDown() && this.getMaxScroll() != 0) {
            int maxScroll = this.getMaxScroll();
            int direction = (int)(Math.ceil(Math.abs(scrollY)) * -Math.signum(scrollY));
            float newTarget = Mth.clamp((int)Math.round(this.itemScroll.getChaseTarget() + (float)direction), (int)0, (int)maxScroll);
            this.itemScroll.chase((double)newTarget, 0.5, LerpedFloat.Chaser.EXP);
            return true;
        }
        boolean orderClicked = (Integer)hoveredSlot.getFirst() == -1;
        boolean bl2 = recipeClicked = (Integer)hoveredSlot.getFirst() == -2;
        BigItemStack entry = recipeClicked ? (BigItemStack)this.recipesToOrder.get((Integer)hoveredSlot.getSecond()) : (orderClicked ? this.itemsToOrder.get((Integer)hoveredSlot.getSecond()) : this.displayedItems.get((Integer)hoveredSlot.getFirst()).get((Integer)hoveredSlot.getSecond()));
        boolean remove = scrollY < 0.0;
        int transfer = Mth.ceil((double)Math.abs(scrollY)) * (StockKeeperRequestScreen.hasControlDown() ? 10 : 1);
        if (recipeClicked && entry instanceof CraftableBigItemStack) {
            CraftableBigItemStack cbis = (CraftableBigItemStack)entry;
            this.requestCraftable(cbis, remove ? -transfer : transfer);
            return true;
        }
        BigItemStack bigItemStack = existingOrder = orderClicked ? entry : this.getOrderForItem(entry.stack);
        if (existingOrder == null) {
            if (this.itemsToOrder.size() >= 9 || remove) {
                return true;
            }
            existingOrder = new BigItemStack(entry.stack.copyWithCount(1), 0);
            this.itemsToOrder.add(existingOrder);
            this.playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.2f);
            this.playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 0.8f);
        }
        int current = existingOrder.count;
        if (remove) {
            existingOrder.count = current - transfer;
            if (existingOrder.count <= 0) {
                this.itemsToOrder.remove(existingOrder);
                this.playUiSound(SoundEvents.WOOL_STEP, 0.75f, 1.8f);
                this.playUiSound(SoundEvents.BAMBOO_WOOD_STEP, 0.75f, 1.8f);
            } else if (existingOrder.count != current) {
                this.playUiSound(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 0.25f, 1.2f);
            }
            return true;
        }
        existingOrder.count = current + Math.min(transfer, this.blockEntity.getLastClientsideStockSnapshotAsSummary().getCountOf(entry.stack) - current);
        if (existingOrder.count != current && current != 0) {
            this.playUiSound(AllSoundEvents.SCROLL_VALUE.getMainEvent(), 0.25f, 1.2f);
        }
        return true;
    }

    private void clampScrollBar() {
        float newTarget;
        int maxScroll = this.getMaxScroll();
        float prevTarget = this.itemScroll.getChaseTarget();
        if (prevTarget != (newTarget = Mth.clamp((float)prevTarget, (float)0.0f, (float)maxScroll))) {
            this.itemScroll.startWithValue((double)newTarget);
        }
    }

    private int getMaxScroll() {
        int visibleHeight = this.windowHeight - 84;
        int totalRows = 2;
        for (int i = 0; i < this.displayedItems.size(); ++i) {
            List<BigItemStack> list = this.displayedItems.get(i);
            if (list.isEmpty()) continue;
            ++totalRows;
            if (this.categories.size() > i && this.categories.get((int)i).hidden) continue;
            totalRows = (int)((double)totalRows + Math.ceil((float)list.size() / 9.0f));
        }
        int maxScroll = Math.max(0, (totalRows * 20 - visibleHeight + 50) / 20);
        return maxScroll;
    }

    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        if (pButton != 0 || !this.scrollHandleActive) {
            return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        }
        Window window = this.minecraft.getWindow();
        double scaleX = (double)window.getGuiScaledWidth() / (double)window.getScreenWidth();
        double scaleY = (double)window.getGuiScaledHeight() / (double)window.getScreenHeight();
        int windowH = this.windowHeight - 92;
        int totalH = this.getMaxScroll() * 20 + windowH;
        int barSize = Math.max(5, Mth.floor((float)((float)windowH / (float)totalH * (float)(windowH - 2))));
        int minY = this.getGuiTop() + 15 + barSize / 2;
        int maxY = this.getGuiTop() + 15 + windowH - barSize / 2;
        if (barSize >= windowH - 2) {
            return true;
        }
        int barX = this.itemsX + 180;
        double target = (pMouseY - (double)this.getGuiTop() - 15.0 - (double)barSize / 2.0) * (double)totalH / (double)(windowH - 2) / 20.0;
        this.itemScroll.chase(Mth.clamp((double)target, (double)0.0, (double)this.getMaxScroll()), 0.8, LerpedFloat.Chaser.EXP);
        if (this.minecraft.isWindowActive()) {
            double forceX = (double)(barX + 2) / scaleX;
            double forceY = Mth.clamp((double)pMouseY, (double)minY, (double)maxY) / scaleY;
            GLFW.glfwSetCursorPos((long)window.getWindow(), (double)forceX, (double)forceY);
        }
        return true;
    }

    public boolean charTyped(char pCodePoint, int pModifiers) {
        if (this.ignoreTextInput) {
            return false;
        }
        if (this.addressBox.isFocused() && this.addressBox.charTyped(pCodePoint, pModifiers)) {
            return true;
        }
        String s = this.searchBox.getValue();
        if (!this.searchBox.charTyped(pCodePoint, pModifiers)) {
            return false;
        }
        if (!Objects.equals(s, this.searchBox.getValue())) {
            this.refreshSearchNextTick = true;
            this.moveToTopNextTick = true;
            this.syncJEI(false);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        this.ignoreTextInput = false;
        if (!this.addressBox.isFocused() && !this.searchBox.isFocused() && this.minecraft.options.keyChat.matches(pKeyCode, pScanCode)) {
            this.ignoreTextInput = true;
            this.searchBox.setFocused(true);
            return true;
        }
        if (pKeyCode == 257 && this.searchBox.isFocused()) {
            this.searchBox.setFocused(false);
            return true;
        }
        if (pKeyCode == 257 && StockKeeperRequestScreen.hasShiftDown()) {
            this.sendIt();
            return true;
        }
        if (this.addressBox.isFocused() && this.addressBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        }
        String s = this.searchBox.getValue();
        if (!this.searchBox.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return this.searchBox.isFocused() && this.searchBox.isVisible() && pKeyCode != 256 || super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
        if (!Objects.equals(s, this.searchBox.getValue())) {
            this.refreshSearchNextTick = true;
            this.moveToTopNextTick = true;
            this.syncJEI(false);
        }
        return true;
    }

    public void removed() {
        BlockPos pos = this.blockEntity.getBlockPos();
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new PackageOrderRequestPacket(pos, PackageOrderWithCrafts.empty(), this.addressBox.getValue(), false));
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new StockKeeperCategoryHidingPacket(pos, new ArrayList<Integer>(this.hiddenCategories)));
        super.removed();
    }

    private void sendIt() {
        this.revalidateOrders();
        if (this.itemsToOrder.isEmpty()) {
            return;
        }
        this.forcedEntries = new InventorySummary();
        InventorySummary summary = this.blockEntity.getLastClientsideStockSnapshotAsSummary();
        for (BigItemStack toOrder : this.itemsToOrder) {
            int countOf = summary.getCountOf(toOrder.stack);
            if (countOf == 1000000000) continue;
            this.forcedEntries.add(toOrder.stack.copy(), -1 - Math.max(0, countOf - toOrder.count));
        }
        PackageOrderWithCrafts order = PackageOrderWithCrafts.simple(this.itemsToOrder);
        if (this.canRequestCraftingPackage && !this.itemsToOrder.isEmpty() && !this.recipesToOrder.isEmpty()) {
            ArrayList<PackageOrderWithCrafts.CraftingEntry> craftList = new ArrayList<PackageOrderWithCrafts.CraftingEntry>();
            block1: for (CraftableBigItemStack cbis : this.recipesToOrder) {
                int availableCrafts;
                Recipe<?> recipe = cbis.recipe;
                if (!(recipe instanceof CraftingRecipe)) continue;
                CraftingRecipe cr = (CraftingRecipe)recipe;
                int targetCount = cbis.count / cbis.getOutputCount(this.blockEntity.getLevel());
                List<BigItemStack> mutableOrder = BigItemStack.duplicateWrappers(this.itemsToOrder);
                for (int craftedCount = 0; craftedCount < targetCount; craftedCount += availableCrafts) {
                    PackageOrder pattern = new PackageOrder(FactoryPanelScreen.convertRecipeToPackageOrderContext(cr, mutableOrder, true));
                    int maxCrafts = targetCount - craftedCount;
                    boolean itemsExhausted = false;
                    block3: for (availableCrafts = 0; availableCrafts < maxCrafts && !itemsExhausted; ++availableCrafts) {
                        List<BigItemStack> previousSnapshot = BigItemStack.duplicateWrappers(mutableOrder);
                        itemsExhausted = true;
                        block4: for (BigItemStack patternStack : pattern.stacks()) {
                            if (patternStack.stack.isEmpty()) continue;
                            for (BigItemStack ordered : mutableOrder) {
                                if (!ItemStack.isSameItemSameComponents((ItemStack)ordered.stack, (ItemStack)patternStack.stack) || ordered.count == 0) continue;
                                --ordered.count;
                                itemsExhausted = false;
                                continue block4;
                            }
                            mutableOrder = previousSnapshot;
                            break block3;
                        }
                    }
                    if (availableCrafts == 0) continue block1;
                    craftList.add(new PackageOrderWithCrafts.CraftingEntry(pattern, availableCrafts));
                }
            }
            order = new PackageOrderWithCrafts(order.orderedStacks(), craftList);
        }
        CatnipServices.NETWORK.sendToServer((CustomPacketPayload)new PackageOrderRequestPacket(this.blockEntity.getBlockPos(), order, this.addressBox.getValue(), this.encodeRequester));
        this.itemsToOrder = new ArrayList<BigItemStack>();
        this.recipesToOrder = new ArrayList<CraftableBigItemStack>();
        this.blockEntity.ticksSinceLastUpdate = 10;
        this.successTicks = 1;
        if (this.isSchematicListMode()) {
            ((StockKeeperRequestMenu)this.menu).player.closeContainer();
        }
    }

    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        this.ignoreTextInput = false;
        return super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public List<Rect2i> getExtraAreas() {
        return this.extraAreas;
    }

    public boolean isSchematicListMode() {
        return this.clipboardItem != null;
    }

    public void requestSchematicList() {
        this.itemsToOrder.clear();
        InventorySummary availableItems = this.blockEntity.getLastClientsideStockSnapshotAsSummary();
        for (List<ClipboardEntry> list : this.clipboardItem) {
            for (ClipboardEntry entry : list) {
                ItemStack stack = entry.icon;
                int toOrder = Math.min(entry.itemAmount, availableItems.getCountOf(stack));
                if (toOrder == 0) continue;
                this.itemsToOrder.add(new BigItemStack(stack, toOrder));
            }
        }
    }

    public void requestCraftable(CraftableBigItemStack cbis, int requestedDifference) {
        boolean takeOrdersAway;
        boolean bl = takeOrdersAway = requestedDifference < 0;
        if (takeOrdersAway) {
            requestedDifference = Math.max(-cbis.count, requestedDifference);
        }
        if (requestedDifference == 0) {
            return;
        }
        InventorySummary availableItems = this.blockEntity.getLastClientsideStockSnapshotAsSummary();
        Function<ItemStack, Integer> countModifier = stack -> {
            BigItemStack ordered = this.getOrderForItem((ItemStack)stack);
            return ordered == null ? 0 : -ordered.count;
        };
        if (takeOrdersAway) {
            availableItems = new InventorySummary();
            for (BigItemStack ordered : this.itemsToOrder) {
                availableItems.add(ordered.stack, ordered.count);
            }
            countModifier = stack -> 0;
        }
        Pair<Integer, List<List<BigItemStack>>> craftingResult = this.maxCraftable(cbis, availableItems, countModifier, takeOrdersAway ? -1 : 9 - this.itemsToOrder.size());
        int outputCount = cbis.getOutputCount(this.blockEntity.getLevel());
        int adjustToRecipeAmount = Mth.ceil((float)((float)Math.abs(requestedDifference) / (float)outputCount)) * outputCount;
        int maxCraftable = Math.min(adjustToRecipeAmount, (Integer)craftingResult.getFirst());
        if (maxCraftable == 0) {
            return;
        }
        cbis.count = cbis.count + (takeOrdersAway ? -maxCraftable : maxCraftable);
        List validEntriesByIngredient = (List)craftingResult.getSecond();
        block1: for (List list : validEntriesByIngredient) {
            int remaining = maxCraftable / outputCount;
            for (BigItemStack entry : list) {
                if (remaining <= 0) continue block1;
                int toTransfer = Math.min(remaining, entry.count);
                BigItemStack order = this.getOrderForItem(entry.stack);
                if (takeOrdersAway) {
                    if (order != null) {
                        order.count -= toTransfer;
                        if (order.count == 0) {
                            this.itemsToOrder.remove(order);
                        }
                    }
                } else {
                    if (order == null) {
                        order = new BigItemStack(entry.stack.copyWithCount(1), 0);
                        this.itemsToOrder.add(order);
                    }
                    order.count += toTransfer;
                }
                remaining -= entry.count;
            }
        }
        this.updateCraftableAmounts();
    }

    private void updateCraftableAmounts() {
        InventorySummary usedItems = new InventorySummary();
        InventorySummary availableItems = new InventorySummary();
        for (BigItemStack ordered : this.itemsToOrder) {
            availableItems.add(ordered.stack, ordered.count);
        }
        for (CraftableBigItemStack cbis : this.recipesToOrder) {
            Pair<Integer, List<List<BigItemStack>>> craftingResult = this.maxCraftable(cbis, availableItems, stack -> -usedItems.getCountOf((ItemStack)stack), -1);
            int maxCraftable = (Integer)craftingResult.getFirst();
            List validEntriesByIngredient = (List)craftingResult.getSecond();
            int outputCount = cbis.getOutputCount(this.blockEntity.getLevel());
            cbis.count = Math.min(cbis.count, maxCraftable);
            block2: for (List list : validEntriesByIngredient) {
                int remaining = cbis.count / outputCount;
                for (BigItemStack entry : list) {
                    if (remaining <= 0) continue block2;
                    usedItems.add(entry.stack, Math.min(remaining, entry.count));
                    remaining -= entry.count;
                }
            }
        }
        this.canRequestCraftingPackage = false;
        for (BigItemStack ordered : this.itemsToOrder) {
            if (usedItems.getCountOf(ordered.stack) == ordered.count) continue;
            return;
        }
        this.canRequestCraftingPackage = true;
    }

    private Pair<Integer, List<List<BigItemStack>>> maxCraftable(CraftableBigItemStack cbis, InventorySummary summary, Function<ItemStack, Integer> countModifier, int newTypeLimit) {
        List<Ingredient> ingredients = cbis.getIngredients();
        List<List<BigItemStack>> validEntriesByIngredient = new ArrayList<List<BigItemStack>>();
        ArrayList<BigItemStack> alreadyCreated = new ArrayList<BigItemStack>();
        for (Ingredient ingredient : ingredients) {
            if (ingredient.isEmpty()) continue;
            ArrayList<BigItemStack> valid = new ArrayList<BigItemStack>();
            for (List<BigItemStack> list : summary.getItemMap().values()) {
                block2: for (BigItemStack entry2 : list) {
                    if (!ingredient.test(entry2.stack)) continue;
                    for (BigItemStack visitedStack : alreadyCreated) {
                        if (!ItemStack.isSameItemSameComponents((ItemStack)visitedStack.stack, (ItemStack)entry2.stack)) continue;
                        valid.add(visitedStack);
                        continue block2;
                    }
                    BigItemStack asBis = new BigItemStack(entry2.stack, summary.getCountOf(entry2.stack) + countModifier.apply(entry2.stack));
                    if (asBis.count <= 0) continue;
                    valid.add(asBis);
                    alreadyCreated.add(asBis);
                }
            }
            if (valid.isEmpty()) {
                return Pair.of((Object)0, List.of());
            }
            Collections.sort(valid, (bis1, bis2) -> -Integer.compare(summary.getCountOf(bis1.stack), summary.getCountOf(bis2.stack)));
            validEntriesByIngredient.add(valid);
        }
        if (newTypeLimit != -1) {
            int toRemove = (int)validEntriesByIngredient.stream().flatMap(l -> l.stream()).filter(entry -> this.getOrderForItem(entry.stack) == null).distinct().count() - newTypeLimit;
            for (int i = 0; i < toRemove; ++i) {
                this.removeLeastEssentialItemStack(validEntriesByIngredient);
            }
        }
        validEntriesByIngredient = this.resolveIngredientAmounts(validEntriesByIngredient);
        int minCount = Integer.MAX_VALUE;
        for (List<BigItemStack> list : validEntriesByIngredient) {
            int sum = 0;
            for (BigItemStack entry3 : list) {
                sum += entry3.count;
            }
            minCount = Math.min(sum, minCount);
        }
        if (minCount == 0) {
            return Pair.of((Object)0, List.of());
        }
        int outputCount = cbis.getOutputCount(this.blockEntity.getLevel());
        return Pair.of((Object)(minCount * outputCount), validEntriesByIngredient);
    }

    private void removeLeastEssentialItemStack(List<List<BigItemStack>> validIngredients) {
        List<BigItemStack> longest = null;
        int most = 0;
        for (List<BigItemStack> list : validIngredients) {
            int count = (int)list.stream().filter(entry -> this.getOrderForItem(entry.stack) == null).count();
            if (longest != null && count <= most) continue;
            longest = list;
            most = count;
        }
        if (longest.isEmpty()) {
            return;
        }
        BigItemStack chosen = null;
        for (int i = 0; i < longest.size(); ++i) {
            BigItemStack entry2 = longest.get(longest.size() - 1 - i);
            if (this.getOrderForItem(entry2.stack) != null) continue;
            chosen = entry2;
            break;
        }
        for (List<BigItemStack> list : validIngredients) {
            list.remove(chosen);
        }
    }

    private List<List<BigItemStack>> resolveIngredientAmounts(List<List<BigItemStack>> validIngredients) {
        ArrayList<List<BigItemStack>> resolvedIngredients = new ArrayList<List<BigItemStack>>();
        for (int i = 0; i < validIngredients.size(); ++i) {
            resolvedIngredients.add(new ArrayList());
        }
        boolean everythingTaken = false;
        while (!everythingTaken) {
            everythingTaken = true;
            block2: for (int i = 0; i < validIngredients.size(); ++i) {
                List<BigItemStack> list = validIngredients.get(i);
                List resolvedList = (List)resolvedIngredients.get(i);
                for (BigItemStack bigItemStack : list) {
                    if (bigItemStack.count == 0) continue;
                    --bigItemStack.count;
                    everythingTaken = false;
                    for (BigItemStack resolvedItemStack : resolvedList) {
                        if (resolvedItemStack.stack != bigItemStack.stack) continue;
                        ++resolvedItemStack.count;
                        continue block2;
                    }
                    resolvedList.add(new BigItemStack(bigItemStack.stack, 1));
                    continue block2;
                }
            }
        }
        return resolvedIngredients;
    }

    private boolean shouldSyncFromJEI() {
        if (Mods.JEI.isLoaded()) {
            boolean hasFocus = CreateJEI.runtime.getIngredientListOverlay().hasKeyboardFocus();
            return hasFocus && !this.previousJEISearchText.equals(CreateJEI.runtime.getIngredientFilter().getFilterText());
        }
        return false;
    }

    private void syncJEI(boolean fromJei) {
        if (!Mods.JEI.isLoaded()) {
            return;
        }
        SearchSyncMode mode = (SearchSyncMode)((Object)AllConfigs.client().syncRecipeViewerSearch.get());
        if (mode == SearchSyncMode.NONE) {
            return;
        }
        IIngredientFilter filter = CreateJEI.runtime.getIngredientFilter();
        if (mode.isBothOr(SearchSyncMode.SYNC_FROM_JEI) && fromJei) {
            this.previousJEISearchText = filter.getFilterText();
            this.searchBox.setValue(this.previousJEISearchText);
        } else if (mode.isBothOr(SearchSyncMode.SYNC_FROM_STOCK_KEEPER) && !fromJei) {
            filter.setFilterText(this.searchBox.getValue());
        }
    }

    public static class CategoryEntry {
        boolean hidden;
        String name;
        int y;
        int targetBECategory;

        public CategoryEntry(int targetBECategory, String name, int y) {
            this.targetBECategory = targetBECategory;
            this.name = name;
            this.hidden = false;
            this.y = y;
        }
    }

    public static enum SearchSyncMode implements StringRepresentable
    {
        SYNC_BOTH(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_BOTH),
        SYNC_FROM_JEI(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_JEI),
        SYNC_FROM_STOCK_KEEPER(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_FROM_STOCK_KEEPER),
        NONE(AllGuiTextures.STOCK_KEEPER_SEARCH_SYNC_DISABLED);

        public final AllGuiTextures buttonTexture;

        private SearchSyncMode(AllGuiTextures buttonTexture) {
            this.buttonTexture = buttonTexture;
        }

        public boolean isBothOr(SearchSyncMode mode) {
            return this == SYNC_BOTH || this == mode;
        }

        public SearchSyncMode next() {
            SearchSyncMode[] vals = SearchSyncMode.values();
            return vals[(this.ordinal() + 1) % vals.length];
        }

        public static void cycleConfig() {
            ConfigBase.ConfigEnum<SearchSyncMode> modeConfig = AllConfigs.client().syncRecipeViewerSearch;
            modeConfig.set((Object)((SearchSyncMode)((Object)modeConfig.get())).next());
        }

        public String getSerializedName() {
            return Lang.asId((String)this.name());
        }
    }
}
