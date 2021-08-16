package io.github.zowpy.shared.manager;

import io.github.zowpy.shared.queue.Queue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@Getter
public class QueueManager {

    private final List<Queue> queues = new ArrayList<>();

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

}
