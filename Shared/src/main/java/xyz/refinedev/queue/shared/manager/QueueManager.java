package xyz.refinedev.queue.shared.manager;

import xyz.refinedev.queue.emerald.shared.server.ServerStatus;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.queue.Queue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This Project is property of RefineDevelopment © 2021
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
     * Returns if a player can join the bukkit
     *
     * @param uuid uuid of the player
     * @param queue bukkit that the player is joining
     * @return {@link Boolean}
     */

    public boolean canJoin(UUID uuid, Queue queue) {
        return getByPlayer(uuid) == null && sharedQueue.getSharedEmerald().getServerManager().getByPlayer(uuid) != queue.getServer()
                && queue.getServer().getStatus() == ServerStatus.ONLINE || queue.getServer().getStatus() == ServerStatus.WHITELISTED && queue.getServer().getWhitelistedPlayers().contains(uuid);
    }

    /**
     * Returns a bukkit matching the name
     *
     * @param name name of the bukkit
     * @return {@link Queue}
     */

    public Queue getByName(String name) {
        return queues.stream()
                .filter(queue -> queue.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns a the player's bukkit
     *
     * @param uuid uuid of the player
     * @return {@link Queue}
     */

    public Queue getByPlayer(UUID uuid) {
        QueuePlayer queuePlayer = sharedQueue.getPlayerManager().getByUUID(uuid);

        return queuePlayer == null ? null : queuePlayer.getQueue();
    }

}
