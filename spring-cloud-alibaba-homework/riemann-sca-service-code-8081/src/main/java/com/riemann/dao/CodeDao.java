package com.riemann.dao;

import com.riemann.pojo.AuthCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeDao extends JpaRepository<AuthCode, Long> {
}
