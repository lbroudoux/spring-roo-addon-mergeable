// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.github.lbroudoux.roo.addon.mergeable.domain;

import com.github.lbroudoux.roo.addon.mergeable.domain.Tweet;
import java.util.Set;

privileged aspect Tweet_Roo_JavaBean {
    
    public String Tweet.getAuthor() {
        return this.author;
    }
    
    public void Tweet.setAuthor(String author) {
        this.author = author;
    }
    
    public String Tweet.getContent() {
        return this.content;
    }
    
    public void Tweet.setContent(String content) {
        this.content = content;
    }
    
    public Set<Tweet> Tweet.getRetweets() {
        return this.retweets;
    }
    
    public void Tweet.setRetweets(Set<Tweet> retweets) {
        this.retweets = retweets;
    }
    
    public Tweet Tweet.getOriginal() {
        return this.original;
    }
    
    public void Tweet.setOriginal(Tweet original) {
        this.original = original;
    }
    
}