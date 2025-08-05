package com.ben.file.cabinet.web;

import com.ben.file.cabinet.model.Artifact;
import com.ben.file.cabinet.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/artifacts")
public class ArtifactController {

    @Autowired
    private ArtifactService artifactService;

    // Paginated endpoint for infinite scroll
    @GetMapping({"/", ""})
    public Map<String, Object> get(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "uploadDate") String sortBy) {

        Collection<Artifact> artifacts;

        switch (sortBy.toLowerCase()) {
            case "likes":
                artifacts = artifactService.getOrderByLikes(page, size);
                break;
            case "uploaddate":
            default:
                artifacts = artifactService.getOrderByUploadDate(page, size);
                break;
        }

        boolean hasMore = artifactService.hasMoreArtifacts(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("artifacts", artifacts);
        response.put("hasMore", hasMore);
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("totalCount", artifactService.getTotalCount());

        return response;
    }

    @GetMapping("/{id}")
    public Artifact get(@PathVariable String id) {
        Artifact artifact = artifactService.get(id);
        if (artifact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }
        return artifact;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        Artifact artifact = artifactService.remove(id);
        if (artifact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Artifact deleted successfully");
        response.put("fileName", artifact.getFileName());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<Artifact> create(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String userFileName) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
            }

            // Validate fileName parameter
            if (userFileName == null || userFileName.trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fileName parameter is required");
            }

            // Validate file type (images only)
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only image files are allowed");
            }

            // Validate file size (e.g., max 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds maximum limit of 10MB");
            }

            Artifact savedArtifact = artifactService.save(file, userFileName.trim());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedArtifact);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error saving file: " + e.getMessage());
        }
    }

    // Like system endpoints
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> incrementLike(@PathVariable String id) {
        Artifact artifact = artifactService.incrementLike(id);
        if (artifact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("artifactId", id);
        response.put("likeCount", artifact.getLikeCount());
        response.put("message", "Like added successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> decrementLike(@PathVariable String id) {
        Artifact artifact = artifactService.decrementLike(id);
        if (artifact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("artifactId", id);
        response.put("likeCount", artifact.getLikeCount());
        response.put("message", "Like removed successfully");
        return ResponseEntity.ok(response);
    }

    // Future per-user like endpoint (when auth is implemented)
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "anonymous") String userId) {

        Artifact artifact = artifactService.toggleLike(id, userId);
        if (artifact == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }

        boolean isLiked = artifactService.isLikedByUser(id, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("artifactId", id);
        response.put("likeCount", artifact.getLikeCount());
        response.put("isLiked", isLiked);
        response.put("message", isLiked ? "Artifact liked" : "Artifact unliked");
        return ResponseEntity.ok(response);
    }

    // Search endpoints
    @GetMapping("/search")
    public Collection<Artifact> search(@RequestParam String fileName) {
        return artifactService.searchByFileName(fileName);
    }

    @GetMapping("/by-type")
    public Collection<Artifact> getByContentType(@RequestParam String contentType) {
        return artifactService.getByContentType(contentType);
    }
}