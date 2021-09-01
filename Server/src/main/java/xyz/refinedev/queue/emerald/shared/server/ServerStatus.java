package xyz.refinedev.queue.emerald.shared.server;

import net.md_5.bungee.api.ChatColor;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/10/2021
 * Project: Emerald
 */
public enum ServerStatus {

    ONLINE(ChatColor.GREEN + "Online"), OFFLINE(ChatColor.RED + "Offline"), WHITELISTED(ChatColor.YELLOW + "Whitelisted");

    String message;

    ServerStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
