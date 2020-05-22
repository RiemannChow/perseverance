package com.riemann.pojo;

import com.google.common.collect.Maps;
import lombok.Data;

import javax.sql.DataSource;
import java.util.Map;

@Data
public class Configuration {

    private DataSource dataSource;

    /**
     * key:statementId
     * value:封装好的MappedStatement
     */
    Map<String, MappedStatement> mappedStatementMap = Maps.newHashMap();

}
