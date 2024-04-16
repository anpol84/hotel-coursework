package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.RoleAlreadyExistException;
import com.example.lakesidehotel.exception.UserAlreadyExistException;
import com.example.lakesidehotel.model.Role;
import com.example.lakesidehotel.model.User;
import com.example.lakesidehotel.repository.RoleRepository;
import com.example.lakesidehotel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    public List<Role> getRoles(){
        return roleRepository.findAll();
    }

    public Role createRole(Role role){
        String name = "ROLE_" + role.getName().toUpperCase();
        Role role1 = new Role(name);
        if (roleRepository.existsByName(name)){
            throw new RoleAlreadyExistException(role.getName() + " role already exists");
        }
        return roleRepository.save(role1);
    }

    public void deleteRole(Long roleId){
        removeAllUsersFromRole(roleId);
        roleRepository.deleteById(roleId);
    }

    public Role findByName(String name){
        return roleRepository.findByName(name).get();
    }

    public User removeUserFromRole(Long userId, Long roleId){
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (role.isPresent() && role.get().getUsers().contains(user.get())){
            role.get().removeUserFromRole(user.get());
            roleRepository.save(role.get());
            return user.get();
        }
        throw new UsernameNotFoundException("User not found");

    }

    public User assignRoleToUser(Long userId, Long roleId){
        Optional<User> user = userRepository.findById(userId);
        Optional<Role> role = roleRepository.findById(roleId);
        if (user.isPresent() && user.get().getRoles().contains(role.get())){
            throw new UserAlreadyExistException(user.get().getFirstName() + " is already assigned to the "
                    + role.get().getName() + " role");
        }
        if (role.isPresent()){
            role.get().assignRoleToUser(user.get());
            roleRepository.save(role.get());
        }
        return user.get();
    }

    public Role removeAllUsersFromRole(Long roleId) {
        Optional<Role> role = roleRepository.findById(roleId);
        role.ifPresent(Role::removeAllUsersFromRole);
        return roleRepository.save(role.get());
    }
}
