package com.riemann.pojo;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "riemann_token")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String token;

}