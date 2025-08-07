package com.ben.file.cabinet.service;

import com.ben.file.cabinet.model.Profile;

import java.util.Collection;

public interface ProfileService {

    Collection<Profile> get();
    Profile get(String id);
    Profile save(String displayName, String email);
    Profile update(String id, String email, String displayName, String AvatarId);
    Profile remove(String id);
}
