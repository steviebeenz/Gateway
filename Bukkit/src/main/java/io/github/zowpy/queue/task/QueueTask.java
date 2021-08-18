package io.github.zowpy.queue.task;

import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.queue.util.JsonChain;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.plaf.synth.SynthRadioButtonMenuItemUI;

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
                        player.sendMessage(ChatColor.AQUA + "You are pos #" + queue.getPosition(queuePlayer1) + " out of #" + queue.getPlayers().size());
                    }
                });

                if (queue.isPaused()) continue;
                if (queue.getServer() == null) continue;
                if (queue.getServer().getOnlinePlayers().size() >= queue.getServer().getMaxPlayers()) continue;


                QueuePlayer queuePlayer = queue.getPlayers().peek();

                JsonChain jc = new JsonChain()
                        .addProperty("uuid", queuePlayer.getUuid().toString())
                        .addProperty("delay", true);

                QueuePlugin.getInstance().getSharedEmerald().getJedisAPI().getJedisHandler().write("send###" + jc.getAsJsonObject().toString());
            }

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }
}
