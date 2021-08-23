package xyz.refinedev.queue.bukkit.task;

import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.bukkit.util.RedisUtil;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * This Project is property of RefineDevelopment © 2021
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
