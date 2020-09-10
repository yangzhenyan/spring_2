package com.yang.spring;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * @author yzy
 * @date 2020/9/7
 * @describe 用来读取对象定义配置文件
 */
public class YBeanDefinitionReader {

    private Properties properties = new Properties();
    //存放全类名
    private List<String> registryBeanClasses = new ArrayList<>();

    public YBeanDefinitionReader(String ... configLocations) {
        doReadConfig(configLocations[0]);
        doScanner(properties.getProperty("scanPackage").replace(".","/"));
    }

    public List<YBeanDefinition> doLoadBeanDefinitions() {
        List<YBeanDefinition> beanDefinitionList = new ArrayList<>();
        for (String registryBeanName : registryBeanClasses){
            try {
                //获取类加载器
                Class<?> aClass = Class.forName(registryBeanName);
                //是接口就不注入
                if (aClass.isInterface()){
                    continue;
                }
                //封装YBeanDefinition对象
                YBeanDefinition beanDefinition = doCreateBeanDefinition(toLowerLetters(aClass.getSimpleName()), registryBeanName);
                beanDefinitionList.add(beanDefinition);
                //获得这个对象实现的所有接口 将其接口也放入
                for (Class<?> inter : aClass.getInterfaces()) {
//                    beanDefinition = doCreateBeanDefinition(toLowerLetters(inter.getSimpleName()), registryBeanName);
                    beanDefinition = doCreateBeanDefinition(inter.getName(), registryBeanName);
                    beanDefinitionList.add(beanDefinition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return beanDefinitionList;
    }

    private YBeanDefinition doCreateBeanDefinition(String factoryBeanName,String beanClassName) {
        YBeanDefinition beanDefinition = new YBeanDefinition();
        beanDefinition.setFactoryBeanName(factoryBeanName);
        beanDefinition.setBeanClassName(beanClassName);
        return beanDefinition;
    }

    //1、读取配置文件
    private void doReadConfig(String configLocation) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(configLocation);
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //2、扫描包路径下的类
    private void doScanner(String scanPackage) {

        URL url = getClass().getClassLoader().getResource("/" + scanPackage);
        //url.getFile() 获取此文件名URL
        File filePath = new File(url.getFile());
        File[] files = filePath.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //如果是文件夹 则在该文件夹的路径下递归扫描
                String s = scanPackage + "/" + file.getName();
                doScanner(s);
            }
            //再判断是不是以.class结尾的文件
            if (file.getName().endsWith(".class")) {
                //拼接全类名 去除.class后缀
                String className = scanPackage.replace("/", ".") + "." + file.getName().replace(".class", "");
                registryBeanClasses.add(className);
            }
        }
    }

    //获取对象名并把首字母转为小写
    private String toLowerLetters(String simpleName) {
        String letters = simpleName.substring(0, 1).toLowerCase();
        return letters + simpleName.substring(1);
    }
}
