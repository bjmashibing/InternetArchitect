package org.ormtest.step030.entity;

/**
 * 用户实体
 */
public class UserEntity {
    /**
     * 用户 Id
     */
    @Column(name = "user_id")
    public long _userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    public String _userName;

    /**
     * 密码
     */
    @Column(name = "password")
    public String _password;
}
