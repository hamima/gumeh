package it.techn.gumeh.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import it.techn.gumeh.domain.enumeration.ResourceType;

/**
 * A DTO for the Resurce entity.
 */
public class ResurceDTO implements Serializable {

    private Long id;

    @NotNull
    private ResourceType type;

    @NotNull
    private String title;

    private Integer publishYear;

    private Integer noPosts;

    private String creator;

    private String link;

    @NotNull
    private Boolean activated;

    @NotNull
    private Boolean certified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getNoPosts() {
        return noPosts;
    }

    public void setNoPosts(Integer noPosts) {
        this.noPosts = noPosts;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean isActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public Boolean isCertified() {
        return certified;
    }

    public void setCertified(Boolean certified) {
        this.certified = certified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ResurceDTO resurceDTO = (ResurceDTO) o;
        if(resurceDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), resurceDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ResurceDTO{" +
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
