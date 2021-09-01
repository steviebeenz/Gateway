package xyz.refinedev.queue.bukkit.util;

import xyz.refinedev.queue.bukkit.Locale;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;
import xyz.refinedev.queue.shared.queue.QueueRank;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/17/2021
 * Project: Gateway
 */

public class RedisUtil {

    /**
     * Adds a player to queue
     *
     * @param player player to add
     * @param queue queue that the player will be added to
     */

    public static void addPlayer(Player player, Queue queue) {

        CompletableFuture<List<QueueRank>> lists = QueuePlugin.getInstance().getSharedQueue().getRankManager().getAsList();

        lists.thenAccept(queueRanks -> {
            QueueRank rank = null;
            queueRanks.sort(Comparator.comparingInt(QueueRank::getPriority));
            for (QueueRank queueRank : queueRanks) {
                if (player.hasPermission(queueRank.getPermission()) || queueRank.isDefault()) {
                    rank = queueRank;
                    break;
                }
            }

            QueuePlayer queuePlayer = new QueuePlayer(player.getUniqueId());
            queuePlayer.setQueue(queue);
            queuePlayer.setRank(rank);

            QueuePlugin.getInstance().getSharedEmerald().getServerManager().getByUUID(QueuePlugin.getInstance().getServerProperties().getUuid()).thenAccept(server -> {
                queuePlayer.setServer(server);
                QueuePlugin.getInstance().getSharedQueue().getPlayerManager().savePlayer(queuePlayer);
            });


        });
    }

    /**
     * Removes a player from queue
     *
     * @param uuid uuid of the player to be removed
     * @param queue queue that the player will be removed from
     */

    public static void removePlayer(UUID uuid, Queue queue) {
        QueuePlugin.getInstance().getSharedQueue().getPlayerManager().getByUUID(uuid).thenAccept(queuePlayer -> {
            if (queuePlayer == null) return;

            QueuePlugin.getInstance().getSharedQueue().getPlayerManager().deletePlayer(queuePlayer);

            queue.getPlayers().remove(queuePlayer);
            QueuePlugin.getInstance().getSharedQueue().getQueueManager().saveQueue(queue);
        });
    }
}
