package com.moraydata.general.management.annotation;

import static org.springframework.util.ReflectionUtils.doWithFields;
import static org.springframework.util.ReflectionUtils.setField;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.moraydata.general.management.system.ConfigurationReader;

/**
 * @ClassName PropertyAnnotationBeanPostProcessor
 * @Description {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation that injects values in configuration file (application.yml) to annotated fields by {@link Property} annotation.
 * @author MingshuJian
 * @Date 2017年8月26日 下午2:34:38
 * @version 1.0.0
 * @see com.beamofsoul.rabbit.management.annotation.Property
 */
@Component
public class PropertyAnnotationBeanPostProcessor implements BeanPostProcessor {
	
	private static final Class<Property> ANNOTATION_CLASS = Property.class;
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		doWithFields(bean.getClass(), (field) -> {
			Property annotation = field.getAnnotation(ANNOTATION_CLASS);
			String propertyPath = annotation.value();
			Object propertyValue = ConfigurationReader.getValue(propertyPath);
			
			if (ObjectUtils.isEmpty(propertyValue)) {
				String defaultValue = annotation.defaultValue();
				if (StringUtils.isNotBlank(defaultValue)) {
					propertyValue = defaultValue;
				}
			}
			
			field.setAccessible(Boolean.TRUE);
			setField(field, bean, ConvertUtils.convert(propertyValue.toString(), field.getType()));
		}, (field) -> field.isAnnotationPresent(ANNOTATION_CLASS));
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}
}
