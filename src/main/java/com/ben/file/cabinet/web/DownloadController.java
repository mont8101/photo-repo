package com.ben.file.cabinet.web;

import com.ben.file.cabinet.model.Artifact;
import com.ben.file.cabinet.service.ArtifactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping(path = "/api")
public class DownloadController {

    @Autowired
    private ArtifactService artifactService;

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String id) {
        try {
            Artifact artifact = artifactService.get(id);
            if (artifact == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
            }

            InputStream fileStream = artifactService.getFileData(id);
            InputStreamResource resource = new InputStreamResource(fileStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(artifact.getContentType()));
            headers.setContentLength(artifact.getFileSize());

            ContentDisposition contentDisposition = ContentDisposition
                    .builder("attachment")
                    .filename(artifact.getFileName())
                    .build();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving file: " + e.getMessage());
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<InputStreamResource> view(@PathVariable String id) {
        try {
            Artifact artifact = artifactService.get(id);
            if (artifact == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
            }

            InputStream fileStream = artifactService.getFileData(id);
            InputStreamResource resource = new InputStreamResource(fileStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(artifact.getContentType()));
            headers.setContentLength(artifact.getFileSize());

            // For viewing in browser instead of downloading
            ContentDisposition contentDisposition = ContentDisposition
                    .builder("inline")
                    .filename(artifact.getFileName())
                    .build();
            headers.setContentDisposition(contentDisposition);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving file: " + e.getMessage());
        }
    }

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<InputStreamResource> thumbnail(@PathVariable String id) {
        // For future implementation - could generate thumbnails on the fly
        // For now, returns the original image
        return view(id);
    }
}