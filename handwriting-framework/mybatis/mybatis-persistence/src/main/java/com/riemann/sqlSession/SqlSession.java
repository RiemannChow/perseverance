package com.riemann.sqlSession;

import java.util.List;

public interface SqlSession {

    /**
     * 查询所有
     * @param statementId sql唯一id
     * @param params      sql有可能是模糊查询，传可变参数
     * @param <E>         泛型
     * @return            List集合
     */
    <E> List<E> selectList(String statementId, Object... params) throws Exception;

    /**
     * 根据条件查询单个
     * @param statementId sql唯一id
     * @param params      sql有可能是模糊查询，传可变参数
     * @param <T>         泛型
     * @return            某一对象
     */
    <T> T selectOne(String statementId, Object... params) throws Exception;

    /**
     * 插入操作
     * @param statementId sql唯一id
     * @param params      可变参数
     * @return            插入条数
     */
    // int insert(String statementId, Object... params) throws Exception;

    /**
     * 更新操作
     * @param statementId sql唯一id
     * @param params      可变参数
     * @return            更新条数
     */
    <T> T update(String statementId, Object... params) throws Exception;

    /**
     * 删除操作
     * @param statementId sql唯一id
     * @param params      可变参数
     * @return            删除条数
     */
   // int delete(String statementId, Object... params) throws Exception;

    /**
     * 为Dao层接口生成代理实现类
     * @param mapperClass 字节码
     * @param <T>         泛型
     * @return            某一对象
     */
    <T> T getMapper(Class<?> mapperClass) throws Exception;

}
