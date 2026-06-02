/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Vec3i
 *  net.minecraft.network.chat.CommonComponents
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket
 *  net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
 *  net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.enchantment.ItemEnchantments
 *  net.neoforged.api.distmarker.Dist
 */
package com.simibubi.create.content.equipment.armor;

import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllEnchantments;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.foundation.utility.DistExecutor;
import com.simibubi.create.infrastructure.config.AllConfigs;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;

public class BacktankUtil {
    private static final List<Function<LivingEntity, List<ItemStack>>> BACKTANK_SUPPLIERS = new ArrayList<Function<LivingEntity, List<ItemStack>>>();

    public static List<ItemStack> getAllWithAir(LivingEntity entity) {
        ArrayList<ItemStack> all = new ArrayList<ItemStack>();
        for (Function<LivingEntity, List<ItemStack>> supplier : BACKTANK_SUPPLIERS) {
            List<ItemStack> result = supplier.apply(entity);
            for (ItemStack stack : result) {
                if (!BacktankUtil.hasAirRemaining(stack)) continue;
                all.add(stack);
            }
        }
        all.sort((a, b) -> Float.compare(BacktankUtil.getAir(a), BacktankUtil.getAir(b)));
        return all;
    }

    public static boolean hasAirRemaining(ItemStack backtank) {
        return BacktankUtil.getAir(backtank) > 0;
    }

    public static int getAir(ItemStack backtank) {
        return Math.min((Integer)backtank.getOrDefault(AllDataComponents.BACKTANK_AIR, (Object)0), BacktankUtil.maxAir(backtank));
    }

    public static void consumeAir(LivingEntity entity, ItemStack backtank, int i) {
        int maxAir = BacktankUtil.maxAir(backtank);
        int air = BacktankUtil.getAir(backtank);
        int newAir = Math.max(air - i, 0);
        backtank.set(AllDataComponents.BACKTANK_AIR, (Object)Math.min(newAir, maxAir));
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        BacktankUtil.sendWarning(player, air, newAir, (float)maxAir / 10.0f);
        BacktankUtil.sendWarning(player, air, newAir, 1.0f);
    }

    private static void sendWarning(ServerPlayer player, float air, float newAir, float threshold) {
        if (newAir > threshold) {
            return;
        }
        if (air <= threshold) {
            return;
        }
        boolean depleted = threshold == 1.0f;
        MutableComponent component = CreateLang.translateDirect(depleted ? "backtank.depleted" : "backtank.low", new Object[0]);
        AllSoundEvents.DENY.play(player.level(), null, (Vec3i)player.blockPosition(), 1.0f, 1.25f);
        AllSoundEvents.STEAM.play(player.level(), null, (Vec3i)player.blockPosition(), 0.5f, 0.5f);
        player.connection.send((Packet)new ClientboundSetTitlesAnimationPacket(10, 40, 10));
        player.connection.send((Packet)new ClientboundSetSubtitleTextPacket((Component)Component.literal((String)"\u26a0 ").withStyle(depleted ? ChatFormatting.RED : ChatFormatting.GOLD).append((Component)component.withStyle(ChatFormatting.GRAY))));
        player.connection.send((Packet)new ClientboundSetTitleTextPacket(CommonComponents.EMPTY));
    }

    public static int maxAir(ItemStack backtank) {
        int enchantLevel = 0;
        ItemEnchantments enchants = backtank.getTagEnchantments();
        for (Object2IntMap.Entry entry : enchants.entrySet()) {
            if (!((Holder)entry.getKey()).is(AllEnchantments.CAPACITY)) continue;
            enchantLevel = entry.getIntValue();
            break;
        }
        return BacktankUtil.maxAir(enchantLevel);
    }

    public static int maxAir(int enchantLevel) {
        return (Integer)AllConfigs.server().equipment.airInBacktank.get() + (Integer)AllConfigs.server().equipment.enchantedBacktankCapacity.get() * enchantLevel;
    }

    public static int maxAirWithoutEnchants() {
        return (Integer)AllConfigs.server().equipment.airInBacktank.get();
    }

    public static boolean canAbsorbDamage(LivingEntity entity, int usesPerTank) {
        if (usesPerTank == 0) {
            return true;
        }
        if (entity instanceof Player && ((Player)entity).isCreative()) {
            return true;
        }
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir(entity);
        if (backtanks.isEmpty()) {
            return false;
        }
        int cost = Math.max(BacktankUtil.maxAirWithoutEnchants() / usesPerTank, 1);
        BacktankUtil.consumeAir(entity, backtanks.getFirst(), cost);
        return true;
    }

    public static boolean isBarVisible(ItemStack stack, int usesPerTank) {
        if (usesPerTank == 0) {
            return false;
        }
        Player player = (Player)DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
        if (player == null) {
            return false;
        }
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir((LivingEntity)player);
        if (backtanks.isEmpty()) {
            return stack.isDamaged();
        }
        return true;
    }

    public static int getBarWidth(ItemStack stack, int usesPerTank) {
        if (usesPerTank == 0) {
            return 13;
        }
        Player player = (Player)DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
        if (player == null) {
            return 13;
        }
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir((LivingEntity)player);
        if (backtanks.isEmpty()) {
            return Math.round(13.0f - (float)stack.getDamageValue() / (float)stack.getMaxDamage() * 13.0f);
        }
        if (backtanks.size() == 1) {
            return backtanks.getFirst().getItem().getBarWidth(backtanks.getFirst());
        }
        int sumBarWidth = backtanks.stream().map(backtank -> backtank.getItem().getBarWidth(backtank)).reduce(0, Integer::sum);
        return Math.round((float)sumBarWidth / (float)backtanks.size());
    }

    public static int getBarColor(ItemStack stack, int usesPerTank) {
        if (usesPerTank == 0) {
            return 0;
        }
        Player player = (Player)DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> Minecraft.getInstance().player);
        if (player == null) {
            return 0;
        }
        List<ItemStack> backtanks = BacktankUtil.getAllWithAir((LivingEntity)player);
        if (backtanks.isEmpty()) {
            return Mth.hsvToRgb((float)(Math.max(0.0f, 1.0f - (float)stack.getDamageValue() / (float)stack.getMaxDamage()) / 3.0f), (float)1.0f, (float)1.0f);
        }
        return backtanks.get(0).getItem().getBarColor(backtanks.get(0));
    }

    public static void addBacktankSupplier(Function<LivingEntity, List<ItemStack>> supplier) {
        BACKTANK_SUPPLIERS.add(supplier);
    }

    static {
        BacktankUtil.addBacktankSupplier(entity -> {
            ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
            for (ItemStack itemStack : entity.getArmorSlots()) {
                if (!AllTags.AllItemTags.PRESSURIZED_AIR_SOURCES.matches(itemStack)) continue;
                stacks.add(itemStack);
            }
            return stacks;
        });
    }
}
