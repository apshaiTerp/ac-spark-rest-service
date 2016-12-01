package com.ac.umkc.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.umkc.rest.data.SimpleErrorData;
import com.ac.umkc.spark.util.TwitterCall;

import scala.Serializable;

/**
 * @author AC010168
 *
 */
@RestController
@RequestMapping("/query5support")
public class QueryFiveSupportController implements Serializable {

  /** Added because it needs it */
  private static final long serialVersionUID = 180014941987416042L;
  
  @RequestMapping(method = RequestMethod.GET, produces="application/json;charset=UTF-8")
  public Object getQuery5(@RequestParam(value="name") String userName,
                          @RequestParam(value="id", defaultValue="0") long statusID) {
    if ((userName == null) || (userName.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No name was provided.");
    if (statusID <= 0)
      return new SimpleErrorData("Invalid Request", "No valid id was provided.");
   
    String requestURL = "https://publish.twitter.com/oembed?url=https://twitter.com/" + userName + 
        "/status/" + statusID;
    String result = "";
    try {
      result = TwitterCall.getEmbedBody(requestURL);
      
      //Let's take out the async block call, since it's not working and unnecessary
      result = result.substring(0, result.indexOf("\n"));
      
      //Now we need to sanitize the output before things blow up
      result = result.replaceAll("\b", "");
      result = result.replaceAll("\f", "");
      result = result.replaceAll("\t", "");
      result = result.replaceAll("\r", "");
      
      result = result.replaceAll("/", "\\\\/");
      result = result.replaceAll("\"", "\\\\\"");
      
    } catch (Throwable t) {
      t.printStackTrace();
      return new SimpleErrorData("REST API Failure", "Oops: " + t.getMessage());
    }

    return "{\"result\": \"" + result + "\"}";
  }
}
