package it.techn.gumeh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Post.
 */
@Entity
@Table(name = "post")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "post")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_brief")
    private String resourceBrief;

    @Column(name = "no_likes")
    private Integer noLikes;

    @Column(name = "tag_str")
    private String tagStr;

    @Column(name = "user_brief")
    private String userBrief;

    @Column(name = "jhi_link")
    private String link;

    @Column(name = "text")
    private String text;

    @ManyToOne(optional = false)
    @NotNull
    private Resurce category;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceBrief() {
        return resourceBrief;
    }

    public Post resourceBrief(String resourceBrief) {
        this.resourceBrief = resourceBrief;
        return this;
    }

    public void setResourceBrief(String resourceBrief) {
        this.resourceBrief = resourceBrief;
    }

    public Integer getNoLikes() {
        return noLikes;
    }

    public Post noLikes(Integer noLikes) {
        this.noLikes = noLikes;
        return this;
    }

    public void setNoLikes(Integer noLikes) {
        this.noLikes = noLikes;
    }

    public String getTagStr() {
        return tagStr;
    }

    public Post tagStr(String tagStr) {
        this.tagStr = tagStr;
        return this;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }

    public String getUserBrief() {
        return userBrief;
    }

    public Post userBrief(String userBrief) {
        this.userBrief = userBrief;
        return this;
    }

    public void setUserBrief(String userBrief) {
        this.userBrief = userBrief;
    }

    public String getLink() {
        return link;
    }

    public Post link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public Post text(String text) {
        this.text = text;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Resurce getCategory() {
        return category;
    }

    public Post category(Resurce resurce) {
        this.category = resurce;
        return this;
    }

    public void setCategory(Resurce resurce) {
        this.category = resurce;
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
        Post post = (Post) o;
        if (post.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), post.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Post{" +
            "id=" + getId() +
            ", resourceBrief='" + getResourceBrief() + "'" +
            ", noLikes=" + getNoLikes() +
            ", tagStr='" + getTagStr() + "'" +
            ", userBrief='" + getUserBrief() + "'" +
            ", link='" + getLink() + "'" +
            ", text='" + getText() + "'" +
            "}";
    }
}
