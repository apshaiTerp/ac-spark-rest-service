package com.ac.umkc.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import scala.Serializable;
import scala.Tuple2;

import com.ac.umkc.rest.SparkRESTApplication;
import com.ac.umkc.rest.data.HashTagData;
import com.ac.umkc.rest.data.SimpleErrorData;
import com.ac.umkc.spark.data.TwitterStatus;
import com.ac.umkc.spark.util.TupleSorter;

/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query3")
public class QueryThreeController implements Serializable {

  /** Gotta have it */
  private static final long serialVersionUID = 1323229533169801071L;

  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery3(@RequestParam(value="startdate") String startDate,
                          @RequestParam(value="enddate") String endDate) {
    if ((startDate == null) || (startDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No startdate was provided.");
    if ((endDate == null) || (endDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No enddate was provided.");

    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 3  ***************************");
    System.out.println ("*************************************************************************");
    
    boolean readFromCache = false;
    String dynamicPath = "/proj3/query3/" + startDate + "/" + endDate;
    try {
      Configuration hdfsConfiguration = new Configuration();
      hdfsConfiguration.set("fs.defaultFS", "hdfs://localhost:9000");
      FileSystem hdfs                 = FileSystem.get(hdfsConfiguration);
      
      Path checkFile = new Path(dynamicPath);
      if (hdfs.exists(checkFile)) {
        System.out.println ("I found the known file!");
        readFromCache = true;
      } else System.out.println ("I could not find the known file.  Boo!");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    System.out.println ("Read from Cache: " + readFromCache);
    
    String hdfsPath = "hdfs://localhost:9000" + dynamicPath;
    System.out.println ("HDFS URL: " + hdfsPath);
    
    if (!readFromCache)
      executeQuery3(startDate, endDate, "hdfs://localhost:9000/proj3/tweetdata.txt");

    try {
      //New Approach for RDD
      JavaRDD<HashTagData> hashRDD = SparkRESTApplication.sparkSession.read().textFile(hdfsPath).javaRDD().map(
          new Function<String, HashTagData>() {
            /** It wants it, so I gave it one */
            private static final long serialVersionUID = 5654145143753968626L;
  
            public HashTagData call(String line) throws Exception {
              HashTagData data = new HashTagData();
              data.parseFromJSON(line);
              return data;
            }
          });
      
      List<HashTagData> results = hashRDD.collect();
      
      String resultJSON = "{\"results\":[";
      int resultCount = 0;
      for (HashTagData data : results) {
        resultCount++;
        String line = data.toString();
        System.out.println (line);
        resultJSON += line;
        if (resultCount < results.size()) resultJSON += ",";
      }
      resultJSON += "]}";

      System.out.println ("-------------------------------------------------------------------------");
      System.out.println ("-----------------------------  End Query 3  -----------------------------");
      System.out.println ("-------------------------------------------------------------------------");

      System.out.println (resultJSON);

      return resultJSON;

    } catch (Throwable t) {
      t.printStackTrace();
      return new SimpleErrorData("Query Error", "An Unknown Error Occurred: " + t.getMessage());
    }
  }
  
  @SuppressWarnings("resource")
  private void executeQuery3(final String startDate, final String endDate, String tweetPath) {
    //Convert our tweet file into objects, then filter down to only tweets with Hash Tags
    JavaRDD<TwitterStatus> tweetRDD = SparkRESTApplication.sparkSession.read().textFile(tweetPath).javaRDD().map(
        new Function<String, TwitterStatus>() {
          
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 1503107307123339206L;

          public TwitterStatus call(String line) throws Exception {
            TwitterStatus status = new TwitterStatus();
            status.parseFromJSON(line);
            return status;
          }
        }).filter(        
        new Function<TwitterStatus, Boolean>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 113462456123339206L;
    
          public Boolean call(TwitterStatus status) throws Exception {
            if ((status.getShortDate().compareTo(startDate) >= 0) && (status.getShortDate().compareTo(endDate) <= 0))
              return ((status.getHashTags() != null) && (status.getHashTags().size() > 0));
            return false;
          }
    });

    //Flat map our individual hashTags to Tuples of (hashTag, count), then
    //run the aggregating reduce operation, and sort in descending order
    JavaPairRDD<String, Integer> hashTags = tweetRDD.flatMapToPair(
        new PairFlatMapFunction<TwitterStatus, String, Integer>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 6310698767617690806L;

          public Iterator<Tuple2<String, Integer>> call(TwitterStatus status) {
            List<Tuple2<String, Integer>> results = new ArrayList<Tuple2<String, Integer>>(status.getHashTags().size());
            for (String hashTag : status.getHashTags())
              results.add(new Tuple2<String, Integer>(hashTag, 1));
            return results.iterator();
          }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = -4583081102611123090L;

          public Integer call(Integer i1, Integer i2) {
            return i1 + i2;
          }
        });
    
    int hashTagCount = (int)hashTags.count();
    System.out.println ("How many HashTags are in the set: " + hashTagCount);
    HashTagData hashTagCountData = new HashTagData("All HashTags", hashTagCount);
    
    JavaPairRDD<String, Integer> hashTagSum = hashTags.mapToPair(
        new PairFunction<Tuple2<String, Integer>, String, Integer>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 6310698767617690806L;

          public Tuple2<String, Integer> call(Tuple2<String, Integer> status) {
            return new Tuple2<String, Integer>("All HashTags Used", status._2());
          }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = -4583081102611123090L;

          public Integer call(Integer i1, Integer i2) {
            return i1 + i2;
          }
        });

    List<Tuple2<String, Integer>> otherResults = hashTagSum.collect();
    int totalHashTagsUsed = otherResults.get(0)._2();
    
    List<HashTagData> allHashTagResults = new ArrayList<HashTagData>();
    allHashTagResults.add(hashTagCountData);
    
    //Take the top 10 ordered results
    List<Tuple2<String, Integer>> results = hashTags.takeOrdered(10, new TupleSorter());
    System.out.println ("The Top 10 HashTags in use are:");
    
    for (Tuple2<String, Integer> tuple : results) {
      HashTagData curData = new HashTagData(tuple._1(), tuple._2());
      totalHashTagsUsed -= tuple._2();
      allHashTagResults.add(curData);
    }
    allHashTagResults.add(new HashTagData("Other HashTags", totalHashTagsUsed));
    
    String dynamicPath = "/proj3/query3/" + startDate + "/" + endDate;
    try {
      Configuration hdfsConfiguration = new Configuration();
      hdfsConfiguration.set("fs.defaultFS", "hdfs://localhost:9000");
      FileSystem hdfs                 = FileSystem.get(hdfsConfiguration);
      
      Path checkFile = new Path(dynamicPath);
      if (hdfs.exists(checkFile)) {
        System.out.println ("I need to purge before writing!");
        hdfs.delete(checkFile, true);
      } 
    } catch (IOException e) {
      e.printStackTrace();
    }

    String outputPath = "hdfs://localhost:9000" + dynamicPath;
    JavaSparkContext context = new JavaSparkContext(SparkRESTApplication.sparkSession.sparkContext());
    JavaRDD<HashTagData> resultRDD = context.parallelize(allHashTagResults);
    resultRDD.saveAsTextFile(outputPath);
    
    System.out.println ("Query Results Written to: " + outputPath);
  }
}
