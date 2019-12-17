package org.tinygame.herostory.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * 名称空间实用工具
 */
public final class PackageUtil {
    /**
     * 类默认构造器
     */
    private PackageUtil() {
    }

    /**
     * 列表指定包中的所有子类
     *
     * @param packageName 包名称
     * @param recursive   是否递归查找
     * @param superClazz  父类的类型
     * @return 子类集合
     */
    static public Set<Class<?>> listSubClazz(
        String packageName,
        boolean recursive,
        Class<?> superClazz) {
        if (superClazz == null) {
            return Collections.emptySet();
        } else {
            return listClazz(packageName, recursive, superClazz::isAssignableFrom);
        }
    }

    /**
     * 列表指定包中的所有类
     *
     * @param packageName 包名称
     * @param recursive   是否递归查找?
     * @param filter      过滤器
     * @return 符合条件的类集合
     */
    static public Set<Class<?>> listClazz(
        String packageName, boolean recursive, IClazzFilter filter) {

        if (packageName == null ||
            packageName.isEmpty()) {
            return null;
        }

        // 将点转换成斜杠
        final String packagePath = packageName.replace('.', '/');
        // 获取类加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        // 结果集合
        Set<Class<?>> resultSet = new HashSet<>();

        try {
            // 获取 URL 枚举
            Enumeration<URL> urlEnum = cl.getResources(packagePath);

            while (urlEnum.hasMoreElements()) {
                // 获取当前 URL
                URL currUrl = urlEnum.nextElement();
                // 获取协议文本
                final String protocol = currUrl.getProtocol();
                // 定义临时集合
                Set<Class<?>> tmpSet = null;

                if ("FILE".equalsIgnoreCase(protocol)) {
                    // 从文件系统中加载类
                    tmpSet = listClazzFromDir(
                        new File(currUrl.getFile()), packageName, recursive, filter
                    );
                } else if ("JAR".equalsIgnoreCase(protocol)) {
                    // 获取文件字符串
                    String fileStr = currUrl.getFile();

                    if (fileStr.startsWith("file:")) {
                        // 如果是以 "file:" 开头的,
                        // 则去除这个开头
                        fileStr = fileStr.substring(5);
                    }

                    if (fileStr.lastIndexOf('!') > 0) {
                        // 如果有 '!' 字符,
                        // 则截断 '!' 字符之后的所有字符
                        fileStr = fileStr.substring(0, fileStr.lastIndexOf('!'));
                    }

                    // 从 JAR 文件中加载类
                    tmpSet = listClazzFromJar(
                        new File(fileStr), packageName, recursive, filter
                    );
                }

                if (tmpSet != null) {
                    // 如果类集合不为空,
                    // 则添加到结果中
                    resultSet.addAll(tmpSet);
                }
            }
        } catch (Exception ex) {
            // 抛出异常!
            throw new RuntimeException(ex);
        }

        return resultSet;
    }

    /**
     * 从目录中获取类列表
     *
     * @param dirFile     目录
     * @param packageName 包名称
     * @param recursive   是否递归查询子包
     * @param filter      类过滤器
     * @return 符合条件的类集合
     */
    static private Set<Class<?>> listClazzFromDir(
        final File dirFile, final String packageName, final boolean recursive, IClazzFilter filter) {

        if (!dirFile.exists() ||
            !dirFile.isDirectory()) {
            // 如果参数对象为空,
            // 则直接退出!
            return null;
        }

        // 获取子文件列表
        File[] subFileArr = dirFile.listFiles();

        if (subFileArr == null ||
            subFileArr.length <= 0) {
            return null;
        }

        // 文件队列, 将子文件列表添加到队列
        Queue<File> fileQ = new LinkedList<>(Arrays.asList(subFileArr));

        // 结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        while (!fileQ.isEmpty()) {
            // 从队列中获取文件
            File currFile = fileQ.poll();

            if (currFile.isDirectory() &&
                recursive) {
                // 如果当前文件是目录,
                // 并且是执行递归操作时,
                // 获取子文件列表
                subFileArr = currFile.listFiles();

                if (subFileArr != null &&
                    subFileArr.length > 0) {
                    // 添加文件到队列
                    fileQ.addAll(Arrays.asList(subFileArr));
                }
                continue;
            }

            if (!currFile.isFile() ||
                !currFile.getName().endsWith(".class")) {
                // 如果当前文件不是文件,
                // 或者文件名不是以 .class 结尾,
                // 则直接跳过
                continue;
            }

            // 类名称
            String clazzName;

            // 设置类名称
            clazzName = currFile.getAbsolutePath();
            // 清除最后的 .class 结尾
            clazzName = clazzName.substring(dirFile.getAbsolutePath().length(), clazzName.lastIndexOf('.'));
            // 转换目录斜杠
            clazzName = clazzName.replace('\\', '/');
            // 清除开头的 /
            clazzName = trimLeft(clazzName, "/");
            // 将所有的 / 修改为 .
            clazzName = join(clazzName.split("/"), ".");
            // 包名 + 类名
            clazzName = packageName + "." + clazzName;

            try {
                // 加载类定义
                Class<?> clazzObj = Class.forName(clazzName);

                if (null != filter &&
                    !filter.accept(clazzObj)) {
                    // 如果过滤器不为空,
                    // 且过滤器不接受当前类,
                    // 则直接跳过!
                    continue;
                }

                // 添加类定义到集合
                resultSet.add(clazzObj);
            } catch (Exception ex) {
                // 抛出异常
                throw new RuntimeException(ex);
            }
        }

