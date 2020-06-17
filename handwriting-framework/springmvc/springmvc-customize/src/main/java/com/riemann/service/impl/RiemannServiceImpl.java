package com.riemann.service.impl;

import com.riemann.annotation.Service;
import com.riemann.service.RiemannService;

@Service("riemannService")
public class RiemannServiceImpl implements RiemannService {

    @Override
    public String get(String name) {
        System.out.println("RiemannService 实现类中的name参数：" + name);
        return name;
    }

    @Override
    public String getAdminPermission(String name) {
        return name + " have admin permission";
    }

    @Override
    public String getGuestPermission(String name) {
        return name + " have guest permission";
    }

    @Override
    public String getGuestAndAdminPermission(String name) {
        return name + " have guest and admin permission";
    }

}
