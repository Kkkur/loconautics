/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.AllItems
 *  com.simibubi.create.Create
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$SpecialInstructions
 *  com.simibubi.create.foundation.ponder.CreateSceneBuilder$WorldInstructions
 *  dev.simulated_team.simulated.ponder.SmoothMovementUtils
 *  dev.simulated_team.simulated.ponder.instructions.AirflowAABBInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction
 *  dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction
 *  dev.simulated_team.simulated.ponder.instructions.OffsetBreakParticlesInstruction
 *  dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction
 *  it.unimi.dsi.fastutil.floats.FloatUnaryOperator
 *  net.createmod.catnip.math.Pointing
 *  net.createmod.ponder.api.ParticleEmitter
 *  net.createmod.ponder.api.PonderPalette
 *  net.createmod.ponder.api.element.ElementLink
 *  net.createmod.ponder.api.element.WorldSectionElement
 *  net.createmod.ponder.api.scene.OverlayInstructions
 *  net.createmod.ponder.api.scene.SceneBuilder
 *  net.createmod.ponder.api.scene.SceneBuildingUtil
 *  net.createmod.ponder.api.scene.Selection
 *  net.createmod.ponder.api.scene.SelectionUtil
 *  net.createmod.ponder.api.scene.VectorUtil
 *  net.createmod.ponder.foundation.instruction.PonderInstruction
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 */
package dev.eriksonn.aeronautics.content.ponder.scenes;

