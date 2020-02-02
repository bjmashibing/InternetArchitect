package com.msb.zookeeper.configurationcenter;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * @author: 马士兵教育
 * @create: 2019-09-20 14:48
 */
public class WatchCallBack implements Watcher, AsyncCallback.DataCallback, AsyncCallback.StatCallback {

    private ZooKeeper zk;
    private String watchPath;
    private CountDownLatch init;
    private MyConf confMsg;

    public void aWait() {
        try {
            zk.exists(watchPath, this, this, "initExists");
            init.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ZooKeeper getZk() {
        return zk;
    }


    public String getWatchPath() {
        return watchPath;
    }


    public void setInit(int init) {
        System.out.println("set init....");
        this.init = new CountDownLatch(init);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public MyConf getConfMsg() {
        return confMsg;
    }

    public void setConfMsg(MyConf confMsg) {
        this.confMsg = confMsg;
    }

    public void setWatchPath(String watchPath) {
        this.watchPath = watchPath;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    @Override
    public void process(WatchedEvent event) {

        System.out.println(event.toString());
        Watcher.Event.EventType type = event.getType();
        switch (type) {
            case NodeCreated:
                System.out.println("...watch@Created");
                zk.getData(watchPath, this, this, "NodeCreated");
                break;
            case NodeDeleted:
                try {
                    confMsg.setConf("");
//                    zk.exists(watchPath, this,this,"aaa");
                    init = new CountDownLatch(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case NodeDataChanged:
                zk.getData(watchPath, this, this, "NodeChanged");
                break;
            case NodeChildrenChanged:
                break;
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
        if (data != null) {
            confMsg.setConf(new String(data));
            init.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {
        if (stat != null) {
            try {
                zk.getData(watchPath, this, this, "ex");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
}
