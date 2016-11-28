package com.ac.umkc.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ac.umkc.rest.data.SimpleErrorData;

import scala.Serializable;

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
  public Object getQuery4() {
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 4  ***************************");
    System.out.println ("*************************************************************************");
    
    //TODO - More Stuff
    
    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 4  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    return new SimpleErrorData("Unimplemented Call", "This call is not yet functional");
  }
}
