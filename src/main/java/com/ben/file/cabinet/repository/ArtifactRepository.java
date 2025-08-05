package com.ben.file.cabinet.repository;

import com.ben.file.cabinet.model.Artifact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ArtifactRepository extends MongoRepository<Artifact, String> {
    Collection<Artifact> findByFileNameContainingIgnoreCase(String fileName);

    Collection<Artifact> findByContentType(String contentType);

    @Query(value = "{}", fields = "{ 'id': 1, 'fileName': 1, 'contentType': 1, 'fileSize': 1, 'uploadDate': 1, 'likeCount': 1, 'gridFsId': 1 }")
    Page<Artifact> findAllArtifactsMetadata(Pageable pageable);

    @Query(value = "{}", sort = "{ 'likeCount': -1 }")
    Page<Artifact> findAllOrderByLikes(Pageable pageable);

    @Query(value = "{}", sort = "{ 'uploadDate': -1 }")
    Page<Artifact> findAllOrderByUploadDate(Pageable pageable);
}