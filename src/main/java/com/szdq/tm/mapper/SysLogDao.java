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
package com.szdq.tm.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 〈〉
 *
 * @author 莉莉
 * @create 2020/11/11
 * @since 1.0.0
 */
@Mapper
public interface SysLogDao {
    @Delete("delete from sys_logs where id=#{id}")
    int deleteObject(Integer id);

    int deleteLog(@Param("id") Integer id);
}