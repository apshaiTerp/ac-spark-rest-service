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
      
      System.out.println ("Printing early result: " + result);
      
      System.out.println ("Has doubleQuotes: " + result.indexOf("\""));
      
      //Now we need to sanitize the output before things blow up
      result = result.replaceAll("\n", "");
      result = result.replaceAll("\b", "");
      result = result.replaceAll("\f", "");
      result = result.replaceAll("\t", "");
      result = result.replaceAll("\r", "");
      
      result = result.replaceAll("/", "\\\\/");
      result = result.replaceAll("\"", "\\\\\"");

      System.out.println ("Mixed Result: " + result);
    } catch (Throwable t) {
      t.printStackTrace();
      fail("I broke, badly: " + t.getMessage());
    }
  }
}
