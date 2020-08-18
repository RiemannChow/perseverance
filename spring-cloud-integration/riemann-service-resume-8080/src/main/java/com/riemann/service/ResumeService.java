package com.riemann.service;


import com.riemann.pojo.Resume;

public interface ResumeService {

    Resume findDefaultResumeByUserId(Long userId);

}
