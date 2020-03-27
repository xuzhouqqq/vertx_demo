package com.xuzhou.vertx.verticle;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.kafka.client.producer.KafkaProducer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: xuzhou
 * Date: 2018/6/4
 * Time: 15:25
 */
public class KafkaProducerVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(KafkaProducerVerticle.class);
    private static KafkaProducer<String, byte[]> producer = null;

    @Override
    public void start() {
        Properties prop_producer = new Properties();
        prop_producer.put("bootstrap.servers", "192.168.1.123:9092");//服务器ip:端口号，集群用逗号分隔
        prop_producer.put("acks", "all");
        prop_producer.put("retries", 0);
        prop_producer.put("batch.size", 16384);
        prop_producer.put("linger.ms", 1);
        prop_producer.put("buffer.memory", 33554432);
        prop_producer.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        prop_producer.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        producer = KafkaProducer.createShared(vertx, "producer-1", prop_producer);

        logger.debug("init kafka producer...");
    }

    @Override
    public void stop() {
        producer.close(res -> {
            if (res.succeeded()) {
                logger.debug("Producer is now closed");
            } else {
                logger.debug("close failed");
            }
        });
    }

    private static void send(String topic, byte[] dd) {
        KafkaProducerRecord<String, byte[]> record = KafkaProducerRecord.create(topic, dd);
        producer.write(record);
    }

    /**
     * 发送消息
     *
     * @param topic   主题
     * @param content 内容
     */
    public static void send(String topic, String content) {
        if (StringUtils.isNotEmpty(content)) {
            try {
                send(topic, content.getBytes("utf8"));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        } else {
            new RuntimeException("要发送的数据不能为空");
        }
    }
}
