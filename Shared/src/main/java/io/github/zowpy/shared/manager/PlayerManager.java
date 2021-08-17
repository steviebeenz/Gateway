package io.github.zowpy.shared.manager;

import io.github.zowpy.shared.queue.QueuePlayer;
import lombok.Getter;

import java.util.HashMap;
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
public class PlayerManager {

    private final HashMap<UUID, QueuePlayer> players = new HashMap<>();

    /**
     * Returns a player matching that uuid
     *
     * @param uuid uuid of the player
     * @return {@link QueuePlayer}
     */

    public QueuePlayer getByUUID(UUID uuid) {
        return players.get(uuid);
    }
}
