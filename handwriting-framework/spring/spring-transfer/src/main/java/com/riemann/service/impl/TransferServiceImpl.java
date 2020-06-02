package com.riemann.service.impl;

import com.riemann.dao.AccountDao;
import com.riemann.pojo.Account;
import com.riemann.service.TransferService;

public class TransferServiceImpl implements TransferService {

    // 原本写法
    // private AccountDao accountDao = new JdbcAccountDaoImpl();

    // private AccountDao accountDao = (AccountDao) BeanFactory.getBean("accountDao");

    // 最佳状态
    private AccountDao accountDao;

    // 构造函数传值/set方法传值

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        // 原本写法
        /*try {
            // 开启事务(关闭事务的自动提交)
            TransactionManager.getInstance().beginTransaction();

            Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            int c = 1/0;
            accountDao.updateAccountByCardNo(from);

            // 提交事务
            TransactionManager.getInstance().commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            TransactionManager.getInstance().rollback();
            // 抛出异常便于上层servlet捕获
            throw e;
        }*/

        // 改造写法
        Account from = accountDao.queryAccountByCardNo(fromCardNo);
        Account to = accountDao.queryAccountByCardNo(toCardNo);

        from.setMoney(from.getMoney() - money);
        to.setMoney(to.getMoney() + money);

        accountDao.updateAccountByCardNo(to);
        int c = 1/0;
        accountDao.updateAccountByCardNo(from);
    }

}
