package com.yang.spring;

import com.yang.annotation.YController;
import com.yang.annotation.YRequestMapping;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author yzy
 * @date 2020/8/23
 * @describe
 */
public class YzyDispatcherServlet extends HttpServlet {

    //handleMapping集合
    private Map<String, Method> handleMap = new HashMap<>();


    private YApplicationContext context;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //7、执行相关方法
        try {
            String requestURI = req.getRequestURI();
            //获取项目名 "/项目名"
            String contextPath = req.getContextPath();
            //获取请求的uri
            String uri = requestURI.replace(contextPath, "");

            //如果请求的uri不包含在handleMapping集合
            if (!handleMap.containsKey(uri)) {
                resp.getWriter().write("404 Not Found !!");
                return;
            }
            Method method = handleMap.get(uri);
            Map<String, String[]> params = req.getParameterMap();
                method.invoke(context.getBean(method.getDeclaringClass()),new Object[]{req,resp,params.get("name")[0],params.get("id")[0]});
        } catch (Exception e) {
            resp.getWriter().write("500 Internal Server Exception !!");
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        /*---------------------------------IoC------------------------------*/

        //读取配置文件
        context = new YApplicationContext(config.getInitParameter("myConfigLocation"));
        /*----------------------------------DI------------------------------*/
        //5、DI 扫描容器中对象 对其属性赋值

        /*----------------------------------MVC-----------------------------*/
        //6、初始化handleMapping 将url和method一一映射
        doHandleMapping();

        System.out.println("Spring Framework init......");
    }


    //6、初始化handleMapping 将url和method一一映射
    private void doHandleMapping() {
        if (context.getBeanDefinitionCount() == 0) {
            return;
        }
        String[] beanNames = context.getBeanDefinitionNames();
        for (String beanName : beanNames) {
            Object bean = context.getBean(beanName);

            Class<?> aClass = bean.getClass();
            if (!aClass.isAnnotationPresent(YController.class)) {
                continue;
            }
            //判断类路径上是否有@YRequestMapping注解 有就获取
            String baseUrl = null;
            if (aClass.isAnnotationPresent(YRequestMapping.class)) {
                baseUrl = aClass.getAnnotation(YRequestMapping.class).value();
            }
            //判断方法上@YRequestMapping注解
            Method[] methods = aClass.getMethods();
            String methodUrl = null;
            for (Method method : methods) {
                if (method.isAnnotationPresent(YRequestMapping.class)) {
                    methodUrl = method.getAnnotation(YRequestMapping.class).value();
                    String uri = "/" + baseUrl + "/" + methodUrl;
                    //url method放入集合
                    handleMap.put(uri, method);
                }
            }

        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
