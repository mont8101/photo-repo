package com.ben.file.cabinet.repository;

import com.ben.file.cabinet.model.Profile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<Profile, String> {

}
