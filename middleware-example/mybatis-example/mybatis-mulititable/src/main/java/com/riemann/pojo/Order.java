package com.riemann.pojo;

import lombok.Data;

@Data
public class Order {

    private Integer id;

    private String orderTime;

    private Double total;

    // 表明该订单属于哪个用户
    private User user;

}
