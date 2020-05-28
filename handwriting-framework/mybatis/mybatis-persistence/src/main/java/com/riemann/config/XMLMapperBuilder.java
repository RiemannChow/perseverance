package com.riemann.config;

import com.google.common.collect.Lists;
import com.riemann.pojo.Configuration;
import com.riemann.pojo.MappedStatement;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }

    public void parse(InputStream in) throws DocumentException {
        Document document = new SAXReader().read(in);
        Element rootElement = document.getRootElement();
        String namespace = rootElement.attributeValue("namespace");

        List<Element> selectList = rootElement.selectNodes("//select");
        List<Element> insertList = rootElement.selectNodes("//insert");
        List<Element> updateList = rootElement.selectNodes("//update");
        List<Element> deleteList = rootElement.selectNodes("//delete");
        List<Element> allList = Lists.newArrayList();
        allList.addAll(selectList);
        allList.addAll(insertList);
        allList.addAll(updateList);
        allList.addAll(deleteList);
        for (Element element : allList) {
            String id = element.attributeValue("id");
            String resultType = element.attributeValue("resultType");
            String parameterType = element.attributeValue("parameterType");
            String sqlText = element.getTextTrim();
            MappedStatement mappedStatement = new MappedStatement();
            mappedStatement.setId(id);
            mappedStatement.setResultType(resultType);
            mappedStatement.setParameterType(parameterType);
            mappedStatement.setSql(sqlText);

            String key = namespace + "." + id;
            configuration.getMappedStatementMap().put(key, mappedStatement);
        }
    }

}
