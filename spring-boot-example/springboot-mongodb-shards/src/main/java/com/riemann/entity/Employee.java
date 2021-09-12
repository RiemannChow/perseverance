package com.riemann.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(value = "riemann_employee_shard")
public class Employee {
    private String id;
    private String name;
    private String city;
    private Date birthday;
    private Double expectSalary;
}
