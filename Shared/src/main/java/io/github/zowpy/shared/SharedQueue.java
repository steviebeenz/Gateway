package io.github.zowpy.shared;

import io.github.zowpy.emerald.shared.SharedEmerald;
import io.github.zowpy.shared.manager.PlayerManager;
import io.github.zowpy.shared.manager.QueueManager;
import io.github.zowpy.shared.manager.RankManager;
import io.github.zowpy.shared.subscription.SharedQueueSubscriber;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This Project is property of Zowpy © 2021
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

        this.queueManager = new QueueManager();
        this.playerManager = new PlayerManager();
        this.rankManager = new RankManager();

        sharedEmerald.getJedisAPI().registerSubscriber(new SharedQueueSubscriber(this));
    }
}
