package com.riemann.mq.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: 微信公众号【老周聊架构】
 */
public class KafkaProducerTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        Properties props = new Properties();

        // kafka地址,列表格式为host1:port1,host2:port2,...，无需添加所有的集群地址，kafka会根据提供的地址发现其他的地址(建议多提供几个，以防提供的服务器关闭) 必须设置
        props.put("bootstrap.servers", "localhost:9092");
        // key序列化方式 必须设置
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // value序列化方式 必须设置
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // 客户端id
        props.put("client.id", "KafkaProducerTest");
        /**
         * 发送返回应答方式：
         * 0:Producer 往集群发送数据不需要等到集群的返回，不确保消息发送成功。安全性最低但是效率最高。
         * 1:Producer 往集群发送数据只要 Leader 应答就可以发送下一条，只确保Leader接收成功。
         * -1或者all:Producer 往集群发送数据需要所有的ISR Follower都完成从Leader的 同步才会发送下一条，确保Leader发送成功和所有的副本都成功接收。安全性最高，但是效率最低。
         */
        props.put("acks", "all");
        // 重试次数
        props.put("retries", 0);
        // 重试间隔时间
        props.put("retries.backoff.ms", 100);
        // 批量发送的大小
        props.put("batch.size", 16384);
        // 一个Batch被创建之后，最多过多久，不管这个Batch有没有写满，都必须发送出去
        props.put("linger.ms", 10);
        // 缓冲区大小
        props.put("buffer.memory", 33554432);


        // topic
        String topic = "riemann_kafka_producer_test";
        Producer<String, String> producer = new KafkaProducer<>(props);
        AtomicInteger count = new AtomicInteger();

        while (true) {
            int num = count.get();
            String key = Integer.toString(num);
            String value = Integer.toString(num);
            // 指定topic,key,value
            ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);

            if (num % 2 == 0) {
                // 偶数异步发送
                // 第一个参数record封装了topic、key、value
                // 第二个参数是一个callback对象，当生产者接收到kafka发来的ACK确认消息时，会调用此CallBack对象的onComplete方法
                producer.send(record, (recordMetadata, e) -> {
                    System.out.println("num:" + num + " topic:" + recordMetadata.topic() + " offset:" + recordMetadata.offset());
                });
            } else {
                // 同步发送
                // KafkaProducer.send方法返回的类型是Future<RecordMetadata>，通过get方法阻塞当前线程，等待kafka服务端ACK响应
                producer.send(record).get();
            }
            count.incrementAndGet();
            TimeUnit.MILLISECONDS.sleep(100);
        }

    }
}
