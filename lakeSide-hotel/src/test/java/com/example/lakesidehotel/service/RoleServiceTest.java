package com.example.lakesidehotel.service;

import com.example.lakesidehotel.exception.RoleAlreadyExistException;
import com.example.lakesidehotel.exception.UserAlreadyExistException;
import com.example.lakesidehotel.model.Role;
import com.example.lakesidehotel.model.User;
import com.example.lakesidehotel.repository.RoleRepository;
import com.example.lakesidehotel.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class RoleServiceTest {
    @Test
    void getRolesTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role1 = new Role("1");
        Role role2 = new Role("2");
        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));
        List<Role> roles = roleService.getRoles();
        assertEquals(2, roles.size());
        assertEquals("1", roles.get(0).getName());
        assertEquals("2", roles.get(1).getName());
    }

    @Test
    void createRoleBadDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        when(roleRepository.existsByName("ROLE_123")).thenReturn(true);
        assertThrows(RoleAlreadyExistException.class, () -> roleService.createRole(new Role("123")));
    }

    @Test
    void createRoleGoodDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        when(roleRepository.existsByName("ROLE_123")).thenReturn(false);
        when(roleRepository.save(any())).thenReturn(new Role());
        roleService.createRole(new Role("123"));
        verify(roleRepository).existsByName("ROLE_123");
        verify(roleRepository).save(any());
    }

    @Test
    void deleteRoleTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        doNothing().when(roleRepository).deleteById(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(new Role()));
        when(roleRepository.save(any())).thenReturn(new Role());
        roleService.deleteRole(1L);
        verify(roleRepository).deleteById(1L);
    }

    @Test
    void findByNameTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        when(roleRepository.findByName("123")).thenReturn(Optional.of(role));
        Role role1 = roleService.findByName("123");
        assertEquals("123", role1.getName());
    }

    @Test
    void removeUserFromRoleGoodDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        role.setId(1L);
        User user = new User();
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        role.setUsers(users);
        user.setRoles(roles);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.save(role)).thenReturn(role);
        roleService.removeUserFromRole(1L, 1L);
        verify(roleRepository).save(role);
        assertEquals(0, role.getUsers().size());
    }

    @Test
    void removeUserFromRoleBadDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        role.setId(1L);
        User user = new User();
        user.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(UsernameNotFoundException.class, () -> roleService.removeUserFromRole(1L, 1L));
    }

    @Test
    void assignRoleToUserBadDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        role.setId(1L);
        User user = new User();
        user.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        List<User> users = new ArrayList<>();
        users.add(user);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        role.setUsers(users);
        user.setRoles(roles);
        assertThrows(UserAlreadyExistException.class, () -> roleService.assignRoleToUser(1L, 1L));
    }

    @Test
    void assignRoleToUserGoodDataTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        role.setId(1L);
        User user = new User();
        user.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.save(role)).thenReturn(role);
        roleService.assignRoleToUser(1L, 1L);
        verify(roleRepository).save(role);
    }

    @Test
    void removeAllUsersFromRoleTest(){
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRepository userRepository = mock(UserRepository.class);
        RoleService roleService = new RoleService(roleRepository, userRepository);
        Role role = new Role("123");
        role.setId(1L);
        Role role1 = new Role("245");
        role1.setId(1L);
        User user = new User();
        user.setId(1L);
        List<User> users = new ArrayList<>();
        users.add(user);
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        roles.add(role1);
        role.setUsers(users);
        user.setRoles(roles);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);
        roleService.removeAllUsersFromRole(1L);
        verify(roleRepository).save(role);
        assertEquals(0, role.getUsers().size());
    }

}
