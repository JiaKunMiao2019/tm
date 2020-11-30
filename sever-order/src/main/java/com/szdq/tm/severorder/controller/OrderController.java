package com.szdq.tm.severorder.controller;


import com.szdq.tm.severorder.po.OrderDetailPO;
import com.szdq.tm.severorder.service.OrderService;
import com.szdq.tm.severorder.service.SysLogServiceImpl;
import com.szdq.tm.severorder.vo.OrderCreateVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    SysLogServiceImpl sysLogService;

    @PostMapping("/orders")
    public void createOrder(@RequestBody OrderCreateVO orderCreateDTO) throws IOException, TimeoutException, InterruptedException {
        log.info("createOrder:orderCreateDTO:{}",orderCreateDTO);
        orderService.createOrder(orderCreateDTO);
    }

    @ResponseBody
    @RequestMapping("/student/page/{currPage}/{pageSize}")
    public List<OrderDetailPO> getStudentByPage(@PathVariable("currPage") int currPage, @PathVariable("pageSize") int pageSize) {
        List<OrderDetailPO> student = sysLogService.queryStudentsByPage(currPage, pageSize);
        return student;
    }
}
