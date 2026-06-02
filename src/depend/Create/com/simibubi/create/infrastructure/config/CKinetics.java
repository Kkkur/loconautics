/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.createmod.catnip.config.ConfigBase
 *  net.createmod.catnip.config.ConfigBase$ConfigBool
 *  net.createmod.catnip.config.ConfigBase$ConfigEnum
 *  net.createmod.catnip.config.ConfigBase$ConfigFloat
 *  net.createmod.catnip.config.ConfigBase$ConfigGroup
 *  net.createmod.catnip.config.ConfigBase$ConfigInt
 */
package com.simibubi.create.infrastructure.config;

import com.simibubi.create.api.contraption.ContraptionMovementSetting;
import com.simibubi.create.infrastructure.config.CStress;
import net.createmod.catnip.config.ConfigBase;

public class CKinetics
extends ConfigBase {
    public final ConfigBase.ConfigBool disableStress = this.b(false, "disableStress", new String[]{Comments.disableStress});
    public final ConfigBase.ConfigInt maxBeltLength = this.i(20, 5, "maxBeltLength", new String[]{Comments.maxBeltLength});
    public final ConfigBase.ConfigInt maxChainConveyorLength = this.i(32, 5, "maxChainConveyorLength", new String[]{Comments.maxChainConveyorLength});
    public final ConfigBase.ConfigInt maxChainConveyorConnections = this.i(4, 1, "maxChainConveyorConnections", new String[]{Comments.maxChainConveyorConnections});
    public final ConfigBase.ConfigInt crushingDamage = this.i(4, 0, "crushingDamage", new String[]{Comments.crushingDamage});
    public final ConfigBase.ConfigInt maxRotationSpeed = this.i(256, 64, "maxRotationSpeed", new String[]{Comments.rpm, Comments.maxRotationSpeed});
    public final ConfigBase.ConfigEnum<DeployerAggroSetting> ignoreDeployerAttacks = this.e(DeployerAggroSetting.CREEPERS, "ignoreDeployerAttacks", new String[]{Comments.ignoreDeployerAttacks});
    public final ConfigBase.ConfigInt kineticValidationFrequency = this.i(60, 5, "kineticValidationFrequency", new String[]{Comments.kineticValidationFrequency});
    public final ConfigBase.ConfigFloat crankHungerMultiplier = this.f(0.01f, 0.0f, 1.0f, "crankHungerMultiplier", new String[]{Comments.crankHungerMultiplier});
    public final ConfigBase.ConfigInt minimumWindmillSails = this.i(8, 0, "minimumWindmillSails", new String[]{Comments.minimumWindmillSails});
    public final ConfigBase.ConfigInt windmillSailsPerRPM = this.i(8, 1, "windmillSailsPerRPM", new String[]{Comments.windmillSailsPerRPM});
    public final ConfigBase.ConfigInt maxEjectorDistance = this.i(32, 0, "maxEjectorDistance", new String[]{Comments.maxEjectorDistance});
    public final ConfigBase.ConfigInt ejectorScanInterval = this.i(120, 10, "ejectorScanInterval", new String[]{Comments.ejectorScanInterval});
    public final ConfigBase.ConfigGroup fan = this.group(1, "encasedFan", new String[]{"Encased Fan"});
    public final ConfigBase.ConfigInt fanPushDistance = this.i(20, 5, "fanPushDistance", new String[]{Comments.fanPushDistance});
    public final ConfigBase.ConfigInt fanPullDistance = this.i(20, 5, "fanPullDistance", new String[]{Comments.fanPullDistance});
    public final ConfigBase.ConfigInt fanBlockCheckRate = this.i(30, 10, "fanBlockCheckRate", new String[]{Comments.fanBlockCheckRate});
    public final ConfigBase.ConfigInt fanRotationArgmax = this.i(256, 64, "fanRotationArgmax", new String[]{Comments.rpm, Comments.fanRotationArgmax});
    public final ConfigBase.ConfigInt fanProcessingTime = this.i(150, 0, "fanProcessingTime", new String[]{Comments.fanProcessingTime});
    public final ConfigBase.ConfigGroup contraptions = this.group(1, "contraptions", new String[]{"Moving Contraptions"});
    public final ConfigBase.ConfigInt maxBlocksMoved = this.i(2048, 1, "maxBlocksMoved", new String[]{Comments.maxBlocksMoved});
    public final ConfigBase.ConfigInt maxChassisRange = this.i(16, 1, "maxChassisRange", new String[]{Comments.maxChassisRange});
    public final ConfigBase.ConfigInt maxPistonPoles = this.i(64, 1, "maxPistonPoles", new String[]{Comments.maxPistonPoles});
    public final ConfigBase.ConfigInt maxRopeLength = this.i(384, 1, "maxRopeLength", new String[]{Comments.maxRopeLength});
    public final ConfigBase.ConfigInt maxCartCouplingLength = this.i(32, 1, "maxCartCouplingLength", new String[]{Comments.maxCartCouplingLength});
    public final ConfigBase.ConfigInt rollerFillDepth = this.i(12, 1, "rollerFillDepth", new String[]{Comments.rollerFillDepth});
    public final ConfigBase.ConfigBool survivalContraptionPickup = this.b(true, "survivalContraptionPickup", new String[]{Comments.survivalContraptionPickup});
    public final ConfigBase.ConfigEnum<ContraptionMovementSetting> spawnerMovement = this.e(ContraptionMovementSetting.NO_PICKUP, "movableSpawners", new String[]{Comments.spawnerMovement});
    public final ConfigBase.ConfigEnum<ContraptionMovementSetting> amethystMovement = this.e(ContraptionMovementSetting.NO_PICKUP, "amethystMovement", new String[]{Comments.amethystMovement});
    public final ConfigBase.ConfigEnum<ContraptionMovementSetting> obsidianMovement = this.e(ContraptionMovementSetting.UNMOVABLE, "movableObsidian", new String[]{Comments.obsidianMovement});
    public final ConfigBase.ConfigEnum<ContraptionMovementSetting> reinforcedDeepslateMovement = this.e(ContraptionMovementSetting.UNMOVABLE, "movableReinforcedDeepslate", new String[]{Comments.reinforcedDeepslateMovement});
    public final ConfigBase.ConfigBool moveItemsToStorage = this.b(true, "moveItemsToStorage", new String[]{Comments.moveItemsToStorage});
    public final ConfigBase.ConfigBool harvestPartiallyGrown = this.b(false, "harvestPartiallyGrown", new String[]{Comments.harvestPartiallyGrown});
    public final ConfigBase.ConfigBool harvesterReplants = this.b(true, "harvesterReplants", new String[]{Comments.harvesterReplants});
    public final ConfigBase.ConfigBool minecartContraptionInContainers = this.b(false, "minecartContraptionInContainers", new String[]{Comments.minecartContraptionInContainers});
    public final ConfigBase.ConfigBool stabiliseStableContraptions = this.b(false, "stabiliseStableContraptions", new String[]{Comments.stabiliseStableContraptions, "[Technical]"});
    public final ConfigBase.ConfigBool syncPlayerPickupHitboxWithContraptionHitbox = this.b(false, "syncPlayerPickupHitboxWithContraptionHitbox", new String[]{Comments.syncPlayerPickupHitboxWithContraptionHitbox, "[Technical]"});
    public final ConfigBase.ConfigBool noDropWhenContraptionReplaceBlocks = this.b(false, "noDropWhenContraptionReplaceBlocks", new String[]{Comments.noDropWhenContraptionReplaceBlocks});
    public final ConfigBase.ConfigGroup stats = this.group(1, "stats", new String[]{Comments.stats});
    public final ConfigBase.ConfigFloat mediumSpeed = this.f(30.0f, 0.0f, 4096.0f, "mediumSpeed", new String[]{Comments.rpm, Comments.mediumSpeed});
    public final ConfigBase.ConfigFloat fastSpeed = this.f(100.0f, 0.0f, 65535.0f, "fastSpeed", new String[]{Comments.rpm, Comments.fastSpeed});
    public final ConfigBase.ConfigFloat mediumStressImpact = this.f(4.0f, 0.0f, 4096.0f, "mediumStressImpact", new String[]{Comments.su, Comments.mediumStressImpact});
    public final ConfigBase.ConfigFloat highStressImpact = this.f(8.0f, 0.0f, 65535.0f, "highStressImpact", new String[]{Comments.su, Comments.highStressImpact});
    public final ConfigBase.ConfigFloat mediumCapacity = this.f(256.0f, 0.0f, 4096.0f, "mediumCapacity", new String[]{Comments.su, Comments.mediumCapacity});
    public final ConfigBase.ConfigFloat highCapacity = this.f(1024.0f, 0.0f, 65535.0f, "highCapacity", new String[]{Comments.su, Comments.highCapacity});
    public final CStress stressValues = (CStress)this.nested(1, CStress::new, new String[]{Comments.stress});

    public String getName() {
        return "kinetics";
    }

    private static class Comments {
        static String maxBeltLength = "Maximum length in blocks of mechanical belts.";
        static String maxChainConveyorLength = "Maximum length in blocks of chain conveyor connections.";
        static String maxChainConveyorConnections = "Maximum amount of connections each chain conveyor can have.";
        static String crushingDamage = "Damage dealt by active Crushing Wheels.";
        static String maxRotationSpeed = "Maximum allowed rotation speed for any Kinetic Block.";
        static String fanPushDistance = "Maximum distance in blocks Fans can push entities.";
        static String fanPullDistance = "Maximum distance in blocks from where Fans can pull entities.";
        static String fanBlockCheckRate = "Game ticks between Fans checking for anything blocking their air flow.";
        static String fanRotationArgmax = "Rotation speed at which the maximum stats of fans are reached.";
        static String fanProcessingTime = "Game ticks required for a Fan-based processing recipe to take effect.";
        static String crankHungerMultiplier = "multiplier used for calculating exhaustion from speed when a crank is turned.";
        static String maxBlocksMoved = "Maximum amount of blocks in a structure movable by Pistons, Bearings or other means.";
        static String maxChassisRange = "Maximum value of a chassis attachment range.";
        static String maxPistonPoles = "Maximum amount of extension poles behind a Mechanical Piston.";
        static String maxRopeLength = "Max length of rope available off a Rope Pulley.";
        static String maxCartCouplingLength = "Maximum allowed distance of two coupled minecarts.";
        static String rollerFillDepth = "Maximum depth of blocks filled in using a Mechanical Roller.";
        static String moveItemsToStorage = "Whether items mined or harvested by contraptions should be placed in their mounted storage.";
        static String harvestPartiallyGrown = "Whether harvesters should break crops that aren't fully grown.";
        static String harvesterReplants = "Whether harvesters should replant crops after harvesting.";
        static String stats = "Configure speed/capacity levels for requirements and indicators.";
        static String rpm = "[in Revolutions per Minute]";
        static String su = "[in Stress Units]";
        static String bytes = "[in Bytes]";
        static String mediumSpeed = "Minimum speed of rotation to be considered 'medium'";
        static String fastSpeed = "Minimum speed of rotation to be considered 'fast'";
        static String mediumStressImpact = "Minimum stress impact to be considered 'medium'";
        static String highStressImpact = "Minimum stress impact to be considered 'high'";
        static String mediumCapacity = "Minimum added Capacity by sources to be considered 'medium'";
        static String highCapacity = "Minimum added Capacity by sources to be considered 'high'";
        static String stress = "Fine tune the kinetic stats of individual components";
        static String ignoreDeployerAttacks = "Select what mobs should ignore Deployers when attacked by them.";
        static String disableStress = "Disable the Stress mechanic altogether.";
        static String kineticValidationFrequency = "Game ticks between Kinetic Blocks checking whether their source is still valid.";
        static String minimumWindmillSails = "Amount of sail-type blocks required for a windmill to assemble successfully.";
        static String windmillSailsPerRPM = "Number of sail-type blocks required to increase windmill speed by 1RPM.";
        static String maxEjectorDistance = "Max Distance in blocks a Weighted Ejector can throw";
        static String ejectorScanInterval = "Time in ticks until the next item launched by an ejector scans blocks for potential collisions";
        static String survivalContraptionPickup = "Whether minecart contraptions can be picked up in survival mode.";
        static String spawnerMovement = "Configure how Spawner blocks can be moved by contraptions.";
        static String amethystMovement = "Configure how Budding Amethyst can be moved by contraptions.";
        static String obsidianMovement = "Configure how Obsidian blocks can be moved by contraptions.";
        static String reinforcedDeepslateMovement = "Configure how Reinforced Deepslate blocks can be moved by contraptions.";
        static String minecartContraptionInContainers = "Whether minecart contraptions can be placed into container items.";
        static String stabiliseStableContraptions = "Whether stabilised bearings create a separated entity even on non-rotating contraptions.";
        static String syncPlayerPickupHitboxWithContraptionHitbox = "Whether the players hitbox should be expanded to the size of the contraption hitbox.";
        static String noDropWhenContraptionReplaceBlocks = "Whether to prevent block dropping when contraption is placed inside in-world blocks.";

        private Comments() {
        }
    }

    public static enum DeployerAggroSetting {
        ALL,
        CREEPERS,
        NONE;

    }
}
