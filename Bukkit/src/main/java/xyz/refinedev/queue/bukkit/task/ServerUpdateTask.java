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

        QueuePlugin.getInstance().getSharedEmerald().getServerManager().saveServer(serverProperties);
    }

}

