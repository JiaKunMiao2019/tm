/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Order
 * Author:   莉莉
 * Date:     2020/11/23 11:50
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.severorder.service;

import com.github.pagehelper.PageHelper;
import com.szdq.tm.severorder.dao.OrderDetailDao;
import com.szdq.tm.severorder.mapper.SysLogDao;
import com.szdq.tm.severorder.po.OrderDetailPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author 莉莉
 * @create 2020/11/23
 * @since 1.0.0
 */
@Service
public class SysLogServiceImpl{
    @Autowired
    private SysLogDao sysLogDao;

    public PageObject<OrderDetailPO> findPageObjects(
            String name, Integer pageCurrent) {
        //1.验证参数合法性
        //1.1验证pageCurrent的合法性，
        //不合法抛出IllegalArgumentException异常
        if(pageCurrent==null||pageCurrent<1)
            throw new IllegalArgumentException("当前页码不正确");
        //2.基于条件查询总记录数
        //2.1) 执行查询
        int rowCount=sysLogDao.getRowCount(name);
        //2.2) 验证查询结果，假如结果为0不再执行如下操作
        if(rowCount==0)
            throw new NullPointerException();
        //3.基于条件查询当前页记录(pageSize定义为2)
        //3.1)定义pageSize
        int pageSize=2;
        //3.2)计算startIndex
        int startIndex=(pageCurrent-1)*pageSize;
        //3.3)执行当前数据的查询操作
        List<OrderDetailPO> records=
                sysLogDao.findPageObjects(name, startIndex, pageSize);
        //4.对分页信息以及当前页记录进行封装
        //4.1)构建PageObject对象
        PageObject<OrderDetailPO> pageObject=new PageObject<>();
        //4.2)封装数据
        pageObject.setPageCurrent(pageCurrent);
        pageObject.setPageSize(pageSize);
        pageObject.setRowCount(rowCount);
        pageObject.setRecords(records);
        pageObject.setPageCount((rowCount-1)/pageSize+1);
        //5.返回封装结果。
        return pageObject;
    }

    public List<OrderDetailPO> queryStudentsByPage(int currPage, int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("currPage", currPage);
        data.put("pageSize", pageSize);
        return sysLogDao.queryStudentsByPage(data);
    }
}