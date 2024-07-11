package pax.religionwithtowny;


import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.metadata.StringDataField;
import com.palmergames.bukkit.towny.object.metadata.CustomDataField;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class Religion extends JavaPlugin {
    private Map<String, String> cityReligions = new HashMap<>();
    private static final String FILE_PATH = "plugins/Religion/religions.json";

    @Override
    public void onEnable() {
        createFileIfNotExists();
        loadReligions();
        PluginCommand command = getCommand("religions");
        if (command != null) {
            command.setExecutor(this);
        }
        Bukkit.getPluginManager().registerEvents(new ReligionListener(this), this);
        getLogger().info("Religion plugin enabled!");
    }

    @Override
    public void onDisable() {
        saveReligions();
        getLogger().info("Religion plugin disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("religions")) {
                Resident resident = TownyAPI.getInstance().getResident(player.getUniqueId());
                if (resident != null && resident.hasTown()) {
                    try {
                        Town town = resident.getTown();
                        if (town.getMayor().equals(resident)) {
                            openReligionSelectionMenu(player);
                        } else {
                            player.sendMessage(ChatColor.RED + "Only the mayor can select a religion for the town.");
                        }
                    } catch (NotRegisteredException e) {
                        player.sendMessage(ChatColor.RED + "You are not part of a town.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "You are not part of a town.");
                }
                return true;
            }
        }
        return false;
    }

    private void openReligionSelectionMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Select a Religion");

        ItemStack islamBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta islamMeta = (BookMeta) islamBook.getItemMeta();
        if (islamMeta != null) {
            islamMeta.setTitle(ChatColor.GOLD + "Islam");
            islamMeta.setAuthor("Religion Plugin");
            islamMeta.setPages(ChatColor.BLACK + "Islam grants higher damage and health during wars. Alcohol and pork are prohibited.");
            islamBook.setItemMeta(islamMeta);
        }

        ItemStack christianityBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta christianityMeta = (BookMeta) christianityBook.getItemMeta();
        if (christianityMeta != null) {
            christianityMeta.setTitle(ChatColor.GOLD + "Christianity");
            christianityMeta.setAuthor("Religion Plugin");
            christianityMeta.setPages(ChatColor.BLACK + "Christianity grants higher damage during wars.");
            christianityBook.setItemMeta(christianityMeta);
        }

        ItemStack buddhismBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta buddhismMeta = (BookMeta) buddhismBook.getItemMeta();
        if (buddhismMeta != null) {
            buddhismMeta.setTitle(ChatColor.GOLD + "Buddhism");
            buddhismMeta.setAuthor("Religion Plugin");
            buddhismMeta.setPages(ChatColor.BLACK + "Buddhism grants higher health and chunk health during wars.");
            buddhismBook.setItemMeta(buddhismMeta);
        }

        inv.addItem(islamBook, christianityBook, buddhismBook);
        player.openInventory(inv);
    }

    private void createFileIfNotExists() {
        File file = new File(FILE_PATH);
        File directory = file.getParentFile();
        if (!directory.exists()) {
            directory.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Инициализация пустого JSON-объекта в файле
                try (FileWriter writer = new FileWriter(FILE_PATH)) {
                    writer.write("{}");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadReligions() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            for (Object key : jsonObject.keySet()) {
                cityReligions.put((String) key, (String) jsonObject.get(key));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void saveReligions() {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, String> entry : cityReligions.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            writer.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTownReligion(String townName, String religion) {
        cityReligions.put(townName, religion);
        saveReligions();
    }
}