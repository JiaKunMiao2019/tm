/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: SysLogDao
 * Author:   莉莉
 * Date:     2020/11/11 23:23
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.severorder.mapper;

import com.szdq.tm.severorder.po.OrderDetailPO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 〈〉
 *
 * @author 莉莉
 * @create 2020/11/11
 * @since 1.0.0
 */
@Mapper
@Repository
public interface SysLogDao {
    @Delete("delete from sys_logs where id=#{id}")
    int deleteObject(Integer id);

    int deleteLog(@Param("id") Integer id);

    /**
     * 基于条件分页查询日志信息
     * @param username  查询条件(例如查询哪个用户的日志信息)
     * @param startIndex 当前页的起始位置
     * @param pageSize 当前页的页面大小
     * @return 当前页的日志记录信息
     * 数据库中每条日志信息封装到一个SysLog对象中
     */
    List<OrderDetailPO> findPageObjects(
            @Param("username") String  username,
            @Param("startIndex") Integer startIndex,
            @Param("pageSize") Integer pageSize);

    /**
     * 基于条件查询总记录数
     * @param username 查询条件(例如查询哪个用户的日志信息)
     * @return 总记录数(基于这个结果可以计算总页数)
     * 说明：假如如下方法没有使用注解修饰，在基于名字进行查询
     * 时候会出现There is no getter for property named
     * 'username' in 'class java.lang.String'
     */
    int getRowCount(@Param("username") String username);

    List<OrderDetailPO> queryStudentsByPage(Map<String,Object> data);
}