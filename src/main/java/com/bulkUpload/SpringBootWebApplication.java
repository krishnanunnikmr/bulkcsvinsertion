package com.bulkUpload;

import javax.servlet.MultipartConfigElement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

@SpringBootApplication
public class SpringBootWebApplication {

	public static void main(String[] args) throws Exception {

		SpringApplication.run(SpringBootWebApplication.class, args);
		Logger.getRootLogger().setLevel(Level.OFF);

	}

	/**
	 * This method available for resolving a SessionFactory by JPA persistence unit name
	 * 
	 */
	@Bean
	public HibernateJpaSessionFactoryBean sessionFactory() {
		return new HibernateJpaSessionFactoryBean();
	}

	@Bean
	MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize("5120MB");
		factory.setMaxRequestSize("5120MB");
		return factory.createMultipartConfig();
	}


}