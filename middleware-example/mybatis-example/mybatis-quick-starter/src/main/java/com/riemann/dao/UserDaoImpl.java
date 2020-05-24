package com.riemann.dao;

import com.riemann.pojo.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
// 传统开发方式
/*public class UserDaoImpl implements UserDao {

    @Override
    public List<User> findAll() throws IOException {
        // 1.Resources工具类，配置文件的加载，把配置文件加载成字节输入流
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        // 2.解析配置文件，并创建sqlSessionFactory工厂
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        // 3.生产sqlSession
        SqlSession sqlSession = sqlSessionFactory.openSession(); // 默认开启事务，但是该事务不会自动提交
        // 在进行增删改查操作时，需要手动提交事务
        // 4.sqlSession调用方法
        List<User> users = sqlSession.selectList("com.riemann.dao.UserDao.findAll");
        sqlSession.close();
        return users;
    }

}*/
