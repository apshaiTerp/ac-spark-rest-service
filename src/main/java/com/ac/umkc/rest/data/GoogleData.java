package com.ac.umkc.rest.data;

import org.json.JSONObject;

import scala.Serializable;

/**
 * @author AC010168
 *
 */
public class GoogleData implements Serializable {
  
  /** Adding so I can serialize this mess */
  private static final long serialVersionUID = 336972756824513853L;
  
  private String location;
  private double geoLat;
  private double geoLon;
  private int    count;
  
  public GoogleData() {
    location = null;
    geoLat = 0.0;
    geoLon = 0.0;
    count  = 0;
  }
  
  @Override
  public String toString() {
    return "{\"location\":\"" + location + "\", \"count\":" + count + ", \"geoLat\":" + geoLat + ", \"geoLon\":" + geoLon + "}";
  }
  
  /**
   * A helper method to parse a JSON String into this object.
   * 
   * @param line the JSON record
   */
  public void parseFromJSON(String line) {
    try {
      JSONObject jsonData = new JSONObject(line);
      location = jsonData.getString("location");
      geoLat   = jsonData.getDouble("geoLat");
      geoLon   = jsonData.getDouble("geoLon");
      count    = jsonData.getInt("count");
    } catch (Throwable t) {
      System.out.println("UNABLE TO PARSE: [" + line + "]");
    }
  }

  /**
   * @return the location
   */
  public String getLocation() {
    return location;
  }

  /**
   * @param location the location to set
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * @return the geoLat
   */
  public double getGeoLat() {
    return geoLat;
  }

  /**
   * @param geoLat the geoLat to set
   */
  public void setGeoLat(double geoLat) {
    this.geoLat = geoLat;
  }

  /**
   * @return the geoLon
   */
  public double getGeoLon() {
    return geoLon;
  }

  /**
   * @param geoLon the geoLon to set
   */
  public void setGeoLon(double geoLon) {
    this.geoLon = geoLon;
  }

  /**
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * @param count the count to set
   */
  public void setCount(int count) {
    this.count = count;
  }
}
