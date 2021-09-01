package xyz.refinedev.queue.proxy.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.refinedev.queue.proxy.ProxyQueue;
import xyz.refinedev.queue.shared.queue.QueuePlayer;

import java.util.concurrent.CompletableFuture;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 9/1/2021
 * Project: Gateway
 */

public class PlayerListener implements Listener {

    @EventHandler
    public void on(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();

        CompletableFuture<QueuePlayer> queuePlayer = ProxyQueue.getInstance().getSharedQueue().getPlayerManager().getByUUID(player.getUniqueId());

        queuePlayer.thenAccept(queuePlayer1 -> {
            if (queuePlayer1 == null) return;


            ProxyQueue.getInstance().getSharedQueue().getPlayerManager().deletePlayer(queuePlayer1);
        });

    }
}
