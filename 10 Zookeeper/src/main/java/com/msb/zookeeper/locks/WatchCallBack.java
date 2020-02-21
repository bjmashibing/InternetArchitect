package com.msb.zookeeper.locks;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author: 马士兵教育
 * @create: 2019-09-20 18:03
 */
public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback,AsyncCallback.StatCallback,AsyncCallback.DataCallback {

    ZooKeeper zk;
    CountDownLatch cc = new CountDownLatch(1);
    String lockName ;
    String threadName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }


    public void tryLock() {
        //重入
        try {
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL, this, threadName );
            cc.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getRootData() throws KeeperException, InterruptedException {
        byte[] data = zk.getData("/", false, new Stat());
        System.out.println(new String(data));
    }

    public void unLock(){
        try {
            zk.delete("/"+lockName,-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    //getChileden....
    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

        //获得所目录的所有有序节点，然后排序，然后取自己在有序list中的index
        if(children == null){
            System.out.println(ctx.toString() + "list null");
        }else{
            try {
                Collections.sort(children);
                int i = children.indexOf(lockName);
                if(i<1){
                    System.out.println(threadName+" i am first...");
                    zk.setData("/",threadName.getBytes(),-1);
                    cc.countDown();
                }else{
                    System.out.println(threadName+" watch "+children.get(i-1));
                    zk.exists("/"+children.get(i-1),this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    //create....
    @Override
    public void processResult(int rc, String path, Object ctx, String name) {

        //每个线程启动后创建锁，然后get锁目录的所有孩子，不注册watch在锁目录
        System.out.println(ctx.toString()+" create path: "+ name);
        lockName = name.substring(1);
        zk.getChildren("/", false, this, ctx );
    }


    @Override
    public void process(WatchedEvent event) {

        Event.EventType type = event.getType();
        switch (type) {

            case NodeDeleted:
                zk.getChildren("/", false, this, "");
                break;

            case NodeChildrenChanged:
                break;
        }

    }


    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
    }
    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

        //监控失败了怎么办
    }
}
