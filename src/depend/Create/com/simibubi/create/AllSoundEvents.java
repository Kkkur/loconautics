/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataGenerator
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.neoforged.neoforge.registries.DeferredHolder
 *  net.neoforged.neoforge.registries.RegisterEvent
 *  net.neoforged.neoforge.registries.RegisterEvent$RegisterHelper
 */
package com.simibubi.create;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.Create;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.RegisterEvent;

public class AllSoundEvents {
    public static final Map<ResourceLocation, SoundEntry> ALL = new HashMap<ResourceLocation, SoundEntry>();
    public static final SoundEntry SCHEMATICANNON_LAUNCH_BLOCK = AllSoundEvents.create("schematicannon_launch_block").subtitle("Schematicannon fires").playExisting((SoundEvent)SoundEvents.GENERIC_EXPLODE.value(), 0.1f, 1.1f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry SCHEMATICANNON_FINISH = AllSoundEvents.create("schematicannon_finish").subtitle("Schematicannon dings").playExisting(() -> ((Holder.Reference)SoundEvents.NOTE_BLOCK_BELL).value(), 1.0f, 0.7f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry DEPOT_SLIDE = AllSoundEvents.create("depot_slide").subtitle("Item slides").playExisting(SoundEvents.SAND_BREAK, 0.125f, 1.5f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry DEPOT_PLOP = AllSoundEvents.create("depot_plop").subtitle("Item lands").playExisting(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.25f, 1.25f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry FUNNEL_FLAP = AllSoundEvents.create("funnel_flap").subtitle("Funnel flaps").playExisting(SoundEvents.ITEM_FRAME_ROTATE_ITEM, 0.125f, 1.5f).playExisting(SoundEvents.WOOL_BREAK, 0.0425f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry PACKAGER = AllSoundEvents.create("packager").subtitle("Packager packages").playExisting(SoundEvents.SHULKER_OPEN, 0.5f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry SLIME_ADDED = AllSoundEvents.create("slime_added").subtitle("Slime squishes").playExisting(SoundEvents.SLIME_BLOCK_PLACE).category(SoundSource.BLOCKS).build();
    public static final SoundEntry MECHANICAL_PRESS_ACTIVATION = AllSoundEvents.create("mechanical_press_activation").subtitle("Mechanical Press clangs").playExisting(SoundEvents.ANVIL_LAND, 0.125f, 1.0f).playExisting(SoundEvents.ITEM_BREAK, 0.5f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry MECHANICAL_PRESS_ACTIVATION_ON_BELT = AllSoundEvents.create("mechanical_press_activation_belt").subtitle("Mechanical Press bonks").playExisting(SoundEvents.WOOL_HIT, 0.75f, 1.0f).playExisting(SoundEvents.ITEM_BREAK, 0.15f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry MIXING = AllSoundEvents.create("mixing").subtitle("Mixing noises").playExisting(SoundEvents.GILDED_BLACKSTONE_BREAK, 0.125f, 0.5f).playExisting(SoundEvents.NETHERRACK_BREAK, 0.125f, 0.5f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry SPOUTING = AllSoundEvents.create("spout").subtitle("Spout spurts").addVariant("spout_1").addVariant("spout_2").addVariant("spout_3").category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRANKING = AllSoundEvents.create("cranking").subtitle("Hand Crank turns").playExisting(SoundEvents.WOOD_PLACE, 0.075f, 0.5f).playExisting(SoundEvents.WOODEN_BUTTON_CLICK_OFF, 0.025f, 0.5f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry WORLDSHAPER_PLACE = AllSoundEvents.create("worldshaper_place").subtitle("Worldshaper zaps").playExisting((Holder<SoundEvent>)SoundEvents.NOTE_BLOCK_BASEDRUM).category(SoundSource.PLAYERS).build();
    public static final SoundEntry SCROLL_VALUE = AllSoundEvents.create("scroll_value").subtitle("Scroll-input clicks").playExisting(() -> ((Holder.Reference)SoundEvents.NOTE_BLOCK_HAT).value(), 0.124f, 1.0f).category(SoundSource.PLAYERS).build();
    public static final SoundEntry CONFIRM = AllSoundEvents.create("confirm").subtitle("Affirmative ding").playExisting(() -> ((Holder.Reference)SoundEvents.NOTE_BLOCK_BELL).value(), 0.5f, 0.8f).category(SoundSource.PLAYERS).build();
    public static final SoundEntry CONFIRM_2 = AllSoundEvents.create("confirm_2").subtitle("Affirmative ding").category(SoundSource.PLAYERS).build();
    public static final SoundEntry DENY = AllSoundEvents.create("deny").subtitle("Declining boop").playExisting(() -> ((Holder.Reference)SoundEvents.NOTE_BLOCK_BASS).value(), 1.0f, 0.5f).category(SoundSource.PLAYERS).build();
    public static final SoundEntry COGS = AllSoundEvents.create("cogs").subtitle("Cogwheels rumble").category(SoundSource.BLOCKS).build();
    public static final SoundEntry FWOOMP = AllSoundEvents.create("fwoomp").subtitle("Resonant fwoomp").category(SoundSource.PLAYERS).build();
    public static final SoundEntry CARDBOARD_SWORD = AllSoundEvents.create("cardboard_bonk").subtitle("Resonant bonk").category(SoundSource.PLAYERS).build();
    public static final SoundEntry FROGPORT_OPEN = AllSoundEvents.create("frogport_open").subtitle("Frogport opens").playExisting(SoundEvents.WARDEN_TENDRIL_CLICKS, 1.0f, 2.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry FROGPORT_CLOSE = AllSoundEvents.create("frogport_close").subtitle("Frogport shuts").category(SoundSource.BLOCKS).build();
    public static final SoundEntry FROGPORT_CATCH = AllSoundEvents.create("frogport_catch").subtitle("Frogport catches package").addVariant("frogport_catch_1").addVariant("frogport_catch_2").addVariant("frogport_catch_3").category(SoundSource.BLOCKS).build();
    public static final SoundEntry STOCK_LINK = AllSoundEvents.create("stock_link").subtitle("Stock link reacts").category(SoundSource.BLOCKS).build();
    public static final SoundEntry FROGPORT_DEPOSIT = AllSoundEvents.create("frogport_deposit").subtitle("Frogport places package").playExisting(SoundEvents.FROG_TONGUE, 1.0f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry POTATO_HIT = AllSoundEvents.create("potato_hit").subtitle("Vegetable impacts").playExisting(SoundEvents.ITEM_FRAME_BREAK, 0.75f, 0.75f).playExisting(SoundEvents.WEEPING_VINES_BREAK, 0.75f, 1.25f).category(SoundSource.PLAYERS).build();
    public static final SoundEntry CONTRAPTION_ASSEMBLE = AllSoundEvents.create("contraption_assemble").subtitle("Contraption moves").playExisting(SoundEvents.WOODEN_TRAPDOOR_OPEN, 0.5f, 0.5f).playExisting(SoundEvents.CHEST_OPEN, 0.045f, 0.74f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CONTRAPTION_DISASSEMBLE = AllSoundEvents.create("contraption_disassemble").subtitle("Contraption stops").playExisting(SoundEvents.IRON_TRAPDOOR_CLOSE, 0.35f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry WRENCH_ROTATE = AllSoundEvents.create("wrench_rotate").subtitle("Wrench used").playExisting(SoundEvents.WOODEN_TRAPDOOR_CLOSE, 0.25f, 1.25f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry WRENCH_REMOVE = AllSoundEvents.create("wrench_remove").subtitle("Component breaks").playExisting(SoundEvents.ITEM_PICKUP, 0.25f, 0.75f).playExisting(SoundEvents.NETHERITE_BLOCK_HIT, 0.25f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry PACKAGE_POP = AllSoundEvents.create("package_pop").subtitle("Package breaks").playExisting(SoundEvents.CHISELED_BOOKSHELF_BREAK, 0.75f, 1.0f).playExisting(SoundEvents.WOOL_BREAK, 0.25f, 1.15f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRAFTER_CLICK = AllSoundEvents.create("crafter_click").subtitle("Crafter clicks").playExisting(SoundEvents.NETHERITE_BLOCK_HIT, 0.25f, 1.0f).playExisting(SoundEvents.WOODEN_TRAPDOOR_OPEN, 0.125f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRAFTER_CRAFT = AllSoundEvents.create("crafter_craft").subtitle("Crafter crafts").playExisting(SoundEvents.ITEM_BREAK, 0.125f, 0.75f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry COPPER_ARMOR_EQUIP = AllSoundEvents.create("copper_armor_equip").subtitle("Diving equipment clinks").playExisting((SoundEvent)SoundEvents.ARMOR_EQUIP_GOLD.value(), 1.0f, 1.0f).category(SoundSource.PLAYERS).build();
    public static final SoundEntry SANDING_SHORT = AllSoundEvents.create("sanding_short").subtitle("Sanding noises").addVariant("sanding_short_1").category(SoundSource.BLOCKS).build();
    public static final SoundEntry SANDING_LONG = AllSoundEvents.create("sanding_long").subtitle("Sanding noises").category(SoundSource.BLOCKS).build();
    public static final SoundEntry CONTROLLER_CLICK = AllSoundEvents.create("controller_click").subtitle("Controller clicks").playExisting(SoundEvents.ITEM_FRAME_ADD_ITEM, 0.35f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CONTROLLER_PUT = AllSoundEvents.create("controller_put").subtitle("Controller thumps").playExisting(SoundEvents.BOOK_PUT, 1.0f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CONTROLLER_TAKE = AllSoundEvents.create("controller_take").subtitle("Lectern empties").playExisting(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry SAW_ACTIVATE_WOOD = AllSoundEvents.create("saw_activate_wood").subtitle("Mechanical Saw activates").playExisting(SoundEvents.BOAT_PADDLE_LAND, 0.75f, 1.5f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry SAW_ACTIVATE_STONE = AllSoundEvents.create("saw_activate_stone").subtitle("Mechanical Saw activates").playExisting(SoundEvents.UI_STONECUTTER_TAKE_RESULT, 0.125f, 1.25f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry BLAZE_MUNCH = AllSoundEvents.create("blaze_munch").subtitle("Blaze Burner munches").playExisting(SoundEvents.GENERIC_EAT, 0.5f, 1.0f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry ITEM_HATCH = AllSoundEvents.create("item_hatch").subtitle("Item Hatch opens").playExisting(SoundEvents.BARREL_OPEN, 0.25f, 1.4f).playExisting(SoundEvents.NETHERITE_BLOCK_PLACE, 0.75f, 1.15f).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRUSHING_1 = AllSoundEvents.create("crushing_1").subtitle("Crushing noises").playExisting(SoundEvents.NETHERRACK_HIT).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRUSHING_2 = AllSoundEvents.create("crushing_2").noSubtitle().playExisting(SoundEvents.GRAVEL_PLACE).category(SoundSource.BLOCKS).build();
    public static final SoundEntry CRUSHING_3 = AllSoundEvents.create("crushing_3").noSubtitle().playExisting(SoundEvents.NETHERITE_BLOCK_BREAK).category(SoundSource.BLOCKS).build();
    public static final SoundEntry PECULIAR_BELL_USE = AllSoundEvents.create("peculiar_bell_use").subtitle("Peculiar Bell tolls").playExisting(SoundEvents.BELL_BLOCK).category(SoundSource.BLOCKS).build();
    public static final SoundEntry DESK_BELL_USE = AllSoundEvents.create("desk_bell").subtitle("Reception bell dings").category(SoundSource.BLOCKS).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_HIGH = AllSoundEvents.create("whistle_high").subtitle("High whistling").category(SoundSource.RECORDS).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_MEDIUM = AllSoundEvents.create("whistle").subtitle("Whistling").category(SoundSource.RECORDS).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_LOW = AllSoundEvents.create("whistle_low").subtitle("Low whistling").category(SoundSource.RECORDS).attenuationDistance(64).build();
    public static final SoundEntry STEAM = AllSoundEvents.create("steam").subtitle("Steam noises").category(SoundSource.NEUTRAL).attenuationDistance(32).build();
    public static final SoundEntry TRAIN = AllSoundEvents.create("train").subtitle("Bogey wheels rumble").category(SoundSource.NEUTRAL).attenuationDistance(128).build();
    public static final SoundEntry TRAIN2 = AllSoundEvents.create("train2").noSubtitle().category(SoundSource.NEUTRAL).attenuationDistance(128).build();
    public static final SoundEntry TRAIN3 = AllSoundEvents.create("train3").subtitle("Bogey wheels rumble muffled").category(SoundSource.NEUTRAL).attenuationDistance(16).build();
    public static final SoundEntry WHISTLE_TRAIN = AllSoundEvents.create("whistle_train").subtitle("Whistling").category(SoundSource.RECORDS).build();
    public static final SoundEntry WHISTLE_TRAIN_LOW = AllSoundEvents.create("whistle_train_low").subtitle("Low whistling").category(SoundSource.RECORDS).build();
    public static final SoundEntry WHISTLE_TRAIN_MANUAL = AllSoundEvents.create("whistle_train_manual").subtitle("Train honks").category(SoundSource.NEUTRAL).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_TRAIN_MANUAL_LOW = AllSoundEvents.create("whistle_train_manual_low").subtitle("Train honks").category(SoundSource.NEUTRAL).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_TRAIN_MANUAL_END = AllSoundEvents.create("whistle_train_manual_end").noSubtitle().category(SoundSource.NEUTRAL).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_TRAIN_MANUAL_LOW_END = AllSoundEvents.create("whistle_train_manual_low_end").noSubtitle().category(SoundSource.NEUTRAL).attenuationDistance(64).build();
    public static final SoundEntry WHISTLE_CHIFF = AllSoundEvents.create("chiff").noSubtitle().category(SoundSource.RECORDS).build();
    public static final SoundEntry HAUNTED_BELL_CONVERT = AllSoundEvents.create("haunted_bell_convert").subtitle("Haunted Bell awakens").category(SoundSource.BLOCKS).build();
    public static final SoundEntry HAUNTED_BELL_USE = AllSoundEvents.create("haunted_bell_use").subtitle("Haunted Bell tolls").category(SoundSource.BLOCKS).build();
    public static final SoundEntry STOCK_TICKER_REQUEST = AllSoundEvents.create("stock_ticker_request").subtitle("Stock ticker requests").category(SoundSource.BLOCKS).build();
    public static final SoundEntry STOCK_TICKER_TRADE = AllSoundEvents.create("stock_ticker_trade").subtitle("Stock ticker goes 'ka-ching!'").category(SoundSource.BLOCKS).build();
    public static final SoundEntry CLIPBOARD_CHECKMARK = AllSoundEvents.create("clipboard_check").noSubtitle().category(SoundSource.BLOCKS).build();
    public static final SoundEntry CLIPBOARD_ERASE = AllSoundEvents.create("clipboard_erase").noSubtitle().category(SoundSource.BLOCKS).build();

    private static SoundEntryBuilder create(String name) {
        return AllSoundEvents.create(Create.asResource(name));
    }

    public static SoundEntryBuilder create(ResourceLocation id) {
        return new SoundEntryBuilder(id);
    }

    public static void prepare() {
        for (SoundEntry entry : ALL.values()) {
            entry.prepare();
        }
    }

    public static void register(RegisterEvent event) {
        event.register(Registries.SOUND_EVENT, helper -> {
            for (SoundEntry entry : ALL.values()) {
                entry.register((RegisterEvent.RegisterHelper<SoundEvent>)helper);
            }
        });
    }

    public static void provideLang(BiConsumer<String, String> consumer) {
        for (SoundEntry entry : ALL.values()) {
            if (!entry.hasSubtitle()) continue;
            consumer.accept(entry.getSubtitleKey(), entry.getSubtitle());
        }
    }

    public static SoundEntryProvider provider(DataGenerator generator) {
        return new SoundEntryProvider(generator);
    }

    public static void playItemPickup(Player player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, 1.0f + player.level().random.nextFloat());
    }

    public static class SoundEntryBuilder {
        protected ResourceLocation id;
        protected String subtitle = "unregistered";
        protected SoundSource category = SoundSource.BLOCKS;
        protected List<ConfiguredSoundEvent> wrappedEvents = new ArrayList<ConfiguredSoundEvent>();
        protected List<ResourceLocation> variants = new ArrayList<ResourceLocation>();
        protected int attenuationDistance;

        public SoundEntryBuilder(ResourceLocation id) {
            this.id = id;
        }

        public SoundEntryBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public SoundEntryBuilder attenuationDistance(int distance) {
            this.attenuationDistance = distance;
            return this;
        }

        public SoundEntryBuilder noSubtitle() {
            this.subtitle = null;
            return this;
        }

        public SoundEntryBuilder category(SoundSource category) {
            this.category = category;
            return this;
        }

        public SoundEntryBuilder addVariant(String name) {
            return this.addVariant(Create.asResource(name));
        }

        public SoundEntryBuilder addVariant(ResourceLocation id) {
            this.variants.add(id);
            return this;
        }

        public SoundEntryBuilder playExisting(Supplier<SoundEvent> event, float volume, float pitch) {
            this.wrappedEvents.add(new ConfiguredSoundEvent(event, volume, pitch));
            return this;
        }

        public SoundEntryBuilder playExisting(SoundEvent event, float volume, float pitch) {
            return this.playExisting(() -> event, volume, pitch);
        }

        public SoundEntryBuilder playExisting(SoundEvent event) {
            return this.playExisting(event, 1.0f, 1.0f);
        }

        public SoundEntryBuilder playExisting(Holder<SoundEvent> event) {
            return this.playExisting(() -> event.value(), 1.0f, 1.0f);
        }

        public SoundEntry build() {
            SoundEntry entry = this.wrappedEvents.isEmpty() ? new CustomSoundEntry(this.id, this.variants, this.subtitle, this.category, this.attenuationDistance) : new WrappedSoundEntry(this.id, this.subtitle, this.wrappedEvents, this.category, this.attenuationDistance);
            ALL.put(entry.getId(), entry);
            return entry;
        }
    }

    public static abstract class SoundEntry {
        protected ResourceLocation id;
        protected String subtitle;
        protected SoundSource category;
        protected int attenuationDistance;

        public SoundEntry(ResourceLocation id, String subtitle, SoundSource category, int attenuationDistance) {
            this.id = id;
            this.subtitle = subtitle;
            this.category = category;
            this.attenuationDistance = attenuationDistance;
        }

        public abstract void prepare();

        public abstract void register(RegisterEvent.RegisterHelper<SoundEvent> var1);

        public abstract void write(JsonObject var1);

        public abstract Holder<SoundEvent> getMainEventHolder();

        public abstract SoundEvent getMainEvent();

        public String getSubtitleKey() {
            return this.id.getNamespace() + ".subtitle." + this.id.getPath();
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public boolean hasSubtitle() {
            return this.subtitle != null;
        }

        public String getSubtitle() {
            return this.subtitle;
        }

        public void playOnServer(Level world, Vec3i pos) {
            this.playOnServer(world, pos, 1.0f, 1.0f);
        }

        public void playOnServer(Level world, Vec3i pos, float volume, float pitch) {
            this.play(world, null, pos, volume, pitch);
        }

        public void play(Level world, Player entity, Vec3i pos) {
            this.play(world, entity, pos, 1.0f, 1.0f);
        }

        public void playFrom(Entity entity) {
            this.playFrom(entity, 1.0f, 1.0f);
        }

        public void playFrom(Entity entity, float volume, float pitch) {
            if (!entity.isSilent()) {
                this.play(entity.level(), null, (Vec3i)entity.blockPosition(), volume, pitch);
            }
        }

        public void play(Level world, Player entity, Vec3i pos, float volume, float pitch) {
            this.play(world, entity, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, volume, pitch);
        }

        public void play(Level world, Player entity, Vec3 pos, float volume, float pitch) {
            this.play(world, entity, pos.x(), pos.y(), pos.z(), volume, pitch);
        }

        public abstract void play(Level var1, Player var2, double var3, double var5, double var7, float var9, float var10);

        public void playAt(Level world, Vec3i pos, float volume, float pitch, boolean fade) {
            this.playAt(world, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, volume, pitch, fade);
        }

        public void playAt(Level world, Vec3 pos, float volume, float pitch, boolean fade) {
            this.playAt(world, pos.x(), pos.y(), pos.z(), volume, pitch, fade);
        }

        public abstract void playAt(Level var1, double var2, double var4, double var6, float var8, float var9, boolean var10);
    }

    public static class SoundEntryProvider
    implements DataProvider {
        private PackOutput output;

        public SoundEntryProvider(DataGenerator generator) {
            this.output = generator.getPackOutput();
        }

        public CompletableFuture<?> run(CachedOutput cache) {
            return this.generate(this.output.getOutputFolder(), cache);
        }

        public String getName() {
            return "Create's Custom Sounds";
        }

        public CompletableFuture<?> generate(Path path, CachedOutput cache) {
            path = path.resolve("assets/create");
            JsonObject json = new JsonObject();
            ALL.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> ((SoundEntry)entry.getValue()).write(json));
            return DataProvider.saveStable((CachedOutput)cache, (JsonElement)json, (Path)path.resolve("sounds.json"));
        }
    }

    private static class CustomSoundEntry
    extends SoundEntry {
        protected List<ResourceLocation> variants;
        protected DeferredHolder<SoundEvent, SoundEvent> event;

        public CustomSoundEntry(ResourceLocation id, List<ResourceLocation> variants, String subtitle, SoundSource category, int attenuationDistance) {
            super(id, subtitle, category, attenuationDistance);
            this.variants = variants;
        }

        @Override
        public void prepare() {
            this.event = DeferredHolder.create((ResourceKey)Registries.SOUND_EVENT, (ResourceLocation)this.id);
        }

        @Override
        public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
            ResourceLocation location = this.event.getId();
            helper.register(location, (Object)SoundEvent.createVariableRangeEvent((ResourceLocation)location));
        }

        @Override
        public Holder<SoundEvent> getMainEventHolder() {
            return this.event;
        }

        @Override
        public SoundEvent getMainEvent() {
            return (SoundEvent)this.event.get();
        }

        @Override
        public void write(JsonObject json) {
            JsonObject entry = new JsonObject();
            JsonArray list = new JsonArray();
            JsonObject s = new JsonObject();
            s.addProperty("name", this.id.toString());
            s.addProperty("type", "file");
            if (this.attenuationDistance != 0) {
                s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
            }
            list.add((JsonElement)s);
            for (ResourceLocation variant : this.variants) {
                s = new JsonObject();
                s.addProperty("name", variant.toString());
                s.addProperty("type", "file");
                if (this.attenuationDistance != 0) {
                    s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
                }
                list.add((JsonElement)s);
            }
            entry.add("sounds", (JsonElement)list);
            if (this.hasSubtitle()) {
                entry.addProperty("subtitle", this.getSubtitleKey());
            }
            json.add(this.id.getPath(), (JsonElement)entry);
        }

        @Override
        public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
            world.playSound(entity, x, y, z, (SoundEvent)this.event.get(), this.category, volume, pitch);
        }

        @Override
        public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
            world.playLocalSound(x, y, z, (SoundEvent)this.event.get(), this.category, volume, pitch, fade);
        }
    }

    private static class WrappedSoundEntry
    extends SoundEntry {
        private List<ConfiguredSoundEvent> wrappedEvents;
        private List<CompiledSoundEvent> compiledEvents;

        public WrappedSoundEntry(ResourceLocation id, String subtitle, List<ConfiguredSoundEvent> wrappedEvents, SoundSource category, int attenuationDistance) {
            super(id, subtitle, category, attenuationDistance);
            this.wrappedEvents = wrappedEvents;
            this.compiledEvents = new ArrayList<CompiledSoundEvent>();
        }

        @Override
        public void prepare() {
            for (int i = 0; i < this.wrappedEvents.size(); ++i) {
                ConfiguredSoundEvent wrapped = this.wrappedEvents.get(i);
                ResourceLocation location = this.getIdOf(i);
                DeferredHolder event = DeferredHolder.create((ResourceKey)Registries.SOUND_EVENT, (ResourceLocation)location);
                this.compiledEvents.add(new CompiledSoundEvent((DeferredHolder<SoundEvent, SoundEvent>)event, wrapped.volume(), wrapped.pitch()));
            }
        }

        @Override
        public void register(RegisterEvent.RegisterHelper<SoundEvent> helper) {
            for (CompiledSoundEvent compiledEvent : this.compiledEvents) {
                ResourceLocation location = compiledEvent.event().getId();
                helper.register(location, (Object)SoundEvent.createVariableRangeEvent((ResourceLocation)location));
            }
        }

        @Override
        public Holder<SoundEvent> getMainEventHolder() {
            return this.compiledEvents.getFirst().event();
        }

        @Override
        public SoundEvent getMainEvent() {
            return (SoundEvent)this.compiledEvents.getFirst().event().get();
        }

        protected ResourceLocation getIdOf(int i) {
            return ResourceLocation.fromNamespaceAndPath((String)this.id.getNamespace(), (String)(i == 0 ? this.id.getPath() : this.id.getPath() + "_compounded_" + i));
        }

        @Override
        public void write(JsonObject json) {
            for (int i = 0; i < this.wrappedEvents.size(); ++i) {
                ConfiguredSoundEvent event = this.wrappedEvents.get(i);
                JsonObject entry = new JsonObject();
                JsonArray list = new JsonArray();
                JsonObject s = new JsonObject();
                s.addProperty("name", event.event().get().getLocation().toString());
                s.addProperty("type", "event");
                if (this.attenuationDistance != 0) {
                    s.addProperty("attenuation_distance", (Number)this.attenuationDistance);
                }
                list.add((JsonElement)s);
                entry.add("sounds", (JsonElement)list);
                if (i == 0 && this.hasSubtitle()) {
                    entry.addProperty("subtitle", this.getSubtitleKey());
                }
                json.add(this.getIdOf(i).getPath(), (JsonElement)entry);
            }
        }

        @Override
        public void play(Level world, Player entity, double x, double y, double z, float volume, float pitch) {
            for (CompiledSoundEvent event : this.compiledEvents) {
                world.playSound(entity, x, y, z, (SoundEvent)event.event().get(), this.category, event.volume() * volume, event.pitch() * pitch);
            }
        }

        @Override
        public void playAt(Level world, double x, double y, double z, float volume, float pitch, boolean fade) {
            for (CompiledSoundEvent event : this.compiledEvents) {
                world.playLocalSound(x, y, z, (SoundEvent)event.event().get(), this.category, event.volume() * volume, event.pitch() * pitch, fade);
            }
        }

        private record CompiledSoundEvent(DeferredHolder<SoundEvent, SoundEvent> event, float volume, float pitch) {
        }
    }

    public record ConfiguredSoundEvent(Supplier<SoundEvent> event, float volume, float pitch) {
    }
}
