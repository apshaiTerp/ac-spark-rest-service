package com.ac.umkc.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.umkc.rest.data.SimpleMessageData;

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
    
    return new SimpleMessageData("Success", "I guess that worked");
  }
}


