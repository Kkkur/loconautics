/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllKeys
 *  com.simibubi.create.content.equipment.goggles.GogglesItem
 *  dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension
 *  dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes
 *  dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes$PhysicsBlockPropertyType
 *  dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.createmod.ponder.foundation.ui.PonderUI
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.TooltipFlag
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.simulated_team.simulated.client;

import com.simibubi.create.AllKeys;
import com.simibubi.create.content.equipment.goggles.GogglesItem;
import dev.ryanhcode.sable.mixinterface.block_properties.BlockStateExtension;
import dev.ryanhcode.sable.physics.config.FloatingBlockMaterialDataHandler;
import dev.ryanhcode.sable.physics.config.block_properties.PhysicsBlockPropertyTypes;
import dev.ryanhcode.sable.physics.floating_block.FloatingBlockMaterial;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimRegistries;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.mixin.accessor.BlockBehaviourAccessor;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.util.SimColors;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import net.createmod.ponder.foundation.ui.PonderUI;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockPropertiesTooltip {
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    private static final Component NONE = Component.translatable((String)"simulated.tooltip.mass.none").withStyle(ChatFormatting.GRAY);
    private static final Component SUPER_LIGHT = Component.translatable((String)"simulated.tooltip.mass.super_light").withStyle(ChatFormatting.AQUA);
    private static final Component LIGHT = Component.translatable((String)"simulated.tooltip.mass.light").withStyle(ChatFormatting.GREEN);
    private static final Component HEAVY = Component.translatable((String)"simulated.tooltip.mass.heavy").withStyle(ChatFormatting.YELLOW);
    private static final Component SUPER_HEAVY = Component.translatable((String)"simulated.tooltip.mass.super_heavy").withColor(SimColors.NUH_UH_RED);
    private static final Component ABSURDLY_HEAVY = Component.translatable((String)"simulated.tooltip.mass.absurdly_heavy").withColor(SimColors.NUH_UH_RED);
    private static final Component BOUNCY = Component.translatable((String)"simulated.tooltip.bouncy").withStyle(ChatFormatting.GREEN);
    private static final Component SLIPPERY = Component.translatable((String)"simulated.tooltip.friction.slippery").withStyle(ChatFormatting.AQUA);
    private static final Component STICKY = Component.translatable((String)"simulated.tooltip.friction.sticky").withStyle(ChatFormatting.DARK_GREEN);
    private static final Component FRAGILE = Component.translatable((String)"simulated.tooltip.fragile").withColor(SimColors.NUH_UH_RED);
    private static final Component AIRTIGHT = Component.translatable((String)"simulated.tooltip.airtight").withStyle(ChatFormatting.WHITE);
    private static final Component FLOATING = Component.translatable((String)"simulated.tooltip.floating").withStyle(ChatFormatting.DARK_GREEN);

    public static boolean shouldShowTooltip(Condition condition, TooltipFlag iTooltipFlag, @Nullable Player player) {
        if (Minecraft.getInstance().screen instanceof PonderUI) {
            return true;
        }
        if (player == null) {
            return condition.allows();
        }
        return condition.test(AllKeys.isKeyDown((int)340), GogglesItem.isWearingGoggles((Player)player));
    }

    public static void register(SimulatedRegistrate registrate, String name, TooltipFunction tooltipFunction, float priority) {
        registrate.propertyTooltip(name, () -> new Entry(tooltipFunction, priority));
    }

    public static void init() {
        SimulatedRegistrate registrate = Simulated.getRegistrate();
        int priority = 0;
        BlockPropertiesTooltip.register(registrate, "mass", BlockPropertiesTooltip::getMassComponent, priority++);
        BlockPropertiesTooltip.register(registrate, "friction", BlockPropertiesTooltip::getFrictionComponent, priority++);
        BlockPropertiesTooltip.register(registrate, "restitution", BlockPropertiesTooltip::getRestitutionComponent, priority++);
        BlockPropertiesTooltip.register(registrate, "fragile", BlockPropertiesTooltip::getFragileComponent, priority++);
        BlockPropertiesTooltip.register(registrate, "airtight", BlockPropertiesTooltip::getAirtightComponent, priority++);
        BlockPropertiesTooltip.register(registrate, "floating", BlockPropertiesTooltip::getFloatingComponent, priority++);
    }

    public static void appendTooltip(ItemStack stack, TooltipFlag iTooltipFlag, Player player, List<Component> itemTooltip) {
        Item item = stack.getItem();
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem)item;
            boolean showNumbers = true;
            BlockStateExtension properties = (BlockStateExtension)blockItem.getBlock().defaultBlockState();
            ObjectArrayList toAdd = new ObjectArrayList();
            SimRegistries.PROPERTY_TOOLTIP.entrySet().stream().sorted(Map.Entry.comparingByValue()).forEach(arg_0 -> BlockPropertiesTooltip.lambda$appendTooltip$1(properties, blockItem, (List)toAdd, arg_0));
            if (!toAdd.isEmpty()) {
                for (Component property : toAdd) {
                    itemTooltip.add((Component)Component.literal((String)" ").append(property));
                }
            }
        }
    }

    public static Component getMassComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        double mass;
        double d = mass = ((BlockBehaviourAccessor)item.getBlock()).getHasCollision() ? (Double)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.MASS.get()) : 0.0;
        if (mass == 1.0) {
            return null;
        }
        Component comp = mass <= 0.0 ? NONE : (mass <= 0.25 ? SUPER_LIGHT : (mass <= 0.5 ? LIGHT : (mass < 4.0 ? HEAVY : (mass < 50.0 ? SUPER_HEAVY : ABSURDLY_HEAVY))));
        if (showNumbers) {
            return Component.empty().append(comp).append((Component)BlockPropertiesTooltip.formatValue("simulated.unit.mass", mass).withStyle(ChatFormatting.DARK_GRAY));
        }
        return comp;
    }

    @Nullable
    public static Component getRestitutionComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        double restitution = (Double)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.RESTITUTION.get());
        if (restitution == 0.0) {
            return null;
        }
        if (showNumbers) {
            return Component.empty().append(BOUNCY).append((Component)BlockPropertiesTooltip.formatValue("simulated.unit.restitution", restitution * 100.0).withStyle(ChatFormatting.DARK_GRAY));
        }
        return BOUNCY;
    }

    @Nullable
    public static Component getFrictionComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        double friction = (Double)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRICTION.get());
        if (friction == 1.0) {
            return null;
        }
        Component comp = friction < 1.0 ? SLIPPERY : STICKY;
        if (showNumbers) {
            return Component.empty().append(comp).append((Component)BlockPropertiesTooltip.formatValue("simulated.unit.friction", friction).withStyle(ChatFormatting.DARK_GRAY));
        }
        return comp;
    }

    @Nullable
    public static Component getFragileComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        boolean fragile = (Boolean)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FRAGILE.get());
        if (fragile) {
            return FRAGILE;
        }
        return null;
    }

    @Nullable
    public static Component getAirtightComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        if (item.getBlock().defaultBlockState().is(SimTags.Blocks.AIRTIGHT)) {
            return AIRTIGHT;
        }
        return null;
    }

    @Nullable
    public static Component getFloatingComponent(BlockStateExtension properties, BlockItem item, boolean showNumbers) {
        ResourceLocation materialID = (ResourceLocation)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FLOATING_MATERIAL.get());
        if (materialID == null) {
            return null;
        }
        FloatingBlockMaterial material = (FloatingBlockMaterial)FloatingBlockMaterialDataHandler.allMaterials.get(materialID);
        if (material == null) {
            return null;
        }
        double materialScale = (Double)properties.sable$getProperty((PhysicsBlockPropertyTypes.PhysicsBlockPropertyType)PhysicsBlockPropertyTypes.FLOATING_SCALE.get());
        double liftStrength = material.liftStrength() * materialScale;
        if (liftStrength <= 0.0) {
            return null;
        }
        if (showNumbers) {
            return Component.empty().append(FLOATING).append((Component)BlockPropertiesTooltip.formatValue("simulated.unit.floating", liftStrength).withStyle(ChatFormatting.DARK_GRAY));
        }
        return FLOATING;
    }

    private static MutableComponent formatValue(String key, double value) {
        String valueString = DECIMAL_FORMAT.format(value);
        return Component.literal((String)" (").append((Component)Component.translatable((String)key, (Object[])new Object[]{valueString})).append(")");
    }

    private static /* synthetic */ void lambda$appendTooltip$1(BlockStateExtension properties, BlockItem blockItem, List toAdd, Map.Entry f) {
        Component component = ((Entry)f.getValue()).tooltipFunction.apply(properties, blockItem, true);
        if (component != null) {
            toAdd.add(component);
        }
    }

    static {
        DECIMAL_FORMAT.setDecimalSeparatorAlwaysShown(false);
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
        DECIMAL_FORMAT.setMinimumIntegerDigits(1);
    }

    public static enum Condition {
        ALWAYS(true, false, false),
        SHIFT(true, true, false),
        GOGGLES(true, false, true),
        SHIFT_GOGGLES(true, true, true),
        NEVER(false, false, false);

        private final boolean allow;
        private final boolean requireShift;
        private final boolean requireGoggles;

        private Condition(boolean allow, boolean requireShift, boolean requireGoggles) {
            this.allow = allow;
            this.requireShift = requireShift;
            this.requireGoggles = requireGoggles;
        }

        public boolean test(boolean shift, boolean goggles) {
            return !(!this.allow || this.requireShift && !shift || this.requireGoggles && !goggles);
        }

        public boolean allows() {
            return this.allow;
        }
    }

    @FunctionalInterface
    public static interface TooltipFunction {
        @Nullable
        public Component apply(BlockStateExtension var1, BlockItem var2, boolean var3);
    }

    public record Entry(TooltipFunction tooltipFunction, float priority) implements Comparable<Entry>
    {
        @Override
        public int compareTo(@NotNull Entry o) {
            return Float.compare(this.priority, o.priority);
        }
    }
}
