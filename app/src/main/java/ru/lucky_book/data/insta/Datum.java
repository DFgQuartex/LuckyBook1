
package ru.lucky_book.data.insta;

import java.util.List;

import ru.lucky_book.entities.SocialImage;

public class Datum implements SocialImage {

    private Object attribution;
    private List<Object> tags = null;
    private String type;
    private Object location;
    private Comments comments;
    private String filter;
    private String createdTime;
    private String link;
    private Likes likes;
    private Images images;
    private List<Object> usersInPhoto = null;
    private Object caption;
    private Boolean userHasLiked;
    private String id;
    private User user;

    public Object getAttribution() {
        return attribution;
    }

    public void setAttribution(Object attribution) {
        this.attribution = attribution;
    }

    public List<Object> getTags() {
        return tags;
    }

    public void setTags(List<Object> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Comments getComments() {
        return comments;
    }

    public void setComments(Comments comments) {
        this.comments = comments;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Likes getLikes() {
        return likes;
    }

    public void setLikes(Likes likes) {
        this.likes = likes;
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<Object> getUsersInPhoto() {
        return usersInPhoto;
    }

    public void setUsersInPhoto(List<Object> usersInPhoto) {
        this.usersInPhoto = usersInPhoto;
    }

    public Object getCaption() {
        return caption;
    }

    public void setCaption(Object caption) {
        this.caption = caption;
    }

    public Boolean getUserHasLiked() {
        return userHasLiked;
    }

    public void setUserHasLiked(Boolean userHasLiked) {
        this.userHasLiked = userHasLiked;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getSource() {
        if (images.getStandardResolution() != null)
            return images.getStandardResolution().getUrl();
        if (images.getLowResolution() != null)
            return images.getLowResolution().getUrl();
        return images.getThumbnail().getUrl();
    }

    @Override
    public String getThumbnail() {
        return images.getThumbnail().getUrl();
    }

    @Override
    public long getDate() {
        return createdTime != null ? Integer.parseInt(createdTime) : 0;
    }

    @Override
    public int getWidth() {
        if (images.getStandardResolution() != null)
            return images.getStandardResolution().getWidth();
        if (images.getLowResolution() != null)
            return images.getLowResolution().getWidth();
        return images.getThumbnail().getWidth();
    }

    @Override
    public int getHeight() {
        if (images.getStandardResolution() != null)
            return images.getStandardResolution().getHeight();
        if (images.getLowResolution() != null)
            return images.getLowResolution().getHeight();
        return images.getThumbnail().getHeight();
    }

    @Override
    public boolean isVideo() {
        return !type.contains("image");
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
