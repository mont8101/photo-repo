package com.ben.file.cabinet.service.impl;

import com.ben.file.cabinet.model.Profile;
import com.ben.file.cabinet.service.ProfileService;

import java.util.UUID;

public class ProfileServiceImpl implements ProfileService {

    @Override
    public Profile create(String displayName, String email) {

        Profile profile = new Profile();
        profile.setId(UUID.randomUUID().toString());
        profile.setDisplayName(displayName);
        profile.setEmail(email);
        return profile;
    }

    @Override
    public Profile update(String id, String AvatarId) {
        return null;
    }

    @Override
    public Profile delete(String id) {
        return null;
    }
}
