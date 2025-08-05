package com.ben.file.cabinet.service;

import com.ben.file.cabinet.model.Artifact;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public interface ArtifactService {

    // Basic CRUD operations
    Collection<Artifact> get();
    Artifact get(String id);
    Artifact save(MultipartFile file, String userFileName) throws IOException;
    Artifact remove(String id);

    // File operations
    InputStream getFileData(String id) throws IOException;

    // Pagination methods for infinite scroll
    Collection<Artifact> get(int page, int size);
    Collection<Artifact> getOrderByLikes(int page, int size);
    Collection<Artifact> getOrderByUploadDate(int page, int size);
    boolean hasMoreArtifacts(int page, int size);
    long getTotalCount();

    // Like system methods
    Artifact incrementLike(String artifactId);
    Artifact decrementLike(String artifactId);

    // Future per-user like methods (when auth is implemented)
    Artifact toggleLike(String artifactId, String userId);
    boolean isLikedByUser(String artifactId, String userId);

    // Search methods
    Collection<Artifact> searchByFileName(String fileName);
    Collection<Artifact> getByContentType(String contentType);
}