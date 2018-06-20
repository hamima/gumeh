package it.techn.gumeh.domain;

import it.techn.gumeh.domain.Post;
import it.techn.gumeh.domain.Resurce;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by H_Maghboli on 6/18/2018.
 */
//@Generated("ir.techn.gumeh.meta")
@StaticMetamodel(Post.class)
public abstract class Post_ {
    public static volatile SingularAttribute<Post, Long> id;
    public static volatile SingularAttribute<Post, String> resourceBrief;
    public static volatile SingularAttribute<Post, Integer> noLikes;
    public static volatile SingularAttribute<Post, String> tagStr;
    public static volatile SingularAttribute<Post, String> userBrief;
    public static volatile SingularAttribute<Post, String> link;
    public static volatile SingularAttribute<Post, String> text;
    public static volatile SingularAttribute<Post, Resurce> category;
}
