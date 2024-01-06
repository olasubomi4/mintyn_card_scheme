package com.mintyn.assessment.security.manager;


import com.mintyn.assessment.entity.User;
import com.mintyn.assessment.exception.InvalidCredentialsException;
import com.mintyn.assessment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component

public class CustomAuthenticationManager implements AuthenticationManager {

    @Autowired
    private UserService userServiceImpl;
    private BCryptPasswordEncoder bCryptPasswordEncoder= new BCryptPasswordEncoder();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = userServiceImpl.getUser(authentication.getName());
        if (!bCryptPasswordEncoder.matches(authentication.getCredentials().toString(), user.getPassword())) {
            throw new InvalidCredentialsException("You provided an incorrect password.");
        }
        return new UsernamePasswordAuthenticationToken(authentication.getName(), user.getPassword());
    }
}
