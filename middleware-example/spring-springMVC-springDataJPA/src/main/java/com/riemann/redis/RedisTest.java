package com.riemann.redis;

import redis.clients.jedis.Jedis;

public class RedisTest {

    public static void main(String[] args) {
        test();
    }

    public static void test() {
        Jedis jedis = new Jedis("47.113.82.141", 6379, 5000);
        String ping = jedis.ping();
        System.out.println(ping);
        /*jedis.set("name", "riemann");
        System.out.println(jedis.get("name"));*/
    }

}
