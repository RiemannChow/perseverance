package com.riemann.mapper;

import com.riemann.pojo.Account;

import java.util.List;

public interface AccountMapper {

    // 定义dao层接口方法
    List<Account> queryAccountList() throws Exception;

}
