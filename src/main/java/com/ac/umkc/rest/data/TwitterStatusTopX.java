package com.ac.umkc.rest.data;

import org.json.JSONObject;

import scala.Serializable;

/**
 * Java POJO to represent the results of one of our queries to combine Spark results
 * with additional Twitter information.
 * 
 * @author AC010168
 *
 */
public class TwitterStatusTopX implements Serializable {

  /** Adding because it needs it */
  private static final long serialVersionUID = 4737010945082679500L;
  
  /** The unique ID for this tweet */
  private long statusID;
  /** The userName who wrote this tweet */
  private String userName;
  /** The date this tweet was tweeted */
  private String createdDate;
  /** The unfiltered text from this tweet */
  private String statusText;
  /** The user type, which is a new addition */
  private String userType;
  
  /**
   * Basic Constructor
   */
  public TwitterStatusTopX() {
    statusID    = -1;
    userName    = null;
    createdDate = null;
    statusText  = null;
    userType    = null;
  }

  @Override
  public String toString() {
    return "{\"userName\":\"" + userName + "\", \"statusID\":" + statusID + ", \"createdDate\":\"" + createdDate + 
        "\", \"statusText\":\"" + statusText + "\", \"userType\":\"" + userType+ "\"}";
  }
  
  /**
   * A helper method to parse a JSON String into this object.
   * 
   * @param line the JSON record
   */
  public void parseFromJSON(String line) {
    try {
      JSONObject jsonData = new JSONObject(line);
      userName    = jsonData.getString("userName");
      createdDate = jsonData.getString("createdDate");
      statusText  = jsonData.getString("statusText");
      statusID    = jsonData.getLong("statusID");
      userType    = jsonData.getString("userType");
    } catch (Throwable t) {
      System.out.println("UNABLE TO PARSE: [" + line + "]");
    }
  }
  
  /**
   * @return the statusID
   */
  public long getStatusID() {
    return statusID;
  }

  /**
   * @param statusID the statusID to set
   */
  public void setStatusID(long statusID) {
    this.statusID = statusID;
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
   * @return the createdDate
   */
  public String getCreatedDate() {
    return createdDate;
  }

  /**
   * @param createdDate the createdDate to set
   */
  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * @return the statusText
   */
  public String getStatusText() {
    return statusText;
  }

  /**
   * @param statusText the statusText to set
   */
  public void setStatusText(String statusText) {
    this.statusText = statusText;
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
}
