package com.riemann.dao;

import com.riemann.pojo.Account;

public interface AccountDao {

    Account queryAccountByCardNo(String cardNo) throws Exception;

    int updateAccountByCardNo(Account account) throws Exception;

}