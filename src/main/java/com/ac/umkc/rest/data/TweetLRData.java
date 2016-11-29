package com.ac.umkc.rest.data;

import org.json.JSONObject;

import scala.Serializable;

/**
 * @author AC010168
 *
 */
public class TweetLRData implements Serializable {

  /** Adding because we need it */
  private static final long serialVersionUID = 495633956697540816L;

  private String userType;
  private String screenName;
  private String userName;
  private double averageLR;
  private int    tweetCount;
  
  public TweetLRData() {
    userType   = null;
    screenName = null;
    userName   = null;
    averageLR  = 0.0;
    tweetCount = 0;
  }

  @Override
  public String toString() {
    return "{\"userType\":\"" + userType + "\",\"screenName\":\"" + screenName + 
        "\",\"userName\":\"" + userName + "\",\"averageLR\":" + averageLR + 
        ",\"tweetCount\":" + tweetCount + "}";
  }  
  
  /**
   * A helper method to parse a JSON String into this object.
   * 
   * @param line the JSON record
   */
  public void parseFromJSON(String line) {
    try {
      JSONObject jsonData = new JSONObject(line);
      userType   = jsonData.getString("userType");
      screenName = jsonData.getString("screenName");
      userName   = jsonData.getString("userName");
      averageLR  = jsonData.getDouble("averageLR");
      tweetCount = jsonData.getInt("tweetCount");
    } catch (Throwable t) {
      System.out.println("UNABLE TO PARSE: [" + line + "]");
    }
  }

  /**
   * @return the userType
   */
  public String getUserType() {
    return userType;
  }

  /**
   * @param userType the userType to set
   */
  public void setUserType(String userType) {
    this.userType = userType;
  }

  /**
   * @return the screenName
   */
  public String getScreenName() {
    return screenName;
  }

  /**
   * @param screenName the screenName to set
   */
  public void setScreenName(String screenName) {
    this.screenName = screenName;
  }

  /**
   * @return the userName
   */
  public String getUserName() {
    return userName;
  }

  /**
   * @param userName the userName to set
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * @return the averageLR
   */
  public double getAverageLR() {
    return averageLR;
  }

  /**
   * @param averageLR the averageLR to set
   */
  public void setAverageLR(double averageLR) {
    this.averageLR = averageLR;
  }

  /**
   * @return the tweetCount
   */
  public int getTweetCount() {
    return tweetCount;
  }

  /**
   * @param tweetCount the tweetCount to set
   */
  public void setTweetCount(int tweetCount) {
    this.tweetCount = tweetCount;
  }
}
