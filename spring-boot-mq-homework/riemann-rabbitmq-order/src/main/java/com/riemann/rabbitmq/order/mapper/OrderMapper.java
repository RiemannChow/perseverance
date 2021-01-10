package com.riemann.rabbitmq.order.mapper;

import com.riemann.rabbitmq.order.pojo.Order;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface OrderMapper {
    @Select("select id, name, count, status, cancel_reason as cancelReason, order_time as orderTime from rabbitmq_order where id = #{id}")
    Order getOrderById(long id);

    @Select("select id, name, count, status, cancel_reason as cancelReason, order_time as orderTime, order_time_str as orderTimeStr from rabbitmq_order")
    List<Order> getOrders();

    @Insert({"insert into rabbitmq_order (id, name, count, status, cancel_reason, order_time, order_time_str)" +
            " values(#{id}, #{name}, #{count}, #{status}, #{cancelReason}, #{orderTime}, #{orderTimeStr})"})
    Integer addOrder(Order order);

    @Update({"update rabbitmq_order set status=#{status}, cancel_reason=#{cancelReason} where id = #{id}"})
    Integer updateOrderStatusById(long id, String status, String cancelReason);
}
