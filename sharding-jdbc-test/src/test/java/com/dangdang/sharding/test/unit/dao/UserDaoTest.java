package com.dangdang.sharding.test.unit.dao;

import com.dangdang.sharding.test.dao.UserMapper;
import com.dangdang.sharding.test.model.User;
import com.dangdang.sharding.test.model.UserExample;
import com.dangdang.sharding.test.unit.AppTest;
import com.dangdang.sharding.test.util.Company;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * weifeng.jiang 2018-06-22 18:09
 */
public class UserDaoTest extends AppTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insert(){
        User user = new User();
        user.setId(2L);
        user.setCompanyCode(Company.TEST_B);
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyyMM").parse("201808");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setCreateTime(date);
        userMapper.insert(user);
    }

    @Test
    public void update(){
        User user = new User();
        user.setAge(40L);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(2L);
        userMapper.updateByExampleSelective(user,userExample);
    }
}
