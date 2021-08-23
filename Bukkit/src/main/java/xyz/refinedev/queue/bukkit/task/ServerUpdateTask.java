package xyz.refinedev.queue.bukkit.task;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import xyz.refinedev.queue.emerald.shared.server.ServerProperties;
import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.emerald.shared.util.TPSUtility;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/19/2021
 * Project: Gateway
 */

public class ServerUpdateTask extends BukkitRunnable {

    public ServerUpdateTask() {
        this.runTaskTimerAsynchronously(QueuePlugin.getInstance(), 0L, 20*5L);

    }


    @Override
    public void run() {

        ServerProperties serverProperties = QueuePlugin.getInstance().getSharedEmerald().getServerProperties();
        serverProperties.setServerStatus(Bukkit.hasWhitelist() ? ServerStatus.WHITELISTED : ServerStatus.ONLINE);
        serverProperties.setOnlinePlayers(Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toList()));
        serverProperties.setWhitelistedPlayers(Bukkit.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList()));
        serverProperties.setMaxPlayers(Bukkit.getMaxPlayers());
        serverProperties.setTps(TPSUtility.round(Double.parseDouble(TPSUtility.getTPS())));

        JsonObject object = new JsonObject();
        object.addProperty("uuid", serverProperties.getUuid().toString());
        object.addProperty("name", serverProperties.getName());
        object.addProperty("serverStatus", serverProperties.getServerStatus().name());
        object.addProperty("ip", serverProperties.getIp());
        object.addProperty("port", serverProperties.getPort());
        object.addProperty("group", serverProperties.getGroup().getName());
        object.addProperty("tps", serverProperties.getTps());

        JsonArray whitelistedPlayers = new JsonArray();

        for (UUID uuid : Bukkit.getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList())) {
            whitelistedPlayers.add(new JsonPrimitive(uuid.toString()));
        }

        object.add("whitelistedPlayers", whitelistedPlayers);

        JsonArray onlinePlayers = new JsonArray();
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(new JsonPrimitive(player.getUniqueId().toString()));
        }

        object.add("onlinePlayers", onlinePlayers);
        object.addProperty("maxPlayers", serverProperties.getMaxPlayers());

        QueuePlugin.getInstance().getSharedEmerald().getJedisAPI().getJedisHandler().write("updateserver###" + object.toString());
    }

}

