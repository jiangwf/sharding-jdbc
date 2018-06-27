package com.dangdang.sharding.unit.dao;

import com.dangdang.sharding.unit.model.User;

import java.util.List;

/**
 * weifeng.jiang 2018-06-27 10:21
 */
public interface UserDao extends UserMapper{

    void updateList(User user);

    List<User> selectForceIndex(User user);

    void updateForceIndex(User user);

    void updateUser(User user);
}
