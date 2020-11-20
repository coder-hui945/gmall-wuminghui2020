package com.wuminghui.gmall.seckill.controller;

import com.wuminghui.gmall.util.RedisUtil;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @autor huihui
 * @date 2020/11/16 - 10:22
 */
@Controller
public class SecKillController {
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    RedissonClient redissonClient;


    @RequestMapping("secKill")
    @ResponseBody
    public String secKill() {//redission做秒杀:先到先得
        RSemaphore semaphore = redissonClient.getSemaphore("116");
        String memberId = "1";
        Jedis jedis = redisUtil.getJedis();
        boolean exec = semaphore.tryAcquire();
        Integer stock = Integer.parseInt(jedis.get("116"));
        if (exec) {
            System.out.println("当前库存剩余数量为：" + stock + ",当前用户" + memberId + "抢购成功！vvvvvvvvvvvvvv");
            //抢购成功用消息队列发送订单
        }else {
            System.out.println("当前库存剩余数量：" + stock + ",当前用户：" + memberId + "抢购失败！！");
        }
        return "2";
    }

    @RequestMapping("kill")
    @ResponseBody
    public String kill() {//redis做秒杀：随机运气
        String memberId = "1";
        Jedis jedis = redisUtil.getJedis();
        //开启商品的监控
        jedis.watch("116");
        Integer stock = Integer.parseInt(jedis.get("116"));
        if (stock > 0) {
            Transaction multi = jedis.multi();
            multi.incrBy("116", -1);
            List<Object> exec = multi.exec();
            if (exec != null && exec.size() > 0) {
                System.out.println("当前库存剩余数量为：" + stock + ",当前用户" + memberId + "抢购成功！vvvvvvvvvvvvvv");
                //抢购成功用消息队列发送订单
            }else {
                System.out.println("当前库存剩余数量：" + stock + ",当前用户：" + memberId + "抢购失败！！");
            }
        }
        jedis.close();
        return "1";
    }
}
