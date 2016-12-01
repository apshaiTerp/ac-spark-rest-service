package com.ac.umkc.spark.util;

import org.springframework.web.client.RestTemplate;

import com.ac.umkc.rest.data.TwitterEmbedData;


/**
 * @author AC010168
 *
 */
public class TwitterCall {

  public static String getEmbedBody(String requestURL) {
    RestTemplate restTemplate = new RestTemplate();
    TwitterEmbedData data = restTemplate.getForObject(requestURL, TwitterEmbedData.class);
    return data.getHtml();
  }
}
