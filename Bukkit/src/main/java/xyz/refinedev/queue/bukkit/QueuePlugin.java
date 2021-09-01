package xyz.refinedev.queue.bukkit;

import com.google.gson.JsonObject;
import io.github.zowpy.jedisapi.redis.RedisCredentials;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.queue.bukkit.command.JoinQueueCommand;
import xyz.refinedev.queue.bukkit.command.LeaveQueueCommand;
import xyz.refinedev.queue.bukkit.command.PauseQueueCommand;
import xyz.refinedev.queue.bukkit.task.QueueTask;
import xyz.refinedev.queue.bukkit.task.ServerUpdateTask;
import xyz.refinedev.queue.bukkit.util.ConfigFile;
import xyz.refinedev.queue.bukkit.util.IPUtil;
import xyz.refinedev.queue.bukkit.util.PiracyMeta;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;
import xyz.refinedev.queue.emerald.shared.server.EmeraldGroup;
import xyz.refinedev.queue.emerald.shared.server.ServerProperties;
import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.emerald.shared.util.TPSUtility;
import xyz.refinedev.queue.shared.SharedQueue;

import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public final class QueuePlugin extends JavaPlugin {

    @Getter
    private static QueuePlugin instance;

    private ConfigFile settingsFile;
    private ConfigFile langFile;

    private SharedEmerald sharedEmerald;
    private ServerProperties serverProperties;

    private SharedQueue sharedQueue;

    private int delay;

    @Override
    public void onEnable() {
        instance = this;

        settingsFile = new ConfigFile(this, "settings");
        langFile = new ConfigFile(this, "lang");

        new PiracyMeta(this, settingsFile.getConfig().getString("license", "null")).verify();

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
        sharedEmerald.getServerManager().saveServer(serverProperties);

        new ServerUpdateTask();

        JsonObject object = new JsonObject();
        object.addProperty("name", serverProperties.getName());

       // sharedEmerald.getJedisAPI().getJedisHandler().write("start###"+ object.toString());

        sharedQueue = new SharedQueue(this, sharedEmerald.getJedisAPI().getCredentials(), sharedEmerald);

        this.delay = settingsFile.getConfig().getInt("queue-interval", 3);

        new QueueTask();

        getCommand("joinqueue").setExecutor(new JoinQueueCommand());
        getCommand("leavequeue").setExecutor(new LeaveQueueCommand());
        getCommand("pause").setExecutor(new PauseQueueCommand());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

    }

    @Override
    public void onDisable() {
        serverProperties.setServerStatus(ServerStatus.OFFLINE);
        sharedEmerald.getServerManager().saveServer(serverProperties);

        instance = null;
    }
}
