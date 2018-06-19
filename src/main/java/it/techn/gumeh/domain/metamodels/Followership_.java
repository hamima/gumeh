package it.techn.gumeh.domain.metamodels;

import it.techn.gumeh.domain.Followership;
import it.techn.gumeh.domain.Tag;
import it.techn.gumeh.domain.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

/**
 * Created by H_Maghboli on 6/18/2018.
 */
@StaticMetamodel(Followership.class)
public class Followership_ {
    public static volatile SingularAttribute<Followership, Long> id;
    public static volatile SingularAttribute<Followership, String> comment;
    public static volatile SingularAttribute<Followership, Instant> createdAt;
    public static volatile SingularAttribute<Followership, User> user;
    public static volatile SingularAttribute<Followership, Tag> tag;
    public static volatile SingularAttribute<Followership, String> reason;


}
