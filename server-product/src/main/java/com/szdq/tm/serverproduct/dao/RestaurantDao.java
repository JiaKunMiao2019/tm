package com.szdq.tm.serverproduct.dao;

import com.szdq.tm.serverproduct.po.RestaurantPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface RestaurantDao {

    @Select("SELECT id,name,address,status,settlement_id settlementId,date FROM restaurant WHERE id = #{id}")
    RestaurantPO selsctRestaurant(Integer id);
}
