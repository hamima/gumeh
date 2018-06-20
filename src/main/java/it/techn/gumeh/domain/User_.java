package it.techn.gumeh.domain;

import it.techn.gumeh.domain.User;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by H_Maghboli on 6/19/2018.
 */
@StaticMetamodel(User.class)
public abstract class User_ {
    public static volatile SingularAttribute<User, Long> id;
}
