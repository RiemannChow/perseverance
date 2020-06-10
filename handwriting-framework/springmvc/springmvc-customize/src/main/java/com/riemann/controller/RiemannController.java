package com.riemann.controller;

import com.riemann.annotation.Autowired;
import com.riemann.annotation.Controller;
import com.riemann.annotation.RequestMapping;
import com.riemann.service.RiemannService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/riemann")
public class RiemannController {

    @Autowired
    private RiemannService riemannService;

    /**
     * URL: /riemann/query
     * @param request
     * @param response
     * @param name
     * @return
     */
    @RequestMapping("/query")
    public String query(HttpServletRequest request, HttpServletResponse response, String name) {
        return riemannService.get(name);
    }

}
