package io.github.zowpy.queue.command;

import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.queue.util.RedisUtil;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
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
 * Created: 8/17/2021
 * Project: Gateway
 */

public class JoinQueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Invalid args: /" + label + " <queue>");
                return false;
            }

            Queue queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByPlayer(player.getUniqueId());

            if (queue != null) {
                player.sendMessage(ChatColor.RED + "You are already in a queue!");
                return false;
            }

            queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByName(args[0]);

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "That queue doesn't exist!");
                return false;
            }

            if (QueuePlugin.getInstance().getSharedQueue().getQueueManager().canJoin(player.getUniqueId(), queue)) {
                RedisUtil.addPlayer(player, queue);
            }else {
                player.sendMessage(ChatColor.RED + "You can't join this queue right now!");
            }


        }else {
            sender.sendMessage(ChatColor.RED + "This command can be only executed in-game!");
        }




        return false;
    }
}
