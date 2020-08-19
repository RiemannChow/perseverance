package com.riemann.dao;


import com.riemann.pojo.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeDao extends JpaRepository<Resume, Long> {
}
