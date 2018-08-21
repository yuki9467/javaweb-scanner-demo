package com.demo;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 扫描类
 * 
 * @author Administrator
 *
 */
public class JavaScan {
	/**
	 * 存放 文件根路径
	 */
	static String classPath;

	/**
	 * 存放结果
	 */
	static List<String> classPathList = new ArrayList<>();

	/**
	 * 使用类全名定义的bean容器
	 */
	static Map<String, Class<?>> beans = new HashMap<>();

	/**
	 * 使用方法的注解的url属性定义的classes容器
	 */
	static Map<String, Class<?>> classes = new HashMap<>();

	/**
	 * method 容器
	 */
	static Map<String, Method> methods = new HashMap<>();

	/**
	 * 初始化
	 *
	 * @return
	 * @throws Exception
	 */
	public static void init(String path) throws Exception {
		if (null == path)
			throw new NullPointerException("JavaScan.init(String path) 参数不能为null");
		// 初始化 获取项目的 classPath 路径，File.separator：\
		classPath = new File(getRootPath()).getPath() + File.separator;
		// 总结：String classPathNoseparator = new File(rootPath).getPath()
		// 结果：G:\woorkspace\servletdemo\javaweb-servlet-demo\build\classes
		// 使用 IO扫描 指定路径下的所有文件
		getFileName(classPath + path);
		// 使用 所有类命名字符串来 初始化容器
		initContainer();
	}

	/**
	 * 获取rootPath的相关的根路径<br>
	 * getResources("") 如果放为空串儿，那么就是获取rootPath的相关的根路径
	 *
	 * @return rootPath的相关的根路径
	 * @throws Exception
	 */
	private static String getRootPath() throws Exception {
		// 注： main方法启动 获取路径方式
//		Enumeration<URL> resources = JavaScan.class.getClassLoader().getResources("");
		// 注： servlet启动 获取路径方式
		Enumeration<URL> resources = JavaScan.class.getClassLoader().getResources("/");
		URL url = resources.nextElement();
		return url.getPath();
		// 总结：String rootPath = this.getClass().getClassLoader().getResources("").nextElement().getPath()
		// 结果：/G:/woorkspace/servletdemo/javaweb-servlet-demo/build/classes/
	}

	/**
	 * 使用 IO扫描 Class文件
	 */
	private static void getFileName(String rootPath) {
		File file = new File(rootPath);
		// 获取所有文件和文件夹
		File[] fileList = file.listFiles();
		for (int i = 0; null != fileList && i < fileList.length; i++) {
			String path = fileList[i].getPath();
			// 如果是目录
			if (fileList[i].isDirectory()) {
				// 继续递归
				getFileName(path);
			}
			if (fileList[i].isFile()) {
				// 输出 所有文件夹下的全路径
				// System.out.println(path);
				// 拼接类路径保存到集合中，(类路径所指的是 去掉根路径以外的项目中 class的全路径)
				// path.replace(fileRootPath, "") 去除根路径
				// .replaceAll(".class", "") 去掉后缀名
				// .replaceAll(File.separator + File.separator, ".") 将斜杠'\或/'转成
				// 点'.'
				String classpath = path.replace(classPath, "").replaceAll(".class", "")
						.replaceAll(File.separator + File.separator, ".");
				classPathList.add(classpath);
			}
		}
	}

	/**
	 * 使用 所有类全命名来 初始化容器
	 *
	 * @throws Exception
	 */
	private static void initContainer() throws Exception {

		if (null == classPathList || classPathList.size() <= 0)
			throw new Exception("文件路径不存在！" + JavaScan.class.getName());

		// 获取 所有类的类全名
		for (int i = 0; i < classPathList.size(); i++) {

			String className = classPathList.get(i);
			Class<?> forName = Class.forName(className);

			// 初始化限制，初始化的文件类型必须是 class文件
			if (!forName.isAnnotation() && !forName.isEnum() && !forName.isInterface()) {

				// 只初始化 实现了CustomAnnotationBean注解的类
				if (forName.isAnnotationPresent(CustomAnnotationBean.class)) {
					// 初始化类对象 添加到容器中
					if (!beans.containsKey(className))
						beans.put(className, forName);
				}

				// 只初始化 实现了CustomAnnotationBean注解的类中的方法
				Method[] methodArray = forName.getDeclaredMethods();
				for (Method method : methodArray) {
					// 初始化 实现了CustomAnnotationMethod注解的方法
					if (method.isAnnotationPresent(CustomAnnotationMethod.class)) {
						// 获取注解
						CustomAnnotationMethod annotation = method.getAnnotation(CustomAnnotationMethod.class);
						// 获取注解的属性
						String attr = annotation.uri();
						if (!methods.containsKey(attr)) {
							// 初始化方法 添加到容器中
							methods.put(attr, method);
							// 将此方法对应的类 添加到容器中
							classes.put(attr, forName);
						}
					}
				}
			}
		}
	}

	/**
	 * 执行 method
	 *
	 * @param url
	 * @return
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	public Object executeMethod(String url, Object... args)
			throws InvocationTargetException, IllegalAccessException, IllegalArgumentException, InstantiationException {
		if (null == url || "".equals(url))
			throw new NullPointerException("ApiPool.executeMethod(String url)：参数不能为null");
		return methods.get(url).invoke(classes.get(url).newInstance(), args);
	}

	/**
	 * 获取 使用类全名定义的bean容器
	 *
	 * @return
	 */
	public Map<String, Class<?>> getBeans() {

		return beans;
	}

	/**
	 * 获取 使用类全名定义的bean
	 *
	 * @return
	 */
	public Class<?> getBean(String key) {

		return beans.get(key);
	}

	/**
	 * 获取 使用方法的注解的url属性定义的classes容器
	 *
	 * @return
	 */
	public Map<String, Class<?>> getClazzs() {

		return classes;
	}

	/**
	 * 获取 使用方法的注解的url属性定义的classes
	 *
	 * @return
	 */
	public Class<?> getClazz(String key) {

		return classes.get(key);
	}

	/**
	 * 获取Method容器
	 *
	 * @return
	 */
	public Map<String, Method> getMethods() {

		return methods;
	}

	/**
	 * 获取Method
	 *
	 * @return
	 */
	public Method getMethod(String key) {

		return methods.get(key);
	}

	/**
	 * 测试
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		JavaScan javaScan = new JavaScan();
		// 在main 方法中调用传空串就可以
		javaScan.init("");

		for (String key : javaScan.getBeans().keySet()) {
			Object object = javaScan.getBean(key);
			System.out.println(object);
		}
	}
}
