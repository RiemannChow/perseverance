package com.riemann.persistent;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class PersistentProducer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri("amqp://root:123456@riemann:5672/%2f");
        final Connection connection = factory.newConnection();
        final Channel channel = connection.createChannel();
        // durable:true表示是持久化消息队列
        channel.queueDeclare("queue.persistent", true, false, false, null);
        // 持久化的交换器
        channel.exchangeDeclare("ex.persistent", "direct", true, false, null);

        channel.queueBind("queue.persistent", "ex.persistent", "key.persistent");

        final AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // 表示是持久化消息
                .build();

        channel.basicPublish("ex.persistent",
                "key.persistent",
                properties,  // 设置消息的属性，此时消息是持久化消息
                "hello world".getBytes());

        channel.close();
        connection.close();
    }
}
