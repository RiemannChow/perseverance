package com.riemann.service;

import com.riemann.pojo.Account;

import java.util.List;

public interface AccountService {

    List<Account> queryAccountList() throws Exception;

}
