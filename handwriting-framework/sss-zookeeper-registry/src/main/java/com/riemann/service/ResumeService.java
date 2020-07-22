package com.riemann.service;

import com.riemann.pojo.Resume;
import com.riemann.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResumeService {

    @Autowired
    private ResumeRepository resumeRepository;

    public List<Resume> findAll() {
        return resumeRepository.findAll();
    }

    public void save(Resume resume) {
        resumeRepository.save(resume);
    }

}
