package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public final class UserManager {
    /**
     * 用户字典
     */
    static private final Map<Integer, User> _userMap = new HashMap<>();

    /**
     * 私有化类默认构造器
     */
    private UserManager() {
    }

    /**
     * 添加用户
     *
     * @param newUser
     */
    static public void addUser(User newUser) {
        if (null != newUser) {
            _userMap.put(newUser.userId, newUser);
        }
    }

    /**
     * 根据用户 Id 移除用户
     *
     * @param userId
     */
    static public void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    /**
     * 列表用户
     *
     * @return
     */
    static public Collection<User> listUser() {
        return _userMap.values();
    }
}
