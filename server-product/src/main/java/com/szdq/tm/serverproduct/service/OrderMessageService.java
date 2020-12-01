/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OrderMessageService
 * Author:   莉莉
 * Date:     2020/11/16 23:34
 * Description: 商家微服务
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.serverproduct.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.szdq.tm.serverproduct.dao.ProductDao;
import com.szdq.tm.serverproduct.dao.RestaurantDao;
import com.szdq.tm.serverproduct.dto.OrderMessageDTO;
import com.szdq.tm.serverproduct.enummeration.ProductStatus;
import com.szdq.tm.serverproduct.enummeration.RestaurantStatus;
import com.szdq.tm.serverproduct.po.ProductPO;
import com.szdq.tm.serverproduct.po.RestaurantPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 〈商家微服务〉
 *
 * @author 莉莉
 * @create 2020/11/16
 * @since 1.0.0
 */
@Service
@Slf4j
public class OrderMessageService {

    @Autowired
    private ProductDao productDao;
    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    Channel channel;

    ObjectMapper objectMapper = new ObjectMapper();

    @Async
    public void handleMessage() throws IOException, TimeoutException, InterruptedException {
        log.info("start linstening message");

        /*--初始化死信队列--*/
        channel.exchangeDeclare("exchange.dlx",
                BuiltinExchangeType.TOPIC,
                true,
                false,
                null);
        channel.queueDeclare("queue.dlx",
                true,
                false,
                false,
                null);
        channel.queueBind("queue.dlx","exchange.dlx","#");
        /*--初始化restaurant--*/
            channel.exchangeDeclare(
                    "exchange.order.restaurant",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);
            Map<String,Object> args = new HashMap<>(16);
        args.put("x-max-length",60);
        args.put("x-message-ttl",15000);//为队列设置超时时间，注意此时的mq需要没有这个队列才能设置
        args.put("x-dead-letter-exchange","exchange.dlx");//设置死信发送的队列,进入死信的情况：消息过期，消息拒绝的，超过队列最大长度
        channel.queueDeclare(
                    "queue.restaurant",
                    true,
                    false,
                    false,
                    args);

            channel.queueBind(
                    "queue.restaurant",
                    "exchange.order.restaurant",
                    "key.restaurant");

            channel.basicQos(2);//消费端限流机制，每次只能消费设定数目的消息，防止过多的消息到服务器造成oom

            channel.basicConsume("queue.restaurant", false, deliverCallback, consumerTag -> {
            });
            while (true) {
                Thread.sleep(100000);
            }
        }

    DeliverCallback deliverCallback = (consumerTag, message) -> {
        String messageBody = new String(message.getBody());
        log.info("deliverCallback:messageBody:{}", messageBody);
        try {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messageBody,
                    OrderMessageDTO.class);

            ProductPO productPO = productDao.selsctProduct(orderMessageDTO.getProductId());
            log.info("onMessage:productPO:{}", productPO);
            RestaurantPO restaurantPO = restaurantDao.selsctRestaurant(productPO.getRestaurantId());
            log.info("onMessage:restaurantPO:{}", restaurantPO);
            if (ProductStatus.AVALIABLE == productPO.getStatus() && RestaurantStatus.OPEN == restaurantPO.getStatus()) {
                orderMessageDTO.setConfirmed(true);
                orderMessageDTO.setPrice(productPO.getPrice());
            } else {
                orderMessageDTO.setConfirmed(false);
            }
            log.info("sendMessage:restaurantOrderMessageDTO:{}", orderMessageDTO);


//                channel.addReturnListener(new ReturnListener() {//如果消费不成功会返回消息
//                    @Override
//                    public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {
//                        log.info("message return replyCode:{}, replyText:{}, exchange:{}, routingKey:{}," +
//                                " properties:{}, body:{}",replyCode, replyText, exchange, routingKey,
//                         properties, body);
//                    }
//                });
                channel.addReturnListener(new ReturnCallback() {//如果消费不成功会返回消息
                    @Override
                    public void handle(Return returnMessage) {
                        log.info("message return returnMessage:{}",returnMessage);
                    }
                });
                Thread.sleep(3000);
                channel.basicAck(message.getEnvelope().getDeliveryTag(),false);//手动消费，不重回队列
                String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("15000").build();
                channel.basicPublish("exchange.order.restaurant", "key.order",true, properties, messageToSend.getBytes());

                Thread.sleep(1000);

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    };
}