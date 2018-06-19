package it.techn.gumeh.domain.metamodels;

import it.techn.gumeh.domain.Tag;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 * Created by H_Maghboli on 6/19/2018.
 */
@StaticMetamodel(Tag.class)
public class Tag_ {
    public static volatile SingularAttribute<Tag, Long> id;
}
