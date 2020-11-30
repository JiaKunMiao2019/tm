/**
 * Copyright (C), 2015-2020, XXX有限公司
 * FileName: Page
 * Author:   莉莉
 * Date:     2020/11/23 11:47
 * Description: 分页公共方法
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.szdq.tm.severorder.service;

import java.io.Serializable;
import java.util.List;

/**
 * 〈分页公共方法〉
 *
 * @author 莉莉
 * @create 2020/11/23
 * @since 1.0.0
 */
public class PageObject<T> implements Serializable {
    private static final long serialVersionUID = 6780580291247550747L;//类泛型
    /**当前页的页码值*/
    private Integer pageCurrent=1;
    /**页面大小*/
    private Integer pageSize=3;
    /**总行数(通过查询获得)*/
    private Integer rowCount=0;
    /**总页数(通过计算获得)*/
    private Integer pageCount=0;
    /**当前页记录*/
    private List<T> records;
    public Integer getPageCurrent() {
        return pageCurrent;
    }
    public void setPageCurrent(Integer pageCurrent) {
        this.pageCurrent = pageCurrent;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
    public Integer getRowCount() {
        return rowCount;
    }
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getPageCount() {
        return pageCount;
    }
    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }
    public List<T> getRecords() {
        return records;
    }
    public void setRecords(List<T> records) {
        this.records = records;
    }
}