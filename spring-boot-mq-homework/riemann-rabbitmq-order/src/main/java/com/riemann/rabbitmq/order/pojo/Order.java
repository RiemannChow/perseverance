package com.riemann.rabbitmq.order.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Order implements Serializable {
    public long id;

    public String name;

    public int count;

    public String status;

    public String cancelReason;

    public Date orderTime;

    public String orderTimeStr;
}
