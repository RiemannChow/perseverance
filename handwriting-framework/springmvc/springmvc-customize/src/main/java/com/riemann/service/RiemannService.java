package com.riemann.service;

public interface RiemannService {

    String get(String name);

    String getAdminPermission(String name);

    String getGuestPermission(String name);

    String getGuestAndAdminPermission(String name);

}
