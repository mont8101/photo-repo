package com.personal.ben.photorepo.web;

import com.personal.ben.photorepo.model.Photo;
import com.personal.ben.photorepo.service.PhotoService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class DownloadController {

        private final PhotoService photoService;

        //spring will scan classes to grab annotated service to inject in this controller
        public PhotoController(PhotoService photoService){
            this.photoService = photoService;
        }

        @GetMapping("/download/{id}")
        public ResponseEntity<byte[]> download(@PathVariable String id){
            Photo photo = photoService.get(id);
            if (photo == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            byte[] data = photo.getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(photo.getContentType()));
            ContentDisposition build = ContentDisposition
                    .builder("attachment")
                    .filename(photo.getFileName())
                    .build();
            headers.setContentDisposition(build);
            return new ResponseEntity<>(data, headers, HttpStatus.OK);
        }

}
