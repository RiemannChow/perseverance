package com.riemann.config;

import com.google.common.collect.Lists;
import com.riemann.util.ParameterMapping;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BoundSql {

    private String sqlText; // 解析过后的sql

    private List<ParameterMapping> parameterMappingList = Lists.newArrayList();

}
