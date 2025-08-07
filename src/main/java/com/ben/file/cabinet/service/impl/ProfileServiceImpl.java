package com.ben.file.cabinet.service.impl;

import com.ben.file.cabinet.model.Profile;
import com.ben.file.cabinet.repository.ProfileRepository;
import com.ben.file.cabinet.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public Collection<Profile> get(){
        return profileRepository.findAll();
    }

    @Override
    public Profile get(String id){
        return profileRepository.findById(id).orElse(null);
    }

    @Override
    public Profile save(String displayName, String email) {

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID().toString());
        profile.setDisplayName(displayName);
        profile.setEmail(email);
        return profileRepository.save(profile);
    }

    @Override
    public Profile update(String id, String email, String displayName, String avatarId) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = new Update();

        if (email != null) {
            update.set("email", email);
        }
        if (displayName != null) {
            update.set("displayName", displayName);
        }
        if (avatarId != null) {
            update.set("avatarId", avatarId);
        }

        return mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Profile.class
        );
    }

    @Override
    public Profile remove(String id) {
        Profile profile = profileRepository.findById(id).orElse(null);
        if(profile != null){
            profileRepository.deleteById(id);
        }
        return profile;
    }
}
