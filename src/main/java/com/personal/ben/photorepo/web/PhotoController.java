package com.personal.ben.photorepo.web;

import com.personal.ben.photorepo.model.Photo;
import com.personal.ben.photorepo.service.PhotoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    // Paginated endpoint for infinite scroll
    @GetMapping({"/", ""})//api or api/ will hit
    public Map<String, Object> get(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // You'll need to update your PhotoService to support pagination
        // For now, this assumes you'll add a paginated method
        Collection<Photo> photos = photoService.get(page, size);
        boolean hasMore = photoService.hasMorePhotos(page, size);

        Map<String, Object> response = new HashMap<>();
        response.put("photos", photos);
        response.put("hasMore", hasMore);
        response.put("currentPage", page);
        response.put("pageSize", size);

        return response;
    }

    @GetMapping("/{id}")
    public Photo get(@PathVariable String id){
        Photo photo = photoService.get(id);
        if(photo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return photo;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id){
        Photo photo = photoService.remove(id);
        if(photo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping({"/", ""})
    public Photo create(@RequestBody PhotoUploadRequest request) throws IOException {
        // Decode base64 data
        byte[] data = Base64.getDecoder().decode(request.getData());
        return photoService.save(request.getFileName(), request.getContentType(), data);
    }

    // DTO for JSON upload
    public static class PhotoUploadRequest {
        private String fileName;
        private String contentType;
        private String data; // base64 encoded

        // Constructors
        public PhotoUploadRequest() {}

        public PhotoUploadRequest(String fileName, String contentType, String data) {
            this.fileName = fileName;
            this.contentType = contentType;
            this.data = data;
        }

        // Getters and setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public String getContentType() { return contentType; }
        public void setContentType(String contentType) { this.contentType = contentType; }

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }
}