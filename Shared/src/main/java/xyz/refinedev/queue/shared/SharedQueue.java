package xyz.refinedev.queue.shared;

import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.shared.manager.PlayerManager;
import xyz.refinedev.queue.shared.manager.QueueManager;
import xyz.refinedev.queue.shared.manager.RankManager;
import xyz.refinedev.queue.shared.subscription.SharedQueueSubscriber;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter
public class SharedQueue {

    private final JavaPlugin plugin;
    private final SharedEmerald sharedEmerald;

    private final QueueManager queueManager;
    private final PlayerManager playerManager;
    private final RankManager rankManager;

    public SharedQueue(JavaPlugin plugin, SharedEmerald sharedEmerald) {
        this.plugin = plugin;
        this.sharedEmerald = sharedEmerald;

        this.queueManager = new QueueManager(this);
        this.playerManager = new PlayerManager();
        this.rankManager = new RankManager();

        sharedEmerald.getJedisAPI().registerSubscriber(new SharedQueueSubscriber(this));
    }
}
