package com.demo;

/**
 * 自定义注解
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) /** 这种类型的Annotations将被JVM保留,所以他们能在运行时被JVM或其他使用反射机制的代码所读取和使用. */
@Target(ElementType.TYPE) /** 此注解应用于 类上. */
@Documented /** 注解表明这个注解应该被 javadoc工具记录. */
public @interface CustomAnnotationBean {
	/**
     * 描述
     */
    String description() default "";
}
