package xyz.refinedev.queue.bukkit;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * This Project is property of RefineDevelopment © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/19/2021
 * Project: Gateway
 */

@Getter
public enum Locale {

    QUEUE_REMINDER(Arrays.asList(" ", "&cYou are in &4<bukkit> &cqueue, you are &4<pos>#&c out of &4<total>#", "&cYou can purchase a rank for a higher priority!", " "), "bukkit-reminder"),

    JOIN_QUEUE("&cYou have been added to &4<bukkit>", "join-bukkit"),
    LEAVE_QUEUE("&cYou have been removed from &4<bukkit>", "left-bukkit"),
    PAUSE_QUEUE("&cYou have <toggle> &4<bukkit>", "pause-bukkit"),

    SEND_PLAYER("&cSending you to &4<bukkit>", "send-player");


    private String message, path;
    private List<String> messageList;

    Locale(String message, String path) {
        this.message = message;
        this.path = path;

        if (!QueuePlugin.getInstance().getLangFile().getConfig().getString(path).equals(message)) {
            this.message = QueuePlugin.getInstance().getLangFile().getConfig().getString(path);
        }
    }

    Locale(List<String> messageList, String path) {
        this.messageList = messageList;
        this.path = path;

        if (!QueuePlugin.getInstance().getLangFile().getConfig().getStringList(path).equals(messageList)) {
            this.messageList = QueuePlugin.getInstance().getLangFile().getConfig().getStringList(path);
        }
    }

}
