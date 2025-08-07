package com.ben.file.cabinet.web;

import com.ben.file.cabinet.model.Artifact;
import com.ben.file.cabinet.model.Profile;
import com.ben.file.cabinet.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping({"/", ""})
    public Map<String, Object> get(){
        Collection<Profile> profiles = profileService.get();
        Map<String, Object> response = new HashMap<>();
        response.put("profiles", profiles);
        return response;
    }

    @GetMapping("/{id}")
    public Profile get(@PathVariable String id){
        Profile profile = profileService.get(id);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }
        return profile;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String id) {
        Profile profile = profileService.remove(id);
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Artifact not found");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "profile deleted successfully");
        response.put("displayName", profile.getDisplayName());
        return ResponseEntity.ok(response);
    }

    @PostMapping({"/", ""})
    public ResponseEntity<Profile> create(
            @RequestParam(value = "displayName", required = true) String displayName,
            @RequestParam(value = "email", required = true) String email) {

            Profile savedProfile = profileService.save(displayName, email);
            return ResponseEntity.status(HttpStatus.OK).body(savedProfile);
    }
}
