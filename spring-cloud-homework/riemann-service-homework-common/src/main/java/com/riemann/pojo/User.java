package com.riemann.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "riemann_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

}