package com.riemann.factory;

import com.riemann.annotation.Autowired;
import com.riemann.annotation.Service;
import com.riemann.annotation.Transactional;
import com.riemann.utils.ClassUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注解工厂类
 * 用于创建并维护bean对象
 */
public class AnnotationBeanFactory {

    public static ConcurrentHashMap<String, Object> beans = new ConcurrentHashMap<>();

    private String pagekageName;

    static {
        Properties pros = readPropertiesFile();
        String value = pros.getProperty("packageName");

        // 扫描指定包下的所有类
        Set<Class<?>> classSet = ClassUtils.getClasses(value, true);

        try {
            // 查找所有使用@Service注解标签类，并实例化
            findServiceAnnotation(classSet);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 遍历注入对象
        for (String key : beans.keySet()) {
            try {
                attributeAssign(beans.get(key));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // 查找所有使用@Transactional注解，并实例化
        try {
            findTransactionalAnnotation(classSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查找@Service 注解类并实例化对象到map中
     *
     * @param classes
     * @return
     * @throws Exception
     */
    public static ConcurrentHashMap<String, Object> findServiceAnnotation(Set<Class<?>> classes) throws Exception {
        for (Class<?> clazz : classes) {
            Service annotation = clazz.getAnnotation(Service.class);
            if (annotation != null) {
                Object obj = clazz.newInstance();
                // key为接口类全限定类名称
                if (clazz.getInterfaces() != null && clazz.getInterfaces().length > 0) {
                    String facesName = clazz.getInterfaces()[0].getName();
                    beans.put(facesName, obj);
                }
                // key为类名首字母小写
                if (annotation.value() == null || "".equals(annotation.value())) {
                    String beanId = ClassUtils.toLowerCaseFirstOne(clazz.getSimpleName());
                    beans.put(beanId, obj);
                }
                //key为设定值
                else {
                    beans.put(annotation.value(), obj);
                }
                continue;
            }
        }
        return beans;
    }

    /**
     * 依赖注入，为使用@Autowired注解标签的属性注入bean对象
     *
     * @param obj
     * @throws IllegalAccessException
     */
    public static void attributeAssign(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                String fieldName = field.getName();
                // 获取到依赖注入的bean
                Object bean = beans.get(fieldName);
                if (bean == null) {
                    bean = beans.get(field.getType().getName());
                }

                if (bean != null) {
                    field.setAccessible(true);
                    field.set(obj, bean);
                }
            }
        }
    }


    /**
     * 查找@Transactional 注解类并实例化对象到map中
     *
     * @param classes
     * @return
     * @throws Exception
     */
    public static ConcurrentHashMap<String, Object> findTransactionalAnnotation(Set<Class<?>> classes) throws Exception {
        beans.entrySet().forEach(x -> {
            String key = x.getKey();
            Object obj = x.getValue();
            // 判断类是否是事务
            Transactional transactional = obj.getClass().getAnnotation(Transactional.class);
            Method[] methods = obj.getClass().getDeclaredMethods();
            boolean isTranMethods = Arrays.stream(methods).anyMatch(y -> y.isAnnotationPresent(Transactional.class));

            if (transactional != null || isTranMethods) {
                ProxyFactory proxyFactory = (ProxyFactory) beans.get(ClassUtils.toLowerCaseFirstOne(ProxyFactory.class.getSimpleName()));
                // 实现接口的代理类，使用jdk方式
                if (obj.getClass().getInterfaces().length > 0) {
                    obj = proxyFactory.getJdkProxy(obj);
                }
                // 非实现接口的代理类，使用cglib
                else {
                    obj = proxyFactory.getCglibProxy(obj);
                }
                beans.put(key, obj);
            }
        });
        return beans;
    }

    /**
     * 通过配置文件名读取内容
     *
     * @return
     */
    private static Properties readPropertiesFile() {
        Properties pros = new Properties();
        try {
            InputStream resourceAsStream = AnnotationBeanFactory.class.getClassLoader().getResourceAsStream("application.properties");
            pros.load(new InputStreamReader(resourceAsStream, "UTF-8"));
            return pros;
        } catch (Exception e) {
            System.out.println("————读取配置文件出现异常，读取失败————");
            e.printStackTrace();
        }
        return null;
    }

    public static Object getBean(String id) {
        return beans.get(id);
    }
}