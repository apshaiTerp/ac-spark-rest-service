package com.ac.umkc.rest.controller;

import com.ac.umkc.rest.data.SimpleErrorData;

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
    
    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 2  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    return new SimpleErrorData("Unimplemented Call", "This call is not yet functional");
  }
}
