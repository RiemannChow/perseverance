package com.riemann.test;

import com.riemann.dao.UserDao;
import com.riemann.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    @Test
    public void test1() throws IOException {
        // 1.Resources工具类，配置文件的加载，把配置文件加载成字节输入流
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2.解析配置文件，并创建sqlSessionFactory工厂
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        // 3.生产sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession(); // 默认开启事务，但是该事务不会自动提交
                                                                 // 在进行增删改查操作时，需要手动提交事务
        // 4.sqlSession调用方法
        List<User> users = sqlSession.selectList("com.riemann.dao.UserDao.findAll");
        System.out.println(users);
        sqlSession.close();
    }

    @Test
    public void test2() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true); // 事务自动提交

        User user = new User();
        user.setId(4);
        user.setUsername("tom");

        sqlSession.insert("com.riemann.dao.UserDao.saveUser", user);
        // 不提交事务的话，数据库的不会成功插入 (上面设置了事务自动提交，所以下面不需要手动提交)
        // sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void test3() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(4);
        user.setUsername("lucy");

        sqlSession.update("com.riemann.dao.UserDao.updateUser", user);
        // 不提交事务的话，数据库的不会成功插入
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void test4() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        sqlSession.update("com.riemann.dao.UserDao.deleteUser", 4);
        // 不提交事务的话，数据库的不会成功插入
        sqlSession.commit();
        sqlSession.close();
    }

    // 传统开发方式
    /*@Test
    public void test5() throws IOException {
        UserDao userDao = new UserDaoImpl();
        List<User> all = userDao.findAll();
        System.out.println(all);
    }*/

    // 代理开发方式
    @Test
    public void test6() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDao mapper = sqlSession.getMapper(UserDao.class);
        List<User> all = mapper.findAll();
        System.out.println(all);
    }

    @Test
    public void test7() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDao mapper = sqlSession.getMapper(UserDao.class);
        User user = new User();
        user.setUsername("riemann");
        List<User> all = mapper.findByCondition(user);
        System.out.println(all);
    }

    @Test
    public void test8() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        UserDao mapper = sqlSession.getMapper(UserDao.class);
        int[] arr = {1, 2};
        List<User> all = mapper.findByIds(arr);
        System.out.println(all);
    }

}
