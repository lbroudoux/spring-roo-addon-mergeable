package com.github.lbroudoux.roo.addon.mergeable.domain;

import com.github.lbroudoux.roo.addon.mergeable.RooMergeable;

import java.util.Set;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
@RooMergeable
public class Tweet {

    @NotNull
    String author;

    @Size(max = 140)
    String content;

    @OneToMany(mappedBy = "original")
    Set<Tweet> retweets;

    @ManyToOne
    Tweet original;
}
