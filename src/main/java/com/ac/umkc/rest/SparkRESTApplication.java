package com.ac.umkc.rest;

import javax.annotation.PreDestroy;

import org.apache.spark.sql.SparkSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author AC010168
 *
 */
@ComponentScan
@EnableAutoConfiguration
public class SparkRESTApplication extends SpringBootServletInitializer {

  public static SparkSession sparkSession;
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SparkRESTApplication.class);
  }
  
  /** Main method, which is starting point for service using Spring launcher */
  public static void main(String[] args) {
    
    sparkSession = SparkSession.builder()
        .master("local")
        .appName("Java SparkDriver")
        .config("spark.some.config.option", "some-value")
        .getOrCreate();
  
    SpringApplication.run(SparkRESTApplication.class, args);
  }
  
  @PreDestroy
  public static void shutdownHook() {
  
    System.out.println (">>>  I'm inside the shutdownHook  <<");
  }
}
