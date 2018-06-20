package it.techn.gumeh.domain;

import it.techn.gumeh.domain.enumeration.ActivityType;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.Instant;

/**
 * Created by H_Maghboli on 6/18/2018.
 */
@StaticMetamodel(Activity.class)
public class Activity_ {
    public static volatile SingularAttribute<Activity, Long> id;
    public static volatile SingularAttribute<Activity, ActivityType> type;
    public static volatile SingularAttribute<Activity, Instant> createdAt;
    public static volatile SingularAttribute<Activity, String> comment;
    public static volatile SingularAttribute<Activity, Boolean> deleted;
    public static volatile SingularAttribute<Activity, String> reportReason;
    public static volatile SingularAttribute<Activity, String> userBrief;
    public static volatile SingularAttribute<Activity, String> activityDesc;
    public static volatile SingularAttribute<Activity, Post> post;
    public static volatile SingularAttribute<Activity, User> user;

}
