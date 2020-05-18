package com.riemann.rpc.service;

public class SomeServiceImpl implements SomeService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
