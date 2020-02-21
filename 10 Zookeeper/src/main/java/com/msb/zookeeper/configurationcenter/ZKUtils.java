package com.msb.zookeeper.configurationcenter;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

public class ZKUtils {
    static ZooKeeper zk ;
    static ZKConf conf ;
    static DefaultWatch watch ;
    static CountDownLatch c = new CountDownLatch(1);

    public static void setConf(ZKConf conf) {
        ZKUtils.conf = conf;
    }

    public static void setWatch(DefaultWatch watch) {
        watch.setInit(c);
        ZKUtils.watch = watch;

    }

    public static ZooKeeper getZK(){
//
        try {
            zk = new ZooKeeper(conf.getAddress(),conf.getSessionTime(),watch);
            c.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zk;
    }

    public static void closeZK(){
        if(zk != null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
