package com.riemann.sqlSession;

import com.riemann.pojo.Configuration;
import com.riemann.pojo.MappedStatement;

import java.beans.IntrospectionException;
import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws SQLException, ClassNotFoundException, NoSuchFieldException, IllegalAccessException, IntrospectionException, InstantiationException, Exception;

}
