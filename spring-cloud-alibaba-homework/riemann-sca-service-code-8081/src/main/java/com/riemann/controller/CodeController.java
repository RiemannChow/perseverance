package com.riemann.controller;

import com.riemann.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    @Autowired
    CodeService codeService;

    @RequestMapping("/create/{email}")
    public boolean create(@PathVariable(value = "email") String email) {
        System.out.println(email);
        return codeService.create(email);
    }

    @RequestMapping("/validate/{email}/{code}")
    public Integer validate(@PathVariable(value = "email") String email, @PathVariable(value = "code") String code) {
        return codeService.validate(email, code);
    }

}
