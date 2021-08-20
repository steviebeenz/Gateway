package io.github.zowpy.shared.subscription;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.zowpy.jedisapi.redis.subscription.IncomingMessage;
import io.github.zowpy.jedisapi.redis.subscription.JedisSubscriber;
import io.github.zowpy.shared.SharedQueue;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import io.github.zowpy.shared.util.BungeeUtil;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@AllArgsConstructor
public class SharedQueueSubscriber extends JedisSubscriber {

    private final SharedQueue sharedQueue;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    /**
     * Updates a queue
     *
     * @param object data to update the queue
     */

    @IncomingMessage(payload = "updateQueue")
    public void updateQueue(JsonObject object) {
        Queue queue = sharedQueue.getQueueManager().getByName(object.get("name").getAsString());

        if (queue != null) {
            queue.setBungeeCordName(object.get("bungee").getAsString());

            PriorityQueue<QueuePlayer> queuePlayers = new PriorityQueue<>(Comparator.comparingInt(QueuePlayer::getPriority));

            for (JsonElement e : object.get("players").getAsJsonArray()) {
                JsonObject object1 = e.getAsJsonObject();

                UUID uuid = UUID.fromString(object1.get("uuid").getAsString());
                QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);

                if (queuePlayer == null) {
                    queuePlayer = new QueuePlayer(uuid);
                }

                queuePlayer.setQueue(queue);
                queuePlayer.setServer(sharedQueue.getSharedEmerald().getServerManager().getByPlayer(uuid));
                queuePlayer.setRank(sharedQueue.getRankManager().getByName(object1.get("rank").getAsString()));
                queuePlayers.add(queuePlayer);
            }

            queue.setPlayers(queuePlayers);
            queue.setServer(sharedQueue.getSharedEmerald().getServerManager().getByUUID(UUID.fromString(object.get("server").getAsString())));
            queue.setPaused(object.get("paused").getAsBoolean());
        }
    }

    /**
     * Updates all queues
     *
     * @param object data that is used to update the queues
     */

    @IncomingMessage(payload = "updateQueues")
    public void updateQueues(JsonObject object) {

        for (JsonElement e : object.get("queues").getAsJsonArray()) {
            JsonObject object1 = e.getAsJsonObject();

            String name =  object1.get("name").getAsString();

            Queue queue = sharedQueue.getQueueManager().getByName(name);

            if (queue == null) {
                queue = new Queue(name);
                sharedQueue.getQueueManager().getQueues().add(queue);
            }

            queue.setBungeeCordName(object1.get("bungee").getAsString());
            queue.setServer(sharedQueue.getSharedEmerald().getServerManager().getByUUID(UUID.fromString(object1.get("server").getAsString())));
            PriorityQueue<QueuePlayer> queuePlayers = new PriorityQueue<>(Comparator.comparingInt(QueuePlayer::getPriority));

            for (JsonElement e1 : object1.get("players").getAsJsonArray()) {
                JsonObject object2 = e1.getAsJsonObject();

                UUID uuid = UUID.fromString(object2.get("uuid").getAsString());
                QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);

                if (queuePlayer == null) {
                    queuePlayer = new QueuePlayer(uuid);
                }

                queuePlayer.setQueue(queue);
                queuePlayer.setServer(sharedQueue.getSharedEmerald().getServerManager().getByUUID(UUID.fromString(object2.get("server").getAsString())));
                queuePlayer.setRank(sharedQueue.getRankManager().getByName(object2.get("rank").getAsString()));
                queuePlayers.add(queuePlayer);
            }

            queue.setPlayers(queuePlayers);
            queue.setPaused(object1.get("paused").getAsBoolean());

        }
    }

    /**
     * Adds a player to queue
     *
     * @param object data used to add the player to queue
     */

    @IncomingMessage(payload = "addPlayer")
    public void addPlayer(JsonObject object) {
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Queue queue = sharedQueue.getQueueManager().getByName(object.get("queue").getAsString());

        QueuePlayer queuePlayer = new QueuePlayer(uuid);
        queuePlayer.setQueue(queue);
        queuePlayer.setRank(sharedQueue.getRankManager().getByName(object.get("rank").getAsString()));
        queuePlayer.setServer(sharedQueue.getSharedEmerald().getServerManager().getByUUID(UUID.fromString(object.get("server").getAsString())));

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
        }

        queue.getPlayers().add(queuePlayer);
        sharedQueue.getPlayerManager().getPlayers().put(uuid, queuePlayer);
    }

    /**
     * Removes a player from queue
     *
     * @param object data used to remove a player from queue
     */

    @IncomingMessage(payload = "removePlayer")
    public void removePlayer(JsonObject object) {
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Queue queue = sharedQueue.getQueueManager().getByName(object.get("queue").getAsString());

        QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);

        Player player = Bukkit.getPlayer(uuid);

        if (player != null) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
        }

        queue.getPlayers().remove(queuePlayer);
        sharedQueue.getPlayerManager().getPlayers().remove(uuid, queuePlayer);

    }

    /**
     * Sends a player
     *
     * @param object data
     */

    @IncomingMessage(payload = "send")
    public void sendPlayer(JsonObject object) {
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);


        if (queuePlayer == null) return;
        sharedQueue.getPlayerManager().getPlayers().remove(uuid, queuePlayer);
        if (queuePlayer.getQueue() == null) return;
        queuePlayer.getQueue().getPlayers().removeIf(queuePlayer1 -> queuePlayer1.getUuid().equals(uuid));


        Queue queue = queuePlayer.getQueue();
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (object.has("delay") && object.get("delay").getAsBoolean()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(sharedQueue.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
                    BungeeUtil.sendPlayer(sharedQueue.getPlugin(), player, queue.getBungeeCordName());
                }
            }, 20*3L);
        }else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
            BungeeUtil.sendPlayer(sharedQueue.getPlugin(), player, queue.getBungeeCordName());
        }



    }

    /**
     * Sends a message to all players queued
     *
     * @param object data used to send the message
     */

    @IncomingMessage(payload = "sendMessage")
    public void sendMessages(JsonObject object) {
        for (Queue queue : sharedQueue.getQueueManager().getQueues()) {
            for (QueuePlayer queuePlayer : queue.getPlayers()) {
                Player player = Bukkit.getPlayer(queuePlayer.getUuid());
                if (player != null) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
                }
            }
        }
    }

    /**
     * Toggles a specific queue pause
     *
     * @param object data used to pause a queue
     */

    @IncomingMessage(payload = "togglePause")
    public void togglePause(JsonObject object) {
        Queue queue = sharedQueue.getQueueManager().getByName(object.get("queue").getAsString());

        if (queue == null) return;

        queue.setPaused(!queue.isPaused());
    }

}
