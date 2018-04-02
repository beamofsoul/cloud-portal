package com.moraydata.general;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableCaching
@EnableTransactionManagement
@ServletComponentScan
@SpringBootApplication(exclude =  {DataSourceAutoConfiguration.class})
public class CloudPortalApplication extends SpringBootServletInitializer {
	
	/**
	 * In order to package the project as .war file, so extends {@link SpringBootServletInitializer}
	 * and override its' configure(SpringApplicationBuilder application) method.
	 * @param SpringApplicationBuilder application
	 * @return SpringApplicationBuilder
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(CloudPortalApplication.class);
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(CloudPortalApplication.class, args);
	}
}
