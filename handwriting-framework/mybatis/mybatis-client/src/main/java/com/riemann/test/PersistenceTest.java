package com.riemann.test;

import com.riemann.dao.UserDao;
import com.riemann.io.Resources;
import com.riemann.pojo.User;
import com.riemann.sqlSession.SqlSession;
import com.riemann.sqlSession.SqlSessionFactory;
import com.riemann.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class PersistenceTest {

    UserDao userDao;

    @Before
    public void before() throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        userDao = sqlSession.getMapper(UserDao.class);
    }

    @Test
    public void testSelect() throws Exception {
        // 调用
        User user = new User();
        user.setId(1);
        user.setUsername("riemann");

//      User user2 = sqlSession.selectOne("user.selectOne", user);
//      System.out.println(user2);

        /*User user2 = userDao.findByCondition(user);
        System.out.println(user2);*/

        List<User> all = userDao.findAll();
        System.out.println(all);

    }

    @Test
    public void testInsert() throws Exception {
        // 调用
        User user = new User();
        user.setId(4);
        user.setUsername("jack");

        userDao.insert(user);
    }

    @Test
    public void testUpdate() throws Exception {
        // 调用
        User user = new User();
        user.setId(4);
        user.setUsername("jackUpdate");

        userDao.update(user);
    }

    @Test
    public void testDelete() throws Exception {
        // 调用
        User user = new User();
        user.setId(4);
        userDao.delete(user);
    }

}
