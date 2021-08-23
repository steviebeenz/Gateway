package xyz.refinedev.queue.bukkit.command;

import xyz.refinedev.queue.bukkit.Locale;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.bukkit.util.RedisUtil;
import xyz.refinedev.queue.shared.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/20/2021
 * Project: Gateway
 */

public class PauseQueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        if (sender instanceof Player && !player.hasPermission("gateway.admin")) {
            player.sendMessage(ChatColor.RED + "You don't have enough permissions to execute this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Invalid usage! /" + label + " <bukkit>");
            return true;
        }

        Queue queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByName(args[0]);

        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "That bukkit doesn't exist!");
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.PAUSE_QUEUE.getMessage().replace("<toggle>", queue.isPaused() ? "resumed" : "paused").replace("<bukkit>", queue.getName())));
        RedisUtil.pauseQueue(queue);

        return false;
    }
}
