package com.xuzhou.vertx.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.net.SocketAddress;

import io.vertx.redis.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: xuzhou
 * Date: 2020/1/7
 * Time: 17:37
 */
public class RedisClientVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(RedisClientVerticle.class);
    private static RedisAPI redis;
    private Redis redisClient;

    @Override
    public void start() {
        RedisOptions redisOptions = new RedisOptions()
                .setType(RedisClientType.SENTINEL)
                .addEndpoint(SocketAddress.inetSocketAddress(7700, "192.168.1.123"))
                .addEndpoint(SocketAddress.inetSocketAddress(7800, "192.168.1.123"))
                .addEndpoint(SocketAddress.inetSocketAddress(7900, "192.168.1.123"))
                .setMasterName("mymaster")
                .setRole(RedisRole.MASTER);

        Redis.createClient(vertx, redisOptions)
                .connect(onConnect -> {
                    if (onConnect.succeeded()) {
                        redisClient = onConnect.result();
                        redis = RedisAPI.api(redisClient);
                    }
                });
    }

    @Override
    public void stop() {
        if (redisClient != null) {
            logger.info("redis close");
            redisClient.close();
        }
    }

    public static RedisAPI getRedis() {
        return redis;
    }
}
