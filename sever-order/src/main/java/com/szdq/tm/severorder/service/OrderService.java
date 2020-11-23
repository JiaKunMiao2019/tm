/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: OrderService
 * Author:   莉莉
 * Date:     2020/11/16 22:37
 * Description: 订单业务处理层
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
import com.szdq.tm.severorder.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

/**
 * 〈订单业务处理层〉
 *
 * @author 莉莉
 * @create 2020/11/16
 * @since 1.0.0
 */
@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderDetailDao orderDetailDao;

    ObjectMapper objectMapper = new ObjectMapper();

    public void createOrder(OrderCreateVO orderCreateVO) throws IOException, TimeoutException, InterruptedException {
        OrderDetailPO orderPO = new OrderDetailPO();
        orderPO.setAddress(orderCreateVO.getAddress());
        orderPO.setAccountId(orderCreateVO.getAccountId());
        orderPO.setProductId(orderCreateVO.getProductId());
        orderPO.setStatus(OrderStatus.ORDER_CREATING);
        orderPO.setDate(new Date());
        orderDetailDao.insert(orderPO);

        OrderMessageDTO orderMessageDTO = new OrderMessageDTO();
        orderMessageDTO.setOrderId(orderPO.getId());
        orderMessageDTO.setProductId(orderPO.getProductId());
        orderMessageDTO.setAccountId(orderCreateVO.getAccountId());

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.confirmSelect();//设置同步确认机制
            String messageToSend = objectMapper.writeValueAsString(orderMessageDTO);
            //设置特殊参数中的expiration的消息过期时间
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("15000").build();
            for (int i = 0; i < 50; i++) {
                channel.basicPublish(
                        "exchange.order.restaurant",
                        "key.restaurant",
                        true,
                                    properties,
                                    messageToSend.getBytes());
                log.info("message send");
            }

//            ConfirmListener listener = new ConfirmListener() {//异步确认机制
//                @Override
//                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
//                    log.info("Ack deliveryTag:{},multiple:{}",deliveryTag,multiple);
//                    //deliveryTag：第几条；multiple:是否为多条
//                }
//
//                @Override
//                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
//                    log.info("Nack deliveryTag:{},multiple:{}",deliveryTag,multiple);
//                    //deliveryTag：第几条；multiple:是否为多条
//                }
//            };
//            Thread.sleep(1000000);
            if (channel.waitForConfirms()){
                log.info("RabbitMq send succese");
            }else {
                log.info("RabbitMq send fail");

            }
        }
    }
}