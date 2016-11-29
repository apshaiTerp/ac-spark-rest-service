package com.ac.umkc.rest.controller;

import java.io.IOException;

import com.ac.umkc.rest.data.SimpleErrorData;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import scala.Serializable;

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
    
    //TODO - More Stuff
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

    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 2  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    return new SimpleErrorData("Unimplemented Call", "This call is not yet functional");
  }
}
