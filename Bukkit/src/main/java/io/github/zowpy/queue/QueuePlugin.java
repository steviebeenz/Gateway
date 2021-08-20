package io.github.zowpy.queue;

import com.google.gson.JsonObject;
import io.github.zowpy.emerald.shared.SharedEmerald;
import io.github.zowpy.emerald.shared.server.EmeraldGroup;
import io.github.zowpy.emerald.shared.server.EmeraldServer;
import io.github.zowpy.emerald.shared.server.ServerProperties;
import io.github.zowpy.emerald.shared.server.ServerStatus;
import io.github.zowpy.emerald.shared.util.TPSUtility;
import io.github.zowpy.jedisapi.redis.RedisCredentials;
import io.github.zowpy.queue.command.JoinQueueCommand;
import io.github.zowpy.queue.command.LeaveQueueCommand;
import io.github.zowpy.queue.task.QueueTask;
import io.github.zowpy.queue.task.ServerUpdateTask;
import io.github.zowpy.queue.util.ConfigFile;
import io.github.zowpy.queue.util.IPUtil;
import io.github.zowpy.shared.SharedQueue;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueueRank;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class QueuePlugin extends JavaPlugin {

    @Getter
    private static QueuePlugin instance;

    private ConfigFile settingsFile;
    private ConfigFile ranksFile;
    private ConfigFile langFile;

    private Jedis jedis;

    private SharedEmerald sharedEmerald;
    private ServerProperties serverProperties;

    private SharedQueue sharedQueue;

    @Override
    public void onEnable() {
        instance = this;

        settingsFile = new ConfigFile(this, "settings");
        ranksFile = new ConfigFile(this, "ranks");
        langFile = new ConfigFile(this, "lang");

        serverProperties = new ServerProperties();
        serverProperties.setServerStatus(getServer().hasWhitelist() ? ServerStatus.WHITELISTED : ServerStatus.ONLINE);
        serverProperties.setIp(IPUtil.getIP());
        serverProperties.setPort(getServer().getPort());
        serverProperties.setName(settingsFile.getConfig().getString("server-name"));
        serverProperties.setOnlinePlayers(getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList()));
        serverProperties.setWhitelistedPlayers(getServer().getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()));
        serverProperties.setMaxPlayers(getServer().getMaxPlayers());
        serverProperties.setTps(Double.parseDouble(TPSUtility.getTPS()));

        UUID uuid;
        if (settingsFile.getConfig().getString("server-uuid").equalsIgnoreCase("null")) {
            uuid = UUID.randomUUID();
            settingsFile.getConfig().set("server-uuid", uuid.toString());
            settingsFile.save();
        }else {
            uuid = UUID.fromString(settingsFile.getConfig().getString("server-uuid"));
        }

        serverProperties.setUuid(uuid);

        /*  Create SharedEmerald instance  */
        sharedEmerald = new SharedEmerald(uuid, new RedisCredentials(
                settingsFile.getConfig().getString("redis.host"),
                settingsFile.getConfig().getString("redis.auth.password"),
                "GATEWAY:BUKKIT",
                settingsFile.getConfig().getInt("redis.port"),
                settingsFile.getConfig().getBoolean("redis.auth.enabled")
        ));

        for (String s : settingsFile.getConfig().getStringList("server-groups")) {
            sharedEmerald.getGroupManager().getGroups().add(new EmeraldGroup(s));
        }

        serverProperties.setGroup(sharedEmerald.getGroupManager().getByName(settingsFile.getConfig().getString("server-group")));

        sharedEmerald.setServerProperties(serverProperties);
        /*  Create the current server to redis cache  */
        sharedEmerald.getServerManager().createServer();

        sharedEmerald.getServerManager().updateServers();

        jedis = sharedEmerald.getJedisAPI().getJedisHandler().getJedisPool().getResource();

        new ServerUpdateTask();

        JsonObject object = new JsonObject();
        object.addProperty("name", serverProperties.getName());

        sharedEmerald.getJedisAPI().getJedisHandler().write("start###"+ object.toString());

        sharedQueue = new SharedQueue(this, sharedEmerald);

        loadQueues(settingsFile.getConfig().getConfigurationSection("queues"));
        loadRanks(ranksFile.getConfig().getConfigurationSection("ranks"));

        new QueueTask().start();

        getCommand("joinqueue").setExecutor(new JoinQueueCommand());
        getCommand("leavequeue").setExecutor(new LeaveQueueCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    }

    @Override
    public void onDisable() {
        sharedEmerald.getServerManager().setOffline(sharedEmerald.getServerManager().getByUUID(sharedEmerald.getUuid()), jedis);

        instance = null;
    }

    private void loadRanks(ConfigurationSection section) {
        if (section == null || section.getKeys(false) == null) return;

        for (String rank : section.getKeys(false)) {
            ConfigurationSection sec = section.getConfigurationSection(rank);
            QueueRank queueRank = new QueueRank(rank);
            queueRank.setDefault(sec.get("default") != null && sec.getBoolean("default"));
            queueRank.setPermission(sec.get("permission") == null ? "" : sec.getString("permission"));
            queueRank.setPriority(sec.getInt("priority", 0));
            sharedQueue.getRankManager().getRanks().add(queueRank);
        }
    }

    private void loadQueues(ConfigurationSection section) {
        if (section == null || section.getKeys(false) == null) return;

        for (String queue : section.getKeys(false)) {
            ConfigurationSection sec = section.getConfigurationSection(queue);
            Queue queue1 = new Queue(queue);
            queue1.setPaused(sec.getBoolean("paused"));
            queue1.setServer(sharedEmerald.getServerManager().getByConnection(sec.getString("ip"), sec.getInt("port")));
            queue1.setBungeeCordName(sec.getString("bungee"));
            sharedQueue.getQueueManager().getQueues().add(queue1);
            System.out.println(queue1.getServer() == null);
        }
    }
}
