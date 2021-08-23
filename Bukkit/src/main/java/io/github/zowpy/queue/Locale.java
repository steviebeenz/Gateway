package io.github.zowpy.queue;

import io.github.zowpy.queue.util.ConfigFile;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/19/2021
 * Project: Gateway
 */

@Getter
public enum Locale {

    QUEUE_REMINDER(Arrays.asList(" ", "&cYou are in &4<queue> &cqueue, you are &4<pos>#&c out of &4<total>#", "&cYou can purchase a rank for a higher priority!", " "), "queue-reminder"),

    JOIN_QUEUE("&cYou have been added to &4<queue>", "join-queue"),
    LEAVE_QUEUE("&cYou have been removed from &4<queue>", "left-queue"),
    PAUSE_QUEUE("&cYou have <toggle> &4<queue>", "pause-queue"),

    SEND_PLAYER("&cSending you to &4<queue>", "send-player");


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
