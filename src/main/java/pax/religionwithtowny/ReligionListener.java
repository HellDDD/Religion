package pax.religionwithtowny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ReligionListener implements Listener {
    private final Religion plugin;

    public ReligionListener(Religion plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.GOLD + "Select a Religion")) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null && clickedItem.hasItemMeta()) {
                String title = clickedItem.getItemMeta().getDisplayName();
                Player player = (Player) event.getWhoClicked();
                Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
                if (resident != null && resident.hasTown()) {
                    try {
                        Town town = resident.getTown();
                        if (town.getMayor().equals(resident)) {
                            if (title.contains("Islam")) {
                                plugin.setTownReligion(town.getName(), "Islam");
                                player.sendMessage(ChatColor.GREEN + "Your town has chosen Islam.");
                            } else if (title.contains("Christianity")) {
                                plugin.setTownReligion(town.getName(), "Christianity");
                                player.sendMessage(ChatColor.GREEN + "Your town has chosen Christianity.");
                            } else if (title.contains("Buddhism")) {
                                plugin.setTownReligion(town.getName(), "Buddhism");
                                player.sendMessage(ChatColor.GREEN + "Your town has chosen Buddhism.");
                            }
                            player.closeInventory();
                        }
                    } catch (NotRegisteredException e) {
                        player.sendMessage(ChatColor.RED + "You are not part of a town.");
                    }
                }
            }
        }
    }
}