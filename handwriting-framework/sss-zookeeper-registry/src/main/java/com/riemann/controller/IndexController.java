package com.riemann.controller;

import com.riemann.pojo.Resume;
import com.riemann.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {

    @Autowired
    ResumeService resumeService;

    @RequestMapping("/")
    public String index(Model model) {
        List<Resume> resumeList = resumeService.findAll();
        model.addAttribute("resumes", resumeList);
        return "home";
    }

    @RequestMapping("/addResume")
    public String addResume(Resume resume) {
        resumeService.save(resume);
        return "redirect:/";
    }

}
