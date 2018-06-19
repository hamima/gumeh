package it.techn.gumeh.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Post entity. This class is used in PostResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /posts?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class PostCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter resourceBrief;

    private IntegerFilter noLikes;

    private StringFilter tagStr;

    private StringFilter userBrief;

    private StringFilter link;

    private StringFilter text;

    private LongFilter categoryId;

    public PostCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getResourceBrief() {
        return resourceBrief;
    }

    public void setResourceBrief(StringFilter resourceBrief) {
        this.resourceBrief = resourceBrief;
    }

    public IntegerFilter getNoLikes() {
        return noLikes;
    }

    public void setNoLikes(IntegerFilter noLikes) {
        this.noLikes = noLikes;
    }

    public StringFilter getTagStr() {
        return tagStr;
    }

    public void setTagStr(StringFilter tagStr) {
        this.tagStr = tagStr;
    }

    public StringFilter getUserBrief() {
        return userBrief;
    }

    public void setUserBrief(StringFilter userBrief) {
        this.userBrief = userBrief;
    }

    public StringFilter getLink() {
        return link;
    }

    public void setLink(StringFilter link) {
        this.link = link;
    }

    public StringFilter getText() {
        return text;
    }

    public void setText(StringFilter text) {
        this.text = text;
    }

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "PostCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (resourceBrief != null ? "resourceBrief=" + resourceBrief + ", " : "") +
                (noLikes != null ? "noLikes=" + noLikes + ", " : "") +
                (tagStr != null ? "tagStr=" + tagStr + ", " : "") +
                (userBrief != null ? "userBrief=" + userBrief + ", " : "") +
                (link != null ? "link=" + link + ", " : "") +
                (text != null ? "text=" + text + ", " : "") +
                (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            "}";
    }

}
