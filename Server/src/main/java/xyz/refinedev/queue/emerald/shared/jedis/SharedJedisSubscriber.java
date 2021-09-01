package xyz.refinedev.queue.emerald.shared.jedis;

import io.github.zowpy.jedisapi.redis.subscription.JedisSubscriber;
import lombok.AllArgsConstructor;
import xyz.refinedev.queue.emerald.shared.SharedEmerald;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/10/2021
 * Project: Emerald
 */

@AllArgsConstructor
public class SharedJedisSubscriber extends JedisSubscriber {

    private SharedEmerald emerald;


   /* @IncomingMessage(payload = "command")
    public void executeCommand(JsonObject object) {
        if (emerald.getUuid().equals(UUID.fromString(object.get("uuid").getAsString()))) {
            String command = object.get("command").getAsString();

            if (object.has("issuer")) {
                UUID uuid = UUID.fromString(object.get("issuer").getAsString());
                if (Bukkit.getPlayer(uuid) != null) {
                    Bukkit.getPlayer(uuid).chat("/" + command);
                    return;
                }
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    } */

  /*  @IncomingMessage(payload = "start")
    public void startServer(JsonObject object) {
        final String name = object.get("name").getAsString();

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("emerald.admin")).collect(Collectors.toList())
                .forEach(player -> player.sendMessage(ChatColor.GREEN + "[Emerald] " + ChatColor.BOLD + name + ChatColor.WHITE + " went online!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Emerald] " + ChatColor.BOLD + name + ChatColor.WHITE + " went online!");
    }
    @IncomingMessage(payload = "shutdown")
    public void stopServer(JsonObject object) {
        final String name = object.get("name").getAsString();

        Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission("emerald.admin")).collect(Collectors.toList())
                .forEach(player -> player.sendMessage(ChatColor.GREEN + "[Emerald] " + ChatColor.BOLD + name + ChatColor.WHITE + " went offline!"));
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Emerald] " + ChatColor.BOLD + name + ChatColor.WHITE + " went offline!");
    }


   */

}
