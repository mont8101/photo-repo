package com.ben.file.cabinet.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "artifact")
public class Artifact {

    @Id
    private String id;

    private String fileName;
    private String contentType;
    private long fileSize;
    private LocalDateTime uploadDate;

    private ObjectId gridFsId;

    private int likeCount;
    private Set<String> likedByUsers;

    public Artifact() {
        this.uploadDate = LocalDateTime.now();
        this.likeCount = 0;
        this.likedByUsers = new HashSet<>();
    }

    public Artifact(String fileName, String contentType, long fileSize, ObjectId gridFsId) {
        this();
        this.fileName = fileName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.gridFsId = gridFsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ObjectId getGridFsId() {
        return gridFsId;
    }

    public void setGridFsId(ObjectId gridFsId) {
        this.gridFsId = gridFsId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public Set<String> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(Set<String> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    // Utility methods for like system
    public boolean isLikedByUser(String profileId) {
        return likedByUsers.contains(profileId);
    }

    public void addLike(String profileId) {
        if (!likedByUsers.contains(profileId)) {
            likedByUsers.add(profileId);
            likeCount++;
        }
    }

    public void removeLike(String profileId) {
        if (likedByUsers.contains(profileId)) {
            likedByUsers.remove(profileId);
            likeCount--;
        }
    }
}