        return resultSet;
    }

    /**
     * 从 .jar 文件中获取类列表
     *
     * @param jarFilePath .jar 文件路径
     * @param recursive   是否递归查询子包
     * @param filter      类过滤器
     * @return 符合条件的类集合
     */
    static private Set<Class<?>> listClazzFromJar(
        final File jarFilePath, final String packageName, final boolean recursive, IClazzFilter filter) {

        if (jarFilePath == null ||
            jarFilePath.isDirectory()) {
            // 如果参数对象为空,
            // 则直接退出!
            return null;
        }

        // 结果对象
        Set<Class<?>> resultSet = new HashSet<>();

        try {
            // 创建 .jar 文件读入流
            JarInputStream jarIn = new JarInputStream(new FileInputStream(jarFilePath));
            // 进入点
            JarEntry entry;

            while ((entry = jarIn.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }

                // 获取进入点名称
                String entryName = entry.getName();

                if (!entryName.endsWith(".class")) {
                    // 如果不是以 .class 结尾,
                    // 则说明不是 JAVA 类文件, 直接跳过!
                    continue;
                }

                if (!recursive) {
                    //
                    // 如果没有开启递归模式,
                    // 那么就需要判断当前 .class 文件是否在指定目录下?
                    //
                    // 获取目录名称
                    String tmpStr = entryName.substring(0, entryName.lastIndexOf('/'));
                    // 将目录中的 "/" 全部替换成 "."
                    tmpStr = join(tmpStr.split("/"), ".");

                    if (!packageName.equals(tmpStr)) {
                        // 如果包名和目录名不相等,
                        // 则直接跳过!
                        continue;
                    }
                }

                String clazzName;

                // 清除最后的 .class 结尾
                clazzName = entryName.substring(0, entryName.lastIndexOf('.'));
                // 将所有的 / 修改为 .
                clazzName = join(clazzName.split("/"), ".");

                // 加载类定义
                Class<?> clazzObj = Class.forName(clazzName);

                if (null != filter &&
                    !filter.accept(clazzObj)) {
                    // 如果过滤器不为空,
                    // 且过滤器不接受当前类,
                    // 则直接跳过!
                    continue;
                }

                // 添加类定义到集合
                resultSet.add(clazzObj);
            }

            // 关闭 jar 输入流
            jarIn.close();
        } catch (Exception ex) {
            // 抛出异常
            throw new RuntimeException(ex);
        }

        return resultSet;
    }

    /**
     * 类名称过滤器
     *
     * @author hjj2019
     */
    @FunctionalInterface
    static public interface IClazzFilter {
        /**
         * 是否接受当前类?
         *
         * @param clazz 被筛选的类
         * @return 是否符合条件
         */
        boolean accept(Class<?> clazz);
    }

    /**
     * 使用连接符连接字符串数组
     *
     * @param strArr 字符串数组
     * @param conn   连接符
     * @return 连接后的字符串
     */
    static private String join(String[] strArr, String conn) {
        if (null == strArr ||
            strArr.length <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < strArr.length; i++) {
            if (i > 0) {
                // 添加连接符
                sb.append(conn);
            }

            // 添加字符串
            sb.append(strArr[i]);
        }

        return sb.toString();
    }

    /**
     * 清除源字符串左边的字符串
     *
     * @param src     原字符串
     * @param trimStr 需要被清除的字符串
     * @return 清除后的字符串
     */
    static private String trimLeft(String src, String trimStr) {
        if (null == src ||
            src.isEmpty()) {
            return "";
        }

        if (null == trimStr ||
            trimStr.isEmpty()) {
            return src;
        }

        if (src.equals(trimStr)) {
            return "";
        }

        while (src.startsWith(trimStr)) {
            src = src.substring(trimStr.length());
        }

        return src;
    }
}
