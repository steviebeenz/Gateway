package xyz.refinedev.queue.shared.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.QueueRank;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
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
public class RankManager {

    private final SharedQueue sharedQueue;

    private final Gson gson = new GsonBuilder().serializeNulls().create();

    public void saveRank(QueueRank queueRank) {
        CompletableFuture.runAsync(() -> {
            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hset("ranks", queueRank.getName(), gson.toJson(queueRank));
            });
        });
    }

    public void deleteRank(QueueRank queueRank) {
        CompletableFuture.runAsync(() -> {
            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                jedis.hdel("ranks", queueRank.getName());
            });
        });
    }

    public CompletableFuture<List<QueueRank>> getAsList() {
        return CompletableFuture.supplyAsync(() -> {
            final List<QueueRank> queueRanks = new ArrayList<>();

            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                for (final String value : jedis.hgetAll("ranks").values()) {
                    if (value == null) continue;

                    final QueueRank rank = gson.fromJson(value, QueueRank.class);
                    if (rank == null) continue;

                    queueRanks.add(rank);
                }
            });

            return queueRanks;
        });
    }

    public CompletableFuture<QueueRank> getByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            AtomicReference<QueueRank> queueRank = new AtomicReference<>(null);

            sharedQueue.getJedisAPI().getJedisHandler().runCommand(jedis -> {
                String value = jedis.hget("ranks", name);
                if (value == null) return;

                QueueRank valueRank = gson.fromJson(value, QueueRank.class);
                if (valueRank == null) return;

                queueRank.set(valueRank);
            });

            return queueRank.get();
        });
    }
}
