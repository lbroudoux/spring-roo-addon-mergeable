package com.github.lbroudoux.roo.addon.mergeable.domain;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;

@RooIntegrationTest(entity = Tweet.class)
public class TweetIntegrationTest {

   @Autowired
   private TweetDataOnDemand dod;
   
   @Test
   public void testMergeableMethod() {
      Tweet obj1 = dod.getSpecificTweet(1);
      Tweet obj2 = dod.getSpecificTweet(2);
      
      // Ensure we've got no merge at start.
      Assert.assertNull(obj1.getMergeResult());
      Assert.assertNull(obj2.getMergeResult());
      
      // Prepare values.
      obj1.setAuthor("lbroudoux");
      obj2.setAuthor("someone");
      obj1.setContent(null);
      obj2.setContent("Someone else content");
      obj1.merge();
      obj2.merge();
      
      // Merge and check references tracking.
      Tweet merge = obj1.merge(obj2);
      Assert.assertNotNull(merge.getMergeMainReference());
      Assert.assertEquals(obj1, merge.getMergeMainReference());
      Assert.assertNotNull(merge.getMergeSecondReference());
      Assert.assertEquals(obj2, merge.getMergeSecondReference());
      Assert.assertNotNull(obj1.getMergeResult());
      Assert.assertEquals(merge, obj1.getMergeResult());
      Assert.assertNotNull(obj2.getMergeResult());
      Assert.assertEquals(merge, obj2.getMergeResult());
      
      // Check verification methods.
      Assert.assertTrue(merge.isMergeResult());
      Assert.assertFalse(obj1.isMergeResult());
      Assert.assertFalse(obj2.isMergeResult());
      Assert.assertFalse(merge.wasMerged());
      Assert.assertTrue(obj1.wasMerged());
      Assert.assertTrue(obj2.wasMerged());
      
      // Check value merging.
      Assert.assertEquals("lbroudoux", merge.getAuthor());
      Assert.assertEquals("Someone else content", merge.getContent());
   }
}
