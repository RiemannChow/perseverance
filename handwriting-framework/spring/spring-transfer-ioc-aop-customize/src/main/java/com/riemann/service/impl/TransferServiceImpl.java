package com.riemann.service.impl;

import com.riemann.annotation.Autowired;
import com.riemann.annotation.Service;
import com.riemann.annotation.Transactional;
import com.riemann.dao.AccountDao;
import com.riemann.pojo.Account;
import com.riemann.service.TransferService;

@Service
public class TransferServiceImpl implements TransferService {

    @Autowired
    private AccountDao accountDao;

    @Override
    @Transactional
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        int c = 1 / 0;
        accountDao.updateAccountByCardNo(from);

    }
}