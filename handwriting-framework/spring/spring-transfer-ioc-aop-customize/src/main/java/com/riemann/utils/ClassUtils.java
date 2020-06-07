package com.riemann.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class类工具
 *
 */
public class ClassUtils {

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @return a boolean.
     */
    public static boolean hasClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return getClassAnnotation(clazz, annotationClass) != null;
    }

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @param fieldName
     *            a {@link java.lang.String} object.
     * @throws
     *             if any.
     * @return a boolean.
     */
    public static boolean hasFieldAnnotation(Class<?> clazz,
                                             Class<? extends Annotation> annotationClass, String fieldName) throws Exception {
        return getFieldAnnotation(clazz, annotationClass, fieldName) != null;
    }

    /**
     * 是否有注解
     *
     * @param clazz
     *            a {@link java.lang.Class} object.
     * @param annotationClass
     *            a {@link java.lang.Class} object.
     * @param methodName
     *            a {@link java.lang.String} object.
     * @param paramType
     *            a {@link java.lang.Class} object.
     * @throws
     *             if any.
     * @return a boolean.
     */
    public static boolean hasMethodAnnotation(Class<?> clazz,
                                              Class<? extends Annotation> annotationClass, String methodName, Class<?>... paramType) throws Exception {
        return getMethodAnnotation(clazz, annotationClass, methodName, paramType) != null;
    }

    /**
     * 获取类注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @return a A object.
     */
    public static <A extends Annotation> A getClassAnnotation(Class<?> clazz, Class<A> annotationClass) {
        return clazz.getAnnotation(annotationClass);
    }

    /**
     * 获取类成员注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @param fieldName
     *            成员属性名
     * @throws
     *             if any.
     * @return a A object.
     */
    public static <A extends Annotation> A getFieldAnnotation(Class<?> clazz,
                                                              Class<A> annotationClass, String fieldName) throws Exception {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            if (field == null) {
                throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
            }
            return field.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: field[" + fieldName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            throw new Exception("no such field[" + fieldName + "] in " + clazz.getCanonicalName());
        }
    }

    /**
     * 获取类方法上的注解
     *
     * @param clazz
     *            类
     * @param annotationClass
     *            注解类
     * @param methodName
     *            方法名
     * @param paramType
     *            方法参数
     * @throws
     *             if any.
     * @return a A object.
     */
    public static <A extends Annotation> A getMethodAnnotation(Class<?> clazz,
                                                               Class<A> annotationClass, String methodName, Class<?>... paramType)
            throws Exception {
        try {
            Method method = clazz.getDeclaredMethod(methodName, paramType);
            if (method == null) {
                throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName());
            }
            return method.getAnnotation(annotationClass);
        } catch (SecurityException e) {
            e.printStackTrace();
            throw new Exception("access error: method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new Exception("no such method[" + methodName + "] in " + clazz.getCanonicalName(), e);
        }
    }

    /**
     * 从包package中获取所有的Class
     *
     * @param pagekageName
     *            包名
     * @param recursive
     *            是否递归
     * @return a {@link java.util.Set} object.
     */
    public static Set<Class<?>> getClasses(String pagekageName, boolean recursive) {
        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        // 获取包的名字 并进行替换
        String packageName = pagekageName;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx)
                                            .replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                if ((idx != -1) || recursive) {
                                    // 如果是一个.class文件 而且不是目录
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        // 去掉后面的".class" 获取真正的类名
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                                        try {
                                            // 添加到classes
                                            classes.add(Class.forName(packageName + '.' + className));
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                            // log
                                            // .error("添加用户自定义视图类错误 找不到此类的.class文件");
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     *            a {@link java.lang.String} object.
     * @param packagePath
     *            a {@link java.lang.String} object.
     * @param recursive
     *            a boolean.
     * @param classes
     *            a {@link java.util.Set} object.
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive, classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * <p>
     * Description：给一个接口，返回这个接口同一个包下的所有实现类
     * </p>
     *
     * @param c
     *            a {@link java.lang.Class} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Class<?>> getAllClassByInterface(Class<?> c) {
        List<Class<?>> returnClassList = new ArrayList<Class<?>>(); // 返回结果
        // 如果不是一个接口，则不做处理
        if (!c.isInterface()) {
            return returnClassList;
        }
        String packageName = c.getPackage().getName(); // 获得当前的包名
        Set<Class<?>> allClass = getClasses(packageName, true); // 获得当前包下以及子包下的所有类
        // 判断是否是同一个接口
        for (Class<?> clazz : allClass) {
            if (c.isAssignableFrom(clazz)) { // 判断是不是一个接口
                if (!c.equals(clazz)) { // 本身不加进去
                    returnClassList.add(clazz);
                }
            }
        }
        return returnClassList;
    }

    public static String toLowerCaseFirstOne(String s) {
        if (Character.isLowerCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
    }

}