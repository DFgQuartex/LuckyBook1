package ru.lucky_book.entities;

public interface SocialImage {

    String getId();
    String getSource();
    String getThumbnail();
    long getDate();
    int getWidth();
    int getHeight();
    boolean isVideo();
}
