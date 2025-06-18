package com.personal.ben.photorepo.service.impl;

import com.personal.ben.photorepo.model.Photo;
import com.personal.ben.photorepo.repository.PhotoRepository;
import com.personal.ben.photorepo.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Collection<Photo> get() {
        // Return all photos without data for performance
        return photoRepository.findAllWithoutData();
    }

    @Override
    public Photo get(String id) {
        // Return photo with data (for download functionality)
        return photoRepository.findById(id).orElse(null);
    }

    @Override
    public Photo save(String fileName, String contentType, byte[] data) {
        Photo photo = new Photo();
        photo.setId(UUID.randomUUID().toString());
        photo.setFileName(fileName);
        photo.setContentType(contentType);
        photo.setData(data);

        return photoRepository.save(photo);
    }

    @Override
    public Photo remove(String id) {
        Photo photo = photoRepository.findById(id).orElse(null);
        if (photo != null) {
            photoRepository.deleteById(id);
        }
        return photo;
    }

    // New paginated methods
    @Override
    public Collection<Photo> get(int page, int size) {
        // Sort by ID descending to get newest photos first (like Instagram)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Photo> photoPage = photoRepository.findAllPhotosWithoutData(pageable);
        return photoPage.getContent();
    }

    @Override
    public boolean hasMorePhotos(int page, int size) {
        long totalCount = getTotalCount();
        long currentlyLoaded = (long) (page + 1) * size;
        return currentlyLoaded < totalCount;
    }

    @Override
    public long getTotalCount() {
        return photoRepository.count();
    }

    // Additional utility methods
    public Collection<Photo> searchByFileName(String fileName) {
        return photoRepository.findByFileNameContainingIgnoreCase(fileName);
    }

    public Collection<Photo> getByContentType(String contentType) {
        return photoRepository.findByContentType(contentType);
    }
}