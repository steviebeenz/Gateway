package xyz.refinedev.queue.shared.subscription;

import com.google.gson.JsonObject;
import io.github.zowpy.jedisapi.redis.subscription.IncomingMessage;
import io.github.zowpy.jedisapi.redis.subscription.JedisSubscriber;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.refinedev.queue.shared.SharedQueue;
import xyz.refinedev.queue.shared.util.BungeeUtil;

import java.util.UUID;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/16/2021
 * Project: Gateway
 */

@AllArgsConstructor
public class SharedQueueSubscriber extends JedisSubscriber {

    private final SharedQueue sharedQueue;

    /**
     * Sends a player
     *
     * @param object data
     */

    @IncomingMessage(payload = "send")
    public void sendPlayer(JsonObject object) {
        UUID uuid = UUID.fromString(object.get("uuid").getAsString());
        Player player = Bukkit.getPlayer(uuid);

        if (player == null) return;

        if (object.has("delay") && object.get("delay").getAsBoolean()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(sharedQueue.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
                    BungeeUtil.sendPlayer(sharedQueue.getPlugin(), player, object.get("server").getAsString());
                }
            }, 20*3L);
        }else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', object.get("message").getAsString()));
            BungeeUtil.sendPlayer(sharedQueue.getPlugin(), player, object.get("server").getAsString());
        }


    }

}
