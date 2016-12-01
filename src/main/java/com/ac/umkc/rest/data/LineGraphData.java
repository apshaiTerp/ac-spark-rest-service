package com.ac.umkc.rest.data;

import scala.Serializable;

/**
 * @author AC010168
 *
 */
public class LineGraphData implements Serializable {

  /** Added because it needs it */
  private static final long serialVersionUID = -1114025168609512243L;
  
  private String shortDate;
  private int    year;
  private int    month;
  private int    day;
  private int    designerCount;
  private int    publisherCount;
  private int    reviewerCount;
  private int    conventionCount;
  private int    communityCount;
  
  public LineGraphData() {
    shortDate       = null;
    year            = 0;
    month           = 0;
    day             = 0;
    designerCount   = 0;
    publisherCount  = 0;
    reviewerCount   = 0;
    conventionCount = 0;
    communityCount  = 0;
  }

  public LineGraphData(int day, int month, int year) {
    this.year       = year;
    this.month      = month;
    this.day        = day;
    shortDate       = "" + year + ".";
    if (month < 9) shortDate += "0" + (month + 1) + ".";
    else           shortDate += "" + (month + 1) + ".";
    if (day <= 9)  shortDate += "0" + day;
    else           shortDate += "" + day;
    designerCount   = 0;
    publisherCount  = 0;
    reviewerCount   = 0;
    conventionCount = 0;
    communityCount  = 0;
  }
  
  @Override
  public String toString() {
    return "{\"shortDate\":\"" + shortDate + "\",\"year\":" + year + ",\"month\":" + month + ",\"day\":" + day +
        ",\"designerCount\":" + designerCount + ",\"publisherCount\":" + publisherCount + 
        ",\"reviewerCount\":" + reviewerCount + ",\"conventionCount\":" + conventionCount + 
        ",\"communityCount\":" + communityCount + "}";
  }

  /**
   * @return the shortDate
   */
  public String getShortDate() {
    return shortDate;
  }

  /**
   * @param shortDate the shortDate to set
   */
  public void setShortDate(String shortDate) {
    this.shortDate = shortDate;
  }

  /**
   * @return the year
   */
  public int getYear() {
    return year;
  }

  /**
   * @param year the year to set
   */
  public void setYear(int year) {
    this.year = year;
  }

  /**
   * @return the month
   */
  public int getMonth() {
    return month;
  }

  /**
   * @param month the month to set
   */
  public void setMonth(int month) {
    this.month = month;
  }

  /**
   * @return the day
   */
  public int getDay() {
    return day;
  }

  /**
   * @param day the day to set
   */
  public void setDay(int day) {
    this.day = day;
  }

  /**
   * @return the designerCount
   */
  public int getDesignerCount() {
    return designerCount;
  }

  /**
   * @param designerCount the designerCount to set
   */
  public void setDesignerCount(int designerCount) {
    this.designerCount = designerCount;
  }

  /**
   * @return the publisherCount
   */
  public int getPublisherCount() {
    return publisherCount;
  }

  /**
   * @param publisherCount the publisherCount to set
   */
  public void setPublisherCount(int publisherCount) {
    this.publisherCount = publisherCount;
  }

  /**
   * @return the reviewerCount
   */
  public int getReviewerCount() {
    return reviewerCount;
  }

  /**
   * @param reviewerCount the reviewerCount to set
   */
  public void setReviewerCount(int reviewerCount) {
    this.reviewerCount = reviewerCount;
  }

  /**
   * @return the conventionCount
   */
  public int getConventionCount() {
    return conventionCount;
  }

  /**
   * @param conventionCount the conventionCount to set
   */
  public void setConventionCount(int conventionCount) {
    this.conventionCount = conventionCount;
  }

  /**
   * @return the communityCount
   */
  public int getCommunityCount() {
    return communityCount;
  }

  /**
   * @param communityCount the communityCount to set
   */
  public void setCommunityCount(int communityCount) {
    this.communityCount = communityCount;
  }
}
