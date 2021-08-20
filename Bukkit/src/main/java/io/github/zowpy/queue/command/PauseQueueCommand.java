package io.github.zowpy.queue.command;

import io.github.zowpy.queue.Locale;
import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.queue.util.RedisUtil;
import io.github.zowpy.shared.queue.Queue;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * This Project is property of Zowpy © 2021
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
            sender.sendMessage(ChatColor.RED + "Invalid usage! /" + label + " <queue>");
            return true;
        }

        Queue queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByName(args[0]);

        if (queue == null) {
            sender.sendMessage(ChatColor.RED + "That queue doesn't exist!");
            return true;
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.PAUSE_QUEUE.getMessage().replace("<toggle>", queue.isPaused() ? "resumed" : "paused").replace("<queue>", queue.getName())));
        RedisUtil.pauseQueue(queue);

        return false;
    }
}
