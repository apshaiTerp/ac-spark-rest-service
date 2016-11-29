package com.ac.umkc.rest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ac.umkc.rest.data.SimpleErrorData;

import scala.Serializable;

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
  public Object getQuery5(@RequestParam(value="startdate") String startDate,
                          @RequestParam(value="enddate") String endDate) {
    if ((startDate == null) || (startDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No startdate was provided.");
    if ((endDate == null) || (endDate.trim().length() == 0))
      return new SimpleErrorData("Invalid Request", "No enddate was provided.");
    
    System.out.println ("*************************************************************************");
    System.out.println ("***************************  Execute Query 5  ***************************");
    System.out.println ("*************************************************************************");
    
    //TODO - More Stuff
    
    System.out.println ("-------------------------------------------------------------------------");
    System.out.println ("-----------------------------  End Query 5  -----------------------------");
    System.out.println ("-------------------------------------------------------------------------");

    return new SimpleErrorData("Unimplemented Call", "This call is not yet functional");
  }
}
