package com.moraydata.general.management.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName Property
 * @Description Injects values in configuration file (application.yml) to annotated fields by current annotation.
 * @author MingshuJian
 * @Date 2017年8月26日 下午3:31:20
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Property {
	
	String value(); // property's full path in configuration file (application.yml)
	
	String defaultValue() default ""; // an alternative value if the value of property does not set
}
