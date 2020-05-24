package com.riemann.dao;

import com.riemann.pojo.User;

import java.io.IOException;
import java.util.List;

public interface UserDao {

    // 查询所有用户
    List<User> findAll() throws IOException;

    // 多条件组合查询：演示if
    List<User> findByCondition(User user) throws IOException;

    // 多值查询：演示foreach
    List<User> findByIds(int[] ids);

}
