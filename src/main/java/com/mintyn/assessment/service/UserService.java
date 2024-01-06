package com.mintyn.assessment.service;


import com.mintyn.assessment.entity.User;

public interface UserService {

    User getUser(String username);

    User saveUser(User user);

}