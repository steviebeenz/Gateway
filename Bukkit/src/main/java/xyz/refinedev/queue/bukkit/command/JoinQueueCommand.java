package xyz.refinedev.queue.bukkit.command;

import xyz.refinedev.queue.bukkit.Locale;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.bukkit.util.RedisUtil;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.util.BungeeUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Project is property of RefineDevelopment © 2021
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

            CompletableFuture<Queue> queueCF = QueuePlugin.getInstance().getSharedQueue().getQueueManager().getByName(args[0]);

            queueCF.thenAccept(queue1 -> {

                if (queue1 == null) {
                    player.sendMessage(ChatColor.RED + "That queue doesn't exist!");
                    return;
                }


                if (QueuePlugin.getInstance().getSharedQueue().getQueueManager().canJoin(player.getUniqueId(), queue1)) {

                    if (player.hasPermission("gateway.bypass")) {
                        player.sendMessage(Locale.SEND_PLAYER.getMessage().replace("<server>", queue1.getBungeeCordName()));
                        BungeeUtil.sendPlayer(QueuePlugin.getInstance(), player, queue1.getBungeeCordName());
                        return;
                    }

                    RedisUtil.addPlayer(player, queue1);
                }else {
                    player.sendMessage(ChatColor.RED + "You can't join this queue right now!");
                }

            });


        }else {
            sender.sendMessage(ChatColor.RED + "This command can be only executed in-game!");
        }




        return false;
    }
}
