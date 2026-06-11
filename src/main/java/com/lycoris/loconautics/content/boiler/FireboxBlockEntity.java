package com.lycoris.loconautics.content.boiler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

/**
 * Firebox Block Entity — manages the fuel inventory and burn state for one firebox block.
 *
 * <p>Fuel acceptance: any item where {@code stack.getBurnTime(RecipeType.SMELTING) > 0},
 * mirroring exactly how the Aeronautics Portable Engine checks fuel.
 *
 * <p>The firebox does not tick combustion itself. Instead, the controller BE reads
 * {@link #consumeFuel()} when it needs to light or sustain the boiler. This keeps
 * all boiler logic centralised in the controller.
 *
 * <p>Hoppers and adjacent inventories can insert fuel automatically via the
 * {@link IItemHandler} capability (registered in Phase 3).
 */
public class FireboxBlockEntity extends BlockEntity {

    /** Number of fuel slots in the firebox. One slot matches vanilla furnace behaviour. */
    public static final int FUEL_SLOTS = 1;

    private final ItemStackHandler inventory = new ItemStackHandler(FUEL_SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isFuel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    /** Remaining burn ticks from the currently active fuel item. */
    private int burnTimeRemaining = 0;

    /** Total burn ticks of the last consumed item (for progress bar rendering later). */
    private int burnTimeTotalForCurrentFuel = 0;

    public FireboxBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // ------------------------------------------------------------------ fuel API

    /** Returns true when any item in the inventory is a valid furnace fuel. */
    public static boolean isFuel(ItemStack stack) {
        return !stack.isEmpty() && stack.getBurnTime(RecipeType.SMELTING) > 0;
    }

    /**
     * Returns true when this firebox has either remaining active burn time
     * or fuel items waiting to be consumed.
     */
    public boolean hasFuel() {
        if (burnTimeRemaining > 0) return true;
        return isFuel(inventory.getStackInSlot(0));
    }

    /**
     * Called by the controller once per controller tick when it needs combustion.
     * Decrements active burn time, or consumes the next fuel item if burn time has
     * expired. Returns true if the firebox is actively burning after this call.
     */
    public boolean consumeFuel() {
        if (burnTimeRemaining > 0) {
            burnTimeRemaining--;
            setChanged();
            return true;
        }

        // Try to light the next item
        ItemStack fuel = inventory.getStackInSlot(0);
        if (!isFuel(fuel)) return false;

        int burnTime = fuel.getBurnTime(RecipeType.SMELTING);
        burnTimeTotalForCurrentFuel = burnTime;
        burnTimeRemaining = burnTime;

        // Handle crafting remainder (e.g. bucket left after lava bucket)
        if (fuel.getCount() == 1 && fuel.getItem().hasCraftingRemainingItem()) {
            inventory.setStackInSlot(0, fuel.getItem().getCraftingRemainingItem().getDefaultInstance());
        } else {
            inventory.extractItem(0, 1, false);
        }

        burnTimeRemaining--;
        setChanged();
        return true;
    }

    /**
     * Tick — currently a no-op. The controller drives combustion via {@link #consumeFuel()}.
     * Reserved for future hopper auto-fill logic (Phase 3).
     */
    public void tick() {
        // Phase 3: auto-fill from adjacent hopper / coal car
    }

    /** Opens the firebox inventory for a player (placeholder — GUI in Phase 3). */
    public void openInventory(Player player) {
        // Phase 3: open container screen
    }

    // ------------------------------------------------------------------ accessors

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public int getBurnTimeRemaining() {
        return burnTimeRemaining;
    }

    public int getBurnTimeTotalForCurrentFuel() {
        return burnTimeTotalForCurrentFuel;
    }

    /** Returns the neighbour direction toward the controller, or null if not yet linked. */
    @Nullable
    public BlockPos findController() {
        if (level == null) return null;
        for (Direction dir : Direction.values()) {
            BlockPos neighbour = worldPosition.relative(dir);
            if (level.getBlockState(neighbour).is(BoilerBlocks.BOILER_CONTROLLER.get())) {
                return neighbour;
            }
        }
        return null;
    }

    // ------------------------------------------------------------------ NBT

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.serializeNBT(registries));
        tag.putInt("BurnTimeRemaining", burnTimeRemaining);
        tag.putInt("BurnTimeTotalForCurrentFuel", burnTimeTotalForCurrentFuel);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("Inventory"));
        burnTimeRemaining = tag.getInt("BurnTimeRemaining");
        burnTimeTotalForCurrentFuel = tag.getInt("BurnTimeTotalForCurrentFuel");
    }
}