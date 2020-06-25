package com.riemann.server.servlet;

import com.riemann.server.Request;
import com.riemann.server.Response;

public interface Servlet {

    void init() throws Exception;

    void destory() throws Exception;

    void service(Request request, Response response) throws Exception;

}
