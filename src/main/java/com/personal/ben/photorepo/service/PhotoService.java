package com.personal.ben.photorepo.service;

import com.personal.ben.photorepo.model.Photo;

import java.util.Collection;

public interface PhotoService {

    // Basic CRUD operations
    Collection<Photo> get();
    Photo get(String id);
    Photo save(String fileName, String contentType, byte[] data);
    Photo remove(String id);

    // Pagination methods for infinite scroll
    Collection<Photo> get(int page, int size);
    boolean hasMorePhotos(int page, int size);
    long getTotalCount();
}