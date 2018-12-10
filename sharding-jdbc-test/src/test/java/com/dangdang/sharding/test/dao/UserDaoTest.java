package com.dangdang.sharding.test.dao;

import com.dangdang.sharding.test.AppTest;
import com.dangdang.sharding.unit.dao.UserDao;
import com.dangdang.sharding.unit.model.User;
import com.dangdang.sharding.unit.model.UserExample;
import com.dangdang.sharding.unit.service.UserService;
import com.dangdang.sharding.unit.util.Company;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * weifeng.jiang 2018-06-22 18:09
 */
public class UserDaoTest extends AppTest {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    @Test
    public void insert() {
        User user = new User();
        user.setId(2L);
        user.setCompanyCode(Company.TEST_B);
        Date date = new Date();
        try {
            date = new SimpleDateFormat("yyyyMM").parse("201806");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        user.setCreateTime(date);
        userDao.insert(user);
    }

    @Test
    public void testUpdate() {
        userService.updateList();
    }

    @Test
    public void testSelect() {
        UserExample example = new UserExample();
        example.createCriteria().andCompanyCodeEqualTo("testa");
        List<User> users = userDao.selectByExample(example);
        for (User user : users) {
            System.out.println(user.getId() + "," + user.getAge());
        }
    }

    @Test
    public void testInSelect() {
        UserExample example = new UserExample();
        example.createCriteria().andCompanyCodeIn(Arrays.asList("testa","testb"));
        List<User> users = userDao.selectByExample(example);
        for (User user : users) {
            System.out.println(user.getId() + "," + user.getAge());
        }
    }

    @Test
    @Rollback(value = false)
    public void testDelete() {
        userService.deleteUser();
    }

    @Test
    public void testSelectForceIndex(){
        User user = new User();
        user.setName("zs");
        List<User> users = userDao.selectForceIndex(user);
        for (User user1 : users) {
            System.out.println(user1.getAge()+","+user1.getName());
        }
    }

    @Test
    public void testUpdateForceIndex(){
        User user = new User();
        user.setName("zs");
        user.setAge(1L);
        userDao.updateForceIndex(user);
    }

    @Test
    public void testUpdateUser(){
        User user = new User();
        user.setName("zs");
        user.setAge(90L);
        userDao.updateUser(user);
    }
}
