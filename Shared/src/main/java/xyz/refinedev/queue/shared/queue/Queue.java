package xyz.refinedev.queue.shared.queue;

import xyz.refinedev.queue.emerald.shared.server.EmeraldServer;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter @Setter
public class Queue {

    private String name, bungeeCordName;
    private EmeraldServer server;
    private PriorityQueue<QueuePlayer> players = new PriorityQueue<>(Comparator.comparingInt(QueuePlayer::getPriority).reversed());
    private boolean paused;

    /**
     * Creates an instance of bukkit
     *
     * @param name name of the bukkit
     */

    public Queue(String name) {
        this.name = name;
    }

    /**
     * Creates an instance of bukkit
     *
     * @param name name of the bukkit
     * @param bungeeCordName name of the bungeecord server
     */

    public Queue(String name, String bungeeCordName) {
        this.name = name;
        this.bungeeCordName = bungeeCordName;
    }


    /**
     * Returns the position of the player
     *
     * @param player player
     * @return {@link Integer}
     */

    public int getPosition(QueuePlayer player) {
        if (getPlayers().isEmpty() || !getPlayers().contains(player)) return 0;

        PriorityQueue<QueuePlayer> players1 = new PriorityQueue<>(getPlayers());

        int pos = 0;
        for (QueuePlayer player1 : players1) {
            pos++;
            if (player1.getUuid().equals(player.getUuid())) {
                break;
            }
        }

        return pos;
    }
}
