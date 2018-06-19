package it.techn.gumeh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import it.techn.gumeh.domain.enumeration.ActivityType;

/**
 * A Activity.
 */
@Entity
@Table(name = "activity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "activity")
public class Activity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private ActivityType type;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "jhi_comment")
    private String comment;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "report_reason")
    private String reportReason;

    @Column(name = "user_brief")
    private String userBrief;

    @Column(name = "activity_desc")
    private String activityDesc;

    @ManyToOne(optional = false)
    @NotNull
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    private Post post;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActivityType getType() {
        return type;
    }

    public Activity type(ActivityType type) {
        this.type = type;
        return this;
    }

    public void setType(ActivityType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Activity createdAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getComment() {
        return comment;
    }

    public Activity comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public Activity deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getReportReason() {
        return reportReason;
    }

    public Activity reportReason(String reportReason) {
        this.reportReason = reportReason;
        return this;
    }

    public void setReportReason(String reportReason) {
        this.reportReason = reportReason;
    }

    public String getUserBrief() {
        return userBrief;
    }

    public Activity userBrief(String userBrief) {
        this.userBrief = userBrief;
        return this;
    }

    public void setUserBrief(String userBrief) {
        this.userBrief = userBrief;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public Activity activityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
        return this;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public User getUser() {
        return user;
    }

    public Activity user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public Activity post(Post post) {
        this.post = post;
        return this;
    }

    public void setPost(Post post) {
        this.post = post;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Activity activity = (Activity) o;
        if (activity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), activity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", comment='" + getComment() + "'" +
            ", deleted='" + isDeleted() + "'" +
            ", reportReason='" + getReportReason() + "'" +
            ", userBrief='" + getUserBrief() + "'" +
            ", activityDesc='" + getActivityDesc() + "'" +
            "}";
    }
}
