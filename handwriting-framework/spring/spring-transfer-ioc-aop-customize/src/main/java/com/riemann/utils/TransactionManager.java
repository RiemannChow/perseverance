package com.riemann.utils;

import com.riemann.annotation.Autowired;
import com.riemann.annotation.Service;

import java.sql.SQLException;

@Service
public class TransactionManager {

    @Autowired
    private ConnectionUtils connectionUtils;

    // 开启手动事务控制
    public void beginTransaction() throws SQLException {
        connectionUtils.getCurrentThreadConn().setAutoCommit(false);
    }


    // 提交事务
    public void commit() throws SQLException {
        connectionUtils.getCurrentThreadConn().commit();
    }


    // 回滚事务
    public void rollback() throws SQLException {
        connectionUtils.getCurrentThreadConn().rollback();
    }

}