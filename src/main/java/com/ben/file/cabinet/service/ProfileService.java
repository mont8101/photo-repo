package com.ben.file.cabinet.service;

import com.ben.file.cabinet.model.Profile;

public interface ProfileService {

    Profile create(String displayName, String email);
    Profile update(String id, String AvatarId);
    Profile delete(String id);
}
