package com.ac.umkc.rest.controller;

import java.io.IOException;

import com.ac.umkc.rest.data.SimpleErrorData;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
  public Object getQuery2() {
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 2  ***************************");
    System.out.println ("*************************************************************************");
    
    //TODO - More Stuff
    try {
      Configuration hdfsConfiguration = new Configuration();
      hdfsConfiguration.set("fs.default.name", "hdfs://localhost:9000");
      FileSystem hdfs                 = FileSystem.get(hdfsConfiguration);
      
      
      Path knownFile = new Path("/proj3/userdata.txt");
      Path localFile = new Path("/proj3/query2/2016/2017");
      if (hdfs.exists(knownFile))
        System.out.println ("I found the known file!");
      else System.out.println ("I could not find the known file.  Boo!");
      
      if (hdfs.exists(localFile))
        System.out.println ("This path exists!");
      else System.out.println ("This path does not exist!  We've got to run the query ad-hoc.  :-(");

    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 2  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    return new SimpleErrorData("Unimplemented Call", "This call is not yet functional");
  }
}
