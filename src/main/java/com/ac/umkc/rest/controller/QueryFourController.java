package com.ac.umkc.rest.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import scala.Serializable;

import com.ac.umkc.rest.SparkRESTApplication;
import com.ac.umkc.rest.data.SimpleErrorData;
import com.ac.umkc.rest.data.TweetsDayData;
import com.ac.umkc.spark.data.TwitterStatus;
import com.ac.umkc.spark.data.TwitterUser;

/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query4")
public class QueryFourController implements Serializable {

  /** Gotta have it */
  private static final long serialVersionUID = -6957417977131234881L;

  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery4(@RequestParam(value="search") String searchTerm,
                          @RequestParam(value="startdate") String startDate,
                          @RequestParam(value="enddate") String endDate) {
    if ((searchTerm == null) || (searchTerm.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No search was provided.");
    if ((startDate == null) || (startDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No startdate was provided.");
    if ((endDate == null) || (endDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No enddate was provided.");
    
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 4  ***************************");
    System.out.println ("*************************************************************************");
    
    boolean readFromCache = false;
    String dynamicPath = "/proj3/query4/" + searchTerm + "/" + startDate + "/" + endDate;
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
    
    if(!readFromCache)
      executeQuery4(searchTerm, startDate, endDate, "hdfs://localhost:9000/proj3/tweetdata.txt", 
          "hdfs://localhost:9000/proj3/userdata.txt");
    
    try {
      //New Approach for RDD
      JavaRDD<TweetsDayData> hashRDD = SparkRESTApplication.sparkSession.read().textFile(hdfsPath).javaRDD().map(
          new Function<String, TweetsDayData>() {
            /** It wants it, so I gave it one */
            private static final long serialVersionUID = 5654145143753968626L;
  
            public TweetsDayData call(String line) throws Exception {
              TweetsDayData data = new TweetsDayData();
              data.parseFromJSON(line);
              return data;
            }
          });
      
      List<TweetsDayData> results = hashRDD.collect();
      
      String resultJSON = "{\"results\":[";
      int resultCount = 0;
      for (TweetsDayData data : results) {
        resultCount++;
        String line = data.toString();
        System.out.println (line);
        resultJSON += line;
        if (resultCount < results.size()) resultJSON += ",";
      }
      resultJSON += "]}";

      System.out.println ("-------------------------------------------------------------------------");
      System.out.println ("-----------------------------  End Query 4  -----------------------------");
      System.out.println ("-------------------------------------------------------------------------");

      System.out.println (resultJSON);

      return resultJSON;

    } catch (Throwable t) {
      t.printStackTrace();
      return new SimpleErrorData("Query Error", "An Unknown Error Occurred: " + t.getMessage());
    }
  }
  
  /**
   * Helper method to run the Spark Job in case we don't have the data.
   * 
   * @param startDate
   * @param endDate
   * @param tweetPath
   * @param userPath
   */
  @SuppressWarnings("resource")
  private void executeQuery4(final String searchTerm, final String startDate, final String endDate, 
      String tweetPath, String userPath) {
    //Open our dataset, then filter out to matching hash tags
    JavaRDD<TwitterStatus> tweetRDD = SparkRESTApplication.sparkSession.read().textFile(tweetPath).javaRDD().map(
        new Function<String, TwitterStatus>() {
          
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 1503107307123339206L;

          public TwitterStatus call(String line) throws Exception {
            TwitterStatus status = new TwitterStatus();
            status.parseFromJSON(line);
            return status;
          }
        }).filter(new Function<TwitterStatus, Boolean>() {
              /** It wants it, so I gave it one */
              private static final long serialVersionUID = 113462456123339206L;

              public Boolean call(TwitterStatus status) throws Exception {
                if ((status.getShortDate().compareTo(startDate) >= 0) && (status.getShortDate().compareTo(endDate) <= 0)) {
                  boolean found = false;
                  for (String compareTerm : status.getHashTags()) {
                    if (searchTerm.equalsIgnoreCase(compareTerm))
                      found = true;
                  }
                  return found;
                }
                return false;
              }
         });

    Dataset<Row> tweetDF = SparkRESTApplication.sparkSession.createDataFrame(tweetRDD, TwitterStatus.class);
    tweetDF.createOrReplaceTempView("tweets");
    
    //New Approach for RDD
    JavaRDD<TwitterUser> userRDD = SparkRESTApplication.sparkSession.read().textFile(userPath).javaRDD().map(
        new Function<String, TwitterUser>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 5654145143753968626L;

          public TwitterUser call(String line) throws Exception {
            TwitterUser user = new TwitterUser();
            user.parseFromJSON(line);
            return user;
          }
        });
    
    Dataset<Row> userDF = SparkRESTApplication.sparkSession.createDataFrame(userRDD, TwitterUser.class);
    userDF.createOrReplaceTempView("users");
    
    Dataset<Row> resultsDF = SparkRESTApplication.sparkSession.sql("Select u.userType, t.shortDate, t.year, t.month, t.day, count(t.statusID) " +
        "FROM tweets t " + 
        "JOIN users u " +
        "ON t.userID = u.twitterID " + 
        "GROUP BY u.userType, t.shortDate, t.year, t.month, t.day " + 
        "ORDER BY u.userType, t.shortDate");
    
    List<Row> results = resultsDF.collectAsList();
    List<TweetsDayData> tweetResults = new ArrayList<TweetsDayData>(results.size());
    
    for (Row row : results) {
      TweetsDayData data = new TweetsDayData();
      data.setUserType(row.getString(0));
      data.setShortDate(row.getString(1));
      data.setYear(row.getInt(2));
      data.setMonth(row.getInt(3));
      data.setDay(row.getInt(4));
      data.setCount((int)row.getLong(5));
      tweetResults.add(data);
    }
    
    String dynamicPath = "/proj3/query4/" + searchTerm + "/" + startDate + "/" + endDate;
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
    JavaRDD<TweetsDayData> resultRDD = context.parallelize(tweetResults);
    resultRDD.saveAsTextFile(outputPath);
    
    System.out.println ("Query Results Written to: " + outputPath);
  }
}
