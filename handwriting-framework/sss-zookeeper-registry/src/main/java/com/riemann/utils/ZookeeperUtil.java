package com.riemann.utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.riemann.pojo.DataBaseConfig;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

@Component
public class ZookeeperUtil {

    public static String url = "127.0.0.1";
    public static String ZK_PATH = "/database-config";
    private static ZkClient zkClient;
    static {
        zkClient = new ZkClient("47.113.82.141:2180");
        if (!zkClient.exists(ZK_PATH)){
            zkClient.createPersistent(ZK_PATH);
        }
        watchDataBaseConfigChange();
    }

    // 读取数据库配置信息
    public static DataBaseConfig getDataBaseConfig() {
        if (!zkClient.exists(ZK_PATH)) {
            return null;
        } else {
            return zkClient.readData(ZK_PATH);
        }
    }

    // 监听数据库配置信息变化
    public static void watchDataBaseConfigChange() {
        zkClient.subscribeDataChanges(ZK_PATH, new IZkDataListener() {
            @Override
            public void handleDataChange(String s, Object object) throws Exception {
                DataBaseConfig dataBaseConfig = (DataBaseConfig) object;
                System.out.println("数据源变更 " + dataBaseConfig);
                DruidDataSource dataSource = ApplicationContextUtil.getBean(DruidDataSource.class);
                if (dataSource.isInited()) {
                    // 先关闭，再重启
                    dataSource.close();
                    dataSource.restart();
                }
                // 设置新的数据库
                dataSource.setUrl(dataBaseConfig.getUrl());
                dataSource.setUsername(dataBaseConfig.getUsername());
                dataSource.setPassword(dataBaseConfig.getPassword());
                dataSource.setDriverClassName(dataBaseConfig.getDriver());
                // 初始化数据源
                dataSource.init();
            }

            @Override
            public void handleDataDeleted(String s) throws Exception {

            }
        });
    }

    public static void main(String[] args) {
        setDataBaseConfig();
    }

    // 设置数据信息
    public static void setDataBaseConfig() {
        DataBaseConfig dataBaseConfig = new DataBaseConfig();
        dataBaseConfig.setDriver("com.mysql.jdbc.Driver");
        dataBaseConfig.setUrl("jdbc:mysql://localhost:3306/mysql?characterEncoding=utf8&useSSL=false");
        dataBaseConfig.setUsername("root");
        dataBaseConfig.setPassword("root");

        if (!zkClient.exists(ZK_PATH)) {
            zkClient.createPersistent(ZK_PATH);
        } else {
            zkClient.writeData(ZK_PATH, dataBaseConfig);
        }
    }

}
