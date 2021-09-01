package xyz.refinedev.queue.shared;

import io.github.zowpy.jedisapi.JedisAPI;
import io.github.zowpy.jedisapi.redis.RedisCredentials;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.shared.manager.PlayerManager;
import xyz.refinedev.queue.shared.manager.QueueManager;
import xyz.refinedev.queue.shared.manager.RankManager;
import xyz.refinedev.queue.shared.subscription.SharedQueueSubscriber;

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
    private final JedisAPI jedisAPI;

    private final QueueManager queueManager;
    private final PlayerManager playerManager;
    private final RankManager rankManager;

    private final SharedEmerald sharedEmerald;

    public SharedQueue(JavaPlugin plugin, RedisCredentials credentials) {
        this.plugin = plugin;
        this.jedisAPI = new JedisAPI(credentials);

        this.queueManager = new QueueManager(this);
        this.playerManager = new PlayerManager(this);
        this.rankManager = new RankManager(this);

        sharedEmerald = null;

        jedisAPI.registerSubscriber(new SharedQueueSubscriber(this));
    }

    public SharedQueue(JavaPlugin plugin, RedisCredentials credentials, SharedEmerald sharedEmerald) {
        this.plugin = plugin;
        this.jedisAPI = new JedisAPI(credentials);

        this.queueManager = new QueueManager(this);
        this.playerManager = new PlayerManager(this);
        this.rankManager = new RankManager(this);

        this.sharedEmerald = sharedEmerald;

        jedisAPI.registerSubscriber(new SharedQueueSubscriber(this));
    }

    public SharedQueue(RedisCredentials credentials, SharedEmerald sharedEmerald, boolean bungee) {
        this.jedisAPI = new JedisAPI(credentials);

        this.queueManager = new QueueManager(this);
        this.playerManager = new PlayerManager(this);
        this.rankManager = new RankManager(this);

        this.sharedEmerald = sharedEmerald;
        this.plugin = null;

        if (!bungee) {
            jedisAPI.registerSubscriber(new SharedQueueSubscriber(this));
        }
    }
}
