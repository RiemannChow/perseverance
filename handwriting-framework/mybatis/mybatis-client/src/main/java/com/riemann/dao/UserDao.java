package com.riemann.dao;

import com.riemann.pojo.User;

import java.util.List;

public interface UserDao {

    /**
     * 查询所有用户
     * @return User的集合
     */
    List<User> findAll();

    /**
     * 根据条件进行用户查询
     * @return User对象
     * @param user
     */
    User findByCondition(User user);

}
