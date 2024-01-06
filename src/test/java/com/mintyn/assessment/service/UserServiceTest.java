//package com.mintyn.assessment.service;
//
//import com.mintyn.assessment.entity.User;
//import com.mintyn.assessment.exception.DuplicateUserException;
//import com.mintyn.assessment.repository.UserRepository;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
//import static org.mockito.Mockito.*;
//
//@RunWith(MockitoJUnitRunner.class)
//public class UserServiceTest {
//    @Mock
//    UserRepository userRepository;
//    @InjectMocks
//    UserService userService = new UserServiceImpl();
//
//    @Test
//    public void givenNewUser_whenRegisterUser_thenSaveUserInRepository() {
//        // Setup
//        User newUser = new User();
//        newUser.setUsername("johnWick");
//        newUser.setPassword("superman");
//
//        // Mocking the entity response
//        when(userRepository.findByUsername(newUser.getUsername())).thenReturn(Optional.empty());
//
//        // Performing the test
//        userService.saveUser(newUser);
//
//        // Verify that the save method was called exactly once
//        verify(userRepository, times(1)).save(newUser);
//    }
//
//    @Test
//    public void givenExistingUser_whenRegisterUser_thenThrowDuplicateUserException() {
//        // Setup
//        User existingUser = new User();
//        existingUser.setId(1);
//        existingUser.setUsername("johnWick");
//        existingUser.setPassword("superman");
//
//        // Mocking the entity response
//        when(userRepository.findByUsername(existingUser.getUsername())).thenReturn(Optional.of(existingUser));
//
//        // Verify the result
//        assertThatExceptionOfType(DuplicateUserException.class).isThrownBy(() ->
//                {
//                    // Performing the test
//                    userService.saveUser(existingUser);
//                }
//        ).withMessage("User with the provided information already exists. Please choose a different username");
//
//        // Verify that the save method was not called
//        verify(userRepository, times(0)).save(existingUser);
//    }
//}
