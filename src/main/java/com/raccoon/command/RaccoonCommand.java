package com.raccoon.command;

import com.raccoon.RaccoonPlugin;
import com.raccoon.entity.RaccoonManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handles all /raccoon commands and tab completion.
 */
public class RaccoonCommand implements CommandExecutor, TabCompleter {

    private final RaccoonPlugin plugin;
    private final RaccoonManager manager;

    public RaccoonCommand(RaccoonPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getRaccoonManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("raccoon.admin") && !sender.hasPermission("raccoon.use")) {
            sender.sendMessage("§cYou do not have permission to use this command.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender, label);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "egg" -> handleEggCommand(sender, args);
            case "spawn" -> handleSpawnCommand(sender, args);
            case "list" -> handleListCommand(sender);
            case "reload" -> handleReloadCommand(sender);
            default -> {
                sender.sendMessage("§cUnknown subcommand. Use §e/" + label + " help §cfor a list of commands.");
            }
        }

        return true;
    }

    private void handleEggCommand(CommandSender sender, String[] args) {
        Player targetPlayer = null;
        int amount = 1;

        if (args.length >= 2) {
            targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage("§cPlayer '" + args[1] + "' not found.");
                return;
            }
        } else if (sender instanceof Player player) {
            targetPlayer = player;
        } else {
            sender.sendMessage("§cUsage from console: /raccoon egg <player> [amount]");
            return;
        }

        if (args.length >= 3) {
            try {
                amount = Math.max(1, Integer.parseInt(args[2]));
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid amount specified.");
                return;
            }
        }

        ItemStack egg = plugin.getRaccoonEgg().createEgg(amount);
        targetPlayer.getInventory().addItem(egg);
        sender.sendMessage("§aGave §e" + amount + "x §6Raccoon Spawn Egg §ato §b" + targetPlayer.getName() + "§a!");
        if (!sender.equals(targetPlayer)) {
            targetPlayer.sendMessage("§aYou received §e" + amount + "x §6Raccoon Spawn Egg§a!");
        }
    }

    private void handleSpawnCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be executed by a player in-game.");
            return;
        }

        boolean isBaby = true;
        if (args.length >= 2) {
            if (args[1].equalsIgnoreCase("adult")) {
                isBaby = false;
            } else if (args[1].equalsIgnoreCase("baby")) {
                isBaby = true;
            }
        }

        Location loc = player.getLocation();
        Fox raccoon = manager.spawnRaccoon(loc, isBaby);
        if (raccoon != null) {
            sender.sendMessage("§aSuccessfully spawned a §e" + (isBaby ? "Baby" : "Adult") + " §6Raccoon 🦝§a!");
        } else {
            sender.sendMessage("§cFailed to spawn raccoon at your location.");
        }
    }

    private void handleListCommand(CommandSender sender) {
        List<Fox> raccoons = manager.getAllRaccoons();
        if (raccoons.isEmpty()) {
            sender.sendMessage("§eNo active raccoons found in loaded worlds.");
            return;
        }

        sender.sendMessage("§8=== §6Active Raccoons (" + raccoons.size() + ") §8===");
        int idx = 1;
        for (Fox r : raccoons) {
            Location loc = r.getLocation();
            boolean isBaby = manager.isBabyRaccoon(r);
            int growth = manager.getGrowthPercentage(r);
            int kills = manager.getAnimalsEaten(r);
            String state = isBaby ? String.format("Baby (%d%%)", growth) : "Adult";
            sender.sendMessage(String.format("§7#%d §6%s §7at §f%s (%.0f, %.0f, %.0f) §7| Kills: §b%d",
                    idx++, state, loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), kills));
        }
    }

    private void handleReloadCommand(CommandSender sender) {
        plugin.getRaccoonConfig().load();
        plugin.getRaccoonEgg().registerRecipe();
        sender.sendMessage("§a[MinecraftRaccoon] Configuration reloaded successfully!");
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage("§8=== §6Minecraft Raccoon Commands §8===");
        sender.sendMessage("§e/" + label + " egg [player] [amount] §7- Give custom Raccoon Spawn Egg");
        sender.sendMessage("§e/" + label + " spawn [baby|adult] §7- Spawn a raccoon at your location");
        sender.sendMessage("§e/" + label + " list §7- View all active raccoons in loaded worlds");
        sender.sendMessage("§e/" + label + " reload §7- Reload configuration settings");
        sender.sendMessage("§e/" + label + " help §7- Show this help menu");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subs = Arrays.asList("egg", "spawn", "list", "reload", "help");
            return filter(subs, args[0]);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("egg")) {
                return filter(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()), args[1]);
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                return filter(Arrays.asList("baby", "adult"), args[1]);
            }
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("egg")) {
            return filter(Arrays.asList("1", "4", "16", "64"), args[2]);
        }
        return Collections.emptyList();
    }

    private List<String> filter(List<String> list, String prefix) {
        String lower = prefix.toLowerCase();
        return list.stream().filter(s -> s.toLowerCase().startsWith(lower)).collect(Collectors.toList());
    }
}
