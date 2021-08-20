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

    QUEUE_REMINDER(Arrays.asList(" ", "&eYou are in &6<queue> &equeue, you are &6<pos>#&e out of &6<total>#", "&eYou can purchase a rank for a higher priority!", " "), "queue-reminder"),

    JOIN_QUEUE("&eYou have been added to &6<queue>", "join-queue"),
    LEAVE_QUEUE("&eYou have been removed from &6<queue>", "left-queue"),
    PAUSE_QUEUE("&eYou have <toggle> &6<queue>", "pause-queue"),

    SEND_PLAYER("&eSending you to &6<queue>", "send-player");


    private String message, path;
    private List<String> messageList;

    Locale(String message, String path) {
        this.message = message;
        this.path = path;
    }

    Locale(List<String> messageList, String path) {
        this.messageList = messageList;
        this.path = path;
    }

    public List<String> getMessageList() {
        return QueuePlugin.getInstance().getLangFile().getConfig().getStringList(path);
    }

    public String getMessage() {
        return QueuePlugin.getInstance().getLangFile().getConfig().getString(path);
    }
}
