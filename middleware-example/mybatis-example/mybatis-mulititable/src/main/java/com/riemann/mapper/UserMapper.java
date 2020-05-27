package com.riemann.mapper;

import com.riemann.pojo.User;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.mybatis.caches.redis.RedisCache;

import java.util.List;

// @CacheNamespace(implementation = RedisCache.class) // redis开启分布式二级缓存
@CacheNamespace(implementation = PerpetualCache.class) // 开启二级缓存
public interface UserMapper {

    // 查询所有用户、同时查询每个用户关联的订单信息 (一对多的接口)
    // List<User> findAll();
    // (一对多的接口 走的注解)
    @Select("select * from user")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "orderList", column = "id", javaType = List.class, many = @Many(select = "com.riemann.mapper.UserMapper.findUserById"))
    })
    List<User> findAll();

    // 查询所有用户、同时查询每个用户关联的角色信息 (多对多的接口)
    // List<User> findAllUserAndRole();
    // (一对多的接口 走的注解)
    @Select("select * from user")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "roleList", column = "id", javaType = List.class, many = @Many(select = "com.riemann.mapper.RoleMapper.findRoleByUid"))
    })
    List<User> findAllUserAndRole();

    // 添加用户 (注解形式)
    @Insert("insert into user values(#{id},#{username})")
    void addUser(User user);

    // 更新用户 (注解形式)
    @Update("update user set username = #{username} where id = #{id}")
    void updateUser(User user);

    // 查询用户 (注解形式)
    @Select("select * from user")
    List<User> selectUser();

    // 删除用户 (注解形式)
    @Delete("delete from user where id = #{id}")
    void deleteUser(Integer id);

    @Options(useCache = false) // 禁用二级缓存
    @Select("select * from user where id = #{id}")
    User findUserById(Integer id);

}
