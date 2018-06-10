package it.techn.gumeh.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Tag.
 */
@Entity
@Table(name = "tag")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "tag")
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "no_posts")
    private Integer noPosts;

    @Column(name = "encyclopedia_link")
    private String encyclopediaLink;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Tag title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNoPosts() {
        return noPosts;
    }

    public Tag noPosts(Integer noPosts) {
        this.noPosts = noPosts;
        return this;
    }

    public void setNoPosts(Integer noPosts) {
        this.noPosts = noPosts;
    }

    public String getEncyclopediaLink() {
        return encyclopediaLink;
    }

    public Tag encyclopediaLink(String encyclopediaLink) {
        this.encyclopediaLink = encyclopediaLink;
        return this;
    }

    public void setEncyclopediaLink(String encyclopediaLink) {
        this.encyclopediaLink = encyclopediaLink;
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
        Tag tag = (Tag) o;
        if (tag.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Tag{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", noPosts=" + getNoPosts() +
            ", encyclopediaLink='" + getEncyclopediaLink() + "'" +
            "}";
    }
}
