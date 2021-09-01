package xyz.refinedev.queue.shared.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.queue.emerald.shared.server.EmeraldServer;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter @RequiredArgsConstructor
public class PlayerManager {

    private final SharedQueue sharedQueue;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public void savePlayer(QueuePlayer queuePlayer) {
        CompletableFuture.runAsync(() -> {
            sharedQueue.getSharedEmerald().getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hset("queue-players", queuePlayer.getUuid().toString(), gson.toJson(queuePlayer));
            });
        });
    }

    public void deletePlayer(QueuePlayer queuePlayer) {
        CompletableFuture.runAsync(() -> {
           sharedQueue.getSharedEmerald().getJedisAPI().getJedisHandler().runCommand(jedis -> {
               jedis.hdel("queue-players", queuePlayer.getUuid().toString());
           });
        });
    }

    public CompletableFuture<QueuePlayer> getByUUID(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
           final AtomicReference<QueuePlayer> queuePlayer = new AtomicReference<>();

            sharedQueue.getSharedEmerald().getJedisAPI().getJedisHandler().runCommand(jedis -> {
                final String data = jedis.hget("queue-players", uuid.toString());
                if (data == null) return;

                final QueuePlayer queuePlayer1 = gson.fromJson(data, QueuePlayer.class);
                if (queuePlayer1 == null) return;

                queuePlayer.set(queuePlayer1);
            });

           return queuePlayer.get();
        });
    }
}
