package com.riemann.controller;

import com.riemann.annotation.Autowired;
import com.riemann.annotation.Controller;
import com.riemann.annotation.RequestMapping;
import com.riemann.annotation.Security;
import com.riemann.service.RiemannService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/riemann")
public class RiemannController {

    @Autowired
    private RiemannService riemannService;

    /**
     * URL: /riemann/query?name=riemann
     * @param request
     * @param response
     * @param arg2
     * @return
     */
    @RequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response, String arg2) throws IOException {
        response.getWriter().write(riemannService.get(arg2));
    }

    @Security({"guest"})
    @RequestMapping("/queryGuest")
    public void queryGuest(HttpServletRequest request, HttpServletResponse response, String arg2) throws IOException {
        response.getWriter().write(riemannService.getGuestPermission(arg2));
    }

    @Security({"admin"})
    @RequestMapping("/queryAdmin")
    public void queryAdmin(HttpServletRequest request, HttpServletResponse response, String arg2) throws IOException {
        response.getWriter().write(riemannService.getAdminPermission(arg2));
    }

    @Security({"admin", "guest"})
    @RequestMapping("/queryGuestAndAdmin")
    public void queryGuestAndAdmin(HttpServletRequest request, HttpServletResponse response, String arg2) throws IOException {
        response.getWriter().write(riemannService.getAdminPermission(arg2));
    }

}
