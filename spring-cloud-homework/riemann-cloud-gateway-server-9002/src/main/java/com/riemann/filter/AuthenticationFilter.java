package com.riemann.filter;

import com.riemann.feign.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * 统一认证过滤器
 */
@Slf4j
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Resource
    UserFeignClient userFeignClient;

    /**
     * 过滤器核心方法
     * @param exchange exchange 封装了request和response对象的上下文
     * @param chain chain 网关过滤器链（包含全局过滤器和单路由过滤器）
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 从上下文中取出request和response对象
        ServerHttpRequest request = exchange.getRequest();
        RequestPath path = request.getPath();

        if (!path.toString().startsWith("/api/user/info")) {
            // 不是info请求的，直接不过滤
            return chain.filter(exchange);
        }

        ServerHttpResponse response = exchange.getResponse();
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        Set<String> strings = cookies.keySet();
        String token = "";
        for (String key : strings) {
            List<HttpCookie> httpCookies = cookies.get(key);
            for (HttpCookie httpCookie : httpCookies) {
                if (httpCookie.getName().equals("token")) {
                    token = httpCookie.getValue();
                }
                break;
            }
            if (token != "") {
                break;
            }
        }
        if (StringUtils.isEmpty(token)) {
            // 没有权限，返回403
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 状态码
            log.debug("没有权限，拒绝访问！");
            String data = "未授权，请登录";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            return response.writeWith(Mono.just(wrap));
        }
        System.out.println(token);
        String email = userFeignClient.info(token);
        // 如果根据token取出来的email不为空，证明有权限，定位到欢迎页
        if (!StringUtils.isEmpty(email)) {
            if (request.getMethod().name().equals(HttpMethod.POST.toString())) {
                // 如果是POST请求，不重定向
                response.setStatusCode(HttpStatus.OK); // 状态码
                DataBuffer wrap = response.bufferFactory().wrap(email.getBytes());
                return response.writeWith(Mono.just(wrap));
            }
            log.debug("有权限，重定向至welcome.html");
            response.setStatusCode(HttpStatus.FOUND);
            response.getHeaders().set(HttpHeaders.LOCATION, "/static/welcome.html");
            return response.setComplete();
        } else {
            // 没有权限，返回403
            response.setStatusCode(HttpStatus.UNAUTHORIZED); // 状态码
            log.debug("没有权限，拒绝访问！");
            String data = "请登录";
            DataBuffer wrap = response.bufferFactory().wrap(data.getBytes());
            return response.writeWith(Mono.just(wrap));
        }
    }

    /**
     * 返回值表示当前过滤器的顺序(优先级)，数值越小，优先级越高，此处返回20，优先级低于IP防爆刷
     * @return
     */
    @Override
    public int getOrder() {
        return 20;
    }

}
