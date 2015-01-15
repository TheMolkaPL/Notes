package me.sainttx.banknotes.command;

import me.sainttx.banknotes.BanknotePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Matthew on 14/01/2015.
 */
public class WithdrawCommand implements CommandExecutor {

    /*
    * The plugin instance
    */
    private BanknotePlugin plugin;

    /**
     * Creates the "/withdraw <amount>" command handler
     */
    public WithdrawCommand(BanknotePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can withdraw bank notes");
        } else if (args.length == 0) {
            return false;
        } else {
            Player player = (Player) sender;

            try {
                double amount = Double.parseDouble(args[0]);

                if (Double.isNaN(amount) || Double.isInfinite(amount) || amount <= 0) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("invalid-number")));
                } else if (plugin.getEconomy().getBalance(player) < amount) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("insufficient-funds")));
                } else if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("inventory-full")));
                } else {
                    ItemStack banknote = plugin.createBanknote(player, amount);
                    plugin.getEconomy().withdrawPlayer(player, amount);

                    player.getInventory().addItem(banknote);
                    player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("note-created").replace("[money]", plugin.formatDouble(amount))));
                }
            } catch (NumberFormatException invalidNumber) {
                player.sendMessage(plugin.colorMessage(plugin.getConfig().getString("invalid-number")));
            }
        }
        return false;
    }
}