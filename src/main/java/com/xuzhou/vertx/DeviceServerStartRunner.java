package com.xuzhou.vertx;


import com.xuzhou.vertx.verticle.HttpServerVerticle;
import com.xuzhou.vertx.verticle.KafkaProducerVerticle;
import com.xuzhou.vertx.verticle.RedisClientVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2015/12/2.
 */
public class DeviceServerStartRunner {
    private static Logger logger = LoggerFactory.getLogger(DeviceServerStartRunner.class);

    public static void main(String[] args) {

        logger.debug("=======================Runner  Deployment======================");
        final Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpServerVerticle(9001), new DeploymentOptions().setWorkerPoolSize(10));
        vertx.deployVerticle(new KafkaProducerVerticle());
        //vertx.deployVerticle(new RedisClientVerticle());



        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            vertx.close(res -> {
                countDownLatch.countDown();
            });
            try {
                countDownLatch.await(30, TimeUnit.SECONDS);
                logger.info("stop vertx success");
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }));

    }
}
