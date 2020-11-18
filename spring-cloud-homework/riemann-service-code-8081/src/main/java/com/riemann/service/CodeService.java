package com.riemann.service;

public interface CodeService {

    public boolean create(String email);

    Integer validate(String email, String code);

}
