package com.msb.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * @author: 马士兵教育
 * @create: 2019-09-20 20:08
 */
public class ZKUtils {

    private  static ZooKeeper zk;

    private static String address = "192.168.150.11:2181,192.168.150.12:2181,192.168.150.13:2181,192.168.150.14:2181/testLock";

    private static DefaultWatch watch = new DefaultWatch();

    private static CountDownLatch init  =  new CountDownLatch(1);
    public static ZooKeeper  getZK(){

        try {
            zk = new ZooKeeper(address,1000,watch);
            watch.setCc(init);
            init.await();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }


}
