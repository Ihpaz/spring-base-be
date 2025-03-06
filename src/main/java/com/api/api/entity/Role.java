package com.api.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private int id;

    private String uuid;

    private String role;

    @OneToMany(mappedBy = "role")
    private List<User> user;

    @OneToMany(mappedBy = "role")
    private List<RoleMenu> roleMenu;
}
