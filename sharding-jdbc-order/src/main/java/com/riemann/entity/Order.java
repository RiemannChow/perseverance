package com.riemann.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "c_order")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "is_del")
    private Boolean isDel;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "company_id")
    private Integer companyId;
    @Column(name = "publish_user_id")
    private Integer publishUserId;
    @Column(name = "position_id")
    private Integer positionId;
    @Column(name = "resume_type")
    private Integer resumeType;
    @Column(name = "status")
    private String status;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "update_time")
    private Date updateTime;
}
