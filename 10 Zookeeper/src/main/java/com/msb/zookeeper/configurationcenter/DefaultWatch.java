package com.msb.zookeeper.configurationcenter;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.CountDownLatch;


/**
 * @author: 马士兵教育
 * @create: 2019-09-20 13:53
 */
public class DefaultWatch implements Watcher {
    CountDownLatch init  ;

    public CountDownLatch getInit() {
        return init;
    }

    public void setInit(CountDownLatch init) {
        this.init = init;
    }

    @Override
    public void process(WatchedEvent event) {
        Event.KeeperState state = event.getState();

        switch (state) {
            case Disconnected:
                System.out.println("Disconnected...c...new...");
                init =  new CountDownLatch(1);
                break;
            case SyncConnected:
                System.out.println("Connected...c...ok...");
                init.countDown();
                break;
        }
    }
}
