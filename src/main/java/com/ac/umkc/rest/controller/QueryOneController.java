package com.ac.umkc.rest.controller;

import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import scala.Serializable;

import com.ac.umkc.rest.SparkRESTApplication;
import com.ac.umkc.rest.data.GoogleData;
import com.ac.umkc.rest.data.SimpleErrorData;


/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query1")
public class QueryOneController implements Serializable {
  
  /** Gotta have it */
  private static final long serialVersionUID = -227649608200738094L;

  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery1() {
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 1  ***************************");
    System.out.println ("*************************************************************************");
    
    try {
      //New Approach for RDD
      JavaRDD<GoogleData> userRDD = SparkRESTApplication.sparkSession.read().textFile("hdfs://localhost:9000/proj3/query1").javaRDD().map(
          new Function<String, GoogleData>() {
            /** It wants it, so I gave it one */
            private static final long serialVersionUID = 5654145143753968626L;
  
            public GoogleData call(String line) throws Exception {
              GoogleData data = new GoogleData();
              data.parseFromJSON(line);
              return data;
            }
          });
      
      List<GoogleData> results = userRDD.collect();
      
      String resultJSON = "{\"results\":[";
      int resultCount = 0;
      for (GoogleData data : results) {
        resultCount++;
        String line = data.toString();
        System.out.println (line);
        resultJSON += line;
        if (resultCount < results.size()) resultJSON += ",";
      }
      resultJSON += "]}";
      
      System.out.println ("-------------------------------------------------------------------------");
      System.out.println ("-----------------------------  End Query 1  -----------------------------");
      System.out.println ("-------------------------------------------------------------------------");
  
      System.out.println (resultJSON);
  
      return resultJSON;
    } catch (Throwable t) {
      t.printStackTrace();
      return new SimpleErrorData("Query Error", "An Unknown Error Occurred: " + t.getMessage());
    }
  }
}


