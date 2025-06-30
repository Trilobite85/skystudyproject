package org.sky.study.service;

import org.sky.study.model.jpa.User;

public interface UserService {

    User getUser(Long id);
    void deleteUser(Long id);
    void registerUser(User user);
    boolean isUserExists(String username);
}