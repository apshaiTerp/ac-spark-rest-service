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
import com.ac.umkc.rest.data.TwitterStatusTopX;
import com.ac.umkc.spark.data.TwitterStatus;
import com.ac.umkc.spark.data.TwitterUser;
import com.ac.umkc.spark.util.TwitterCall;

/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query5")
public class QueryFiveController implements Serializable {

  /** Gotta have it */
  private static final long serialVersionUID = 6288275762967579609L;

  /**
   * 
   * @return
   */
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery5(@RequestParam(value="search") String searchTerm,
                          @RequestParam(value="topx", defaultValue="0") int topX) {
    if ((searchTerm == null) || (searchTerm.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No search was provided.");
    if (topX <= 0)
      return new SimpleErrorData("Invalid Request", "No valid topx was provided.");
    
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 5  ***************************");
    System.out.println ("*************************************************************************");
    
    boolean readFromCache = false;
    String altSearchTerm = searchTerm.toLowerCase().replaceAll(" ", "_");
    String dynamicPath = "/proj3/query5/" + altSearchTerm + "/" + topX;
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
      executeQuery5(searchTerm, topX, "hdfs://localhost:9000/proj3/tweetdata.txt", "hdfs://localhost:9000/proj3/userdata.txt");
    
    try {
      //New Approach for RDD
      JavaRDD<TwitterStatusTopX> hashRDD = SparkRESTApplication.sparkSession.read().textFile(hdfsPath).javaRDD().map(
          new Function<String, TwitterStatusTopX>() {
            /** It wants it, so I gave it one */
            private static final long serialVersionUID = 5654145143753968626L;
  
            public TwitterStatusTopX call(String line) throws Exception {
              TwitterStatusTopX data = new TwitterStatusTopX();
              data.parseFromJSON(line);
              return data;
            }
          });
      
      List<TwitterStatusTopX> results = hashRDD.collect();
      
      String resultJSON = "{\"results\":[";
      int resultCount = 0;
      for (TwitterStatusTopX data : results) {
        resultCount++;
        String line = data.toString();
        System.out.println (line);
        resultJSON += line;
        if (resultCount < results.size()) resultJSON += ",";
      }
      resultJSON += "]}";

      System.out.println ("-------------------------------------------------------------------------");
      System.out.println ("-----------------------------  End Query 5  -----------------------------");
      System.out.println ("-------------------------------------------------------------------------");

      System.out.println (resultJSON);

      return resultJSON;

    } catch (Throwable t) {
      t.printStackTrace();
      return new SimpleErrorData("Query Error", "An Unknown Error Occurred: " + t.getMessage());
    }
  }
  
  /**
   * Helper method for running and caching the query
   * @param searchTerm
   * @param termLimit
   */
  @SuppressWarnings("resource")
  private void executeQuery5(final String searchTerm, int termLimit, String tweetPath, String userPath) {
    final String searchFor = searchTerm.toLowerCase();
    
    //Open our dataset
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

    Dataset<Row> results = SparkRESTApplication.sparkSession.sql("SELECT t.userName, u.userType, t.statusID, t.createdDate " + 
        "FROM tweets t " + 
        "JOIN users u " +
        "ON t.userID = u.twitterID " + 
        "WHERE LOWER(t.filteredText) LIKE '%" + searchFor + "%' ORDER BY t.createdDate desc");
    
    List<Row> topX = results.takeAsList(termLimit);
    List<TwitterStatusTopX> searchResults = new ArrayList<TwitterStatusTopX>(topX.size());
    for (Row row : topX) {
      TwitterStatusTopX tstx = new TwitterStatusTopX();
      tstx.setUserName(row.getString(0));
      tstx.setUserType(row.getString(1));
      tstx.setStatusID(row.getLong(2));
      tstx.setCreatedDate(row.getString(3));
      
      /**********************************************************************************
      //This is where we make our External API call to pull in additional data
      TwitterStatusExtras extras = TwitterCall.getTweet(tstx.getStatusID());
      tstx.setStatusText(extras.getStatusText());
      **********************************************************************************/
      
      //Instead, create the dynamic widget which will be used to render the tweet block
      String requestURL = "https://publish.twitter.com/oembed?url=https://twitter.com/" + tstx.getUserName() + 
          "/status/" + tstx.getStatusID();
      tstx.setStatusText(TwitterCall.getEmbedBody(requestURL));
      
      searchResults.add(tstx);
    }
    
    //Since the dynamicPath can't have spaces, replace any that might be in the search term
    //with an underscore
    String altSearchTerm = searchTerm.toLowerCase().replaceAll(" ", "_");
    
    String dynamicPath = "/proj3/query5/" + altSearchTerm + "/" + termLimit;
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
    JavaRDD<TwitterStatusTopX> resultRDD = context.parallelize(searchResults);
    resultRDD.saveAsTextFile(outputPath);
    
    System.out.println ("Query Results Written to: " + outputPath);
  }
}
