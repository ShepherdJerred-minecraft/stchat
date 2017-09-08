package com.shepherdjerred.stchat.redis;

import redis.clients.jedis.Jedis;

public class JedisManager {

    private static Jedis jedis;

    public static Jedis getJedis() {
        return jedis;
    }

    public static void setupRedis() {
        jedis = new Jedis("localhost");
    }

}
