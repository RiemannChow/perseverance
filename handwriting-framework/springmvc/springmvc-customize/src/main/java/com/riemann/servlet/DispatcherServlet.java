package com.riemann.servlet;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.riemann.annotation.*;
import com.riemann.interceptor.HandlerExecutionChain;
import com.riemann.interceptor.HandlerInterceptor;
import com.riemann.interceptor.SecurityInterceptor;
import com.riemann.pojo.Handler;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DispatcherServlet extends HttpServlet {

    private Properties properties = new Properties();

    private List<String> classNames = Lists.newArrayList(); // 缓存扫描

    private Map<String, Object> ioc = Maps.newHashMap(); // ioc容器

    // handlerMapping
    // private Map<String, Method> handlerMapping = Maps.newHashMap(); // 存储url和method之间的映射关系
    private List<HandlerExecutionChain> handlerMapping = Lists.newArrayList();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        // 1.加载配置文件 springmvc.properties
        String contextConfigLocation = servletConfig.getInitParameter("contextConfigLocation");
        doLoadConfig(contextConfigLocation);

        // 2.扫描相关的类，扫描注解。
        doScan(properties.getProperty("scanPackage"));

        // 3.初始化bean对象（实现ioc容器，基于注解）
        doInstance();

        // 4.实现依赖注入
        doAutowired();

        // 5.构造一个HandlerMapping处理器映射器，将配置好的url和Method建立映射关系
        initHandlerMapping();

        System.out.println("riemann mvc init success...");

        // 6.等待请求进入，处理请求。
    }

    /**
     * 构造一个HandlerMapping处理器映射器
     * 最关键的步骤
     * 目的：将url和method建立关联
     */
    private void initHandlerMapping() {
        if (ioc.isEmpty()) return;
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取ioc容器中当前遍历的对象的class类型
            Class<?> clazz = entry.getValue().getClass();
            if (!clazz.isAnnotationPresent(Controller.class)) continue;

            String baseUrl = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping annotation = clazz.getAnnotation(RequestMapping.class);
                baseUrl = annotation.value(); // 等同于 /riemann
            }

            String[] classSecurityInterceptorValue = {};
            // 如果加了Security注解，则记录该拦截器的值
            if (clazz.isAnnotationPresent(Security.class)) {
                Security annotation = clazz.getAnnotation(Security.class);
                classSecurityInterceptorValue = annotation.value(); // 获取到有权限的username
            }

            // 获取方法
            Method[] methods = clazz.getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                // 方法没有标识RequestMapping，就不处理
                if (!method.isAnnotationPresent(RequestMapping.class)) continue;
                // 如果标识则处理
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                String methodUrl = annotation.value(); // 等同于 /query
                String url = baseUrl + methodUrl; // 计算出来的url /riemann/query

                // 把method所有信息及url封装为一个Handler
                Handler handler = new Handler(entry.getValue(), method, Pattern.compile(url));

                // 计算方法的参数位置信息 // query(HttpServletRequest request, HttpServletResponse response, String name)
                Parameter[] parameters = method.getParameters();
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    if (parameter.getType() == HttpServletRequest.class || parameter.getType() == HttpServletResponse.class) {
                        // 如果是request和response对象，那么参数名称写HttpServletRequest和HttpServletResponse
                        handler.getParamIndexMapping().put(parameter.getType().getSimpleName(), j);
                    } else {
                        handler.getParamIndexMapping().put(parameter.getName(), j); // <name, 2>
                    }
                }

                SecurityInterceptor classSecurityInterceptor = null;
                SecurityInterceptor methodSecurityInterceptor = null;

                if (method.isAnnotationPresent(Security.class)) {
                    Security methodAnnotation = method.getAnnotation(Security.class);
                    String[] usernames = methodAnnotation.value(); // 获取到有权限的username
                    List<String> result = Lists.newArrayList();
                    result.addAll(Arrays.asList(usernames));
                    if (classSecurityInterceptorValue.length > 0) {
                        result.addAll(Arrays.asList(classSecurityInterceptorValue));
                    }
                    methodSecurityInterceptor = new SecurityInterceptor();
                    methodSecurityInterceptor.setHasAuthUsernames(result);
                } else if (classSecurityInterceptorValue.length > 0) {
                    classSecurityInterceptor = new SecurityInterceptor();
                    classSecurityInterceptor.setHasAuthUsernames(Arrays.asList(classSecurityInterceptorValue));
                }

                HandlerExecutionChain handlerExecutionChain = new HandlerExecutionChain(handler);
                // 如果当前handler的拦截器不为空，就给当前handler加入拦截器。
                if (methodSecurityInterceptor != null) {
                    handlerExecutionChain.getHandlerInterceptors().add(methodSecurityInterceptor);
                } else if (classSecurityInterceptor != null) {
                    handlerExecutionChain.getHandlerInterceptors().add(classSecurityInterceptor);
                }

                // 建立url和method之间的映射关系（map缓存起来）
                // handlerMapping.put(url, method);

                handlerMapping.add(handlerExecutionChain);
            }

        }
    }

    /**
     * 实现依赖注入
     */
    private void doAutowired() {
        if (ioc.isEmpty()) return;
        // 有对象，再进行依赖注入处理
        // 遍历ioc中所有对象，查看对象中的字段，是否有@Autowired注解，如果有需要维护依赖注入的关系
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            // 获取bean对象中的字段信息
            Field[] declaredFields = entry.getValue().getClass().getDeclaredFields();
            // 遍历判断处理
            for (int i = 0; i < declaredFields.length; i++) {
                Field declaredField = declaredFields[i]; // @Autowired private RiemannService riemannService;
                if (!declaredField.isAnnotationPresent(Autowired.class)) continue;
                // 有该注解
                Autowired annotation = declaredField.getAnnotation(Autowired.class);
                String beanName = annotation.value(); // 需要注入的bean的id
                if ("".equals(beanName.trim())) {
                    // 没有配置具体的bean id，那就需要根据当前字段类型注入（接口注入）RiemannService
                    beanName = declaredField.getType().getName();
                }
                // 开启赋值
                declaredField.setAccessible(true);
                try {
                    declaredField.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ioc容器
     * 基于className缓存的类的全限定类名，以及反射技术，完成对象创建和管理。
     */
    private void doInstance() {
        if (classNames.size() == 0) return;
        try {
            for (int i = 0; i < classNames.size(); i++) {
                String className = classNames.get(i); // com.riemann.controller.RiemannController
                // 反射
                Class<?> clazz = Class.forName(className);
                // 区分controller，区分service
                if (clazz.isAnnotationPresent(Controller.class)) {
                    // controller的id不做过多处理，不取value了，就拿类的首字母小写作为id,保存到ioc中
                    String simpleName = clazz.getSimpleName(); // RiemannController
                    String lowerLetterSimpleName = lowerLetterFirst(simpleName); // riemannController
                    Object o = clazz.newInstance();
                    ioc.put(lowerLetterSimpleName, o);
                } else if (clazz.isAnnotationPresent(Service.class)) {
                    Service annotation = clazz.getAnnotation(Service.class);
                    // 获取注解的值
                    String beanName = annotation.value();
                    // 如果指定了id,就以指定的为准
                    if (!"".equals(beanName.trim())) {
                        ioc.put(beanName, clazz.newInstance());
                    } else {
                        // 如果没有指定，就以类名首字母小写
                        beanName = lowerLetterFirst(clazz.getSimpleName());
                        ioc.put(beanName, clazz.newInstance());
                    }

                    // service层往往是有接口的，面向接口开发，此时再以接口名为id,放入一份对象到ioc容器中，便于后期根据接口类型注入
                    Class<?>[] interfaces = clazz.getInterfaces();
                    for (int j = 0; j < interfaces.length; j++) {
                        Class<?> anInterface = interfaces[j];
                        // 以接口的全限定类名作为id放入
                        ioc.put(anInterface.getName(), clazz.newInstance());
                    }

                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String lowerLetterFirst(String str) {
        char[] chars = str.toCharArray();
        if ('A' <= chars[0] && chars[0] <= 'Z') {
            chars[0] += 32;
        }
        return String.valueOf(chars);
    }

    /**
     * 扫描类
     * scanPackage:com.riemann ---> 磁盘上的文件夹(File) com/riemann
     * @param scanPackage
     */
    private void doScan(String scanPackage) {
        String scanPackagePath = Thread.currentThread().getContextClassLoader().getResource("").getPath() +
                scanPackage.replaceAll("\\.", "/");
        File packageName = new File(scanPackagePath);
        for (File file : packageName.listFiles()) {
            if (file.isDirectory()) { // 子package
                // 递归
                doScan(scanPackage + "." + file.getName()); // com.riemann.controller
            } else if (file.getName().endsWith(".class")) {
                String className = scanPackage + "." + file.getName().replaceAll(".class", "");
                classNames.add(className);
            }
        }
    }

    /**
     * 加载配置文件
     * @param contextConfigLocation
     */
    private void doLoadConfig(String contextConfigLocation) {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(contextConfigLocation);
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 处理请求,根据url,找到对应的Method方法，进行调用。
        // 获取uri
        // String requestURI = req.getRequestURI();
        // Method method = handlerMapping.get(requestURI); // 获取到一个反射的方法
        // 反射调用,需要传入对象，需要传入参数，此处无法完成调用，没有把对象缓存起来，也没有参数！！！需要改造initHandlerMapping()
        // method.invoke();

        // 根据uri获取到我们能够处理当前请求的handler（从handlerMapping中（List））
        HandlerExecutionChain handlerExecutionChain = getHandlerExecutionChain(req);
        if (handlerExecutionChain == null) {
            resp.getWriter().write("404 not found");
            return;
        }

        Handler handler = handlerExecutionChain.getHandler();
        if(handler == null) {
            resp.getWriter().write("404 not found");
            return;
        }

        // 先执行拦截器
        List<HandlerInterceptor> handlerInterceptors = handlerExecutionChain.getHandlerInterceptors();
        Boolean result = true;
        for(HandlerInterceptor handlerInterceptor : handlerInterceptors) {
            result = handlerInterceptor.preHandle(req, resp);
            if (result == false) {
                break;
            }
        }

        if (result == false) {
            resp.getWriter().write("403 Forbidden");
            return;
        }

        // 参数绑定
        // 获取所有参数类型数组，这个数组的长度就是我们最后要传入的args数组的长度
        Class<?>[] parameterTypes = handler.getMethod().getParameterTypes();

        // 根据上述数组长度创建一个新的数组（参数数组，是要传入反射调用的）
        Object[] paramValues = new Object[parameterTypes.length];

        // 以下就是为了向参数数组中塞值，而且还得保证参数的顺序和方法中形参顺序一致
        Map<String, String[]> parameterMap = req.getParameterMap();

        // 遍历request中所有参数（填充除了request、response之外的）
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            // name=1&name=2 name [1,2]
            String value = StringUtils.join(param.getValue(), ","); // 如同 1，2

            // 如果参数和方法中的参数匹配上了，填充数据。
            if (!handler.getParamIndexMapping().containsKey(param.getKey())) continue;

            // 方法形参确实有该参数，找到它的索引位置，对应的把参数值放入paramValues
            Integer index = handler.getParamIndexMapping().get(param.getKey()); // name在第2个位置

            paramValues[index] = value; // 把前台传递过来的参数值填充到对应的位置去
        }

        int requestIndex = handler.getParamIndexMapping().get(HttpServletRequest.class.getSimpleName()); // 0
        paramValues[requestIndex] = req;

        int responseIndex = handler.getParamIndexMapping().get(HttpServletResponse.class.getSimpleName()); // 1
        paramValues[responseIndex] = resp;


        // 最终调用handler的method属性
        try {
            handler.getMethod().invoke(handler.getController(), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private HandlerExecutionChain getHandlerExecutionChain(HttpServletRequest req) {
        if(handlerMapping.isEmpty()) return null;
        String url = req.getRequestURI();

        for(HandlerExecutionChain handlerExecutionChain: handlerMapping) {
            Matcher matcher = handlerExecutionChain.getHandler().getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handlerExecutionChain;
        }
        return null;
    }

}
