package com.ac.umkc.rest.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author AC010168
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TwitterEmbedData {

  private String url;
  private String author_name;
  private String author_url;
  private String html;
  private int    width;
  private int    height;
  private String type;
  private String cache_age;
  private String provider_name;
  private String provider_url;
  private String version;
  
  public TwitterEmbedData() {}

  /**
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * @param url the url to set
   */
  public void setUrl(String url) {
    this.url = url;
  }

  /**
   * @return the author_name
   */
  public String getAuthor_name() {
    return author_name;
  }

  /**
   * @param author_name the author_name to set
   */
  public void setAuthor_name(String author_name) {
    this.author_name = author_name;
  }

  /**
   * @return the author_url
   */
  public String getAuthor_url() {
    return author_url;
  }

  /**
   * @param author_url the author_url to set
   */
  public void setAuthor_url(String author_url) {
    this.author_url = author_url;
  }

  /**
   * @return the html
   */
  public String getHtml() {
    return html;
  }

  /**
   * @param html the html to set
   */
  public void setHtml(String html) {
    this.html = html;
  }

  /**
   * @return the width
   */
  public int getWidth() {
    return width;
  }

  /**
   * @param width the width to set
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * @return the height
   */
  public int getHeight() {
    return height;
  }

  /**
   * @param height the height to set
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * @return the cache_age
   */
  public String getCache_age() {
    return cache_age;
  }

  /**
   * @param cache_age the cache_age to set
   */
  public void setCache_age(String cache_age) {
    this.cache_age = cache_age;
  }

  /**
   * @return the provider_name
   */
  public String getProvider_name() {
    return provider_name;
  }

  /**
   * @param provider_name the provider_name to set
   */
  public void setProvider_name(String provider_name) {
    this.provider_name = provider_name;
  }

  /**
   * @return the provider_url
   */
  public String getProvider_url() {
    return provider_url;
  }

  /**
   * @param provider_url the provider_url to set
   */
  public void setProvider_url(String provider_url) {
    this.provider_url = provider_url;
  }

  /**
   * @return the version
   */
  public String getVersion() {
    return version;
  }

  /**
   * @param version the version to set
   */
  public void setVersion(String version) {
    this.version = version;
  }
  
  
  
  
}
