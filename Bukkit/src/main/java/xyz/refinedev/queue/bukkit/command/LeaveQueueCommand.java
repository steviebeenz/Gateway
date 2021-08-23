package xyz.refinedev.queue.bukkit.command;

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
 * Created: 8/17/2021
 * Project: Gateway
 */

public class LeaveQueueCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {

            Player player = (Player) sender;

            Queue queue = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByPlayer(player.getUniqueId());

            if (queue == null) {
                player.sendMessage(ChatColor.RED + "You are not in a bukkit!");
                return false;
            }

            RedisUtil.removePlayer(player.getUniqueId(), queue);

        }else {
            sender.sendMessage(ChatColor.RED + "This command can be only executed in-game!");
        }



        return false;
    }
}
