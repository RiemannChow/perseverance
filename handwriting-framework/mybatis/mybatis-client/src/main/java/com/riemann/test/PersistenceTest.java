package com.riemann.test;

import com.riemann.io.Resources;
import com.riemann.pojo.User;
import com.riemann.sqlSession.SqlSession;
import com.riemann.sqlSession.SqlSessionFactory;
import com.riemann.sqlSession.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.InputStream;

public class PersistenceTest {

    @Test
    public void test() throws Exception {
        InputStream resourceAsStream = Resources.getResourceAsStream("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(resourceAsStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();

        // 调用
        User user = new User();
        user.setId(1);
        user.setUsername("riemann");
        User user2 = sqlSession.selectOne("user.selectOne", user);
        System.out.println(user2);
    }

}
