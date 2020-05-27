package com.riemann.test;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.riemann.mapper.CommonUserMapper;
import com.riemann.mapper.OrderMapper;
import com.riemann.mapper.UserMapper;
import com.riemann.pojo.Order;
import com.riemann.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MybatisTest {

    // 一对一
    @Test
    public void test1() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        OrderMapper mapper = sqlSession.getMapper(OrderMapper.class);
        List<Order> orderAndUser = mapper.findOrderAndUser();
        System.out.println(orderAndUser);
    }

    // 一对多
    @Test
    public void test2() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> all = mapper.findAll();
        System.out.println(all);
    }

    // 多对多
    @Test
    public void test3() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        List<User> all = mapper.findAllUserAndRole();
        System.out.println(all);
    }

    private UserMapper userMapper;
    private OrderMapper orderMapper;

    @Before
    public void before() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        userMapper = sqlSession.getMapper(UserMapper.class);
        orderMapper = sqlSession.getMapper(OrderMapper.class);
    }

    @Test
    public void addUser() {
        User user = new User();
        user.setId(4);
        user.setUsername("test");

        userMapper.addUser(user);
    }

    @Test
    public void updateUser() {
        User user = new User();
        user.setId(4);
        user.setUsername("update test");

        userMapper.updateUser(user);
    }

    @Test
    public void selectUser() {
        List<User> users = userMapper.selectUser();
        System.out.println(users);
    }

    @Test
    public void deleteUser() {
        userMapper.deleteUser(4);
    }

    // 一对一 (注解方式)
    @Test
    public void oneToOne() {
        List<Order> orderAndUser = orderMapper.findOrderAndUser();
        System.out.println(orderAndUser);
    }

    // 一对多 (注解方式)
    @Test
    public void oneToMany() {
        List<User> all = userMapper.findAll();
        System.out.println(all);
    }

    // 多对多 (注解方式)
    @Test
    public void manyToMany() {
        List<User> all = userMapper.findAllUserAndRole();
        System.out.println(all);
    }

    @Test
    public void pageHelperTest() {
        PageHelper.startPage(1, 2);
        List<User> all = userMapper.selectUser();
        System.out.println(all);

        PageInfo<User> pageInfo = new PageInfo<>(all);
        System.out.println("总条数：" + pageInfo.getTotal());
        System.out.println("总页数：" + pageInfo.getPages());
        System.out.println("当前页：" + pageInfo.getPageNum());
        System.out.println("每页显示的条数：" + pageInfo.getPageSize());
    }

    @Test
    public void commonMapper() throws IOException {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        CommonUserMapper mapper = sqlSession.getMapper(CommonUserMapper.class);

        // 1.通用mapper 增删改查
        User user = new User();
        user.setId(1);
        User user1 = mapper.selectOne(user);
        System.out.println(user1);

        // 2.example方法 增删改查
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("id", 1);
        List<User> users = mapper.selectByExample(example);
        System.out.println(users);
    }

}
