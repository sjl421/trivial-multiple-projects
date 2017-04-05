package com.xxx.ioc;

import com.xxx.ioc.bean.BeanDefinition;
import com.xxx.ioc.bean.BeanReader;
import com.xxx.ioc.bean.MyBean;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dhy on 17-4-5.
 *
 */
public class ApplicationContext {

    private Map<String, BeanDefinition> beans = new HashMap<>();
    private BeanReader reader = new BeanReader();

    public ApplicationContext(String basePackage) {
        findInPackage(basePackage);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String beanName ,Class<T> clazz) {
        BeanDefinition beanDefinition = beans.get(beanName);
        return (T) beanDefinition.getInstance();
    }

    private void findInPackage(String basePackage) {
        String basePath = basePackage.replace(PKG_SEPARATOR, FILE_SEPARATOR);
        URL packageUrl = ClassLoader.getSystemResource(basePath);
        if (packageUrl == null) {
            return;
        }
        File root = new File(packageUrl.getFile());
        if (root.isDirectory()) {
            File[] files = root.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                findClass(f, basePackage);
            }
        }
    }

    private void findClass(File root, String scannedPackage) {
        String resource = scannedPackage + PKG_SEPARATOR + root.getName();
        int index = resource.indexOf(CLASS_SUFFIX);
        if (resource.endsWith(CLASS_SUFFIX)) {
            resource = resource.substring(0, index);
            try {
                Class<?> clazz = Class.forName(resource);
                if (clazz.isAnnotationPresent(MyBean.class)) {
                    BeanDefinition beanDefinition = reader.create(clazz, Collections.emptyMap());
                    beans.put(clazz.getName(), beanDefinition);
                    System.out.println(clazz.getName());
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            findInPackage(resource);
        }
    }

    private final static String PKG_SEPARATOR = ".";
    private final static String FILE_SEPARATOR = "/";
    private final static String CLASS_SUFFIX = ".class";

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext("com.xxx.ioc");
    }
}
