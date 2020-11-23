package com.szdq.tm.severorder.controller;


import com.szdq.tm.severorder.service.OrderService;
import com.szdq.tm.severorder.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderCreateVO orderCreateDTO) throws IOException, TimeoutException, InterruptedException {
        log.info("createOrder:orderCreateDTO:{}",orderCreateDTO);
        orderService.createOrder(orderCreateDTO);
    }
}
