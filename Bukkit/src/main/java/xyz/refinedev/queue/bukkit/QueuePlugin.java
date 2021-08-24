package xyz.refinedev.queue.bukkit;

import com.google.gson.JsonObject;
import xyz.refinedev.queue.bukkit.listener.PlayerListener;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.emerald.shared.server.EmeraldGroup;
import xyz.refinedev.queue.emerald.shared.server.ServerProperties;
import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.emerald.shared.util.TPSUtility;
import xyz.refinedev.queue.jedisapi.redis.RedisCredentials;
import xyz.refinedev.queue.bukkit.command.JoinQueueCommand;
import xyz.refinedev.queue.bukkit.command.LeaveQueueCommand;
import xyz.refinedev.queue.bukkit.command.PauseQueueCommand;
import xyz.refinedev.queue.bukkit.task.QueueTask;
import xyz.refinedev.queue.bukkit.task.QueueUpdateTask;
import xyz.refinedev.queue.bukkit.task.ServerUpdateTask;
import xyz.refinedev.queue.bukkit.util.ConfigFile;
import xyz.refinedev.queue.bukkit.util.IPUtil;
import xyz.refinedev.queue.bukkit.util.PiracyMeta;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueueRank;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class QueuePlugin extends JavaPlugin {

    @Getter
    private static QueuePlugin instance;

    private ConfigFile settingsFile;
    private ConfigFile ranksFile;
    private ConfigFile langFile;

    private SharedEmerald sharedEmerald;
    private ServerProperties serverProperties;

    private SharedQueue sharedQueue;

    private int delay;

    @Override
    public void onEnable() {
        instance = this;

        settingsFile = new ConfigFile(this, "settings");
        ranksFile = new ConfigFile(this, "ranks");
        langFile = new ConfigFile(this, "lang");

        PiracyMeta piracyMeta = new PiracyMeta(this, settingsFile.getConfig().getString("license", "null"));
        piracyMeta.verify();

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

        new ServerUpdateTask();

        JsonObject object = new JsonObject();
        object.addProperty("name", serverProperties.getName());

        sharedEmerald.getJedisAPI().getJedisHandler().write("start###"+ object.toString());

        sharedQueue = new SharedQueue(this, sharedEmerald);

        loadQueues(settingsFile.getConfig().getConfigurationSection("queues"));
        loadRanks(ranksFile.getConfig().getConfigurationSection("ranks"));

        this.delay = settingsFile.getConfig().getInt("bukkit-interval", 3);

        new QueueUpdateTask();
        new QueueTask();

        getCommand("joinqueue").setExecutor(new JoinQueueCommand());
        getCommand("leavequeue").setExecutor(new LeaveQueueCommand());
        getCommand("pause").setExecutor(new PauseQueueCommand());

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    }

    @Override
    public void onDisable() {
        sharedEmerald.getServerManager().setOffline(sharedEmerald.getServerManager().getByUUID(sharedEmerald.getUuid()));

        instance = null;
    }

    private void loadRanks(ConfigurationSection section) {
        if (section == null || section.getKeys(false) == null) return;

        for (String rank : section.getKeys(false)) {
            ConfigurationSection sec = section.getConfigurationSection(rank);
            QueueRank queueRank = new QueueRank(rank);
            queueRank.setDefault(sec.getBoolean("default", false));
            queueRank.setPermission(sec.getString("permission", ""));
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
        }
    }
}
