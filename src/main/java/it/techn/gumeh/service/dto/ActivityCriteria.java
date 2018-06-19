package it.techn.gumeh.service.dto;

import java.io.Serializable;
import it.techn.gumeh.domain.enumeration.ActivityType;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;

import io.github.jhipster.service.filter.InstantFilter;




/**
 * Criteria class for the Activity entity. This class is used in ActivityResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /activities?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class ActivityCriteria implements Serializable {
    /**
     * Class for filtering ActivityType
     */
    public static class ActivityTypeFilter extends Filter<ActivityType> {
    }

    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private ActivityTypeFilter type;

    private InstantFilter createdAt;

    private StringFilter comment;

    private BooleanFilter deleted;

    private StringFilter reportReason;

    private StringFilter userBrief;

    private StringFilter activityDesc;

    private LongFilter userId;

    private LongFilter postId;

    public ActivityCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public ActivityTypeFilter getType() {
        return type;
    }

    public void setType(ActivityTypeFilter type) {
        this.type = type;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public StringFilter getComment() {
        return comment;
    }

    public void setComment(StringFilter comment) {
        this.comment = comment;
    }

    public BooleanFilter getDeleted() {
        return deleted;
    }

    public void setDeleted(BooleanFilter deleted) {
        this.deleted = deleted;
    }

    public StringFilter getReportReason() {
        return reportReason;
    }

    public void setReportReason(StringFilter reportReason) {
        this.reportReason = reportReason;
    }

    public StringFilter getUserBrief() {
        return userBrief;
    }

    public void setUserBrief(StringFilter userBrief) {
        this.userBrief = userBrief;
    }

    public StringFilter getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(StringFilter activityDesc) {
        this.activityDesc = activityDesc;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getPostId() {
        return postId;
    }

    public void setPostId(LongFilter postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "ActivityCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (type != null ? "type=" + type + ", " : "") +
                (createdAt != null ? "createdAt=" + createdAt + ", " : "") +
                (comment != null ? "comment=" + comment + ", " : "") +
                (deleted != null ? "deleted=" + deleted + ", " : "") +
                (reportReason != null ? "reportReason=" + reportReason + ", " : "") +
                (userBrief != null ? "userBrief=" + userBrief + ", " : "") +
                (activityDesc != null ? "activityDesc=" + activityDesc + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
                (postId != null ? "postId=" + postId + ", " : "") +
            "}";
    }

}
