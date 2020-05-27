package com.riemann.pojo;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Table(name = "user")
public class User implements Serializable {

    @Id // 对应的是主键id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键的生成策略
    private Integer id;

    // @Column(name = "username") 如果数据库的字段与属性字段不一致，就需要用@Column来映射
    private String username;

    // 表示用户关联的订单
    List<Order> orderList = Lists.newArrayList();

    // 用户关联的角色
    List<Role> roleList = Lists.newArrayList();

}
