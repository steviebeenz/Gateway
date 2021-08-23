package xyz.refinedev.queue.jedisapi.redis.subscription.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import xyz.refinedev.queue.jedisapi.JedisAPI;
import lombok.AllArgsConstructor;
import xyz.refinedev.queue.jedisapi.redis.JedisHandler;
import xyz.refinedev.queue.jedisapi.redis.subscription.IncomingMessage;
import xyz.refinedev.queue.jedisapi.redis.subscription.JedisSubscriber;
import redis.clients.jedis.JedisPubSub;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * This Project is property of RefineDevelopment © 2021
 *
 * @author Zowpy
 * Created: 8/10/2021
 * Project: JedisAPI
 */

@AllArgsConstructor
public class JedisListener extends JedisPubSub {

    private JedisAPI jedisAPI;
    private JedisHandler jedisHandler;

    private final Executor executor = Executors.newFixedThreadPool(1);

    @Override
    public void onMessage(String channel, String message) {
        if (channel.equals(jedisHandler.getCredentials().getChannel())) {

            String[] args = message.split("###");
            executor.execute(() -> {

                if (args.length != 2) return;

                if (!jedisAPI.getPayloads().containsKey(args[0])) return;

                JedisSubscriber subscriber = jedisAPI.getPayloads().get(args[0]);

                if (subscriber == null) return;

                for (Method method : jedisAPI.getExecutors().get(subscriber)) {
                    IncomingMessage incomingMessage = method.getAnnotation(IncomingMessage.class);

                    if (incomingMessage.payload().equalsIgnoreCase(args[0])) {
                        try {
                            if (method.getParameters().length == 0) {
                                if (!method.isAccessible()) method.setAccessible(true);
                                method.invoke(subscriber);
                                return;
                            }
                            JsonObject object = new JsonParser().parse(args[1]).getAsJsonObject();
                            if (!method.isAccessible()) method.setAccessible(true);
                            method.invoke(subscriber, object);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }

            });
        }
    }
}
