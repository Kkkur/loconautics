/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.util.datafix.fixes.References
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package com.simibubi.create.foundation.utility;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.util.datafix.fixes.References;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DataFixerHelper {
    private static final Set<BlockPosFixer> BLOCK_POS_FIXERS = new HashSet<BlockPosFixer>();
    public static final Set<BlockPosFixer> BLOCK_POS_FIXERS_VIEW = Collections.unmodifiableSet(BLOCK_POS_FIXERS);

    private static void addBlockPosFixer(DSL.TypeReference reference, Set<String> ids, Set<String> fields) {
        for (String id : ids) {
            DataFixerHelper.addBlockPosFixer(reference, id, fields);
        }
    }

    private static void addBlockPosFixer(DSL.TypeReference reference, String id, Set<String> fields) {
        ImmutableMap.Builder map = ImmutableMap.builder();
        for (String field : fields) {
            map.put((Object)field, (Object)field);
        }
        DataFixerHelper.addBlockPosFixer(reference, id, (Map<String, String>)map.build());
    }

    private static void addBlockPosFixer(DSL.TypeReference reference, Set<String> ids, Function<Dynamic<?>, Dynamic<?>> customFixer) {
        for (String id : ids) {
            DataFixerHelper.addBlockPosFixer(reference, id, customFixer);
        }
    }

    private static void addBlockPosFixer(DSL.TypeReference reference, String id, Function<Dynamic<?>, Dynamic<?>> customFixer) {
        BLOCK_POS_FIXERS.add(new BlockPosFixer(reference, (String)(id.contains(":") ? id : "create:" + id), null, customFixer));
    }

    private static void addBlockPosFixer(DSL.TypeReference reference, String id, Map<String, String> renames) {
        BLOCK_POS_FIXERS.add(new BlockPosFixer(reference, (String)(id.contains(":") ? id : "create:" + id), renames, null));
    }

    private static Dynamic<?> convertListOfBlockPositions(String tagKey, Dynamic<?> data) {
        return data.update(tagKey, dynamic -> dynamic.createList(dynamic.asStream().map(listData -> {
            Optional x = listData.get("X").asNumber().result();
            Optional y = listData.get("Y").asNumber().result();
            Optional z = listData.get("Z").asNumber().result();
            if (x.isPresent() && y.isPresent() && z.isPresent()) {
                listData.remove("X");
                listData.remove("Y");
                listData.remove("Z");
                return listData.set("Pos", listData.createIntList(IntStream.of(((Number)x.get()).intValue(), ((Number)y.get()).intValue(), ((Number)z.get()).intValue())));
            }
            return listData;
        })));
    }

    static {
        DataFixerHelper.addBlockPosFixer(References.ENTITY, "minecraft:item", Set.of("BypassCrushingWheel"));
        DataFixerHelper.addBlockPosFixer(References.ENTITY, "stationary_contraption", Set.of("ControllerRelative"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, Set.of("adjustable_chain_gearshift", "backtank", "belt", "clockwork_bearing", "clutch", "cuckoo_clock", "deployer", "drill", "elevator_pulley", "encased_fan", "flap_display", "fluid_valve", "flywheel", "gantry_pinion", "gearbox", "gearshift", "hand_crank", "hose_pulley", "large_water_wheel", "mechanical_arm", "mechanical_bearing", "mechanical_crafter", "mechanical_mixer", "mechanical_piston", "mechanical_press", "mechanical_pump", "millstone", "powered_shaft", "rope_pulley", "saw", "sequenced_gearshift", "simple_kinetic", "speedometer", "stressometer", "valve_handle", "water_wheel", "weighted_ejector", "windmill_bearing"), Set.of("Source"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, "belt", Set.of("Controller"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, Set.of("item_vault", "fluid_tank"), Set.of("LastKnownPos", "Controller"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, "display_link", Set.of("TargetOffset"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, Set.of("drill", "saw"), Set.of("Breaking"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, Set.of("rope_pulley", "elevator_pulley"), Set.of("MirrorChildren"));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, Set.of("rope_pulley", "elevator_pulley"), (Dynamic<?> data) -> DataFixerHelper.convertListOfBlockPositions("MirrorChildren", data));
        DataFixerHelper.addBlockPosFixer(References.BLOCK_ENTITY, "powered_shaft", Set.of("EnginePos"));
    }

    public record BlockPosFixer(DSL.TypeReference reference, String id, Map<String, String> renames, Function<Dynamic<?>, Dynamic<?>> customFixer) {
    }
}
