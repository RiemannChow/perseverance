package com.riemann.test;

import com.riemann.mapper.OrderMapper;
import com.riemann.mapper.UserMapper;
import com.riemann.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class CacheTest {

    private UserMapper userMapper;

    private OrderMapper orderMapper;

    private SqlSession sqlSession;

    private SqlSessionFactory sqlSessionFactory;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        sqlSession = sqlSessionFactory.openSession(true);
        userMapper = sqlSession.getMapper(UserMapper.class);
        orderMapper = sqlSession.getMapper(OrderMapper.class);
    }

    @Test
    public void firstLevelCache() {
        // 第一次查询id为1的用户
        User user1 = userMapper.findUserById(1);

        // 第二次查询id为1的用户
        User user2 = userMapper.findUserById(1);

        System.out.println(user1 == user2);
    }

    @Test
    public void firstLevelCacheOfUpdate() {
        // 第一次查询id为1的用户
        User user1 = userMapper.findUserById(1);

        // 更新用户
        User user = new User();
        user.setId(3);
        user.setUsername("tom");

        userMapper.updateUser(user);
        sqlSession.commit(); // 刷新一级缓存
        // sqlSession.clearCache(); 这个也可以刷新一级缓存

        // 第二次查询id为1的用户
        User user2 = userMapper.findUserById(1);

        System.out.println(user1 == user2);
    }

    @Test
    public void secondLevelCache() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();

        UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);

        // 第一次查询id为1的用户
        User user1 = userMapper1.findUserById(1);
        sqlSession1.close(); // 清空一级缓存

        // 第二次查询id为1的用户
        User user2 = userMapper2.findUserById(1);
        sqlSession2.close();

        System.out.println(user1 == user2);
    }

    @Test
    public void secondLevelCacheOfUpdate() {
        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        SqlSession sqlSession2 = sqlSessionFactory.openSession();
        SqlSession sqlSession3 = sqlSessionFactory.openSession();

        UserMapper userMapper1 = sqlSession1.getMapper(UserMapper.class);
        UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
        UserMapper userMapper3 = sqlSession3.getMapper(UserMapper.class);

        // 第一次查询id为1的用户
        User user1 = userMapper1.findUserById(1);
        sqlSession1.close(); // 清空一级缓存

        User user = new User();
        user.setId(3);
        user.setUsername("edgar");
        userMapper3.updateUser(user);
        sqlSession3.commit();

        // 第二次查询id为1的用户
        User user2 = userMapper2.findUserById(1);
        sqlSession2.close();

        System.out.println(user1 == user2);
    }

}
