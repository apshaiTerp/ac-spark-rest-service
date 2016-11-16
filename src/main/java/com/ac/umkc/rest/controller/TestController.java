package com.ac.umkc.rest.controller;

import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import scala.Tuple2;

import com.ac.umkc.rest.SparkRESTApplication;
import com.ac.umkc.rest.data.SimpleMessageData;
import com.ac.umkc.spark.data.TwitterUser;
import com.ac.umkc.spark.util.TupleSorter;


/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {
  
  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getTest() {
    

    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 1  ***************************");
    System.out.println ("*************************************************************************");
    
    //New Approach for RDD
    JavaRDD<TwitterUser> userRDD = SparkRESTApplication.sparkSession.read().textFile("hdfs://localhost:9321/proj2/userdata.txt").javaRDD().map(
        new Function<String, TwitterUser>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 5654145143753968626L;

          public TwitterUser call(String line) throws Exception {
            TwitterUser user = new TwitterUser();
            user.parseFromJSON(line);
            return user;
          }
        }).filter(new Function<TwitterUser, Boolean>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = -2462072955148041130L;

          public Boolean call(TwitterUser user) {
            return user.getLocation().length() > 0;
          }
        });
    
    JavaPairRDD<String, Integer> locations = userRDD.mapToPair(new PairFunction<TwitterUser, String, Integer>() {
      /** Gave it cause it wants one. */
      private static final long serialVersionUID = 7711668945522265992L;

          public Tuple2<String, Integer> call(TwitterUser user) {
            return new Tuple2<String, Integer>(user.getLocation(), 1);
          }
      });
    
    JavaPairRDD<String, Integer> sortLocations = locations.reduceByKey(new Function2<Integer, Integer, Integer>() {
      /** Gave it cause it wants one. */
      private static final long serialVersionUID = 1758905397312207150L;

          public Integer call(Integer i1, Integer i2) {
            return i1 + i2;
          }
      }).sortByKey();
    
    List<Tuple2<String, Integer>> results = sortLocations.takeOrdered(10, new TupleSorter());

    String resultJSON = "{\"results\":[";
    int resultCount = 0;
    for (Tuple2<String, Integer> tuple : results) {
      resultCount++;
      String line = "{\"location\":\"" + tuple._1() + "\", \"count\":" + tuple._2() + "}";
      System.out.println (line);
      resultJSON += line;
      if (resultCount < results.size()) resultJSON += ",";
    }
    resultJSON += "]}";
    
    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 1  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    System.out.println (resultJSON);

    return new SimpleMessageData("Success", "I guess that worked");
  }
}


