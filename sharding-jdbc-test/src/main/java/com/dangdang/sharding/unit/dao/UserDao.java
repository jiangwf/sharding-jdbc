package com.dangdang.sharding.unit.dao;

import com.dangdang.sharding.unit.model.User;

/**
 * weifeng.jiang 2018-06-27 10:21
 */
public interface UserDao extends UserMapper{

    void updateList(User user);
}
