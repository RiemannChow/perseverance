package com.riemann.sqlSession;

import com.riemann.pojo.Configuration;
import com.riemann.pojo.MappedStatement;

import java.util.List;

public interface Executor {

    <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception;

}
