package net.bitbylogic.furnaceenhanced.events;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bitbylogic.furnaceenhanced.FurnaceEnhanced;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.permissions.PermissionAttachmentInfo;

@RequiredArgsConstructor
public class FurnaceListener implements Listener {

    private final FurnaceEnhanced plugin;

    @EventHandler
    public void onFurnaceClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof FurnaceInventory furnace) || furnace.getHolder() == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        if (event.getSlotType() != InventoryType.SlotType.CONTAINER) {
            return;
        }

        furnace.getHolder().setMetadata("cook_reduction", new FixedMetadataValue(plugin, getTimeReduction(player)));
    }


    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent event) {
        if (!(event.getBlock().getState() instanceof Furnace furnace) || furnace.getInventory().getHolder() == null) {
            return;
        }

        int burnTime = event.getBurnTime();
        int cookTimeReduction = burnTime * getTimeReduction(furnace) / 100;

        event.setBurnTime(Math.max(0, burnTime - cookTimeReduction));
    }

    @EventHandler
    public void onFurnaceStartSmelt (FurnaceStartSmeltEvent event) {
        if (!(event.getBlock().getState() instanceof Furnace furnace) || furnace.getInventory().getHolder() == null) {
            return;
        }

        int burnTime = event.getTotalCookTime();
        int cookTimeReduction = burnTime * getTimeReduction(furnace) / 100;

        event.setTotalCookTime(Math.max(0, burnTime - cookTimeReduction));
    }

    private int getTimeReduction(@NonNull Furnace furnace) {
        if (furnace.getInventory().getHolder() == null || furnace.getInventory().getHolder().getMetadata("cook_reduction").isEmpty()) {
            return 0;
        }

        return furnace.getInventory().getHolder().getMetadata("cook_reduction").get(0).asInt();
    }

    private int getTimeReduction(@NonNull Player player) {
        int currentReduction = 0;

        for (PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            if(!permission.getPermission().startsWith("furnaceenhanced.")) {
                continue;
            }

            String time = permission.getPermission().replaceFirst("furnaceenhanced.", "");

            try {
                int permissionReduction = Integer.parseInt(time);

                if (permissionReduction < currentReduction) {
                    continue;
                }

                currentReduction = permissionReduction;
            } catch (NumberFormatException e) {
                // Ignored.
            }
        }

        return currentReduction;
    }

}