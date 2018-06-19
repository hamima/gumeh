package it.techn.gumeh.service.dto;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A DTO for the Post entity.
 */
public class PostDTO implements Serializable {

    private Long id;

    private String resourceBrief;

    private Integer noLikes;

    private String tagStr;

    private String userBrief;

    private String link;

    private String text;

    private Long categoryId;

    private String categoryTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResourceBrief() {
        return resourceBrief;
    }

    public void setResourceBrief(String resourceBrief) {
        this.resourceBrief = resourceBrief;
    }

    public Integer getNoLikes() {
        return noLikes;
    }

    public void setNoLikes(Integer noLikes) {
        this.noLikes = noLikes;
    }

    public String getTagStr() {
        return tagStr;
    }

    public void setTagStr(String tagStr) {
        this.tagStr = tagStr;
    }

    public String getUserBrief() {
        return userBrief;
    }

    public void setUserBrief(String userBrief) {
        this.userBrief = userBrief;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long resurceId) {
        this.categoryId = resurceId;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public void setCategoryTitle(String resurceTitle) {
        this.categoryTitle = resurceTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PostDTO postDTO = (PostDTO) o;
        if(postDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), postDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PostDTO{" +
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
