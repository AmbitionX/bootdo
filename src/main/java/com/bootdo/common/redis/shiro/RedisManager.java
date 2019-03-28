package com.bootdo.common.redis.shiro;

/**
 * @author bootdo 1992lcg@163.com
 * @version V1.0
 */

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 *
 */
public class RedisManager {

    private static Logger logger = LoggerFactory.getLogger(RedisManager.class);

    @Value("${spring.redis.host}")
    private String host = "127.0.0.1";

    @Value("${spring.redis.port}")
    private int port = 6379;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.database}")
    private int database;

    @Value("${spring.redis.password}")
    private String password = "";

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxidle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minidle;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxactive;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxwait;

    private int expire;


    private static JedisPool jedisPool = null;

    public RedisManager() {

    }

    /**
     * 初始化方法
     */
    public void init() {
        if (jedisPool == null) {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxIdle(maxidle);
            config.setMinIdle(minidle);
            config.setMaxTotal(maxactive);
            config.setMaxWaitMillis(maxwait);
            if (password != null && !"".equals(password)) {
                jedisPool = new JedisPool(config, host, port, timeout, password);
            } else if (timeout != 0) {
                jedisPool = new JedisPool(config, host, port, timeout,null,database);
            } else {
                jedisPool = new JedisPool(config, host, port);
            }
        }
    }

    /**
     * get value from redis
     *
     * @param key
     * @return
     */
    public byte[] get(byte[] key) {
        byte[] value = null;
        Jedis jedis = jedisPool.getResource();
        try {
            value = jedis.get(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @return
     */
    public byte[] set(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            if (this.expire != 0) {
                jedis.expire(key, this.expire);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * set
     *
     * @param key
     * @param value
     * @param expire
     * @return
     */
    public byte[] set(byte[] key, byte[] value, int expire) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
            if (expire != 0) {
                jedis.expire(key, expire);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    /**
     * del
     *
     * @param key
     */
    public void del(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * flush
     */
    public void flushDB() {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.flushDB();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * size
     */
    public Long dbSize() {
        Long dbSize = 0L;
        Jedis jedis = jedisPool.getResource();
        try {
            dbSize = jedis.dbSize();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return dbSize;
    }

    /**
     * keys
     *
     * @param pattern
     * @return
     */
    public Set<byte[]> keys(String pattern) {
        Set<byte[]> keys = null;
        Jedis jedis = jedisPool.getResource();
        try {
            keys = jedis.keys(pattern.getBytes());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return keys;
    }

    public static byte[] getKey(String key) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key.getBytes());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static String getKeyAsString(String key) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                return new String(data, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error(String.format("Get key:%s from redis failed", key), ex);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static String getKeyAsString(String key, int expiredSeconds) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            byte[] data = jedis.get(key.getBytes());
            if (data != null) {
                jedis.expire(key, expiredSeconds);
                return new String(data, "UTF-8");
            } else {
                return null;
            }
        } catch (Exception ex) {
            logger.error(String.format("Get key:%s from redis failed", key), ex);
            return null;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void setex(String key, String value, int seconds) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.setex(key, seconds, value);
        } catch (Exception ex) {
            logger.error(String.format("Set key:%s with value:%s to redis failed", key, value), ex);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void setex(String key, byte[] value, int seconds) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.setex(key.getBytes("utf-8"), seconds, value);
        } catch (Exception ex) {
            logger.error(String.format("Set key:%s with value:%s to redis failed", key, value), ex);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void set(String key, String value) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
        } catch (Exception ex) {
            logger.error(String.format("Set key:%s with value:%s to redis failed", key, value), ex);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static Long del(String key) {
        if (jedisPool == null) {
            logger.error("reids pool is null,please check the configuration");
            return 0L;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.del(key);
        } catch (Exception ex) {
            logger.error(String.format("Delete key:%s from redis failed", key), ex);
            return 0L;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static long llen(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.llen(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static List<String> lrange(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptyList();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(key, 0, -1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static List<byte[]> lrange(byte[] key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptyList();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(key, 0, -1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static void lpush(String key, String... values) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.lpush(key, values);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static void hrem(byte[] key, byte[] field) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hdel(key, field);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static void hset(byte[] key, byte[] field, byte[] value) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static Map<byte[], byte[]> hGetAll(byte[] key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static long lrem(byte[] key, int count, byte[] value) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrem(key, 0, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static String rpop(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.rpop(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
    public static boolean exists(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return false;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static long incr(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return (Long) (jedis.incr(key));
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static long incrBy(String key, long count) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incrBy(key, count);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static Double zincrBy(String key, int score, String member) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0.0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zincrby(key, (double) score, member);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static Set<String> zrevrange(String key, int topn) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptySet();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zrevrange(key, 0, topn - 1);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
    public static double zscore(String key, String member) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            return 0;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static long zrevrank(String key, String member) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zrevrank(key, member);
        } catch (Exception e) {
            return -1;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Long zunionstore(String dstKey, String[] keys) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0L;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zunionstore(dstKey, keys);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Long zcount(String dstKey, int min, int max) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0L;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.zcount(dstKey, (double) min, (double) max);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void sadd(String key, String[] members) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.sadd(key, members);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static Set<String> smembers(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptySet();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.smembers(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static void sremove(String key, String[] members) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.srem(key, members);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void incrby(String key, int count) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.incrBy(key, (long) count);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void expire(String key, int seconds) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.expire(key, seconds);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static String hget(String key, String field) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return null;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key, field);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static void hset(String key, String field, String value) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static void hsetnx(String key, String field, String value) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hsetnx(key, field, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }


    public static void hmset(String key, Map<String, String> value) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hmset(key, value);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static List<String> hmget(String key, String... fields) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptyList();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hmget(key, fields);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static Map<String, String> hgetall(String key) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return Collections.emptyMap();
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hgetAll(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static boolean hExists(String key, String field) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return false;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hexists(key, field);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public static long hincrBy(String key, String field, int increment) {
        if (jedisPool == null) {
            logger.error("redis pool is null,please check the configuration");
            return 0L;
        }
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hincrBy(key, field, (long) increment);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getMaxidle() {
        return maxidle;
    }

    public void setMaxidle(int maxidle) {
        this.maxidle = maxidle;
    }

    public int getMinidle() {
        return minidle;
    }

    public void setMinidle(int minidle) {
        this.minidle = minidle;
    }

    public int getMaxactive() {
        return maxactive;
    }

    public void setMaxactive(int maxactive) {
        this.maxactive = maxactive;
    }

    public int getMaxwait() {
        return maxwait;
    }

    public void setMaxwait(int maxwait) {
        this.maxwait = maxwait;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
