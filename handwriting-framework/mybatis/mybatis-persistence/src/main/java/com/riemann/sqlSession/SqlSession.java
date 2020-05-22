package com.riemann.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     * @param statementId sql唯一id
     * @param params      sql有可能十四模糊查询，传可变参数
     * @param <E>         泛型
     * @return            List集合
     */
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 根据条件查询单个
     * @param statementId sql唯一id
     * @param params      sql有可能十四模糊查询，传可变参数
     * @param <T>         泛型
     * @return            某一对象
     */
    <T> T selectOne(String statementId, Object... params) throws Exception;

}
