package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.UserAlreadyExistException;
import com.example.lakesidehotel.model.Role;
import com.example.lakesidehotel.model.User;
import com.example.lakesidehotel.repository.RoleRepository;
import com.example.lakesidehotel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

    @Test
    void registerUserBadDataTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        when(userRepository.existsByEmail("123")).thenReturn(true);
        User user = new User();
        user.setEmail("123");
        assertThrows(UserAlreadyExistException.class, () -> userService.registerUser(user));
    }

    @Test
    void registerUserGoodDataTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        User user = new User();
        user.setEmail("123");
        user.setPassword("123");
        when(userRepository.existsByEmail("123")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("123");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(new Role("ROLE_USER")));
        when(userRepository.save(user)).thenReturn(user);
        userService.registerUser(user);
        verify(userRepository).existsByEmail("123");
        verify(passwordEncoder).encode("123");
        verify(roleRepository).findByName("ROLE_USER");
        verify(userRepository).save(user);
    }

    @Test
    void getUsersTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        User user1 = new User();
        user1.setEmail("1");
        User user2 = new User();
        user2.setEmail("2");
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        List<User> users = userService.getUsers();
        assertEquals(2, users.size());
        assertEquals("1", users.get(0).getEmail());
        assertEquals("2", users.get(1).getEmail());
    }

    @Test
    void deleteUserTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        User user = new User();
        user.setEmail("123");
        when(userRepository.findByEmail("123")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteByEmail("123");
        userService.deleteUser("123");
        verify(userRepository).findByEmail("123");
        verify(userRepository).deleteByEmail("123");
    }

    @Test
    void getUserBadDataTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        when(userRepository.findByEmail("123")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("123"));
    }

    @Test
    void getUserGoodDataTest(){
        UserRepository userRepository = mock(UserRepository.class);
        RoleRepository roleRepository = mock(RoleRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService userService = new UserService(userRepository, roleRepository, passwordEncoder);
        User user = new User();
        user.setEmail("123");
        when(userRepository.findByEmail("123")).thenReturn(Optional.of(user));
        User user1 = userService.getUser("123");
        assertEquals("123", user1.getEmail());
    }
}
