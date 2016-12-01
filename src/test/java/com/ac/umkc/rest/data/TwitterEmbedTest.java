package com.ac.umkc.rest.data;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.ac.umkc.spark.util.TwitterCall;

/**
 * @author AC010168
 *
 */
public class TwitterEmbedTest {

  @Test
  public void testEmbedURL() {
    try {
      String oembedURL = "https://publish.twitter.com/oembed?url=https://twitter.com/trzewik/status/799334851447058432";
      String result = TwitterCall.getEmbedBody(oembedURL);
      System.out.println ("Result: " + result);
    } catch (Throwable t) {
      t.printStackTrace();
      fail("I broke, badly: " + t.getMessage());
    }
  }
}
