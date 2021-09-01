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
import xyz.refinedev.queue.shared.queue.QueuePlayer;

import java.util.concurrent.CompletableFuture;

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
            sender.sendMessage(ChatColor.RED + "Invalid usage! /" + label + " <queue>");
            return true;
        }

        CompletableFuture<Queue> queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByName(args[0]);
        queue.thenAccept(queue1 -> {
            if (queue1 == null) {
                sender.sendMessage(ChatColor.RED + "That queue doesn't exist!");
                return;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Locale.PAUSE_QUEUE.getMessage().replace("<toggle>", queue1.isPaused() ? "resumed" : "paused").replace("<queue>", queue1.getName())));
            queue1.setPaused(!queue1.isPaused());
            QueuePlugin.getInstance().getSharedQueue().getQueueManager().saveQueue(queue1);
        });

        return false;
    }
}
