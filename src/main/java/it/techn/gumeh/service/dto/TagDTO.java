package it.techn.gumeh.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Tag entity.
 */
public class TagDTO implements Serializable {

    private Long id;

    @NotNull
    private String title;

    private Integer noPosts;

    private Integer noFollowers;

    private String relatedTags;

    private String encyclopediaLink;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNoPosts() {
        return noPosts;
    }

    public void setNoPosts(Integer noPosts) {
        this.noPosts = noPosts;
    }

    public Integer getNoFollowers() {
        return noFollowers;
    }

    public void setNoFollowers(Integer noFollowers) {
        this.noFollowers = noFollowers;
    }

    public String getRelatedTags() {
        return relatedTags;
    }

    public void setRelatedTags(String relatedTags) {
        this.relatedTags = relatedTags;
    }

    public String getEncyclopediaLink() {
        return encyclopediaLink;
    }

    public void setEncyclopediaLink(String encyclopediaLink) {
        this.encyclopediaLink = encyclopediaLink;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TagDTO tagDTO = (TagDTO) o;
        if(tagDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tagDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TagDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", noPosts=" + getNoPosts() +
            ", noFollowers=" + getNoFollowers() +
            ", relatedTags='" + getRelatedTags() + "'" +
            ", encyclopediaLink='" + getEncyclopediaLink() + "'" +
            "}";
    }
}
