package xyz.refinedev.queue.shared.manager;

import xyz.refinedev.queue.shared.queue.QueueRank;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter
public class RankManager {

    private final List<QueueRank> ranks = new ArrayList<>();

    /**
     * Returns a Rank matching that name
     *
     * @param name name of the rank
     * @return {@link QueueRank}
     */

    public QueueRank getByName(String name) {
        return ranks.stream()
                .filter(queueRank -> queueRank.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    /**
     * Returns the default rank
     *
     * @return {@link QueueRank}
     */

    public QueueRank getDefault() {
        return ranks.stream()
                .filter(QueueRank::isDefault).findFirst().orElse(null);
    }
}
