package com.msb.zookeeper.configurationcenter;

/**
 * @author: 马士兵教育
 * @create: 2019-09-20 13:51
 */
public class ZKConf {

    private String address;
    private Integer sessionTime;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getSessionTime() {
        return sessionTime;
    }

    public void setSessionTime(Integer sessionTime) {
        this.sessionTime = sessionTime;
    }
}
