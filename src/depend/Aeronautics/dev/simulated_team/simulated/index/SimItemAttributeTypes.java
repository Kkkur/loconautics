/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.simibubi.create.api.registry.CreateBuiltInRegistries
 *  com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType
 *  foundry.veil.platform.registry.RegistrationProvider
 *  foundry.veil.platform.registry.RegistryObject
 *  net.minecraft.core.Registry
 */
package dev.simulated_team.simulated.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttributeType;
import dev.simulated_team.simulated.content.item_attributes.BlockBouncinessItemAttribute;
import dev.simulated_team.simulated.content.item_attributes.BlockFrictionItemAttribute;
import dev.simulated_team.simulated.content.item_attributes.BlockMassItemAttribute;
import foundry.veil.platform.registry.RegistrationProvider;
import foundry.veil.platform.registry.RegistryObject;
import net.minecraft.core.Registry;

public class SimItemAttributeTypes {
    private static final RegistrationProvider<ItemAttributeType> REGISTRY = RegistrationProvider.get((Registry)CreateBuiltInRegistries.ITEM_ATTRIBUTE_TYPE, (String)"simulated");
    public static final RegistryObject<BlockMassItemAttribute.Type> BLOCK_MASS = REGISTRY.register("block_mass", BlockMassItemAttribute.Type::new);
    public static final RegistryObject<BlockFrictionItemAttribute.Type> BLOCK_FRICTION = REGISTRY.register("block_friction", BlockFrictionItemAttribute.Type::new);
    public static final RegistryObject<BlockBouncinessItemAttribute.Type> BLOCK_BOUNCINESS = REGISTRY.register("block_bounciness", BlockBouncinessItemAttribute.Type::new);

    public static void init() {
    }
}
