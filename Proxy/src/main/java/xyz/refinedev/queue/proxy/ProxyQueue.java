package xyz.refinedev.queue.proxy;

import io.github.zowpy.jedisapi.redis.RedisCredentials;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.proxy.listener.PlayerListener;
import xyz.refinedev.queue.proxy.util.ConfigFile;
import xyz.refinedev.queue.proxy.util.PiracyMeta;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueueRank;

import java.util.concurrent.CompletableFuture;

@Getter
public final class ProxyQueue extends Plugin {

    @Getter private static ProxyQueue instance;

    private ConfigFile settingsFile;
    private ConfigFile ranksFile;

    private SharedQueue sharedQueue;
    private SharedEmerald sharedEmerald;

    @Override
    public void onEnable() {
        instance = this;

        settingsFile = new ConfigFile(this, "settings");
        ranksFile = new ConfigFile(this, "ranks");

        new PiracyMeta(this, settingsFile.getConfig().getString("license", "null")).verify();

        sharedEmerald = new SharedEmerald(null, new RedisCredentials(
                settingsFile.getConfig().getString("redis.host"),
                settingsFile.getConfig().getString("redis.auth.password"),
                "GATEWAY:BUKKIT",
                settingsFile.getConfig().getInt("redis.port"),
                settingsFile.getConfig().getBoolean("redis.auth.enabled")));

        sharedQueue = new SharedQueue(sharedEmerald.getJedisAPI().getCredentials(), sharedEmerald,true);

        loadQueues();
        loadRanks();

        getProxy().getPluginManager().registerListener(this, new PlayerListener());

        getProxy().getConsole().sendMessage(new ComponentBuilder("[Gateway-Proxy] Successfully enabled!").color(ChatColor.RED).create());
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    private void loadRanks() {
        if (ranksFile.getConfig().getSection("ranks") == null || ranksFile.getConfig().getSection("ranks").getKeys().isEmpty()) return;

        for (String rank : ranksFile.getConfig().getSection("ranks").getKeys()) {
            Configuration configuration = ranksFile.getConfig().getSection("ranks." + rank);
            QueueRank queueRank = new QueueRank(rank);
            queueRank.setDefault(configuration.getBoolean("default", false));
            queueRank.setPermission(configuration.getString("permission", ""));
            queueRank.setPriority(configuration.getInt("priority", 0));

            sharedQueue.getRankManager().getByName(rank).thenAccept(queueRank1 -> {
                if (queueRank1 == null) {
                    sharedQueue.getRankManager().saveRank(queueRank);
                }
            });
        }
    }

    @SneakyThrows
    private void loadQueues() {
        if (settingsFile.getConfig().getSection("queues") == null || settingsFile.getConfig().getSection("queues").getKeys().isEmpty()) return;

        for (String queue : settingsFile.getConfig().getSection("queues").getKeys()) {
            Configuration sec = settingsFile.getConfig().getSection("queues." + queue);
            Queue queue1 = new Queue(queue);
            queue1.setPaused(sec.getBoolean("paused"));
            queue1.setServer(sharedEmerald.getServerManager().getByConnection(sec.getString("ip"), sec.getInt("port")).get());
            queue1.setBungeeCordName(sec.getString("bungee"));

            sharedQueue.getQueueManager().saveQueue(queue1);
        }
    }
}
