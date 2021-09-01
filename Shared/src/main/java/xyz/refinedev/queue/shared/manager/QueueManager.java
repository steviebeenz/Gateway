package xyz.refinedev.queue.shared.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter
@RequiredArgsConstructor
public class QueueManager {

    private final SharedQueue sharedQueue;

    private final Gson gson = new GsonBuilder().create();

    /**
     * Returns if a player can join the bukkit
     *
     * @param uuid uuid of the player
     * @param queue queue that the player is joining
     * @return {@link Boolean}
     */

    public boolean canJoin(UUID uuid, Queue queue) {
        final AtomicBoolean inServer = new AtomicBoolean(false);

        sharedQueue.getSharedEmerald().getServerManager().getByPlayer(uuid).thenAccept(emeraldServer -> {
            inServer.set(emeraldServer == queue.getServer());
        });

        return getByPlayer(uuid) == null && !inServer.get()
                && queue.getServer().getServerStatus() == ServerStatus.ONLINE || queue.getServer().getServerStatus() == ServerStatus.WHITELISTED && queue.getServer().getWhitelistedPlayers().contains(uuid);
    }

    /**
     * saves a queue to redis cache
     *
     * @param queue queue to save
     */

    public void saveQueue(Queue queue) {
        CompletableFuture.runAsync(() -> {
            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                System.out.println(queue.getName() + "|" + queue.getPlayers().toString() + "|" + queue.getBungeeCordName());
                jedis.hset("queues", queue.getName(), gson.toJson(queue));
            });
        });
    }

    /**
     * Deletes a queue from redis cache
     *
     * @param queue queue to delete
     */

    public void deleteQueue(Queue queue) {
        CompletableFuture.runAsync(() -> {
            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hdel("queues", queue.getName());
            });
        });
    }

    /**
     * removes a player from queue
     *
     * @param queuePlayer player to remove
     * @param queue queue
     */

    public void removePlayer(QueuePlayer queuePlayer, Queue queue) {
        queue.getPlayers().remove(queuePlayer);
        saveQueue(queue);
    }

    /**
     * Returns a bukkit matching the name
     *
     * @param name name of the queue
     * @return {@link CompletableFuture<Queue>}
     */

    public CompletableFuture<Queue> getByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            final AtomicReference<Queue> queue = new AtomicReference<>(null);

            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                final String value = jedis.hget("queues", name);
                if (value == null) return;

                final Queue valueQueue = gson.fromJson(value, Queue.class);
                if (valueQueue == null) return;

                queue.set(valueQueue);
            });


            return queue.get();
        });
    }

    /**
     * returns all queues in a list
     *
     * @return {@link CompletableFuture<List>}
     */

    public CompletableFuture<List<Queue>> getAsList() {
        return CompletableFuture.supplyAsync(() -> {
           final List<Queue> queues = new ArrayList<>();

           sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
               for (final String value : jedis.hgetAll("queues").values()) {
                   if (value == null) continue;

                   final Queue valueQueue = gson.fromJson(value, Queue.class);
                   if (valueQueue == null) continue;

                   queues.add(valueQueue);
               }
           });

           return queues;
        });
    }

    /**
     * Returns a the player's queue
     *
     * @param uuid uuid of the player
     * @return {@link Queue}
     */

    public Queue getByPlayer(UUID uuid) {
        final CompletableFuture<QueuePlayer> queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);
        final AtomicReference<QueuePlayer> player = new AtomicReference<>();

        queuePlayer.thenAccept(player::set);

        return player.get() == null ? null : player.get().getQueue();
    }

}
