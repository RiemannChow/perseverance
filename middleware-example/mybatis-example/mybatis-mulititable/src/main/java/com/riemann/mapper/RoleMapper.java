package com.riemann.mapper;

import com.riemann.pojo.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper {

    @Select("select * from sys_role r, sys_user_role ur where r.id = ur.roleid and ur.userid = #{uid}")
    List<Role> findRoleByUid(Integer uid);

}
