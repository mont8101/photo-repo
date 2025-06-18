package com.personal.ben.photorepo.repository;

import com.personal.ben.photorepo.model.Photo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends MongoRepository<Photo, String> {

    // Basic CRUD operations are inherited from MongoRepository:
    // - save(Photo photo)
    // - findById(String id)
    // - findAll()
    // - deleteById(String id)
    // - count()

    // Paginated query - excludes the data field for performance
    @Query(value = "{}", fields = "{ 'data' : 0 }")
    Page<Photo> findAllPhotosWithoutData(Pageable pageable);

    // Find all without data field (for listing)
    @Query(value = "{}", fields = "{ 'data' : 0 }")
    List<Photo> findAllWithoutData();

    // Find by filename (useful for search functionality later)
    @Query(value = "{ 'fileName' : { $regex: ?0, $options: 'i' } }", fields = "{ 'data' : 0 }")
    List<Photo> findByFileNameContainingIgnoreCase(String fileName);

    // Find by content type (useful for filtering by image type)
    @Query(value = "{ 'contentType' : ?0 }", fields = "{ 'data' : 0 }")
    List<Photo> findByContentType(String contentType);

    // Custom method to find photo with data (for download)
    Optional<Photo> findById(String id);
}