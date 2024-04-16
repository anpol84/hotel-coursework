package com.example.lakesidehotel.controller;

import com.example.lakesidehotel.model.User;
import com.example.lakesidehotel.response.UserResponse;
import com.example.lakesidehotel.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getUsers(){
        List<User> users = userService.getUsers();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users){
            UserResponse userResponse = new UserResponse(user.getId(),
                    user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getPassword());
            List<String> roles = user.getRoles().stream().map(Object::toString).toList();
            userResponse.setRoles(roles);
            userResponses.add(userResponse);
        }
        return new ResponseEntity<>(userResponses, HttpStatus.FOUND);
    }

    @GetMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){
        try{
            User user = userService.getUser(email);
            UserResponse userResponse = new UserResponse(user.getId(),
                    user.getFirstName(), user.getLastName(),
                    user.getEmail(), user.getPassword());
            List<String> roles = user.getRoles().stream().map(Object::toString).toList();
            userResponse.setRoles(roles);
            return ResponseEntity.ok(userResponse);
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user");
        }
    }

    @DeleteMapping("/{email}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or (hasRole('ROLE_USER') and #email == principal.username)")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email){
        try{
            userService.deleteUser(email);
            return ResponseEntity.ok("User deleted successfully");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user");
        }
    }


}
