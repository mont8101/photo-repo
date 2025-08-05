package com.ben.file.cabinet.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.ben.file.cabinet.model.Artifact;
import com.ben.file.cabinet.repository.ArtifactRepository;
import com.ben.file.cabinet.service.ArtifactService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;

@Service
public class ArtifactServiceImpl implements ArtifactService {

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Override
    public Collection<Artifact> get() {
        return artifactRepository.findAll();
    }

    @Override
    public Artifact get(String id) {
        return artifactRepository.findById(id).orElse(null);
    }

    @Override
    public Artifact save(MultipartFile file, String userFileName) throws IOException {
        // Create unique filename for GridFS storage (to prevent conflicts)
        String uniqueGridFsFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Store file in GridFS with metadata
        Document metadata = new Document()
                .append("originalFileName", file.getOriginalFilename())
                .append("userFileName", userFileName)
                .append("contentType", file.getContentType())
                .append("fileSize", file.getSize());

        ObjectId gridFsId = gridFsTemplate.store(
                file.getInputStream(),
                uniqueGridFsFileName,
                file.getContentType(),
                metadata
        );

        Artifact artifact = new Artifact();
        artifact.setId(UUID.randomUUID().toString());
        artifact.setFileName(userFileName); // Use user-specified filename
        artifact.setContentType(file.getContentType());
        artifact.setFileSize(file.getSize());
        artifact.setGridFsId(gridFsId);

        return artifactRepository.save(artifact);
    }

    @Override
    public Artifact remove(String id) {
        Artifact artifact = artifactRepository.findById(id).orElse(null);
        if (artifact != null) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(artifact.getGridFsId())));
            artifactRepository.deleteById(id);
        }
        return artifact;
    }

    @Override
    public InputStream getFileData(String id) throws IOException {
        Artifact artifact = artifactRepository.findById(id).orElse(null);
        if (artifact == null) {
            throw new IOException("Artifact not found");
        }

        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(artifact.getGridFsId())));
        if (gridFSFile == null) {
            throw new IOException("File not found in GridFS");
        }

        return gridFSBucket.openDownloadStream(artifact.getGridFsId());
    }

    @Override
    public Collection<Artifact> get(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "uploadDate"));
        Page<Artifact> artifactPage = artifactRepository.findAllArtifactsMetadata(pageable);
        return artifactPage.getContent();
    }

    @Override
    public Collection<Artifact> getOrderByLikes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Artifact> artifactPage = artifactRepository.findAllOrderByLikes(pageable);
        return artifactPage.getContent();
    }

    @Override
    public Collection<Artifact> getOrderByUploadDate(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Artifact> artifactPage = artifactRepository.findAllOrderByUploadDate(pageable);
        return artifactPage.getContent();
    }

    @Override
    public boolean hasMoreArtifacts(int page, int size) {
        long totalCount = getTotalCount();
        long currentlyLoaded = (long) (page + 1) * size;
        return currentlyLoaded < totalCount;
    }

    @Override
    public long getTotalCount() {
        return artifactRepository.count();
    }

    @Override
    public Artifact incrementLike(String artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElse(null);
        if (artifact != null) {
            artifact.setLikeCount(artifact.getLikeCount() + 1);
            return artifactRepository.save(artifact);
        }
        return null;
    }

    @Override
    public Artifact decrementLike(String artifactId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElse(null);
        if (artifact != null && artifact.getLikeCount() > 0) {
            artifact.setLikeCount(artifact.getLikeCount() - 1);
            return artifactRepository.save(artifact);
        }
        return artifact;
    }

    @Override
    public Artifact toggleLike(String artifactId, String userId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElse(null);
        if (artifact != null) {
            if (artifact.isLikedByUser(userId)) {
                artifact.removeLike(userId);
            } else {
                artifact.addLike(userId);
            }
            return artifactRepository.save(artifact);
        }
        return null;
    }

    @Override
    public boolean isLikedByUser(String artifactId, String userId) {
        Artifact artifact = artifactRepository.findById(artifactId).orElse(null);
        return artifact != null && artifact.isLikedByUser(userId);
    }

    @Override
    public Collection<Artifact> searchByFileName(String fileName) {
        return artifactRepository.findByFileNameContainingIgnoreCase(fileName);
    }

    @Override
    public Collection<Artifact> getByContentType(String contentType) {
        return artifactRepository.findByContentType(contentType);
    }
}