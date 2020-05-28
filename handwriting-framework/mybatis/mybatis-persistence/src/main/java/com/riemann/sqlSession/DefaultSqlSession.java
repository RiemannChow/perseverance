package com.riemann.sqlSession;

import com.riemann.config.CommandType;
import com.riemann.pojo.Configuration;
import com.riemann.pojo.MappedStatement;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <E> List<E> selectList(String statementId, Object... params) throws Exception {
        // 将要去完成对SimpleExecutor里的query方法的调用
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        List<Object> list = simpleExecutor.query(this.configuration, mappedStatement, params);
        return (List<E>) list;
    }

    @Override
    public <T> T selectOne(String statementId, Object... params) throws Exception {
        List<Object> objects = selectList(statementId, params);
        if (objects.size() == 1) {
            return (T) objects.get(0);
        } else {
            throw new RuntimeException("查询结果为空或者结果过多！");
        }
    }

    @Override
    public <T> T update(String statementId, Object... params) throws Exception {
        SimpleExecutor simpleExecutor = new SimpleExecutor();
        MappedStatement mappedStatement = this.configuration.getMappedStatementMap().get(statementId);
        return (T) simpleExecutor.query(this.configuration, mappedStatement, params);
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) throws Exception {
        // 使用JDK动态代理来为Dao层接口生成代理对象，并返回。
        Object proxyInstance = Proxy.newProxyInstance(mapperClass.getClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                /**
                 * 底层都还是去执行JDBC代码
                 * 根据不同情况来调用findAll或者findByCondition方法
                 * 准备参数：
                 * 1.statementId: sql语句的唯一标识 nnamespace.id = 接口全限定名.方法名
                 */
                // 方法名
                String methodName = method.getName();
                String className = method.getDeclaringClass().getName();

                String statementId = className + "." + methodName;

                // 准备参数 2.params:args
                // 获取被调用方法的返回值类型
                Type genericReturnType = method.getGenericReturnType();

                if (Arrays.asList(CommandType.sqlCommand).contains(methodName)) {
                    return update(statementId, args);
                }

                // 判断是否进行了泛型类型参数化
                if (genericReturnType instanceof ParameterizedType) {
                    List<Object> objects = selectList(statementId, args);
                    return objects;
                }
                return selectOne(statementId, args);
            }
        });
        return (T) proxyInstance;
    }

}
