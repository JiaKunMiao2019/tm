/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OrderMessegeService
 * Author:   莉莉
 * Date:     2020/11/16 22:38
 * Description: 队列处理层
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.severorder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import com.szdq.tm.severorder.dao.OrderDetailDao;
import com.szdq.tm.severorder.dto.OrderMessageDTO;
import com.szdq.tm.severorder.enummeration.OrderStatus;
import com.szdq.tm.severorder.po.OrderDetailPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 〈队列处理层〉
 *
 * @author 莉莉
 * @create 2020/11/16
 * @since 1.0.0
 */
@Service
@Slf4j
public class OrderMessegeService {
    @Autowired
    private OrderDetailDao orderDetailDao;
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 声明消息队列，交换机，绑定和消息的处理
     */
    @Async
    public void handleMessege() throws IOException, TimeoutException, InterruptedException {
        /*--创建connectionFactory--*/
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        /*--创建绑定和消息处理--*/
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            /*--retaurant相关--*/
            channel.exchangeDeclare(//声明交换机
                    "exchange.order.restaurant",//交换机名称
                    BuiltinExchangeType.DIRECT,            //交换机类型
                    true,                         //是否持久化
                    false,                     //没有队列是否删掉
                    null);                      //特殊字段

            channel.queueDeclare(//声明队列
                    "queue.order",  //声明队列名称
                    true,          //是否持久化
                    false,        //是否独占
                    false,      //没有队列是否删掉
                    null);      //特殊字段

            channel.queueBind(
                    "queue.order",
                    "exchange.order.restaurant",
                    "key.order");

            /*--deliveryman相关--*/
            channel.exchangeDeclare(
                    "exchange.order.deliveryman",
                    BuiltinExchangeType.DIRECT,
                    true,
                    false,
                    null);
            channel.queueBind(
                    "queue.order",
                    "exchange.order.deliveryman",
                    "key.order");

            /*--settlment--*/
            channel.exchangeDeclare(
                    "exchange.order.settlement",
                    BuiltinExchangeType.FANOUT,
                    true,
                    false,
                    null);

            channel.queueBind(
                    "queue.order",
                    "exchange.settlement.order",
                    "key.order");

            /*---------------------reward---------------------*/

            channel.exchangeDeclare(
                    "exchange.order.reward",
                    BuiltinExchangeType.TOPIC,
                    true,
                    false,
                    null);

            channel.queueBind(
                    "queue.order",
                    "exchange.order.reward",
                    "key.order");

            channel.basicConsume(
                    "queue.order",
                    true,
                    deliverCallback,
                    consumerTag -> {
                    });
            while (true) {
                Thread.sleep(100000);
            }
        }
    }

    DeliverCallback deliverCallback = ((consumerTag, message) -> {
        String messegeBody = new String(message.getBody());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try {
            OrderMessageDTO orderMessageDTO = objectMapper.readValue(messegeBody, OrderMessageDTO.class);
            OrderDetailPO orderPO = orderDetailDao.selectOrder(orderMessageDTO.getOrderId());

            switch (orderPO.getStatus()) {
                case ORDER_CREATING:
                    if (orderMessageDTO.getConfirmed() && null != orderMessageDTO.getPrice()) {
                        orderPO.setStatus(OrderStatus.RESTAURANT_CONFIRMED);
                        orderPO.setPrice(orderMessageDTO.getPrice());
                        orderDetailDao.update(orderPO);
                        try (Connection connection = connectionFactory.newConnection();
                             Channel channel = connection.createChannel()) {
                            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish("exchange.order.deliveryman", "key.deliveryman", null,
                                    messageToSend.getBytes());
                        }
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                case RESTAURANT_CONFIRMED:
                    if (null != orderMessageDTO.getDeliverymanId()) {
                        orderPO.setDeliverymanId(orderMessageDTO.getDeliverymanId());
                        orderPO.setStatus(OrderStatus.DELIVERYMAN_CONFIRMED);
                        orderDetailDao.update(orderPO);

                        try (Connection connection = connectionFactory.newConnection();
                             Channel channel = connection.createChannel()) {
                            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish("exchange.order.settlement", "key.settlement", null, messageToSend.getBytes());
                        }
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                case DELIVERYMAN_CONFIRMED:
                    if (null != orderMessageDTO.getSettlementId()) {
                        orderPO.setStatus(OrderStatus.SETTLEMENT_CONFIRMED);
                        orderPO.setSettlementId(orderMessageDTO.getSettlementId());
                        orderDetailDao.update(orderPO);
                        try (Connection connection = connectionFactory.newConnection();
                             Channel channel = connection.createChannel()) {
                            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
                            channel.basicPublish("exchange.order.reward", "key.reward", null, messageToSend.getBytes());
                        }
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
                case SETTLEMENT_CONFIRMED:
                    if (null != orderMessageDTO.getRewardId()) {
                        orderPO.setStatus(OrderStatus.ORDER_CREATED);
                        orderPO.setRewardId(orderMessageDTO.getRewardId());
                        orderDetailDao.update(orderPO);
                    } else {
                        orderPO.setStatus(OrderStatus.FAILED);
                        orderDetailDao.update(orderPO);
                    }
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    });
}