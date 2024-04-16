package com.example.lakesidehotel.response;

import com.example.lakesidehotel.model.User;
import jakarta.persistence.ManyToMany;
import lombok.Data;

import java.util.Collection;
import java.util.HashSet;
@Data
public class RoleResponse {
    private Long id;
    private String name;

    private Collection<String> users;

    public RoleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
