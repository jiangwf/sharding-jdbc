package com.dangdang.sharding.unit.service;

import com.dangdang.sharding.unit.dao.UserDao;
import com.dangdang.sharding.unit.model.User;
import com.dangdang.sharding.unit.model.UserExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * weifeng.jiang 2018-06-27 10:38
 */
@Service
@Transactional
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Override
    public void updateList() {
        User user = new User();
        user.setAge(40L);
        user.setId(20L);
        userDao.updateList(user);
    }

    @Override
    public void deleteUser() {
        UserExample example = new UserExample();
        example.createCriteria().andIdEqualTo(10L);
        userDao.deleteByExample(example);
    }
}
