package io.github.zowpy.queue.task;

import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.queue.util.RedisUtil;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.stream.Collectors;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/23/2021
 * Project: Gateway
 */

public class QueueUpdateTask extends BukkitRunnable {

    public QueueUpdateTask() {
        this.runTaskTimerAsynchronously(QueuePlugin.getInstance(), 0L, 20*5L);
    }

    @Override
    public void run() {
        try {
            RedisUtil.updateQueues(QueuePlugin.getInstance().getSharedQueue().getQueueManager().getQueues());
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
