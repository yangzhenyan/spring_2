package com.yang.spring;

/**
 * @author yzy
 * @date 2020/9/7
 * @describe 对原生类以及代理类的封装
 */
public class YBeanWrapper {

    private Object wrapperInstance;

    private Class<?> wrapperClass;

    public YBeanWrapper() {
    }

    public YBeanWrapper(Object wrapperInstance) {
        this.wrapperInstance = wrapperInstance;
        this.wrapperClass = wrapperInstance.getClass();
    }

    public Object getWrapperInstance() {
        return wrapperInstance;
    }

    public Class<?> getWrapperClass() {
        return wrapperClass;
    }
}
