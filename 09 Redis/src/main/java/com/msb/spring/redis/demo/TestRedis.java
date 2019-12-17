package com.msb.spring.redis.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: 马士兵教育
 * @create: 2019-09-11 21:28
 */
@Component
public class TestRedis {

    @Autowired
    RedisTemplate  redisTemplate;

    @Autowired
    @Qualifier("ooxx")
    StringRedisTemplate  stringRedisTemplate;

    @Autowired
    ObjectMapper  objectMapper;


    public void testRedis(){



//        stringRedisTemplate.opsForValue().set("hello01","china");
//
//        System.out.println(stringRedisTemplate.opsForValue().get("hello01"));

        RedisConnection conn = redisTemplate.getConnectionFactory().getConnection();

        conn.set("hello02".getBytes(),"mashibing".getBytes());
        System.out.println(new String(conn.get("hello02".getBytes())));


//        HashOperations<String, Object, Object> hash = stringRedisTemplate.opsForHash();
//        hash.put("sean","name","zhouzhilei");
//        hash.put("sean","age","22");
//
//        System.out.println(hash.entries("sean"));


        Person p = new Person();
        p.setName("zhangsan");
        p.setAge(16);

//        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));

        Jackson2HashMapper jm = new Jackson2HashMapper(objectMapper, false);

        stringRedisTemplate.opsForHash().putAll("sean01",jm.toHash(p));

        Map map = stringRedisTemplate.opsForHash().entries("sean01");

        Person per = objectMapper.convertValue(map, Person.class);
        System.out.println(per.getName());


        stringRedisTemplate.convertAndSend("ooxx","hello");

        RedisConnection cc = stringRedisTemplate.getConnectionFactory().getConnection();
        cc.subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        }, "ooxx".getBytes());

        while(true){
            stringRedisTemplate.convertAndSend("ooxx","hello  from wo zi ji ");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }


    }






}
