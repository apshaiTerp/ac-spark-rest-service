package com.ac.umkc.rest.data;

import static org.junit.Assert.fail;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

/**
 * @author AC010168
 *
 */
public class SimpleTest {
  
  /** Need to help with date assignment */
  private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");

  @Test
  public void testCalendarCrap() {
    try {
      String startDate = "2016.01.01";
      String endDate   = "2017.01.01";
      
      Date trueStartDate = formatter.parse(startDate);
      Date trueEndDate   = formatter.parse(endDate);
      
      Calendar startCalendar = Calendar.getInstance();
      Calendar endCalendar   = Calendar.getInstance();
      
      startCalendar.setTime(trueStartDate);
      endCalendar.setTime(trueEndDate);
      
      System.out.println ("startCalendar.year:  " + startCalendar.get(Calendar.YEAR));
      System.out.println ("startCalendar.month: " + startCalendar.get(Calendar.MONTH));
      System.out.println ("startCalendar.day:   " + startCalendar.get(Calendar.DATE));
      
      System.out.println ("endCalendar.year:  " + endCalendar.get(Calendar.YEAR));
      System.out.println ("endCalendar.month: " + endCalendar.get(Calendar.MONTH));
      System.out.println ("endCalendar.day:   " + endCalendar.get(Calendar.DATE));
      
      LineGraphData data = new LineGraphData(startCalendar.get(Calendar.DATE), startCalendar.get(Calendar.MONTH), 
          startCalendar.get(Calendar.YEAR));
      
      System.out.println ("LineGraphData.shortDate: " + data.getShortDate());
      
      for (int i = 0; i < 60; i++) {
        startCalendar.add(Calendar.DATE, 1);
        System.out.println ("startCalendar.year:  " + startCalendar.get(Calendar.YEAR));
        System.out.println ("startCalendar.month: " + startCalendar.get(Calendar.MONTH));
        System.out.println ("startCalendar.day:   " + startCalendar.get(Calendar.DATE));
        
        LineGraphData data2 = new LineGraphData(startCalendar.get(Calendar.DATE), startCalendar.get(Calendar.MONTH), 
            startCalendar.get(Calendar.YEAR));
        
        System.out.println ("LineGraphData.shortDate: " + data2.getShortDate());
      }
      
      
    } catch (Throwable t) {
      t.printStackTrace();
      fail("Something bad happened: " + t.getMessage());
    }
  }

}
