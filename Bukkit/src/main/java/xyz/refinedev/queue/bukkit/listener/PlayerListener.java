package xyz.refinedev.queue.bukkit.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.refinedev.queue.bukkit.QueuePlugin;
import xyz.refinedev.queue.bukkit.util.RedisUtil;
import xyz.refinedev.queue.shared.queue.QueuePlayer;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/24/2021
 * Project: Gateway
 */

public class PlayerListener implements Listener {

    @EventHandler
    public void on(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        QueuePlayer queuePlayer = QueuePlugin.getInstance().getSharedQueue().getPlayerManager().getByUUID(player.getUniqueId());

        if (queuePlayer == null) return;

        RedisUtil.removePlayer(queuePlayer.getUuid(), queuePlayer.getQueue());
    }
}
