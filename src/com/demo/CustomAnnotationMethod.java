package com.demo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 */
@Retention(RetentionPolicy.RUNTIME) /** 这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用. */
@Target(ElementType.METHOD) /** 此注解应用于 方法上. */
@Documented /** 注解表明这个注解应该被 javadoc工具记录. */
public @interface CustomAnnotationMethod {
	 /**
     * 描述
     */
    String description() default "";

    /**
     * 访问路径
     *
     * @return
     */
    String uri();
}
