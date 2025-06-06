package com.personal.ben.photorepo.web;

import com.personal.ben.photorepo.model.Photo;
import com.personal.ben.photorepo.service.PhotoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;

@RestController
public class PhotoController {

    private final PhotoService photoService;

    //spring will scan classes to grab annotated service to inject in this controller
    public PhotoController(PhotoService photoService){
        this.photoService = photoService;
    }

    @GetMapping("/")
    public String hello() {
        return "hello";
    }

    @GetMapping("/photos")
    public Collection<Photo> get(){
        return photoService.get();
    }

    @GetMapping("/photos/{id}")
    public Photo get(@PathVariable String id){
        Photo photo = photoService.get(id);
        if(photo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return photo;
    }

    @DeleteMapping("/photos/{id}")
    public void delete(@PathVariable String id){
        Photo photo = photoService.remove(id);
        if(photo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/photos")
    public Photo create(@RequestPart("data") MultipartFile file) throws IOException {
        //@RequestPart("data") matches form data append name in upload.html
        return photoService.save(file.getOriginalFilename(), file.getContentType(), file.getBytes());
    }
}