package com.mintyn.assessment.services.interfaces;


import com.mintyn.assessment.entities.User;

public interface UserService {
    User getUser(String username);
    User saveUser(User user);

}