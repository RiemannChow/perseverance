package com.riemann.mapper;

import com.riemann.pojo.Order;
import com.riemann.pojo.User;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface OrderMapper {

    // 查询订单的同时还查询该订单所属的用户 (一对一的接口 走的xml)
    // List<Order> findOrderAndUser();
    // (一对一的接口 走的注解)
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "orderTime", column = "orderTime"),
            @Result(property = "total", column = "total"),
            @Result(property = "user", column = "uid", javaType = User.class, one = @One(select = "com.riemann.mapper.UserMapper.findUserById"))
    })
    @Select("select * from orders")
    List<Order> findOrderAndUser();

    @Select("select * from orders where uid = #{uid}")
    List<Order> findOrderByUid(Integer uid);

}
