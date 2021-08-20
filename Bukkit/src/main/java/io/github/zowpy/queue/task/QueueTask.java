package io.github.zowpy.queue.task;

import io.github.zowpy.emerald.shared.server.ServerStatus;
import io.github.zowpy.queue.Locale;
import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.queue.util.JsonChain;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/17/2021
 * Project: Gateway
 */

public class QueueTask extends Thread {

    @Override
    public void run() {

        while (true) {

            for (Queue queue : QueuePlugin.getInstance().getSharedQueue().getQueueManager().getQueues()) {
                if (queue.getPlayers().isEmpty()) continue;


                queue.getPlayers().forEach(queuePlayer1 -> {
                    Player player = Bukkit.getPlayer(queuePlayer1.getUuid());

                    if (player != null) {
                        for (String s : Locale.QUEUE_REMINDER.getMessageList()) {
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("<pos>", queue.getPosition(queuePlayer1) + "")
                            .replace("<total>", queue.getPlayers().size() + "")
                            .replace("<queue>", queue.getName())));
                        }
                    }
                });

                if (queue.isPaused()) continue;
                if (queue.getServer() == null) continue;
                if (queue.getServer().getStatus() == ServerStatus.OFFLINE) continue;
                if (queue.getServer().getOnlinePlayers().size() >= queue.getServer().getMaxPlayers()) continue;

                QueuePlayer queuePlayer = queue.getPlayers().peek();
               // if (queue.getServer().getStatus() == ServerStatus.WHITELISTED && !queue.getServer().getWhitelistedPlayers().contains(queuePlayer.getUuid())) continue

                if (queuePlayer == null) continue;
                if (queuePlayer.getServer().getUuid().equals(queue.getServer().getUuid())) {
                    queue.getPlayers().remove(queuePlayer);
                    continue;
                }

                JsonChain jc = new JsonChain()
                        .addProperty("uuid", queuePlayer.getUuid().toString())
                        .addProperty("delay", true);

                QueuePlugin.getInstance().getSharedEmerald().getJedisAPI().getJedisHandler().write("send###" + jc.getAsJsonObject().toString());

            }

            try {
                Thread.sleep(QueuePlugin.getInstance().getSettingsFile().getConfig().getInt("queue-interval", 3) * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
