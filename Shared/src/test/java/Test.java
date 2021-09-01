import io.github.zowpy.jedisapi.JedisAPI;
import io.github.zowpy.jedisapi.redis.RedisCredentials;

/**
 * This Project is property of Zowpy © 2021
 * Redistribution of this Project is not allowed
 *
 * @author Zowpy
 * Created: 8/26/2021
 * Project: Gateway
 */

public class Test {

    public static void main(String[] args) {
        JedisAPI jedisAPI = new JedisAPI(new RedisCredentials("redis-14262.c81.us-east-1-2.ec2.cloud.redislabs.com",
                "n1r5hrijYDeEF7IKsMOYn9hoUnr8YUQF",
                "BALLS",
                14262,
                true));


        jedisAPI.getJedisHandler().runCommand(jedis -> {
            for (String values : jedis.hgetAll("queues").values()) {
                System.out.println(values);
            }
        });
    }
}
