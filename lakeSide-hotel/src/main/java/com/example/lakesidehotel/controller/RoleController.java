package com.example.lakesidehotel.controller;

import com.example.lakesidehotel.exception.RoleAlreadyExistException;
import com.example.lakesidehotel.exception.UserAlreadyExistException;
import com.example.lakesidehotel.model.Role;
import com.example.lakesidehotel.model.User;
import com.example.lakesidehotel.response.RoleResponse;
import com.example.lakesidehotel.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles(){
        List<Role> roles = roleService.getRoles();
        List<RoleResponse> roleResponses = new ArrayList<>();
        for (Role role : roles){
            RoleResponse roleResponse = new RoleResponse(role.getId(), role.getName());
            List<String> users = role.getUsers().stream().map(Objects::toString).toList();
            roleResponse.setUsers(users);
            roleResponses.add(roleResponse);
        }
        return new ResponseEntity<>(roleResponses, HttpStatus.FOUND);
    }

    @PostMapping
    public ResponseEntity<String> createRole(@RequestBody Role role){
        try {
            roleService.createRole(role);
            return ResponseEntity.ok("New role created successfully");
        }catch (RoleAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void deleteRole(@PathVariable("id") Long id){
        roleService.deleteRole(id);
    }

    @PostMapping("/removeAll/{id}")
    public ResponseEntity<String> removeAllUsersFromRole(@PathVariable("id") Long id){
        roleService.removeAllUsersFromRole(id);
        return ResponseEntity.ok("Removed");
    }

    @PostMapping("/remove")
    public ResponseEntity<String> removeUserFromRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        try {
            roleService.removeUserFromRole(userId, roleId);
            return ResponseEntity.ok("Removed");
        }catch (UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User not found");
        }
    }

    @PostMapping("/assign")
    public ResponseEntity<String> assignRoleToUser(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        try {
            roleService.assignRoleToUser(userId, roleId);
            return ResponseEntity.ok("Assigned");
        }catch (UserAlreadyExistException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exist");
        }
    }

}
