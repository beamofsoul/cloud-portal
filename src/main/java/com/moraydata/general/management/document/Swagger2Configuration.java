package com.moraydata.general.management.document;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Configuration {

	@Value("${swagger.api.title}")  
    String title;
	
	@Value("${swagger.api.description}")  
    String description;
	
	@Value("${swagger.api.version}")  
    String version;
	
	@Value("${swagger.api.author.name}")  
    String authorName;
	
	@Value("${swagger.api.author.url}")  
    String url;
	
	@Value("${swagger.api.author.email}")  
    String email;
	
	@Bean
	public Docket createRestApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo())
				.select()
				.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
				.build();
	}
	
	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title(title)
				.description(description)
				.contact(new Contact(authorName, url, email))
				.version(version)
				.build();
	}
}
