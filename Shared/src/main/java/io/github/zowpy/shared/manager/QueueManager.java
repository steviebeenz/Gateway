package io.github.zowpy.shared.manager;

import io.github.zowpy.emerald.shared.server.ServerStatus;
import io.github.zowpy.shared.SharedQueue;
import io.github.zowpy.shared.queue.Queue;
import io.github.zowpy.shared.queue.QueuePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter
@AllArgsConstructor
public class QueueManager {

    private final SharedQueue sharedQueue;

    private final List<Queue> queues = new ArrayList<>();

    /**
     * Returns if a player can join the queue
     *
     * @param uuid uuid of the player
     * @param queue queue that the player is joining
     * @return {@link Boolean}
     */

    public boolean canJoin(UUID uuid, Queue queue) {
        return getByPlayer(uuid) == null && sharedQueue.getSharedEmerald().getServerManager().getByPlayer(uuid) != queue.getServer()
                && queue.getServer().getStatus() == ServerStatus.ONLINE || queue.getServer().getStatus() == ServerStatus.WHITELISTED && queue.getServer().getWhitelistedPlayers().contains(uuid);
    }

    /**
     * Returns a queue matching the name
     *
     * @param name name of the queue
     * @return {@link Queue}
     */

    public Queue getByName(String name) {
        return queues.stream()
                .filter(queue -> queue.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns a the player's queue
     *
     * @param uuid uuid of the player
     * @return {@link Queue}
     */

    public Queue getByPlayer(UUID uuid) {
        QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);

        return queuePlayer == null ? null : queuePlayer.getQueue();
    }

}
