package it.techn.gumeh.domain;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

/**
 * Created by H_Maghboli on 6/18/2018.
 */
@StaticMetamodel(Followership.class)
public abstract class Followership_ {
    public static volatile SingularAttribute<Followership, Long> id;
    public static volatile SingularAttribute<Followership, String> comment;
    public static volatile SingularAttribute<Followership, Instant> createdAt;
    public static volatile SingularAttribute<Followership, User> user;
    public static volatile SingularAttribute<Followership, Tag> tag;
    public static volatile SingularAttribute<Followership, String> reason;


}
