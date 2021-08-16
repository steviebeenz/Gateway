package io.github.zowpy.shared.manager;

import io.github.zowpy.shared.queue.QueuePlayer;
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
public class PlayerManager {

    private final List<QueuePlayer> players = new ArrayList<>();

    /**
     * Returns a player matching that uuid
     *
     * @param uuid uuid of the player
     * @return {@link QueuePlayer}
     */

    public QueuePlayer getByUUID(UUID uuid) {
        return players.stream()
                .filter(queuePlayer -> queuePlayer.getUuid().equals(uuid)).findFirst().orElse(null);
    }
}
