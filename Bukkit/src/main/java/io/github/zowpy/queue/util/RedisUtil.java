package io.github.zowpy.queue.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.zowpy.queue.Locale;
import io.github.zowpy.queue.QueuePlugin;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import io.github.zowpy.shared.queue.QueueRank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/17/2021
 * Project: Gateway
 */

public class RedisUtil {

    private static final QueuePlugin queuePlugin = QueuePlugin.getInstance();

    /**
     * Sends a message to redis to update all queues
     *
     * @param queues queues to update
     */

    public static void updateQueues(List<Queue> queues) {
        JsonObject object = new JsonObject();
        JsonArray queuesArray = new JsonArray();

        for (Queue queue : queues) {
            JsonChain jc = new JsonChain();

            jc.addProperty("name", queue.getName()).addProperty("bungee", queue.getBungeeCordName())
            .addProperty("server", queue.getServer().getUuid().toString())
            .addProperty("paused", queue.isPaused());

            JsonArray players = new JsonArray();
            for (QueuePlayer queuePlayer : queue.getPlayers()) {
                JsonChain player = new JsonChain();
                player.addProperty("uuid", queuePlayer.getUuid().toString())
                        .addProperty("rank", queuePlayer.getRank().getName())
                        .addProperty("server", queuePlayer.getServer().getUuid().toString());
                players.add(player.getAsJsonObject());
            }
            jc.add("players", players);

            queuesArray.add(jc.getAsJsonObject());
        }

        object.add("queues", queuesArray);

        queuePlugin.getSharedEmerald().getJedisAPI().getJedisHandler().write("updateQueues###" + object.toString());
    }

    /**
     * Sends a message to all queued players
     *
     * @param message message to send
     */

    public static void sendMessage(String message) {
        JsonObject object = new JsonObject();
        object.addProperty("message", message);

        queuePlugin.getSharedEmerald().getJedisAPI().getJedisHandler().write("sendMessage###" + object.toString());
    }

    /**
     * Adds a player to queue
     *
     * @param player player to add
     * @param queue queue that the player will be added to
     */

    public static void addPlayer(Player player, Queue queue) {
        QueueRank rank = null;

        List<QueueRank> ranks = new ArrayList<>(queuePlugin.getSharedQueue().getRankManager().getRanks());
        ranks.sort(Comparator.comparingInt(QueueRank::getPriority));
        for (QueueRank queueRank : ranks) {
            if (player.hasPermission(queueRank.getPermission()) || queueRank.isDefault()) {
                rank = queueRank;
                break;
            }
        }

        JsonChain jc = new JsonChain()
                .addProperty("uuid", player.getUniqueId().toString())
                .addProperty("queue", queue.getName())
                .addProperty("rank", rank == null ? "null" : rank.getName())
                .addProperty("server", queuePlugin.getSharedEmerald().getUuid().toString())
                .addProperty("message", Locale.JOIN_QUEUE.getMessage().replace("<queue>", queue.getName()));

        queuePlugin.getSharedEmerald().getJedisAPI().getJedisHandler().write("addPlayer###" + jc.getAsJsonObject().toString());
    }

    /**
     * Removes a player from queue
     *
     * @param uuid uuid of the player to be removed
     * @param queue queue that the player will be removed from
     */

    public static void removePlayer(UUID uuid, Queue queue) {
        JsonChain jc = new JsonChain()
                .addProperty("uuid", uuid.toString())
                .addProperty("queue", queue.getName())
                .addProperty("message", Locale.LEAVE_QUEUE.getMessage().replace("<queue>", queue.getName()));

        queuePlugin.getSharedEmerald().getJedisAPI().getJedisHandler().write("removePlayer###" + jc.getAsJsonObject().toString());
    }

    /**
     * Pauses a specific queue
     *
     * @param queue queue to pause
     */

    public static void pauseQueue(Queue queue) {
        JsonChain jc = new JsonChain().addProperty("queue", queue.getName());

        queuePlugin.getSharedEmerald().getJedisAPI().getJedisHandler().write("togglePause###" + jc.getAsJsonObject().toString());
    }


}