import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.eriksonn.aeronautics.config.AeroConfig;
import dev.eriksonn.aeronautics.content.ponder.instructions.ChangePropellerRotateInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerParticleSpawningInstruction;
import dev.eriksonn.aeronautics.content.ponder.instructions.PropellerRotateInstruction;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.service.AeroLevititeService;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.AirflowAABBInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.CustomToggleBaseShadowInstruction;
import dev.simulated_team.simulated.ponder.instructions.OffsetBreakParticlesInstruction;
import dev.simulated_team.simulated.ponder.instructions.PullTheAssemblerKronkInstruction;
import it.unimi.dsi.fastutil.floats.FloatUnaryOperator;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.ParticleEmitter;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.createmod.ponder.foundation.instruction.PonderInstruction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LevititeScenes {
    public static void levititeBlend(SceneBuilder scene, SceneBuildingUtil util) {
        boolean pass;
        int i;
        scene.title("levitite_blend", "Crystallizing Levitite Blend into Levitite");
        scene.configureBasePlate(1, 1, 5);
        scene.world().showSection(util.select().fromTo(1, 0, 1, 5, 0, 5), Direction.UP);
        scene.idle(10);
        ElementLink link1 = scene.world().showIndependentSection(util.select().position(3, 3, 3), Direction.DOWN);
        scene.world().moveSection(link1, new Vec3(0.0, -2.0, 0.0), 0);
        scene.idle(20);
        scene.overlay().showText(60).pointAt(util.vector().topOf(3, 1, 3)).attachKeyFrame().placeNearTarget().text("Levitite Blend can be crystallized into Levitite");
        scene.idle(80);
        scene.overlay().showText(50).pointAt(util.vector().topOf(3, 1, 3)).attachKeyFrame().placeNearTarget().text("This process requires heat");
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(3, 1, 3), Pointing.DOWN, 20).withItem(new ItemStack((ItemLike)Items.FLINT_AND_STEEL));
        scene.idle(5);
        scene.effects().emitParticles(new Vec3(3.5, 1.5, 3.5), LevititeScenes.withinBlockSpace(ParticleTypes.SMOKE, 1.5f), 15.0f, 2);
        scene.idle(50);
        scene.world().replaceBlocks(util.select().position(3, 3, 3), AeroBlocks.LEVITITE.getDefaultState(), false);
        scene.effects().emitParticles(new Vec3(3.5, 1.5, 3.5), LevititeScenes.withinBlockSpace(ParticleTypes.FLAME, 1.5f), 15.0f, 2);
        scene.idle(25);
        scene.overlay().showText(60).pointAt(util.vector().centerOf(3, 1, 3)).attachKeyFrame().placeNearTarget().colored(PonderPalette.RED).text("Once catalyzed, the Levitite cannot be recollected");
        scene.idle(20);
        for (int i2 = 0; i2 < 4; ++i2) {
            scene.world().incrementBlockBreakingProgress(new BlockPos(3, 3, 3));
            scene.world().incrementBlockBreakingProgress(new BlockPos(3, 3, 3));
            scene.idle(5);
        }
        scene.world().setBlock(new BlockPos(3, 3, 3), Blocks.AIR.defaultBlockState(), false);
        scene.addInstruction((PonderInstruction)new OffsetBreakParticlesInstruction(AABB.unitCubeFromLowerCorner((Vec3)new Vec3(3.0, 1.0, 3.0)), AeroBlocks.LEVITITE.getDefaultState()));
        scene.idle(50);
        ElementLink[] link2 = new ElementLink[3];
        for (int i3 = 0; i3 < 3; ++i3) {
            link2[i3] = scene.world().showIndependentSection(util.select().position(5 - 2 * i3, 2, 3), Direction.DOWN);
            scene.world().moveSection(link2[i3], new Vec3(0.0, -1.0, 0.0), 0);
            scene.idle(5);
        }
        scene.idle(5);
        ElementLink[] link3 = new ElementLink[3];
        Selection[] selections = new Selection[]{util.select().fromTo(1, 3, 3, 1, 4, 4), util.select().position(4, 3, 4), util.select().position(5, 3, 4)};
        for (i = 0; i < 3; ++i) {
            link3[i] = scene.world().showIndependentSection(selections[i], Direction.DOWN);
            scene.world().moveSection(link3[i], new Vec3((double)((i == 1 ? -1 : 0) + (1 - i) * 4), -2.0, 0.0), 0);
            scene.idle(5);
        }
        scene.idle(10);
        scene.overlay().showText(70).pointAt(util.vector().topOf(3, 1, 4)).attachKeyFrame().placeNearTarget().text("Nearby heat sources can also start the crystallization process");
        for (i = 0; i < 3; ++i) {
            scene.effects().emitParticles(new Vec3(5.5 - (double)i * 2.0, 1.5, 3.5), LevititeScenes.withinBlockSpace(ParticleTypes.SMOKE, 1.5f), 15.0f, 2);
            scene.idle(5);
        }
        scene.idle(60);
        for (i = 0; i < 3; ++i) {
            scene.effects().emitParticles(new Vec3(5.5 - (double)i * 2.0, 1.5, 3.5), LevititeScenes.withinBlockSpace(ParticleTypes.FLAME, 1.5f), 15.0f, 2);
            scene.world().replaceBlocks(util.select().position(5 - i * 2, 2, 3), AeroBlocks.LEVITITE.getDefaultState(), false);
            scene.idle(5);
        }
        scene.idle(20);
        for (i = 0; i < 3; ++i) {
            scene.world().hideIndependentSection(link2[i], Direction.UP);
            scene.idle(2);
        }
        for (i = 0; i < 3; ++i) {
            scene.world().hideIndependentSection(link3[i], Direction.UP);
            scene.idle(2);
        }
        scene.idle(20);
        Selection pulley = util.select().fromTo(2, 2, 6, 3, 5, 6).add(util.select().fromTo(3, 5, 5, 3, 5, 6));
        ElementLink pulleySection = scene.world().showIndependentSection(pulley, Direction.DOWN);
        scene.world().moveSection(pulleySection, new Vec3(0.0, -2.0, 0.0), 0);
        scene.idle(15);
        for (int i4 = 0; i4 < 7; ++i4) {
            Selection sel = null;
            for (int x = 0; x < 5; ++x) {
                int z = 4 - i4 + Math.abs(x - 2);
                if (z < 0 || z >= 5) continue;
                sel = LevititeScenes.addSelection(sel, util.select().position(x + 1, 1, z + 1));
            }
            if (sel != null) {
                scene.world().showSection(sel, Direction.DOWN);
            }
            scene.idle(4);
        }
        scene.idle(3);
        scene.world().hideIndependentSection(pulleySection, Direction.UP);
        scene.idle(20);
        scene.overlay().showControls(util.vector().topOf(3, 1, 3), Pointing.DOWN, 20).withItem(new ItemStack((ItemLike)Items.FLINT_AND_STEEL));
        scene.addKeyframe();
        scene.idle(5);
        LevititeScenes.animateLeviblendSpread(scene, util, 0L, new BlockPos(3, 1, 3), 2, 5, k -> {
            if (k == 3) {
                scene.overlay().showText(60).pointAt(util.vector().topOf(3, 1, 3)).placeNearTarget().text("After the reaction is started, the crystal will spread throughout the entire fluid");
            }
        });
        try {
            pass = (Boolean)AeroConfig.server().blocks.breakBlocksOnCrystallize.get();
        }
        catch (Exception ignored) {
            pass = true;
        }
        if (pass) {
            long clayIndex = 152746674644658L;
            Selection claySelection = null;
            Selection levititeSelection = null;
            long x = 1L;
            for (int j = 0; j < 7; ++j) {
                for (int i5 = 0; i5 < 7; ++i5) {
                    if ((x & clayIndex) > 0L) {
                        claySelection = LevititeScenes.addSelection(claySelection, util.select().position(i5, 1, j));
                    } else if (i5 > 0 && j > 0 && i5 < 6 && j < 6) {
                        levititeSelection = LevititeScenes.addSelection(levititeSelection, util.select().position(i5, 1, j));
                    }
                    x <<= 1;
                }
            }
            scene.idle(30);
            scene.world().hideSection(util.select().fromTo(1, 1, 1, 5, 1, 5), Direction.UP);
            scene.idle(20);
            scene.world().replaceBlocks(claySelection, Blocks.CLAY.defaultBlockState(), false);
            scene.world().replaceBlocks(levititeSelection, AeroLevititeService.INSTANCE.getFluid().defaultFluidState().createLegacyBlock(), false);
            scene.world().showSection(claySelection, Direction.DOWN);
            scene.idle(20);
            scene.world().showSection(levititeSelection, Direction.DOWN);
            scene.idle(30);
            scene.overlay().showText(60).pointAt(util.vector().topOf(1, 1, 3)).attachKeyFrame().placeNearTarget().text("When certain blocks are used to cast Levitite...");
            scene.idle(70);
            scene.overlay().showControls(util.vector().topOf(2, 1, 3), Pointing.DOWN, 20).withItem(new ItemStack((ItemLike)Items.FLINT_AND_STEEL));
            scene.idle(5);
            LevititeScenes.animateLeviblendSpread(scene, util, clayIndex, new BlockPos(2, 1, 3), 2, 4, k -> {});
            scene.idle(10);
            scene.overlay().showText(60).pointAt(util.vector().topOf(1, 1, 3)).placeNearTarget().text("...they will be destroyed during the crystallization process");
        }
    }

    static void animateLeviblendSpread(SceneBuilder scene, SceneBuildingUtil util, long clayIndex, BlockPos startPos, int minDelay, int maxDelay, Consumer<Integer> stepEvent) {
        int[] arr1 = new int[25];
        int[] arr2 = new int[25];
        for (int x = 0; x < 5; ++x) {
            for (int y = 0; y < 5; ++y) {
                int i = x + 5 * y;
                int i2 = 1 + x + (y + 1) * 7;
                long n = 1L << i2;
                if ((n & clayIndex) <= 0L) continue;
                arr1[i] = -2;
                arr2[i] = -2;
            }
        }
        arr1[startPos.getX() + startPos.getZ() * 5 - 6] = 4;
        scene.effects().emitParticles(new Vec3(0.5 + (double)startPos.getX(), 1.5, 0.5 + (double)startPos.getZ()), LevititeScenes.withinBlockSpace(ParticleTypes.SMOKE, 1.5f), 10.0f, 2);
        for (int k = 0; k < 20; ++k) {
            int[] source = k % 2 == 0 ? arr1 : arr2;
            int[] target = k % 2 == 0 ? arr2 : arr1;
            for (int x = 0; x < 5; ++x) {
                for (int y = 0; y < 5; ++y) {
                    int i = x + 5 * y;
                    if (source[i] <= 0) continue;
                    int n = i;
                    source[n] = source[n] - 1;
                    if (source[i] != 0) continue;
                    source[i] = -1;
                    scene.world().replaceBlocks(util.select().position(x + 1, 1, y + 1), AeroBlocks.LEVITITE.getDefaultState(), false);
                    scene.effects().emitParticles(new Vec3(1.5 + (double)x, 1.5, 1.5 + (double)y), LevititeScenes.withinBlockSpace(ParticleTypes.FLAME, 1.5f), 10.0f, 2);
                }
            }
            boolean done = true;
            for (int x = 0; x < 5; ++x) {
                for (int y = 0; y < 5; ++y) {
                    int i = x + 5 * y;
                    target[i] = source[i];
                    if (source[i] >= 0) {
                        done = false;
                    }
                    if (source[i] != 0 || !(x > 0 && source[x - 1 + 5 * y] == -1 || x < 4 && source[x + 1 + 5 * y] == -1 || y > 0 && source[x + 5 * y - 5] == -1) && (y >= 4 || source[x + 5 * y + 5] != -1)) continue;
                    target[i] = Create.RANDOM.nextInt(minDelay, maxDelay);
                    scene.effects().emitParticles(new Vec3(1.5 + (double)x, 1.5, 1.5 + (double)y), LevititeScenes.withinBlockSpace(ParticleTypes.SMOKE, 1.5f), 10.0f, 1);
                }
            }
            long bit = 1L;
            for (int y = 0; y < 7; ++y) {
                for (int x = 0; x < 7; ++x) {
                    if ((bit & clayIndex) > 0L) {
                        boolean remove = true;
                        for (int i = 0; i < 4; ++i) {
                            Vec3i dir = Direction.from2DDataValue((int)i).getNormal();
                            int x2 = x + dir.getX() - 1;
                            int y2 = y + dir.getZ() - 1;
                            if (x2 < 0 || y2 < 0 || x2 >= 5 || y2 >= 5 || source[x2 + y2 * 5] < 0) continue;
                            remove = false;
                            break;
                        }
                        if (remove) {
                            scene.world().destroyBlock(new BlockPos(x, 1, y));
                            clayIndex &= bit ^ 0xFFFFFFFFFFFFFFFFL;
                        }
                    }
                    bit <<= 1;
                }
            }
            if (done) {
                return;
            }
            scene.idle(6);
            stepEvent.accept(k);
        }
    }

    static Selection addSelection(Selection source, Selection a) {
        if (source == null) {
            return a;
        }
        return source.add(a);
    }

    public static <T extends ParticleOptions> ParticleEmitter withinBlockSpace(T data, float range) {
        return (w, x, y, z) -> w.addParticle(data, Math.floor(x) + 0.5 + ((double)Create.RANDOM.nextFloat() - 0.5) * (double)range, Math.floor(y) + 0.5 + ((double)Create.RANDOM.nextFloat() - 0.5) * (double)range, Math.floor(z) + 0.5 + ((double)Create.RANDOM.nextFloat() - 0.5) * (double)range, 0.0, 0.0, 0.0);
    }

    public static void levitite(SceneBuilder builder, SceneBuildingUtil util, boolean watermelon) {
        ElementLink[] grounds;
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        CreateSceneBuilder.WorldInstructions world = scene.world();
        CreateSceneBuilder.SpecialInstructions special = scene.special();
        OverlayInstructions overlay = scene.overlay();
        SelectionUtil select = util.select();
        VectorUtil vector = util.vector();
        scene.title("levitite", "Levitating contraptions with Levitite");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-1.0f);
        scene.scaleSceneView(0.8f);
        ElementLink ground = world.showIndependentSection(select.fromTo(0, 0, 0, 8, 0, 8), Direction.UP);
        scene.idle(10);
        world.multiplyKineticSpeed(util.select().everywhere(), 1.0f);
        BlockPos propellerPos = new BlockPos(2, 3, 4);
        BlockPos gearshiftPos = new BlockPos(3, 3, 4);
        Selection propeller = select.fromTo(1, 2, 3, 1, 4, 5);
        scene.addInstruction((PonderInstruction)new PullTheAssemblerKronkInstruction(util.grid().at(7, 3, 4), true, true));
        ElementLink airship = world.showIndependentSection(select.fromTo(2, 2, 2, 7, 2, 6), Direction.DOWN);
        ElementLink stirlingSection = null;
        scene.idle(5);
        for (int i = 0; i < 5; ++i) {
            int k;
            int n = k = i > 1 ? 1 : 0;
            if (i == 2) {
                stirlingSection = world.showIndependentSection(select.position(propellerPos.offset(2, 0, 0)), Direction.DOWN);
            } else {
                world.showSectionAndMerge(select.fromTo(propellerPos.offset(5 - i - k, 0, 0), propellerPos.offset(5 - i - k, i == 3 ? 1 : 0, 0)), Direction.DOWN, airship);
            }
            scene.idle(2);
        }
        ElementLink propellerLink = world.showIndependentSection(propeller, Direction.EAST);
        scene.idle(4);
        ElementLink stairs = world.showIndependentSection(util.select().position(propellerPos.offset(0, 1, 2)).add(util.select().position(propellerPos.offset(4, 1, 2))).add(util.select().position(propellerPos.offset(0, 1, -2))).add(util.select().position(propellerPos.offset(4, 1, -2))), Direction.DOWN);
        world.moveSection(stairs, new Vec3(0.0, -1.0, 0.0), 0);
        scene.idle(2);
        world.showSectionAndMerge(util.select().fromTo(propellerPos.offset(1, 0, 2), propellerPos.offset(3, 0, 2)).add(util.select().fromTo(propellerPos.offset(1, 0, -2), propellerPos.offset(3, 0, -2))), Direction.DOWN, airship);
        scene.idle(10);
        BlockState state = watermelon ? AeroBlocks.PEARLESCENT_LEVITITE.getDefaultState() : AeroBlocks.LEVITITE.getDefaultState();
        SimpleParticleType particle = watermelon ? ParticleTypes.SOUL_FIRE_FLAME : ParticleTypes.FLAME;
        for (int i = 0; i < 3; ++i) {
            BlockPos p = propellerPos.offset(3 - i, 0, 2);
            world.replaceBlocks(util.select().position(p), state, false);
            scene.effects().emitParticles(p.getCenter(), LevititeScenes.withinBlockSpace(particle, 1.5f), 10.0f, 2);
            p = propellerPos.offset(3 - i, 0, -2);
            world.replaceBlocks(util.select().position(p), state, false);
            scene.effects().emitParticles(p.getCenter(), LevititeScenes.withinBlockSpace(particle, 1.5f), 10.0f, 2);
            scene.idle(5);
        }
        overlay.showText(50).pointAt(util.vector().topOf(propellerPos.offset(1, 0, 2))).attachKeyFrame().placeNearTarget().text("When enough Levitite is attached to a simulated contraption...");
        scene.idle(50);
        float revolutions = 0.75f;
        int duration = (int)(40.0f * revolutions);
        ArrayList<ElementLink> links = new ArrayList<ElementLink>();
        links.add(airship);
        links.add(propellerLink);
        links.add(stairs);
        links.add(stirlingSection);
        PropellerRotateInstruction propellerRotate = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propellerLink, Direction.WEST, -32.0f, 4.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, (ElementLink<WorldSectionElement>)airship, 2.0f, -5.0f, 1.5f, false));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(1.5, 0.0, 0.0), (int)duration, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(20);
        overlay.showText(40).pointAt(util.vector().topOf(propellerPos.offset(3, 0, 2))).attachKeyFrame().placeNearTarget().text("...it will keep the contraption afloat");
        scene.idle(duration -= 20);
        world.multiplyKineticSpeed(util.select().everywhere(), -1.0f);
        duration = (int)(40.0f * (revolutions *= 2.0f));
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetRotationRate(propellerRotate, 32.0f));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(1.5, 0.0, 0.0), (int)duration, (FloatUnaryOperator)SmoothMovementUtils.quadraticJump()));
        }
        scene.idle(duration);
        world.multiplyKineticSpeed(util.select().everywhere(), -1.0f);
        duration = (int)(40.0f * (revolutions /= 2.0f));
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotate, 20.0f));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(-1.5, 0.0, 0.0), (int)duration, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        }
        scene.idle(duration);
        world.toggleRedstonePower(util.select().fromTo(gearshiftPos, gearshiftPos.above()));
        scene.idle(5);
        world.hideIndependentSection(stairs, Direction.UP);
        scene.idle(16);
        Selection levititeCorners = util.select().position(propellerPos.offset(0, 0, 2)).add(util.select().position(propellerPos.offset(4, 0, 2))).add(util.select().position(propellerPos.offset(0, 0, -2))).add(util.select().position(propellerPos.offset(4, 0, -2)));
        if (watermelon) {
            world.replaceBlocks(levititeCorners, AeroBlocks.PEARLESCENT_LEVITITE.getDefaultState(), false);
        }
        world.showSectionAndMerge(levititeCorners, Direction.DOWN, airship);
        scene.idle(15);
        overlay.showText(60).pointAt(util.vector().topOf(propellerPos.offset(1, 0, 2))).attachKeyFrame().placeNearTarget().text("Simulated Contraptions cannot gain altitude using Levitite alone");
        scene.idle(70);
        world.moveSection(stirlingSection, new Vec3(1.0, 0.0, 0.0), 10);
        scene.idle(20);
        Selection minipropSelection = select.fromTo(propellerPos.offset(2, 1, 0), propellerPos.offset(2, 2, 0));
        ElementLink miniprop = world.showIndependentSection(minipropSelection, Direction.DOWN);
        links.add(miniprop);
        world.moveSection(miniprop, new Vec3(0.0, -1.0, 0.0), 0);
        world.setKineticSpeed(minipropSelection, -32.0f);
        scene.idle(20);
        scene.addInstruction((PonderInstruction)new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)miniprop, propellerPos.offset(2, 2, 0), Direction.DOWN, 25, 2.0f, 3.0f, 0.7f));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(0.0, 0.5, 0.0), (int)25, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(25);
        overlay.showText(60).pointAt(util.vector().centerOf(propellerPos.offset(2, 2, 0))).attachKeyFrame().placeNearTarget().text("Instead, additional forces are required");
        world.multiplyKineticSpeed(select.everywhere(), -1.0f);
        scene.addInstruction((PonderInstruction)new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)miniprop, propellerPos.offset(2, 2, 0), Direction.UP, 50, 1.0f, 3.0f, 0.7f));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(0.0, 0.5, 0.0), (int)50, (FloatUnaryOperator)SmoothMovementUtils.quadraticJump()));
        }
        scene.idle(50);
        world.multiplyKineticSpeed(select.everywhere(), -1.0f);
        scene.addInstruction((PonderInstruction)new PropellerParticleSpawningInstruction((ElementLink<WorldSectionElement>)miniprop, propellerPos.offset(2, 2, 0), Direction.DOWN, 25, 1.0f, 3.0f, 0.7f));
        for (ElementLink link : links) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(0.0, -0.5, 0.0), (int)25, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        }
        scene.idle(30);
        world.hideIndependentSection(miniprop, Direction.UP);
        scene.idle(15);
        world.moveSection(stirlingSection, new Vec3(-1.0, 0.0, 0.0), 10);
        scene.idle(20);
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
        world.toggleRedstonePower(util.select().fromTo(gearshiftPos, gearshiftPos.above()));
        propellerRotate = new PropellerRotateInstruction(propellerPos, (ElementLink<WorldSectionElement>)propellerLink, Direction.WEST, -32.0f, 4.0f);
        scene.addInstruction((PonderInstruction)propellerRotate);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetParticles(propellerRotate, (ElementLink<WorldSectionElement>)airship, 2.0f, -5.0f, 1.5f, false));
        ElementLink ground2 = world.showIndependentSection(util.select().fromTo(9, 0, 2, 39, 1, 6), Direction.WEST);
        for (ElementLink link : grounds = new ElementLink[]{ground, ground2}) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(-0.5, 0.0, 0.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        }
        scene.idle(20);
        float movementDistance = 6.0f;
        int movementDuration = (int)(20.0f * movementDistance);
        for (ElementLink link : grounds) {
            world.moveSection(link, new Vec3((double)(-movementDistance), 0.0, 0.0), movementDuration);
        }
        scene.idle(10);
        movementDuration -= 10;
        AABB aabb1 = new AABB(propellerPos.offset(0, 0, 2)).expandTowards(4.0, 0.0, 0.0);
        AABB aabb2 = new AABB(propellerPos.offset(0, 0, -2)).expandTowards(4.0, 0.0, 0.0);
        overlay.chaseBoundingBoxOutline(PonderPalette.RED, (Object)aabb1, aabb1.contract(-5.0, 0.0, 0.0), 1);
        overlay.chaseBoundingBoxOutline(PonderPalette.RED, (Object)aabb1, aabb1, 100);
        overlay.chaseBoundingBoxOutline(PonderPalette.RED, (Object)aabb2, aabb2.contract(-5.0, 0.0, 0.0), 1);
        overlay.chaseBoundingBoxOutline(PonderPalette.RED, (Object)aabb2, aabb2, 100);
        scene.idle(10);
        movementDuration -= 10;
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.RED, aabb1, 90, Direction.WEST, 1.0f, 1.0f, false, false));
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.RED, aabb2, 90, Direction.WEST, 1.0f, 1.0f, false, false));
        scene.idle(15);
        overlay.showText(60).pointAt(util.vector().topOf(propellerPos.offset(1, 0, 2))).attachKeyFrame().placeNearTarget().text("At low movement speeds, Levitite significantly resists motion");
        scene.idle((movementDuration -= 15) - 4);
        world.hideIndependentSection(ground, null);
        overlay.showControls(util.vector().topOf(gearshiftPos.east()), Pointing.DOWN, 10).withItem(new ItemStack((ItemLike)AllItems.BLAZE_CAKE.asItem()));
        scene.idle(4);
        world.multiplyKineticSpeed(util.select().everywhere(), 2.0f);
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.SetRotationRate(propellerRotate, -64.0f));
        for (ElementLink link : grounds) {
            scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)link, (Vec3)new Vec3(-2.0, 0.0, 0.0), (int)40, t -> SmoothMovementUtils.quadraticRise().apply(t) + t));
        }
        scene.idle(20);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground2, (Vec3)new Vec3(-0.5, 0.0, 0.0), (int)20, (FloatUnaryOperator)SmoothMovementUtils.quadraticRise()));
        scene.idle(20);
        movementDistance = 24.0f;
        movementDuration = (int)(movementDistance * 5.0f);
        world.moveSection(ground2, new Vec3((double)(-movementDistance), 0.0, 0.0), movementDuration);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aabb1, aabb1.contract(-5.0, 0.0, 0.0), 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aabb1, aabb1, 100);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aabb2, aabb2.contract(-5.0, 0.0, 0.0), 1);
        scene.overlay().chaseBoundingBoxOutline(PonderPalette.INPUT, (Object)aabb2, aabb2, 100);
        scene.idle(10);
        movementDuration -= 10;
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.INPUT, aabb1, 90, Direction.WEST, 4.0f, 3.0f, false, false));
        scene.addInstruction((PonderInstruction)new AirflowAABBInstruction(PonderPalette.INPUT, aabb2, 90, Direction.WEST, 4.0f, 3.0f, false, false));
        scene.idle(15);
        scene.overlay().showText(60).pointAt(util.vector().topOf(propellerPos.offset(1, 0, 2))).attachKeyFrame().placeNearTarget().text("This resistance drops at higher movement speeds");
        scene.idle(movementDuration -= 15);
        world.multiplyKineticSpeed(util.select().everywhere(), 0.0f);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground2, (Vec3)new Vec3(-5.0, 0.0, 0.0), (int)50, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        ElementLink ground3 = world.showIndependentSection(util.select().fromTo(0, 0, 0, 8, 0, 8), Direction.WEST);
        world.moveSection(ground3, new Vec3(5.0, 0.0, 0.0), 0);
        scene.addInstruction((PonderInstruction)CustomAnimateWorldSectionInstruction.move((ElementLink)ground3, (Vec3)new Vec3(-5.0, 0.0, 0.0), (int)50, (FloatUnaryOperator)SmoothMovementUtils.quadraticRiseDual()));
        scene.addInstruction((PonderInstruction)new ChangePropellerRotateInstruction.StopRotation(propellerRotate, 30.0f));
        scene.idle(20);
        world.hideIndependentSection(ground2, null);
        scene.markAsFinished();
        scene.idle(30);
        scene.addInstruction((PonderInstruction)new CustomToggleBaseShadowInstruction());
    }
}
