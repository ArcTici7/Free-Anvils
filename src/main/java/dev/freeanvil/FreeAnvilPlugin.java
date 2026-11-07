package dev.freeanvil;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Makes every anvil action free and removes the "Too Expensive!" cap.
 * The anvil block itself is untouched, so it still wears down / breaks as in vanilla.
 *
 * How it works:
 *  - The max repair cost is raised so vanilla never rejects an operation as too expensive.
 *  - The displayed cost is forced to 1, because vanilla refuses to let you take the result
 *    when the cost is 0.
 *  - When the player takes the result, their XP state is snapshotted (and they are given a
 *    temporary level if they have none) and then restored on the next tick,
 *    so the net cost is zero.
 */
public final class FreeAnvilPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        if (event.getView() instanceof AnvilView view) {
            view.setMaximumRepairCost(Integer.MAX_VALUE);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepare(PrepareAnvilEvent event) {
        AnvilView view = event.getView();
        view.setMaximumRepairCost(Integer.MAX_VALUE);
        if (event.getResult() == null) {
            return;
        }
        view.setRepairCost(1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTakeResult(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }
        if (event.getRawSlot() != 2) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir()) {
            return;
        }

        final int level = player.getLevel();
        final float progress = player.getExp();
        final int total = player.getTotalExperience();

        // Vanilla needs at least the (forced) cost of 1 level to release the item.
        if (level < 1) {
            player.setLevel(1);
        }

        // Whatever vanilla deducted, put the player's XP back exactly as it was.
        getServer().getScheduler().runTask(this, () -> {
            player.setLevel(level);
            player.setExp(progress);
            player.setTotalExperience(total);
        });
    }
}
