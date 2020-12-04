package com.riemann.service;

public interface UserService {

    public String register(String email, String password);

    public boolean isRegistered(String email);

    public String login(String email, String password);

    public String info(String token);

}