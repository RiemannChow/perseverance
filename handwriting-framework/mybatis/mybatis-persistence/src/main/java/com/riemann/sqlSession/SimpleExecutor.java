package com.riemann.sqlSession;

import com.google.common.collect.Lists;
import com.riemann.config.BoundSql;
import com.riemann.config.CommandType;
import com.riemann.pojo.Configuration;
import com.riemann.pojo.MappedStatement;
import com.riemann.util.GenericTokenParser;
import com.riemann.util.ParameterMapping;
import com.riemann.util.ParameterMappingTokenHandler;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

public class SimpleExecutor implements Executor {

    @Override
    public <E> List<E> query(Configuration configuration, MappedStatement mappedStatement, Object... params) throws Exception {
        // 1.注册驱动，获取连接
        Connection connection = configuration.getDataSource().getConnection();

        // 2.获取sql语句 : select * from user where id = #{id} and username = #{username}
        //   转换sql语句 : select * from user where id = ? and username = ?
        //   转换的过程中 : 还要对#{}里面的值进行解析存储
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);

        // 3.获取预处理对象：preparedStatement
        PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSqlText());

        // 4.设置参数
        //   获取到了参数的全路径
        String parameterType = mappedStatement.getParameterType();
        Class<?> parameterTypeClass = getClassType(parameterType);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappingList();
        for (int i = 0; i < parameterMappingList.size(); i++) {
            ParameterMapping parameterMapping = parameterMappingList.get(i);
            String content = parameterMapping.getContent();

            // 反射
            Field declaredField = parameterTypeClass.getDeclaredField(content);
            // 暴力访问，防止访问的字段是private修饰
            declaredField.setAccessible(true);
            Object obj = declaredField.get(params[0]);
            preparedStatement.setObject(i + 1, obj);
        }

        // 5.执行sql
        String id = mappedStatement.getId();
        ResultSet resultSet = null;
        if (!Arrays.asList(CommandType.sqlCommand).contains(id)) {
            resultSet = preparedStatement.executeQuery();
        } else {
            Integer result = preparedStatement.executeUpdate();
            List<Integer> resultList = Lists.newArrayList();
            resultList.add(result);
            return (List<E>) resultList;
        }

        String resultType = mappedStatement.getResultType();
        Class<?> resultTypeClass = getClassType(resultType);
        List<Object> objects = Lists.newArrayList();

        // 6.封装返回结果集
        while (resultSet.next()) {
            Object o = resultTypeClass.newInstance();
            // 元数据
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                // 字段名
                String columnName = metaData.getColumnName(i);
                // 字段的值
                Object value = resultSet.getObject(columnName);
                // 使用反射或者内省，根据数据库表和实体的对应关系，完成封装
                PropertyDescriptor propertyDescriptor = new PropertyDescriptor(columnName, resultTypeClass);
                Method writeMethod = propertyDescriptor.getWriteMethod();
                writeMethod.invoke(o, value);
            }
            objects.add(o);
        }
        return (List<E>) objects;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            Class<?> clazz = Class.forName(parameterType);
            return clazz;
        }
        return null;
    }

    /**
     * 完成对#{}的解析工作:
     * 1.将#{}使用？进行代替
     * 2.解析出#{}里面的值进行存储
     * @param sql 原生sql
     * @return    解析后的sql
     */
    private BoundSql getBoundSql(String sql) {
        // 1.标记处理类：配置标记解析器来完成对占位符的解析处理工作
        ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        // 2.解析出来的sql
        String parseSql = genericTokenParser.parse(sql);
        // 3.#{}里面解析出来的参数名称
        List<ParameterMapping> parameterMappings = parameterMappingTokenHandler.getParameterMappings();
        BoundSql boundSql = new BoundSql(parseSql, parameterMappings);
        return boundSql;
    }

}
