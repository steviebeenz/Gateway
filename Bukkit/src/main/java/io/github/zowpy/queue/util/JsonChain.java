package io.github.zowpy.queue.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/17/2021
 * Project: Gateway
 */

public class JsonChain {

    public JsonObject object;

    public JsonChain() {
        this.object = new JsonObject();
    }

    public JsonChain addProperty(String key, String value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonChain addProperty(String key, Number value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonChain addProperty(String key, Boolean value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonChain add(String key, JsonElement element) {
        object.add(key, element);
        return this;
    }

    public JsonObject getAsJsonObject() {
        return object;
    }

}
