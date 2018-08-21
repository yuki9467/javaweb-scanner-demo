package com.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

public class AnnotationHandleFilter implements Filter {
	private ServletContext servletContext = null;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.println("---进入注解处理过滤器---");
		// 将ServletRequest强制转换成HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// 初始化方法init（）中注入类的容器
		Map<String, Class<?>> classMap = (Map<String, Class<?>>) servletContext.getAttribute("servletClassMap");
		// 获取contextPath:/javaweb-scanner-demo
		String contextPath = req.getContextPath();
		// 获取用户请求的URI资源:/javaweb-scanner-demo/t1/getId
		String uri = req.getRequestURI();
		// 截取项目名后的uri如有的话：t1/getId
		String reqUri = uri.substring(contextPath.length() + 1);
		// 如果请求了某种带自定义注解的类的方法
		if (!"".equals(reqUri)) {
			// 获取要使用的类
			Class<?> clazz = classMap.get(reqUri);
			// 创建类的实例
			Method[] methods = clazz.getMethods();
			// 循环执行方法
			for (Method method : methods) {
				if (method.getName().contains("get")) {
					try {
						// 创建类的实例
						Object o = clazz.newInstance();
						// 执行方法
						method.invoke(o);
					} catch (InstantiationException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException | InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		// 返回页面
		res.setCharacterEncoding("UTF-8");
		res.setContentType("application/json; charset=utf-8");
		PrintWriter out = null;
		// 返回json对象
		JSONObject json = new JSONObject();
		try {
			// 返回json
			json.put("code", "200");
			json.put("msg", "执行成功");
			out = res.getWriter();
			out.append(json.toString());
		} catch (Exception e) {
			e.printStackTrace();
			res.sendError(500);
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("---AnnotationHandleFilter过滤器初始化开始---");
		servletContext = filterConfig.getServletContext();
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		addServletClassToServletContext(classMap);
		System.out.println("----AnnotationHandleFilter过滤器初始化结束---");
	}

	private void addServletClassToServletContext(Map<String, Class<?>> classMap) {
		try {
			JavaScan.init("");
			Map<String, Class<?>> classes = JavaScan.classes;
			for (Map.Entry<String, Class<?>> entry : classes.entrySet()) {
				String key = entry.getKey();
				Class<?> value = entry.getValue();
				System.out.println("Key = " + key + ", Value = " + value);
				classMap.put(key, value);
				servletContext.setAttribute("servletClassMap", classMap);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
