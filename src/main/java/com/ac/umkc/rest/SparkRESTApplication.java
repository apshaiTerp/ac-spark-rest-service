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

  public static SparkSession sparkSession = null;
  
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SparkRESTApplication.class);
  }
  
  /** Main method, which is starting point for service using Spring launcher */
  public static void main(String[] args) {
    
    System.out.println ("Starting the SparkSession...");
    
    sparkSession = SparkSession.builder()
        .master("local")
        .appName("Java SparkDriver")
        .config("spark.some.config.option", "some-value")
        .getOrCreate();
    
    System.out.println ("My SparkSession appears to be live...");
    
    try {
      System.out.println ("I'm trying to read in the user");
      sparkSession.read().textFile("hdfs://localhost:9000/proj3/userdata.txt");
      System.out.println ("I could read it");
    } catch (Throwable t) {
      System.out.println ("Something didn't work...");
      t.printStackTrace();
      return;
    }
    
    SpringApplication.run(SparkRESTApplication.class, args);
  }
  
  @PreDestroy
  public static void shutdownHook() {
  
    System.out.println (">>>  I'm inside the shutdownHook  <<");
  }
}
