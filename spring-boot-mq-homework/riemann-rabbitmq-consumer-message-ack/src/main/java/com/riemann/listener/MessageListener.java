package com.riemann.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.io.IOException;
import java.util.Random;

public class MessageListener {
    private Random random = new Random();

    /**
     * NONE模式，则只要收到消息后就立即确认(消息出列，标记已消费)，有丢失数据的风险
     * AUTO模式，看情况确认，如果此时消费者抛出异常则消息会返回到队列中
     * MANUAL模式，需要显式的调用当前channel的basicAck方法
     *
     * 在配置文件配置
     */
    // @RabbitListener(queues = "q.biz", ackMode = "MANUAL")
    @RabbitListener(queues = "q.biz")
    public void handleMessageTopic(Channel channel,
                                   @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                                   @Payload String message) {
        System.out.println("RabbitListener消费消息，消息内容:" + message);
        try {
            if (random.nextInt(10) % 3 != 0) {
                // 手动nack，告诉broker消费者处理失败，最后一个参数表示是否需要将消息重新入列
                // channel.basicNack(deliveryTag, false, true);
                // 手动拒绝消息。第二个参数表示是否重新入列
                channel.basicReject(deliveryTag, true);
            } else {
                // 手动ack，deliveryTag表示消息的唯一标志，multiple表示是否是批量确认
                channel.basicAck(deliveryTag, false);
                System.err.println("已确认消息:" + message);
            }
        } catch (IOException e) {

        }
    }
}
