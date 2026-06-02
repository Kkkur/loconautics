/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  it.unimi.dsi.fastutil.Pair
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3d
 */
package dev.ryanhcode.sable.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ryanhcode.sable.ActiveSableCompanion;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.api.command.SableCommandHelper;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorModifierType;
import dev.ryanhcode.sable.command.argument.SubLevelSelectorType;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

public class SubLevelSelector {
    private final SubLevelSelectorType type;
    private final List<Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier>> modifiers;

    public SubLevelSelector(SubLevelSelectorType type, List<Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier>> modifiers) {
        this.type = type;
        this.modifiers = modifiers;
    }

    public SubLevelSelectorType getSelectorType() {
        return this.type;
    }

    public Collection<ServerSubLevel> getSubLevels(CommandSourceStack source) throws CommandSyntaxException {
        if (this.type == null) {
            return List.of();
        }
        ServerLevel level = source.getLevel();
        ServerSubLevelContainer container = SableCommandHelper.requireSubLevelContainer(source);
        List<ServerSubLevel> containerBodies = container.getAllSubLevels();
        ObjectArrayList bodies = new ObjectArrayList();
        for (ServerSubLevel subLevel : containerBodies) {
            bodies.add(subLevel);
        }
        if (bodies.isEmpty()) {
            return Collections.emptySet();
        }
        ActiveSableCompanion helper = Sable.HELPER;
        Set<Object> collectedSubLevels = switch (this.type) {
            default -> throw new MatchException(null, null);
            case SubLevelSelectorType.ALL -> new HashSet(bodies);
            case SubLevelSelectorType.NEAREST -> {
                double closest = Double.MAX_VALUE;
                ServerSubLevel closestSubLevel = null;
                for (ServerSubLevel body : bodies) {
                    Vec3 sourcePosition = helper.projectOutOfSubLevel((Level)source.getLevel(), source.getPosition());
                    double distance = body.logicalPose().position().distance(sourcePosition.x, sourcePosition.y, sourcePosition.z);
                    if (!(distance < closest)) continue;
                    closest = distance;
                    closestSubLevel = body;
                }
                yield Collections.singleton(closestSubLevel);
            }
            case SubLevelSelectorType.RANDOM -> {
                ArrayList list = new ArrayList(bodies);
                yield Collections.singleton((ServerSubLevel)list.get(level.random.nextInt(list.size())));
            }
            case SubLevelSelectorType.INSIDE -> {
                ServerSubLevel subLevel = (ServerSubLevel)helper.getContaining((Level)level, (Position)source.getPosition());
                if (subLevel != null) {
                    yield Collections.singleton(subLevel);
                }
                yield Collections.emptySet();
            }
            case SubLevelSelectorType.TRACKING -> {
                if (source.getEntity() == null) {
                    yield Collections.emptySet();
                }
                ServerSubLevel subLevel = (ServerSubLevel)Sable.HELPER.getTrackingSubLevel(source.getEntity());
                if (subLevel != null) {
                    yield Collections.singleton(subLevel);
                }
                yield Collections.emptySet();
            }
            case SubLevelSelectorType.VIEWED -> {
                if (source.getEntity() != null) {
                    HitResult res = source.getEntity().pick(100.0, 1.0f, true);
                    if (res instanceof BlockHitResult) {
                        BlockHitResult blockHitResult = (BlockHitResult)res;
                        ServerSubLevel containing = (ServerSubLevel)helper.getContaining((Level)level, (Vec3i)blockHitResult.getBlockPos());
                        if (containing != null) {
                            yield Collections.singleton(containing);
                        }
                        yield Collections.emptySet();
                    }
                    yield Collections.emptySet();
                }
                yield Collections.emptySet();
            }
            case SubLevelSelectorType.LATEST -> {
                List<ServerSubLevel> subLevels = container.getAllSubLevels();
                if (subLevels.isEmpty()) {
                    yield Collections.emptySet();
                }
                yield Collections.singleton(subLevels.getLast());
            }
        };
        Object modifiedSubLevels = new ObjectArrayList(collectedSubLevels);
        Vector3d position = new Vector3d(source.getPosition().x, source.getPosition().y, source.getPosition().z);
        this.modifiers.sort(Comparator.comparingInt(a -> ((SubLevelSelectorModifierType)a.first()).getFilterPriority().ordinal()));
        for (Pair<SubLevelSelectorModifierType, SubLevelSelectorModifierType.Modifier> modifier : this.modifiers) {
            modifiedSubLevels = ((SubLevelSelectorModifierType.Modifier)modifier.right()).apply((List<ServerSubLevel>)modifiedSubLevels, position);
        }
        return modifiedSubLevels;
    }
}
