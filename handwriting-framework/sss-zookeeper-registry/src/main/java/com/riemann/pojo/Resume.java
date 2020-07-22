package com.riemann.pojo;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "tb_resume")
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = true, length = 50)
    private String name;

    @Column(name = "address", nullable = true, length = 50)
    private String address;

    @Column(name = "phone", nullable = true, length = 50)
    private String phone;

}
