package com.dangdang.sharding.test.dao;

import com.dangdang.sharding.unit.dao.UserMapper;
import com.dangdang.sharding.unit.model.User;
import com.dangdang.sharding.unit.model.UserExample;
import com.dangdang.sharding.test.AppTest;
import com.dangdang.sharding.unit.util.Company;
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
    public void testUpdate(){
        User user = new User();
        user.setAge(40L);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(20L);
        userMapper.updateByExampleSelective(user,userExample);
    }

    @Test
    public void testSelect(){

    }
}
