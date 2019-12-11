package org.ormtest.step040.entity;

/**
 * 用户实体
 */
public class UserEntity {
    /**
     * 用户 Id
     */
    @Column(name = "user_id")
    public Integer _userId;

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
