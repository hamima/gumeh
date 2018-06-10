package it.techn.gumeh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

import it.techn.gumeh.domain.enumeration.ResourceType;

/**
 * A Resurce.
 */
@Entity
@Table(name = "resurce")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "resurce")
public class Resurce implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private ResourceType type;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(name = "no_posts")
    private Integer noPosts;

    @Column(name = "creator")
    private String creator;

    @Column(name = "jhi_link")
    private String link;

    @NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated;

    @NotNull
    @Column(name = "certified", nullable = false)
    private Boolean certified;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceType getType() {
        return type;
    }

    public Resurce type(ResourceType type) {
        this.type = type;
        return this;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public Resurce title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public Resurce publishYear(Integer publishYear) {
        this.publishYear = publishYear;
        return this;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getNoPosts() {
        return noPosts;
    }

    public Resurce noPosts(Integer noPosts) {
        this.noPosts = noPosts;
        return this;
    }

    public void setNoPosts(Integer noPosts) {
        this.noPosts = noPosts;
    }

    public String getCreator() {
        return creator;
    }

    public Resurce creator(String creator) {
        this.creator = creator;
        return this;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLink() {
        return link;
    }

    public Resurce link(String link) {
        this.link = link;
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean isActivated() {
        return activated;
    }

    public Resurce activated(Boolean activated) {
        this.activated = activated;
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean isCertified() {
        return certified;
    }

    public Resurce certified(Boolean certified) {
        this.certified = certified;
        return this;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
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
        Resurce resurce = (Resurce) o;
        if (resurce.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resurce.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Resurce{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", title='" + getTitle() + "'" +
            ", publishYear=" + getPublishYear() +
            ", noPosts=" + getNoPosts() +
            ", creator='" + getCreator() + "'" +
            ", link='" + getLink() + "'" +
            ", activated='" + isActivated() + "'" +
            ", certified='" + isCertified() + "'" +
            "}";
    }
}
