package com.ac.umkc.rest.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import scala.Serializable;

import com.ac.umkc.rest.SparkRESTApplication;
import com.ac.umkc.rest.data.SimpleErrorData;
import com.ac.umkc.rest.data.TweetLRData;
import com.ac.umkc.spark.data.TwitterStatus;
import com.ac.umkc.spark.data.TwitterUser;

/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query2")
public class QueryTwoController implements Serializable {

  /** Gotta have it */
  private static final long serialVersionUID = -405482409825525659L;

  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery2(@RequestParam(value="startdate") String startDate,
                          @RequestParam(value="enddate") String endDate) {
    if ((startDate == null) || (startDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No startdate was provided.");
    if ((endDate == null) || (endDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No enddate was provided.");

    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 2  ***************************");
    System.out.println ("*************************************************************************");
    
    boolean readFromCache = false;
    String dynamicPath = "/proj3/query2/" + startDate + "/" + endDate;
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
    
    String hdfsPath = "http://localhost:9000" + dynamicPath;
    System.out.println ("HDFS URL: " + hdfsPath);
    
    if (!readFromCache)
      executeQuery2(startDate, endDate, "hdfs://localhost:9000/proj3/tweetdata.txt", "hdfs://localhost:9000/proj3/userdata.txt");

    try {
      //New Approach for RDD
      JavaRDD<TweetLRData> tweetRDD = SparkRESTApplication.sparkSession.read().textFile(hdfsPath).javaRDD().map(
          new Function<String, TweetLRData>() {
            /** It wants it, so I gave it one */
            private static final long serialVersionUID = 5654145143753968626L;
  
            public TweetLRData call(String line) throws Exception {
              TweetLRData data = new TweetLRData();
              data.parseFromJSON(line);
              return data;
            }
          });
      
      List<TweetLRData> results = tweetRDD.collect();
      
      String resultJSON = "{\"results\":[";
      int resultCount = 0;
      for (TweetLRData data : results) {
        resultCount++;
        String line = data.toString();
        System.out.println (line);
        resultJSON += line;
        if (resultCount < results.size()) resultJSON += ",";
      }
      resultJSON += "]}";

      System.out.println ("-------------------------------------------------------------------------");
      System.out.println ("-----------------------------  End Query 2  -----------------------------");
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
  private void executeQuery2(String startDate, String endDate, String tweetPath, String userPath) {
    //Can do this in RDD or DataFrames
    JavaRDD<TwitterStatus> tweetRDD = SparkRESTApplication.sparkSession.read().textFile(tweetPath).javaRDD().map(
        new Function<String, TwitterStatus>() {
          /** It wants it, so I gave it one */
          private static final long serialVersionUID = 1503107307123339206L;

          public TwitterStatus call(String line) throws Exception {
            TwitterStatus status = new TwitterStatus();
            status.parseFromJSON(line);
            return status;
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

    Dataset<Row> resultsDF = SparkRESTApplication.sparkSession.sql(
        "SELECT u.userType, u.screenName, u.userName, AVG(t.favoriteCount + t.retweetCount), COUNT(t.statusID) " + 
        "FROM tweets t " +
        "JOIN users u " + 
        "ON t.userName = u.screenName " + 
        "WHERE t.shortDate >= '" + startDate + "' and t.shortDate <= '" + endDate + "' " +
        "GROUP BY u.userType, u.screenName, u.userName " + 
        "ORDER BY AVG(t.favoriteCount + t.retweetCount) DESC");

    //Let's sample the top 10 most popular from each group
    List<Row> designerList   = resultsDF.filter(new Column("userType").equalTo("DESIGNER")).takeAsList(10);
    List<Row> publisherList  = resultsDF.filter(new Column("userType").equalTo("PUBLISHER")).takeAsList(10);
    List<Row> reviewerList   = resultsDF.filter(new Column("userType").equalTo("REVIEWER")).takeAsList(10);
    List<Row> conventionList = resultsDF.filter(new Column("userType").equalTo("CONVENTION")).takeAsList(10);
    List<Row> communityList  = resultsDF.filter(new Column("userType").equalTo("COMMUNITY")).takeAsList(10);
    
    List<TweetLRData> fullResults = new LinkedList<TweetLRData>();
    
    //Process all designers
    for (Row row : designerList) {
      TweetLRData rowData = new TweetLRData();
      rowData.setUserType(row.getString(0));
      rowData.setScreenName(row.getString(1));
      rowData.setUserName(row.getString(2));
      rowData.setAverageLR(row.getDouble(3));
      rowData.setTweetCount((int)row.getLong(4));
      fullResults.add(rowData);
    }

    //Process all publishers
    for (Row row : publisherList) {
      TweetLRData rowData = new TweetLRData();
      rowData.setUserType(row.getString(0));
      rowData.setScreenName(row.getString(1));
      rowData.setUserName(row.getString(2));
      rowData.setAverageLR(row.getDouble(3));
      rowData.setTweetCount((int)row.getLong(4));
      fullResults.add(rowData);
    }

    //Process all reviewers
    for (Row row : reviewerList) {
      TweetLRData rowData = new TweetLRData();
      rowData.setUserType(row.getString(0));
      rowData.setScreenName(row.getString(1));
      rowData.setUserName(row.getString(2));
      rowData.setAverageLR(row.getDouble(3));
      rowData.setTweetCount((int)row.getLong(4));
      fullResults.add(rowData);
    }

    //Process all conventions
    for (Row row : conventionList) {
      TweetLRData rowData = new TweetLRData();
      rowData.setUserType(row.getString(0));
      rowData.setScreenName(row.getString(1));
      rowData.setUserName(row.getString(2));
      rowData.setAverageLR(row.getDouble(3));
      rowData.setTweetCount((int)row.getLong(4));
      fullResults.add(rowData);
    }

    //Process all community users
    for (Row row : communityList) {
      TweetLRData rowData = new TweetLRData();
      rowData.setUserType(row.getString(0));
      rowData.setScreenName(row.getString(1));
      rowData.setUserName(row.getString(2));
      rowData.setAverageLR(row.getDouble(3));
      rowData.setTweetCount((int)row.getLong(4));
      fullResults.add(rowData);
    }
    
    String dynamicPath = "/proj3/query2/" + startDate + "/" + endDate;
    String outputPath = "hdfs://localhost:9000" + dynamicPath;
    JavaSparkContext context = new JavaSparkContext(SparkRESTApplication.sparkSession.sparkContext());
    JavaRDD<TweetLRData> resultRDD = context.parallelize(fullResults);
    resultRDD.saveAsTextFile(outputPath);
    
    System.out.println ("Query Results Written to: " + outputPath);

  }
}
